package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.VotedPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotedPlanRepository extends JpaRepository<VotedPlan, Integer> {

    @Query("Select vp from VotedPlan vp where vp.member.id=?1 and vp.plan.id=?2")
    Optional<VotedPlan> findByIdMemberAndIdPlan(Integer idMember, Integer idPlan);

}
