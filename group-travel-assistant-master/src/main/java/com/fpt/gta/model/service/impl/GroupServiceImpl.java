package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ConflictException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.*;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.*;
import com.fpt.gta.util.StringUtil;
import com.fpt.gta.util.ZonedDateTimeUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    GroupRepository groupRepository;
    PlaceService placeService;
    PersonService personService;
    AuthenticationService authenticationService;
    MemberService memberService;
    BlobService blobService;
    DocumentRepository documentRepository;
    CurrencyRepository currencyRepository;
    TransactionRepository transactionRepository;
    MemberRepository memberRepository;
    MessagingService messagingService;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, PlaceService placeService, PersonService personService, AuthenticationService authenticationService, MemberService memberService, BlobService blobService, DocumentRepository documentRepository, CurrencyRepository currencyRepository, TransactionRepository transactionRepository, MemberRepository memberRepository, MessagingService messagingService) {
        this.groupRepository = groupRepository;
        this.placeService = placeService;
        this.personService = personService;
        this.authenticationService = authenticationService;
        this.memberService = memberService;
        this.blobService = blobService;
        this.documentRepository = documentRepository;
        this.currencyRepository = currencyRepository;
        this.transactionRepository = transactionRepository;
        this.memberRepository = memberRepository;
        this.messagingService = messagingService;
    }

    @Override
    public Group getGroupById(Integer groupId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        } else {
            throw new NotFoundException("Can not found Group");
        }
    }

    @Override
    public List<Group> findAllJoinedGroup(String firebaseUid) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        List<Member> memberList = memberService.findAllByIdPerson(person.getId());
        List<Group> groupList = new ArrayList();

        for (int i = 0; i < memberList.size(); i++) {
            Member currentMember = memberList.get(i);
            Group currentGroup = currentMember.getJoinedGroup();
            if (currentMember.getIdStatus() == MemberStatus.ACTIVE) {
                //init lazy
                currentGroup.getStartPlace().getPlaceImageList().size();
                currentGroup.getMemberList().size();
                currentGroup.getStartPlace().getPlaceImageList().size();
                groupList.add(memberList.get(i).getJoinedGroup());
            }
        }

        return groupList;
    }

    @Override
    public Group createGroup(String firebaseUid, Group group) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);

        Place place = placeService.findOrCreatePlaceByGooglePlaceId(group.getStartPlace());

        //        setup group before create
        group.setStartPlace(place);
        group.setStartAt(group.getStartAt());
        group.setEndAt(group.getEndAt());

        try {
            group.setCurrency(currencyRepository.findByCode(group.getCurrency().getCode()).get());
        } catch (NullPointerException e) {

        }

        group.setStartUtcAt(ZonedDateTimeUtil.convertToUtcWithTimeZone(group.getStartAt(), group.getStartPlace().getTimeZone()));
        group.setEndUtcAt(ZonedDateTimeUtil.convertToUtcWithTimeZone(group.getEndAt(), group.getStartPlace().getTimeZone()));

        //      create Group
        Group newGroup = groupRepository.save(group);
        newGroup.setInvitationCode(makeInvitationCode(newGroup.getId()));
        newGroup.setIdStatus(GroupStatus.PLANNING);
        //      Merge person and group to member
        Member newMember = new Member();
        newMember.setIdRole(MemberRole.ADMIN);
        newMember.setIdStatus(MemberStatus.ACTIVE);
        newMember.setPerson(person);
        newMember.setJoinedGroup(newGroup);
        newMember = memberService.saveMember(newMember);
        newGroup.setMemberList(Arrays.asList(newMember));
        return newGroup;
    }

    @Override
    public void updateGroup(String firebaseUid, Group group) {
        //        prepare group entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Place place = placeService.findOrCreatePlaceByGooglePlaceId(group.getStartPlace());
        Group oldGroup = getGroupById(group.getId());

        //        check owner
        authenticationService.checkGroupOwner(person.getId(), oldGroup.getId());

        //        put update data
        oldGroup.setName(group.getName());
        oldGroup.setStartPlace(place);
        oldGroup.setStartAt(group.getStartAt());
        oldGroup.setEndAt(group.getEndAt());
        if (!oldGroup.getCurrency().getCode().equals(group.getCurrency().getCode())) {
            for (Member member : oldGroup.getMemberList()) {
                for (Transaction transaction : member.getTransactionList()) {
                    transaction.setCustomCurrencyRate(BigDecimal.ZERO);
                    transactionRepository.save(transaction);
                }
            }
        }
        try {
            System.out.println(currencyRepository.findByCode(group.getCurrency().getCode()).get().getCode());
            oldGroup.setCurrency(currencyRepository.findByCode(group.getCurrency().getCode()).get());
        } catch (NullPointerException e) {
        }

        oldGroup.setStartUtcAt(ZonedDateTimeUtil.convertToUtcWithTimeZone(oldGroup.getStartAt(), oldGroup.getStartPlace().getTimeZone()));
        oldGroup.setEndUtcAt(ZonedDateTimeUtil.convertToUtcWithTimeZone(oldGroup.getEndAt(), oldGroup.getStartPlace().getTimeZone()));

        LocalDate groupStartUtcLocalDate = oldGroup.getStartUtcAt().toLocalDateTime().toLocalDate();
        LocalDate groupEndUtcLocalDate = oldGroup.getEndUtcAt().toLocalDateTime().toLocalDate();

        for (Trip trip : oldGroup.getTripList()) {
            LocalDate tripStartUtcLocalDate = trip.getStartUtcAt().toLocalDateTime().toLocalDate();
            LocalDate tripEndUtcLocalDate = trip.getEndUtcAt().toLocalDateTime().toLocalDate();
            if (tripStartUtcLocalDate.isBefore(groupStartUtcLocalDate)) {
                throw new ConflictException();
            }
            if (tripEndUtcLocalDate.isAfter(groupEndUtcLocalDate)) {
                throw new ConflictException();
            }
        }

        //        update group
        groupRepository.save(oldGroup);

    }

    @Override
    public void leaveGroup(String firebaseUid, Integer idGroup) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group group = getGroupById(idGroup);

        if (!group.getIdStatus().equals(GroupStatus.PLANNING)) {
            throw new ConflictException("Only can enroll when planning");
        }

        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(), group.getId());
        member.setIdStatus(MemberStatus.INACTIVE);
        memberService.saveMember(member);

//        Integer inactiveMemberCount = memberService.countMemberByIdGroupAndIdStatus(group.getId(), MemberStatus.ACTIVE);
//        if (inactiveMemberCount == 0) {
//            groupRepository.deleteById(idGroup);
//        }
    }

    @Override
    public Group getPreviewGroup(Integer idGroup) {
        Group group = groupRepository.findById(idGroup).get();
        List<Trip> tripList = group.getTripList();
//        tripList.parallelStream().forEach(trip -> {
//            Hibernate.initialize(trip.getPlanList());
//            for (Plan plan : trip.getPlanList()) {
//                if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
//                    trip.setElectedPlan(plan);
//                    Hibernate.initialize(plan.getActivityList());
//                    plan.getActivityList().parallelStream().forEach(
//                            activity -> {
//                                Hibernate.initialize(activity.getDocumentWithActivityList());
//                                Hibernate.initialize(activity.getStartPlace().getPlaceImageList());
//                                Hibernate.initialize(activity.getEndPlace().getPlaceImageList());
//                            }
//                    );
//                    break;
//                }
//            }
//        });

        for (Trip trip : tripList) {
            for (Plan plan : trip.getPlanList()) {
                if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
                    trip.setElectedPlan(plan);
                    for (Activity activity : plan.getActivityList()) {
                        activity.getDocumentWithActivityList().size();
                        activity.getStartPlace().getPlaceImageList().size();
                        activity.getEndPlace().getPlaceImageList().size();
                    }
                    break;
                }
            }
        }
        group.getMemberList().size();
        return group;
    }

    @Override
    public String getInvitationCode(String firebaseUid, Integer idGroup) {
        return getGroupById(idGroup).getInvitationCode();
    }

    @Override
    public String getNewInvitationCode(String firebaseUid, Integer idGroup) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group group = getGroupById(idGroup);
        authenticationService.checkGroupOwner(person.getId(), idGroup);

        String invitationCode = makeInvitationCode(idGroup);
        group.setInvitationCode(invitationCode);
        groupRepository.save(group);

        return invitationCode;
    }

    @Override
    public Group enroll(String firebaseUid, String invitationCode) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Integer idGroup = getIdGroupFromInvitationCode(invitationCode);
        Group group = getGroupById(idGroup);

        if (!group.getIdStatus().equals(GroupStatus.PLANNING)) {
            throw new ConflictException("Only can enroll when planning");
        }

        if (!group.getInvitationCode().equals(invitationCode)) {
            throw new NotFoundException("invalid invitation code");
        }
        Optional<Member> optionalMember = Optional.empty();
        for (Member member : memberRepository.findAllByIdPerson(person.getId())) {
            if (member.getJoinedGroup().getId().equals(group.getId())) {
                optionalMember = Optional.of(member);
            }
        }
        if (optionalMember.isPresent()) {
            Member oldMember = optionalMember.get();
            oldMember.setIdStatus(MemberStatus.ACTIVE);
            oldMember = memberService.saveMember(oldMember);
            for (int i = 0; i < group.getMemberList().size(); i++) {
                if (group.getMemberList().get(i).getId().equals(oldMember.getId())) {
                    group.getMemberList().remove(i);
                    group.getMemberList().add(oldMember);
                    break;
                }
            }
        } else {
            //      Merge person and group to member
            Member newMember = new Member();
            newMember.setIdRole(MemberRole.MEMBER);
            newMember.setIdStatus(MemberStatus.ACTIVE);
            newMember.setPerson(person);
            newMember.setJoinedGroup(group);
            newMember = memberService.saveMember(newMember);
            group.getMemberList().add(newMember);
        }

        return group;
    }

    @Override
    public void deactivateMember(String firebaseUid, Integer idGroup, Integer idMember) {
        //        prepare entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group oldGroup = getGroupById(idGroup);
        Member member = memberService.getMemberByIdMember(idMember);
        //        check owner
        authenticationService.checkGroupOwner(person.getId(), oldGroup.getId());
        // deactivate member
        member.setIdStatus(MemberStatus.INACTIVE);
        memberService.saveMember(member);
    }

    @Override
    public void makePending(String firebaseUid, Integer idGroup) {
        //        prepare entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group oldGroup = getGroupById(idGroup);

        authenticationService.checkGroupOwner(person.getId(), idGroup);
        if (!oldGroup.getIdStatus().equals(GroupStatus.PLANNING)) {
            throw new ConflictException("action is invalid with current status");
        }
        if (oldGroup.getTripList().size()==0){
            throw new ConflictException("need at least one trip elected");
        }
        boolean isAllTripElected = true;
        for (Trip trip : oldGroup.getTripList()) {
            boolean isAnyElected = false;
            for (Plan plan : trip.getPlanList()) {
                if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
                    isAnyElected = true;
                    break;
                }
            }
            if (!isAnyElected) {
                isAllTripElected = false;
            }
        }
        if (!isAllTripElected) {
            throw new ConflictException("There is one or more trip have not bean elected yet");
        }
        oldGroup.setIdStatus(GroupStatus.PENDING);
        groupRepository.save(oldGroup);

        for (Member member : oldGroup.getMemberList()) {
            member.setIsReady(false);
        }
        memberRepository.saveAll(oldGroup.getMemberList());

        reloadGroupStatusRealtimeAndNotify(idGroup, oldGroup);
    }

    @Override
    public void makePlanning(String firebaseUid, Integer idGroup) {
        //        prepare entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group oldGroup = getGroupById(idGroup);

        authenticationService.checkGroupOwner(person.getId(), idGroup);
        if (!oldGroup.getIdStatus().equals(GroupStatus.PENDING)) {
            throw new ConflictException("action is invalid with current status");
        }

        oldGroup.setIdStatus(GroupStatus.PLANNING);
        oldGroup = groupRepository.save(oldGroup);

        for (Member member : oldGroup.getMemberList()) {
            member.setIsReady(false);
        }
        memberRepository.saveAll(oldGroup.getMemberList());

        reloadGroupStatusRealtimeAndNotify(idGroup, oldGroup);
    }

    @Override
    public void makeMemberReady(String firebaseUid, Integer idGroup, Boolean isReady) {
        //        prepare entity
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Group oldGroup = getGroupById(idGroup);
        authenticationService.checkJoinedGroup(person.getId(), idGroup);
        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(), idGroup);

        if (oldGroup.getIdStatus().equals(GroupStatus.PENDING)) {
            member.setIsReady(isReady);
            memberRepository.save(member);
            List<Member> memberList = oldGroup.getMemberList();
            boolean isAllReady = true;
            for (Member eachMember : memberList) {
                if (eachMember.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    if (eachMember.getIsReady() == false) {
                        isAllReady = false;
                    }
                }
            }
            if (isAllReady) {
                oldGroup.setIdStatus(GroupStatus.EXECUTING);
                oldGroup = groupRepository.save(oldGroup);
                reloadGroupStatusRealtimeAndNotify(idGroup, oldGroup);
            } else {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference(FirebaseDatabaseConstant.getReloadReadyPath(idGroup));
                ref.setValueAsync(Instant.now().toEpochMilli());
            }
        } else {
            throw new ConflictException("Need pending to make ready");
        }
    }

    private void reloadGroupStatusRealtimeAndNotify(Integer idGroup, Group oldGroup) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDatabaseConstant.getReloadGroupStatusPath(idGroup));
        ref.setValueAsync(Instant.now().toEpochMilli());

        try {
            Map<String, String> data = new HashMap<>();
            data.put("messageType", "groupStatus");
            data.put("idGroup", idGroup.toString());
            data.put("idStatus", oldGroup.getIdStatus().toString());
            data.put("name", oldGroup.getName());
            messagingService.messageAllInGroupAsync(idGroup, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeInvitationCode(Integer idGroup) {
        String info = idGroup + "," + Instant.now().toEpochMilli() + "," + StringUtil.generateRandomBase64Token(20);
        String code = StringUtil.encodeBase64(info);
        return code;
    }

    private static Integer getIdGroupFromInvitationCode(String invitationCode) {
        String info = StringUtil.decodeBase64(invitationCode);
        List<String> stringList = Arrays.asList(info.split(","));
        return Integer.parseInt(stringList.get(0));
    }

}
