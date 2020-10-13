package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Place;
import com.fpt.gta.model.entity.Trip;

import java.util.List;

public interface TripService {

    Trip getTripById(Integer idTrip);

    Trip getTripById2(Integer idTrip);

    List<Trip> findAllTripInGroup(Integer idGroup);

    void createTrip(String firebaseUid, Integer idGroup, Trip trip);

    void updateTrip(String firebaseUid, Trip trip);

    void deleteTrip(String firebaseUid, Integer idTrip);
}
