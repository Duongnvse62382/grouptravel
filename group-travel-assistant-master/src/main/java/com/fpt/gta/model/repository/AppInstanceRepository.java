package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.AppInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppInstanceRepository extends JpaRepository<AppInstance, Integer> {

    Optional<AppInstance> findByIdInstance(String idInstance);
}
