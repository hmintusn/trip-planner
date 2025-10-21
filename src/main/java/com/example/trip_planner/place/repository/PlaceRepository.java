package com.example.trip_planner.place.repository;

import com.example.trip_planner.place.model.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Place entity
 * Spring Data MongoDB will auto-implement basic CRUD and custom queries
 */
@Repository
public interface PlaceRepository extends MongoRepository<Place, String> {
    
    /**
     * Find places by province and category with pagination
     */
    Page<Place> findByProvinceIdAndCategory(Integer provinceId, String category, Pageable pageable);
    
    /**
     * Find places by province
     */
    @Query("{'provinceId': ?0}")
    Page<Place> findByProvinceId(Integer provinceId, Pageable pageable);
    
    /**
     * Find places by category with pagination
     */
    Page<Place> findByCategory(String category, Pageable pageable);
    
    /**
     * Find top N places by category
     */
    List<Place> findTopByCategoryOrderByScoreDesc(String category, Pageable pageable);
    
    /**
     * Find all places by category
     */
    List<Place> findByCategoryOrderByScoreDesc(String category);
}
