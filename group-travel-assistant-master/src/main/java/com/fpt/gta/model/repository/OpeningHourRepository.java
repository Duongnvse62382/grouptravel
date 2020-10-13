package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.OpeningHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpeningHourRepository extends JpaRepository<OpeningHour, Integer> {
}
