package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    @Query("Select distinct p from Plan p " +
            "where p.trip.id=?1 and p.idStatus=?2 ")
    List<Plan> findAllPlanByIdTripAndIdStatus(Integer idTrip, Integer idStatus);

    @Query("Select distinct p from Plan p " +
            "where p.trip.id=?1")
    List<Plan> findAllPlanByIdTrip(Integer idTrip);

    @Query("Select distinct p from Plan p " +
            "where p.trip.id=?1 and p.owner.id=?2 ")
    List<Plan> findAllMyPlanByIdTrip(Integer idTrip, Integer idMember);

}
