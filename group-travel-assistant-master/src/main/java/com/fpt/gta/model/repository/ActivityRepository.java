package com.fpt.gta.model.repository;

import com.fpt.gta.model.constant.GroupStatus;
import com.fpt.gta.model.constant.PlanStatus;
import com.fpt.gta.model.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    @Query("select a from Activity a where a.plan.id = ?1")
    List<Activity> findAllByIdPlanAndIdActivityStatus(Integer idPlan);

    //    @Query("select a from Activity a where  a.plan.idStatus=" + PlanStatus.ELECTED + " and a.startUtcAt>=?1 and a.startUtcAt <?2 and a.startUtcAt>=a.plan.trip.startUtcAt and a.startUtcAt<=a.plan.trip.endUtcAt")
    @Query("select a from Activity a where  a.plan.idStatus=" + PlanStatus.ELECTED + " and a.plan.trip.group.idStatus=" + GroupStatus.EXECUTING + " and a.startUtcAt>=?1 and a.startUtcAt <?2 ")
    List<Activity> findAllActivityStartUtcBetweenUtcDate(Date from, Date to);

    @Query("select a from Activity a where  a.plan.idStatus=" + PlanStatus.ELECTED + " and a.plan.trip.group.idStatus=" + GroupStatus.EXECUTING + " and a.endUtcAt>=?1 and a.endUtcAt <?2 ")
    List<Activity> findAllActivityEndUtcBetweenUtcDate(Date from, Date to);
}
