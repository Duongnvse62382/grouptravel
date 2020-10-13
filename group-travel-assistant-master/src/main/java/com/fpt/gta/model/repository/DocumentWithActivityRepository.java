package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.DocumentWithActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentWithActivityRepository extends JpaRepository<DocumentWithActivity, Integer> {

}
