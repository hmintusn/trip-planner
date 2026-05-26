package com.example.trip_planner.place.controller;

import com.example.trip_planner.common.util.JsonUtils;
import com.example.trip_planner.place.dto.ImportResponse;
import com.example.trip_planner.place.dto.PlaceDetailResponse;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.service.PlaceService;
import com.fasterxml.jackson.core.type.TypeReference;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Admin controller for place management
 * Handles CRUD operations for places (admin only)
 */
@RestController
@RequestMapping("/api/v1/admin/places")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class PlaceAdminController {
    
    private final PlaceService placeService;
    
    /**
     * Create a new place (Admin only)
     * 
     * @param place Place data to create
     * @return Created place details
     */
    @PostMapping  
    public ResponseEntity<PlaceDetailResponse> createPlace(@RequestBody Place place) {
        log.info("POST /api/v1/admin/places - name: {}", place.getName());
        
        PlaceDetailResponse result = placeService.createPlace(place);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Update an existing place (Admin only)
     * 
     * @param id Place ID to update
     * @param place Updated place data
     * @return Updated place details
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlaceDetailResponse> updatePlace(
            @PathVariable String id,
            @RequestBody Place place) {
        log.info("PUT /api/v1/admin/places/{}", id);
        
        place.setId(id);
        PlaceDetailResponse result = placeService.updatePlace(place);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Delete a place (Admin only)
     * 
     * @param id Place ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlace(@PathVariable String id) {
        log.info("DELETE /api/v1/admin/places/{}", id);
        
        placeService.deletePlace(id);
        
        return ResponseEntity.ok(Map.of("message", "Place deleted successfully", "id", id));
    }
    
    /**
     * Import places from JSON file (Admin only)
     * Accepts a JSON array of place objects
     * Upserts places by their ID (insert new, update existing)
     * 
     * @param file JSON file containing array of places
     * @return Import statistics (total, inserted, updated)
     */
    @PostMapping("/import")
    public ResponseEntity<ImportResponse> importPlaces(@RequestParam("file") MultipartFile file) {
        
        log.info("POST /api/v1/admin/places/import - filename: {}, size: {} bytes", 
                file.getOriginalFilename(), file.getSize());
        
        try {
            // Parse JSON file into list of Place objects
            List<Place> places = JsonUtils.fromJson(
                    file.getInputStream(), 
                    new TypeReference<List<Place>>() {}
            );
            
            log.info("Parsed {} places from file", places.size());
            
            // Import places
            Map<String, Integer> stats = placeService.importPlaces(places);
            
            // Build response
            ImportResponse response = ImportResponse.success(
                    stats.get("total"),
                    stats.get("inserted"),
                    stats.get("updated")
            );
            
            log.info("Import completed successfully: {}", response.getMessage());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to import places", e);
            return ResponseEntity.badRequest()
                    .body(new ImportResponse(0, 0, 0, "Import failed: " + e.getMessage()));
        }
    }
}
