package com.example.trip_planner.exploration.controller;

import com.example.trip_planner.exploration.dto.ExplorationCreateRequest;
import com.example.trip_planner.exploration.dto.ExplorationDetailsResponse;
import com.example.trip_planner.exploration.dto.ExplorationUpdateRequest;
import com.example.trip_planner.exploration.service.ExplorationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for exploration admin operations
 * These endpoints should be protected with admin role authorization
 */
@RestController
@RequestMapping("/api/v1/admin/explorations")
@RequiredArgsConstructor
@Slf4j
public class ExplorationAdminController {
    
    private final ExplorationService explorationService;
    
    /**
     * Create a new exploration
     * 
     * @param request Exploration creation request
     * @return Created exploration details
     */
    @PostMapping
    public ResponseEntity<ExplorationDetailsResponse> createExploration(
            @RequestBody @Valid ExplorationCreateRequest request) {
        
        log.info("POST /api/v1/admin/explorations - title: {}", request.getTitle());
        
        ExplorationDetailsResponse result = explorationService.createExploration(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * Update an existing exploration
     * 
     * @param id Exploration ID
     * @param request Exploration update request
     * @return Updated exploration details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExplorationDetailsResponse> updateExploration(
            @PathVariable String id,
            @RequestBody @Valid ExplorationUpdateRequest request) {
        
        log.info("PUT /api/v1/admin/explorations/{} - title: {}", id, request.getTitle());
        
        ExplorationDetailsResponse result = explorationService.updateExploration(id, request);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Delete an exploration
     * 
     * @param id Exploration ID
     * @return No content (204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExploration(@PathVariable String id) {
        log.info("DELETE /api/v1/admin/explorations/{}", id);
        
        explorationService.deleteExploration(id);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Bulk import explorations
     * Creates new explorations with auto-generated UUIDs
     * 
     * @param requests List of exploration creation requests
     * @return Import summary with counts (imported, failed)
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importExplorations(
            @RequestBody @Valid List<ExplorationCreateRequest> requests) {
        
        log.info("POST /api/v1/admin/explorations/import - count: {}", requests.size());
        
        Map<String, Integer> result = explorationService.importExplorations(requests);
        
        return ResponseEntity.ok(result);
    }
}
