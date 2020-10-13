package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.SuggestedActivity;

import java.util.List;

public interface SuggestedActivityService {
    SuggestedActivity getSuggestedActivityById(Integer idSuggestedActivity);

    List<SuggestedActivity> findAllSuggestedActivityInTrip(Integer idTrip);
    List<SuggestedActivity> findAllSuggestedActivityInTripByType(Integer idTrip, Integer idType);

    void createSuggestedActivity(String firebaseUid, Integer idTrip, SuggestedActivity suggestedActivity);

    void updateSuggestedActivity(String firebaseUid, SuggestedActivity suggestedActivity);

    void removeSuggestedActivity(String firebaseUid, Integer idSuggestedActivity);

    void voteSuggestedActivity(String firebaseUid, Integer idSuggestedActivity);

    void removeVoteSuggestedActivity(String firebaseUid, Integer idSuggestedActivity);


}
