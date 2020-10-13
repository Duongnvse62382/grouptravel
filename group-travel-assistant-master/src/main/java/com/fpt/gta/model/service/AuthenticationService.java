package com.fpt.gta.model.service;

public interface AuthenticationService {
    void checkGroupOwner(Integer idPerson, Integer idGroup);

    void checkTripOwner(Integer idPerson, Integer idTrip);

    boolean isGroupOwner(Integer idPerson, Integer idGroup);

    boolean isTripOwner(Integer idPerson, Integer idTrip);

    void checkSuggestedActivityOwner(Integer idPerson, Integer idSuggestedActivity);

    boolean isSuggestedActivityOwner(Integer idPerson, Integer idSuggestedActivity);

    void checkJoinedGroup(Integer idPerson, Integer idGroup);

    void checkJoinedGroupByEmail(String email, Integer idGroup);

    void checkJoinedGroupByFirebaseUid(String firebaseUid, Integer idGroup);

    boolean isJoinedGroup(Integer idPerson, Integer idGroup);

    void checkPlanOwner(Integer idPerson, Integer idPlan);

    boolean isPlanOwner(Integer idPerson, Integer idPlan);

    void checkTransactionOwner(Integer idPerson, Integer idTransaction);

    boolean isTransactionOwner(Integer idPerson, Integer idTransaction);

    boolean isTransactionParticipationOrPayer(Integer idPerson, Integer idTransaction);
}
