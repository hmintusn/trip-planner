package com.example.trip_planner.exploration.repository;

import com.example.trip_planner.exploration.model.Exploration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Exploration entity
 * Spring Data MongoDB will auto-implement basic CRUD and custom queries
 */
@Repository
public interface ExplorationRepository extends MongoRepository<Exploration, String> {
    
    /**
     * Find explorations by province and category with pagination
     */
    Page<Exploration> findByProvinceIdAndCategory(Integer provinceId, String category, Pageable pageable);
    
    /**
     * Find explorations by province with pagination
     */
    Page<Exploration> findByProvinceId(Integer provinceId, Pageable pageable);
    
    /**
     * Find explorations by category with pagination
     */
    Page<Exploration> findByCategory(String category, Pageable pageable);
    
    /**
     * Check if exploration exists by ID
     */
    boolean existsById(String id);
}
