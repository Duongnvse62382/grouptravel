package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.PlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceImageRepository extends JpaRepository<PlaceImage, Integer> {

}
