package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceTypeRepository extends JpaRepository<PlaceType, Integer> {
    PlaceType findByName(String name);
}
