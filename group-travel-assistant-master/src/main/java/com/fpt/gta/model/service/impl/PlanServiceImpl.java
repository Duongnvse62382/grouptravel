package com.fpt.gta.model.service.impl;

import com.fpt.gta.exception.ForbiddenException;
import com.fpt.gta.exception.NotFoundException;
import com.fpt.gta.model.constant.MemberStatus;
import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.*;
import com.fpt.gta.model.repository.*;
import com.fpt.gta.model.service.*;
import com.fpt.gta.rest.manageplan.PlanDTO;
import com.fpt.gta.util.ZonedDateTimeUtil;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    PlanRepository planRepository;
    TripService tripService;
    VotedPlanRepository votedPlanRepository;
    ActivityRepository activityRepository;
    MemberService memberService;
    PersonService personService;
    AuthenticationService authenticationService;
    TripRepository tripRepository;
    PlaceRepository placeRepository;
    ModelMapper modelMapper;
    SuggestedActivityService suggestedActivityService;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository, TripService tripService, VotedPlanRepository votedPlanRepository, ActivityRepository activityRepository, MemberService memberService, PersonService personService, AuthenticationService authenticationService, TripRepository tripRepository, PlaceRepository placeRepository, ModelMapper modelMapper, SuggestedActivityService suggestedActivityService) {
        this.planRepository = planRepository;
        this.tripService = tripService;
        this.votedPlanRepository = votedPlanRepository;
        this.activityRepository = activityRepository;
        this.memberService = memberService;
        this.personService = personService;
        this.authenticationService = authenticationService;
        this.tripRepository = tripRepository;
        this.placeRepository = placeRepository;
        this.modelMapper = modelMapper;
        this.suggestedActivityService = suggestedActivityService;
    }

    @Override
    public Plan getPlanById(Integer idPlan) {
        Optional<Plan> optionalPlan = planRepository.findById(idPlan);
        if (optionalPlan.isPresent()) {
            return optionalPlan.get();
        } else {
            throw new NotFoundException("Plan not found");
        }
    }

    @Override
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    @Override
    public List<Plan> findAllPublicPlanInTrip(String firebaseUid, Integer idTrip) {
        List<Plan> planList = planRepository.findAllPlanByIdTrip(idTrip);
        authenticationService.checkJoinedGroupByFirebaseUid(firebaseUid, tripService.getTripById(idTrip).getGroup().getId());
        for (Plan plan : planList) {
            plan.getVotedPlanList().size();

            //remove vote of inactive member
            for (int i = plan.getVotedPlanList().size() - 1; i >= 0; i--) {
                if (plan.getVotedPlanList().get(i).getMember().getIdStatus().equals(MemberStatus.INACTIVE)) {
                    plan.getVotedPlanList().remove(i);
                }
            }

            plan.setActivityList(
                    activityRepository.findAllByIdPlanAndIdActivityStatus(plan.getId())
            );
            for (Activity activity : plan.getActivityList()) {
                activity.getStartPlace().getPlaceImageList().size();
                activity.getEndPlace().getPlaceImageList().size();
            }
        }
        return planList;
    }

    @Override
    public List<PlanDTO> findAllPublicPlanDTOInTrip(String firebaseUid, Integer idTrip) {
//        List<Plan> planList = findAllPublicPlanInTrip(firebaseUid, idTrip);
//        List<PlanDTO> planDTOList = Arrays.asList(modelMapper.map(planList, PlanDTO[].class));
//
//        List<SuggestedActivity> suggestedActivityList = suggestedActivityService.findAllSuggestedActivityInTrip(idTrip);
//        Map<String, BigDecimal> suggestedActivityMap = new HashMap<>();
//
//        for (SuggestedActivity suggestedActivity : suggestedActivityList) {
//            BigDecimal score = BigDecimal.ONE;
//
//            for (VotedSuggestedActivity votedSuggestedActivity : suggestedActivity.getVotedSuggestedActivityList()
//            ) {
//                if (!votedSuggestedActivity.getMember().getPerson().getId().equals(suggestedActivity.getOwner().getPerson().getId())) {
//                    score = score.add(BigDecimal.ONE);
//                }
//            }
//            suggestedActivityMap.put(suggestedActivity.getStartPlace().getGooglePlaceId(), score);
//        }
//
//        BigDecimal totalScore = BigDecimal.ZERO;
//        for (BigDecimal value : suggestedActivityMap.values()) {
//            totalScore = totalScore.add(value);
//        }
//

//        return planDTOList;
        return null;
    }

    @Override
    public List<Plan> findAllMyPlanByIdTrip(String firebaseUid, Integer idTrip) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Trip trip = tripService.getTripById(idTrip);
        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(), trip.getGroup().getId());
        List<Plan> planList = planRepository.findAllMyPlanByIdTrip(trip.getId(), member.getId());
        //init lazy
        for (Plan plan : planList) {
            plan.getVotedPlanList().size();
            plan.setActivityList(
                    activityRepository.findAllByIdPlanAndIdActivityStatus(plan.getId())
            );
        }
        return planList;
    }

    @Override
    public Plan createPlan(String firebaseUid, Integer idTrip, List<Activity> activityList) {
        Trip trip = tripService.getTripById(idTrip);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member member = memberService.getMemberByIdPersonAndIdGroup(person.getId(), trip.getGroup().getId());

        authenticationService.checkJoinedGroup(person.getId(), trip.getGroup().getId());

        Plan plan = new Plan();
        plan.setIdStatus(PlanStatus.PUBLIC);
        plan.setOwner(member);
        plan.setTrip(trip);
        plan = savePlan(plan);
        List<Activity> persistedActivityList = new ArrayList<>();
        for (Activity activity : activityList) {
            Place startPlace = placeRepository.getOne(activity.getStartPlace().getId());
            Place endPlace = placeRepository.getOne(activity.getEndPlace().getId());
            activity.setStartPlace(startPlace);
            activity.setEndPlace(endPlace);
            activity.setPlan(plan);

            activity.setStartUtcAt(
                    ZonedDateTimeUtil.convertToUtcWithTimeZone(activity.getStartAt(), activity.getStartPlace().getTimeZone())
            );
            activity.setEndUtcAt(
                    ZonedDateTimeUtil.convertToUtcWithTimeZone(activity.getEndAt(), activity.getEndPlace().getTimeZone())
            );

            persistedActivityList.add(activityRepository.save(activity));
        }
        plan.setActivityList(persistedActivityList);
        return plan;
    }

    @Override
    public void removePlan(String firebaseUid, Integer idPlan) {
        Plan plan = getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        if (authenticationService.isPlanOwner(person.getId(), plan.getId())) {
            planRepository.deleteById(idPlan);
        } else {
            throw new ForbiddenException("only plan owner can delete");
        }
    }

    @Override
    public void publishPlan(String firebaseUid, Integer idPlan) {
        Plan plan = getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        if (authenticationService.isPlanOwner(person.getId(), plan.getId())) {
            plan.setIdStatus(PlanStatus.PUBLIC);
            planRepository.save(plan);
        } else {
            throw new ForbiddenException("only plan owner can publish");
        }
    }

    @Override
    public void concealPlan(String firebaseUid, Integer idPlan) {
        Plan plan = getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        if (authenticationService.isPlanOwner(person.getId(), plan.getId())
                || authenticationService.isGroupOwner(person.getId(), plan.getTrip().getGroup().getId())) {
            plan.setIdStatus(PlanStatus.PRIVATE);
            planRepository.save(plan);
        } else {
            throw new ForbiddenException("only group owner or plan owner can conceal");
        }
    }

    @Override
    public List<Plan> getHighestVotingPlanList(String firebaseUid, Integer idTrip) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkTripOwner(person.getId(), idTrip);

        return getHighestVotingPlanListWithoutPermission(idTrip);
    }

    @Override
    @NotNull
    public List<Plan> getHighestVotingPlanListWithoutPermission(Integer idTrip) {
        Trip trip = tripService.getTripById(idTrip);

        List<Plan> planList = planRepository.findAllPlanByIdTrip(trip.getId());
        List<Plan> highestVotePlanList = new ArrayList<>();
        Integer highestVoteCount = 0;

        for (Plan plan : planList) {
            Integer count = 0;
            for (VotedPlan votedPlan : plan.getVotedPlanList()) {
                if (!votedPlan.getMember().getIdStatus().equals(MemberStatus.INACTIVE)) {
                    count = count + 1;
                }
            }
            if (count.compareTo(highestVoteCount) > 0) {
                highestVoteCount = count;
                highestVotePlanList = new ArrayList<>();
                highestVotePlanList.add(plan);
            } else if (count.compareTo(highestVoteCount) == 0) {
                highestVotePlanList.add(plan);
            }
        }
        //init lazy
        for (Plan plan : highestVotePlanList) {
            plan.getVotedPlanList().size();
            plan.setActivityList(
                    activityRepository.findAllByIdPlanAndIdActivityStatus(plan.getId())
            );
        }
        return highestVotePlanList;
    }

    @Override
    public void pickHighestVotePlan(String firebaseUid, Integer idPlan) {
        Plan pickedPlan = getPlanById(idPlan);

        List<Plan> electedPlanList = planRepository.findAllPlanByIdTripAndIdStatus(pickedPlan.getTrip().getId(), PlanStatus.ELECTED);
        if (electedPlanList.size() == 0) {
            List<Plan> highestVotePlanList = getHighestVotingPlanList(firebaseUid, pickedPlan.getTrip().getId());
            boolean isHighestVotePlan = false;
            for (Plan highestVotePlan : highestVotePlanList) {
                if (highestVotePlan.getId().compareTo(pickedPlan.getId()) == 0) {
                    isHighestVotePlan = true;
                }
            }
            if (isHighestVotePlan) {
                pickedPlan.setIdStatus(PlanStatus.ELECTED);
                planRepository.save(pickedPlan);

                Trip trip = tripService.getTripById(pickedPlan.getTrip().getId());
                trip.setVoteEndAt(new Timestamp(Instant.now().toEpochMilli()));
                tripRepository.save(trip);
            } else {
                throw new ForbiddenException("this is not highest vote plan");
            }
        } else {
            throw new ForbiddenException("This trip is already voted, please change vote deadline and pick again");
        }

    }

    @Override
    public void pickHighestVotePlanWithoutPermission(Integer idPlan) {
        Plan pickedPlan = getPlanById(idPlan);
        List<Plan> electedPlanList = planRepository.findAllPlanByIdTripAndIdStatus(pickedPlan.getTrip().getId(), PlanStatus.ELECTED);
        if (electedPlanList.size() == 0) {
            List<Plan> highestVotePlanList = getHighestVotingPlanListWithoutPermission(pickedPlan.getTrip().getId());
            boolean isHighestVotePlan = false;
            for (Plan highestVotePlan : highestVotePlanList) {
                if (highestVotePlan.getId().compareTo(pickedPlan.getId()) == 0) {
                    isHighestVotePlan = true;
                }
            }
            if (isHighestVotePlan) {
                pickedPlan.setIdStatus(PlanStatus.ELECTED);
                planRepository.save(pickedPlan);

                Trip trip = tripService.getTripById(pickedPlan.getTrip().getId());
                trip.setVoteEndAt(new Timestamp(Instant.now().toEpochMilli()));
                tripRepository.save(trip);
            } else {
                throw new ForbiddenException("this is not highest vote plan");
            }
        } else {
            throw new ForbiddenException("This trip is already voted, please change vote deadline and pick again");
        }

    }

    @Override
    public void changeVoteDeadline(String firebaseUid, Integer idTrip, Timestamp voteEndAt) {
        Optional<Trip> optionalTrip = tripRepository.findById(idTrip);
        Trip trip = optionalTrip.get();

        trip.setVoteEndAt(voteEndAt);
        tripRepository.save(trip);

        List<Plan> electedPlanList = planRepository.findAllPlanByIdTripAndIdStatus(idTrip, PlanStatus.ELECTED);
        for (Plan plan : electedPlanList) {
            plan.setIdStatus(PlanStatus.PUBLIC);
            planRepository.save(plan);
        }
    }

    @Override
    public void setPlanBudget(String firebaseUid,
                              Integer idPlan,
                              BigDecimal activityBudget,
                              BigDecimal accommodationBudget,
                              BigDecimal transportationBudget,
                              BigDecimal foodBudget
    ) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkPlanOwner(person.getId(), idPlan);
        Plan plan = planRepository.findById(idPlan).get();
        plan.setActivityBudget(activityBudget);
        plan.setAccommodationBudget(accommodationBudget);
        plan.setTransportationBudget(transportationBudget);
        plan.setFoodBudget(foodBudget);
        planRepository.save(plan);
    }

    @Override
    public void resolvePlanVotingConflict(String firebaseUid, Integer idTrip, Integer idPlan) {
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        authenticationService.checkTripOwner(person.getId(), idTrip);
        Trip trip = tripService.getTripById(idTrip);

        List<Plan> planConflictList = planRepository
                .findAllPlanByIdTripAndIdStatus(trip.getId(), PlanStatus.PENDING_RESOLVE);

        for (Plan plan : planConflictList) {
            if (plan.getId().compareTo(idPlan) == 0) {
                plan.setIdStatus(PlanStatus.ELECTED);
            } else {
                plan.setIdStatus(PlanStatus.PUBLIC);
            }
            planRepository.save(plan);
        }
    }


    @Override
    public void votePlan(String firebaseUid, Integer idPlan) {
        Plan plan = getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member member = memberService.getByIdPersonAndIdGroup(person.getId(), plan.getTrip().getGroup().getId());

        Optional<VotedPlan> optionalVotedPlan = votedPlanRepository.findByIdMemberAndIdPlan(member.getId(), plan.getId());
        if (optionalVotedPlan.isPresent()) {
            throw new ForbiddenException("Already vote");
        } else {
            VotedPlan votedPlan = new VotedPlan();
            votedPlan.setMember(member);
            votedPlan.setPlan(plan);

            votedPlanRepository.save(votedPlan);
        }

    }

    @Override
    public void removeVotePlan(String firebaseUid, Integer idPlan) {
        Plan plan = getPlanById(idPlan);
        Person person = personService.getPersonByFirebaseUid(firebaseUid);
        Member member = memberService.getByIdPersonAndIdGroup(person.getId(), plan.getTrip().getGroup().getId());

        Optional<VotedPlan> optionalVotedPlan = votedPlanRepository.findByIdMemberAndIdPlan(member.getId(), plan.getId());
        if (optionalVotedPlan.isPresent()) {
            votedPlanRepository.deleteById(optionalVotedPlan.get().getId());
        } else {
            throw new NotFoundException("Cant find vote to Delete");
        }
    }


}
