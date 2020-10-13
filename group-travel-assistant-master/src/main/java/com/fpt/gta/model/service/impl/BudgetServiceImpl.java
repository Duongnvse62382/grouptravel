package com.fpt.gta.model.service.impl;

import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.GroupRepository;
import com.fpt.gta.model.repository.TripRepository;
import com.fpt.gta.model.service.AuthenticationService;
import com.fpt.gta.model.service.BudgetService;
import com.fpt.gta.model.service.MemberService;
import com.fpt.gta.model.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {
    TripRepository tripRepository;
    AuthenticationService authenticationService;
    GroupRepository groupRepository;
    PersonService personService;
    MemberService memberService;


    @Autowired
    public BudgetServiceImpl(TripRepository tripRepository, AuthenticationService authenticationService, GroupRepository groupRepository, PersonService personService, MemberService memberService) {
        this.tripRepository = tripRepository;
        this.authenticationService = authenticationService;
        this.groupRepository = groupRepository;
        this.personService = personService;
        this.memberService = memberService;
    }

    @Override
    public List<Trip> getTripWithElectedPlan(String firebaseUid, Integer idGroup) {
//        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, idGroup);
        Group group = groupRepository.findById(idGroup).get();
        List<Trip> tripList = group.getTripList();
        for (Trip trip : tripList) {
            for (Plan plan : trip.getPlanList()) {
                if (plan.getIdStatus().equals(PlanStatus.ELECTED)) {
                    trip.setElectedPlan(plan);
                    break;
                }
            }
        }
        return tripList;
    }

    @Override
    public Member getMyBudgetInGroup(String firebaseUid, Integer idGroup) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        return memberService.getByIdPersonAndIdGroup(person.getId(), idGroup);
    }

    @Override
    public void updateMyBudgetInGroup(String firebaseUid, Integer idGroup, Member member) {
        Member oldMember = getMyBudgetInGroup(firebaseUid, idGroup);

        oldMember.setActivityBudget(member.getActivityBudget());
        oldMember.setAccommodationBudget(member.getAccommodationBudget());
        oldMember.setTransportationBudget(member.getTransportationBudget());
        oldMember.setFoodBudget(member.getFoodBudget());

        memberService.saveMember(oldMember);
    }

    @Override
    public void updateGroupBudget(String firebaseUid, Integer idGroup, Group group) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkGroupOwner(person.getId(), idGroup);

        Group oldGroup = groupRepository.findById(idGroup).get();
        oldGroup.setActivityBudget(group.getActivityBudget());
        oldGroup.setAccommodationBudget(group.getAccommodationBudget());
        oldGroup.setTransportationBudget(group.getTransportationBudget());
        oldGroup.setFoodBudget(group.getFoodBudget());

        groupRepository.save(oldGroup);
    }
}
