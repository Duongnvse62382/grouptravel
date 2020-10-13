package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ForbiddenException;
import com.fpt.gta.model.constant.MemberRole;
import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    GroupRepository groupRepository;
    MemberRepository memberRepository;
    TripRepository tripRepository;
    SuggestedActivityRepository suggestedActivityRepository;
    PlanRepository planRepository;
    TransactionRepository transactionRepository;

    @Autowired
    public AuthenticationServiceImpl(GroupRepository groupRepository, MemberRepository memberRepository, TripRepository tripRepository, SuggestedActivityRepository suggestedActivityRepository, PlanRepository planRepository, TransactionRepository transactionRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.tripRepository = tripRepository;
        this.suggestedActivityRepository = suggestedActivityRepository;
        this.planRepository = planRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void checkGroupOwner(Integer idPerson, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdGroupAndIdRole(idGroup, MemberRole.ADMIN);
        if (memberList.size() > 0) {
            if (memberList.get(0).getPerson().getId() == idPerson) {
                check = true;
            }
        }
        if (!check) {
            throw new ForbiddenException("Only Admin can performs this action");
        }
    }

    @Override
    public boolean isGroupOwner(Integer idPerson, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdGroupAndIdRole(idGroup, MemberRole.ADMIN);
        if (memberList.size() > 0) {
            if (memberList.get(0).getPerson().getId() == idPerson) {
                check = true;
            }
        }
        return check;
    }

    @Override
    public void checkTripOwner(Integer idPerson, Integer idTrip) {
        Trip trip = tripRepository.findById(idTrip).get();
        Group group = trip.getGroup();
        checkGroupOwner(idPerson, group.getId());
    }

    @Override
    public boolean isTripOwner(Integer idPerson, Integer idTrip) {
        Trip trip = tripRepository.findById(idTrip).get();
        Group group = trip.getGroup();
        return isGroupOwner(idPerson, group.getId());
    }

    @Override
    public void checkSuggestedActivityOwner(Integer idPerson, Integer idSuggestedActivity) {
        Member member = suggestedActivityRepository.findById(idSuggestedActivity).get().getOwner();
        if (!member.getPerson().getId().equals(idPerson) || !member.getIdStatus().equals(MemberStatus.ACTIVE)) {
            throw new ForbiddenException("you are not owner of this Suggested Activity");
        }
    }

    @Override
    public boolean isSuggestedActivityOwner(Integer idPerson, Integer idSuggestedActivity) {
        return suggestedActivityRepository.findById(idSuggestedActivity).get().getOwner().getPerson().getId().equals(idPerson);
    }

    @Override
    public void checkJoinedGroup(Integer idPerson, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdJoinedGroupAndIdStatus(idGroup, MemberStatus.ACTIVE);
        for (Member member : memberList) {
            if (member.getPerson().getId().equals(idPerson)) {
                if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    check = true;
                }
                break;
            }
        }
        if (!check) {
            throw new ForbiddenException("You are not member of this group");
        }
    }

    @Override
    public void checkJoinedGroupByEmail(String email, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdJoinedGroupAndIdStatus(idGroup, MemberStatus.ACTIVE);
        for (Member member : memberList) {
            if (member.getPerson().getEmail().equals(email)) {
                if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    check = true;
                }
                break;
            }
        }
        if (!check) {
            throw new ForbiddenException("You are not member of this group");
        }
    }

    @Override
    public void checkJoinedGroupByFirebaseUid(String firebaseUid, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdJoinedGroupAndIdStatus(idGroup, MemberStatus.ACTIVE);
        for (Member member : memberList) {
            if (member.getPerson().getFirebaseUid().equals(firebaseUid)) {
                if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    check = true;
                }
                break;
            }
        }
        if (!check) {
            throw new ForbiddenException("You are not member of this group");
        }
    }

    @Override
    public boolean isJoinedGroup(Integer idPerson, Integer idGroup) {
        boolean check = false;
        List<Member> memberList = memberRepository.findAllByIdJoinedGroupAndIdStatus(idGroup, MemberStatus.ACTIVE);
        for (Member member : memberList) {
            if (member.getPerson().getId().equals(idPerson)) {
                if (member.getIdStatus().equals(MemberStatus.ACTIVE)) {
                    check = true;
                }
                break;
            }
        }
        return check;
    }

    @Override
    public void checkPlanOwner(Integer idPerson, Integer idPlan) {
        Optional<Plan> plan = planRepository.findById(idPlan);
        if (!plan.get().getOwner().getPerson().getId().equals(idPerson)) {
            throw new ForbiddenException("you are not owner of this plan");
        }
    }

    @Override
    public boolean isPlanOwner(Integer idPerson, Integer idPlan) {
        Optional<Plan> plan = planRepository.findById(idPlan);
        return plan.get().getOwner().getPerson().getId().equals(idPerson);

    }

    @Override
    public void checkTransactionOwner(Integer idPerson, Integer idTransaction) {
        boolean check = false;
        Optional<Transaction> optionalTransaction = transactionRepository.findById(idTransaction);

        if (optionalTransaction.isPresent()) {
            if (optionalTransaction.get().getOwner().getPerson().getId().equals(idPerson)) {
                check = true;
            }
        }

        if (!check) {
            throw new ForbiddenException("you are not owner of this Transaction");
        }
    }

    @Override
    public boolean isTransactionOwner(Integer idPerson, Integer idTransaction) {
        boolean check = false;
        Optional<Transaction> optionalTransaction = transactionRepository.findById(idTransaction);

        if (optionalTransaction.isPresent()) {
            if (optionalTransaction.get().getOwner().getPerson().getId().equals(idPerson)) {
                check = true;
            }
        }

        return check;
    }

    @Override
    public boolean isTransactionParticipationOrPayer(Integer idPerson, Integer idTransaction) {
        boolean check = false;
        Optional<Transaction> optionalTransaction = transactionRepository.findById(idTransaction);

        if (optionalTransaction.isPresent()) {
            for (TransactionDetail transactionDetail : optionalTransaction.get().getTransactionDetailList()) {
                if (transactionDetail.getMember() != null) {
                    if (transactionDetail.getMember().getPerson().getId().equals(idPerson)) {
                        check = true;
                        break;
                    }
                }
            }
        }

        return check;
    }
}
