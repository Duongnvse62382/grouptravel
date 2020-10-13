package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.SuggestedActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SuggestedActivityRepository extends JpaRepository<SuggestedActivity, Integer> {


    @Query("select a from SuggestedActivity a where a.trip.id=?1")
    List<SuggestedActivity> findAllByIdTrip(Integer idTrip);

    @Query("select a from SuggestedActivity a where a.trip.id=?1 and a.idType=?2")
    List<SuggestedActivity> findActivityIdTrip(Integer idTrip, Integer idType);
}
