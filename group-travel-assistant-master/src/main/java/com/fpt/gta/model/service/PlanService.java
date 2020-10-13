package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Activity;
import com.fpt.gta.model.entity.Plan;
import com.fpt.gta.rest.manageplan.PlanDTO;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public interface PlanService {

    Plan getPlanById(Integer idPlan);

    Plan savePlan(Plan plan);

    List<Plan> findAllPublicPlanInTrip(String firebaseUid, Integer idTrip);

    List<PlanDTO> findAllPublicPlanDTOInTrip(String firebaseUid, Integer idTrip);

    List<Plan> findAllMyPlanByIdTrip(String firebaseUid, Integer idTrip);

    Plan createPlan(String firebaseUid, Integer idTrip, List<Activity> activityList);

    void removePlan(String firebaseUid, Integer idPlan);

    void setPlanBudget(String firebaseUid,
                       Integer idPlan,
                       BigDecimal activityBudget,
                       BigDecimal accommodationBudget,
                       BigDecimal transportationBudget,
                       BigDecimal foodBudget
    );

    void resolvePlanVotingConflict(String firebaseUid, Integer idTrip, Integer idPlan);

    void votePlan(String firebaseUid, Integer idPlan);

    void removeVotePlan(String firebaseUid, Integer idPlan);

    void publishPlan(String firebaseUid, Integer idPlan);

    void concealPlan(String firebaseUid, Integer idPlan);

    List<Plan> getHighestVotingPlanList(String firebaseUid, Integer idTrip);

    @NotNull
    List<Plan> getHighestVotingPlanListWithoutPermission(Integer idTrip);

    void pickHighestVotePlan(String firebaseUid, Integer idPlan);

    void pickHighestVotePlanWithoutPermission(Integer idPlan);

    void changeVoteDeadline(String firebaseUid, Integer idTrip, Timestamp voteEndAt);
}
