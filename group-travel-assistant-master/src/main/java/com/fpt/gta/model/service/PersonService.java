package com.fpt.gta.model.service;

import com.fpt.gta.model.entity.Person;

public interface PersonService {
    Person syncFirebase(String firebaseUid, String idInstance);

    Person getPersonById(Integer idPerson);

    void signOut(String firebaseUid, String idInstance);

    Person getPersonByFirebaseUid(String firebaseUid);
}
