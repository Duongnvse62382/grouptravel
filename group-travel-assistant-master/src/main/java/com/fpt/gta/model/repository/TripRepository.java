package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Place;
import com.fpt.gta.model.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Integer> {

    @Query("Select t from Trip t where t.group.id=?1")
    List<Trip> findTripsByIdGroup(Integer idGroup);

    @Query("select t from Trip t where t.voteEndAt >= ?1 and t.voteEndAt < ?2")
    List<Trip> findAllTripByVoteEndAtBetween(Date from, Date to);
}
