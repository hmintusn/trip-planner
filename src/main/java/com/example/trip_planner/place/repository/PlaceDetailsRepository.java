package com.example.trip_planner.place.repository;

import com.example.trip_planner.place.model.PlaceDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PlaceDetails entity
 */
@Repository
public interface PlaceDetailsRepository extends MongoRepository<PlaceDetails, String> {

    /**
     * Find place details by province
     */
    List<PlaceDetails> findByProvinceId(Integer provinceId);

    /**
     * Check if place details exist for a place ID
     */
    boolean existsById(String id);
}