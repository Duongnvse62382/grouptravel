package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.VotedSuggestedActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VotedSuggestedActivityRepository extends JpaRepository<VotedSuggestedActivity, Integer> {

    @Query("select v from VotedSuggestedActivity v where v.member.id=?1 and v.suggestedActivity.id=?2")
    VotedSuggestedActivity findByIdMemberAndIdSuggestedActivity(Integer idMember, Integer idSuggestedActivity);
}
