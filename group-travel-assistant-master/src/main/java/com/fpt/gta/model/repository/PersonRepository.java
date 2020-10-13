package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByFirebaseUid(String FirebaseUid);

}
