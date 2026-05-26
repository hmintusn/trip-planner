package com.example.trip_planner.exploration.controller;

import com.example.trip_planner.exploration.dto.ExplorationDetailsResponse;
import com.example.trip_planner.exploration.dto.ExplorationFeedResponse;
import com.example.trip_planner.exploration.service.ExplorationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for exploration operations (User endpoints)
 */
@RestController
@RequestMapping("/api/v1/explorations")
@RequiredArgsConstructor
@Slf4j
public class ExplorationController {
    
    private final ExplorationService explorationService;
    
    /**
     * Get a paginated list of explorations with optional filtering
     * Supports filtering by provinceId and category
     * 
     * @param provinceId Province identifier - optional
     * @param category Category filter - optional
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated list of explorations
     */
    @GetMapping
    public ResponseEntity<Page<ExplorationFeedResponse>> getExplorations(
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/v1/explorations - provinceId: {}, category: {}, page: {}, size: {}", 
                provinceId, category, page, size);
        
        Page<ExplorationFeedResponse> result = explorationService.getExplorations(provinceId, category, page, size);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get detailed information about a specific exploration
     * 
     * @param id Exploration ID
     * @return Complete exploration details including content blocks
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExplorationDetailsResponse> getExploration(@PathVariable String id) {
        log.info("GET /api/v1/explorations/{}", id);
        
        ExplorationDetailsResponse result = explorationService.getExplorationById(id);
        
        return ResponseEntity.ok(result);
    }
}
