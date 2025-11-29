package com.example.trip_planner.exploration.service;

import com.example.trip_planner.exploration.dto.ExplorationCreateRequest;
import com.example.trip_planner.exploration.dto.ExplorationDetailsResponse;
import com.example.trip_planner.exploration.dto.ExplorationFeedResponse;
import com.example.trip_planner.exploration.dto.ExplorationUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Exploration operations
 */
public interface ExplorationService {
    
    /**
     * Get paginated explorations with optional filters
     * @param provinceId Optional province filter
     * @param category Optional category filter
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated list of explorations
     */
    Page<ExplorationFeedResponse> getExplorations(Integer provinceId, String category, int page, int size);
    
    /**
     * Get exploration detail by ID
     * @param id Exploration ID
     * @return Exploration detail
     */
    ExplorationDetailsResponse getExplorationById(String id);
    
    /**
     * Create a new exploration (Admin)
     * @param request Exploration creation request
     * @return Created exploration detail
     */
    ExplorationDetailsResponse createExploration(ExplorationCreateRequest request);
    
    /**
     * Update an existing exploration (Admin)
     * @param id Exploration ID
     * @param request Exploration update request
     * @return Updated exploration detail
     */
    ExplorationDetailsResponse updateExploration(String id, ExplorationUpdateRequest request);
    
    /**
     * Delete an exploration (Admin)
     * @param id Exploration ID
     */
    void deleteExploration(String id);
    
    /**
     * Bulk import explorations (Admin)
     * Creates new explorations with auto-generated UUIDs
     * @param requests List of exploration creation requests
     * @return Summary map with counts (imported, failed)
     */
    Map<String, Integer> importExplorations(List<ExplorationCreateRequest> requests);
}
