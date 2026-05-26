package com.example.trip_planner.place.repository;

import com.example.trip_planner.place.model.Province;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Province operations
 */
@Repository
public interface ProvinceRepository extends MongoRepository<Province, Integer> {

    /**
     * Find provinces by region
     * @param region Region name
     * @return List of provinces in the region
     */
    List<Province> findByRegion(String region);

    /**
     * Check if province exists by name
     * @param name Province name
     * @return true if exists
     */
    boolean existsByName(String name);
}