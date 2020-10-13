package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.Place;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {

    Optional<Place> findByGooglePlaceId(String googlePlaceId);

    @Query("SELECT p FROM Place p WHERE p.name LIKE %?1%")
    List<Place> searchByNameStartsWith(String searchValue, Pageable pageable);
}
