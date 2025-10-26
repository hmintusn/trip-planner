package com.example.trip_planner.place.controller;

import com.example.trip_planner.place.dto.PlaceDetailResponse;
import com.example.trip_planner.place.dto.PlaceDetailsResponse;
import com.example.trip_planner.place.dto.PlaceFeedResponse;
import com.example.trip_planner.place.service.PlaceDetailsService;
import com.example.trip_planner.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for place operations (User endpoints)
 */
@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Slf4j
public class PlaceController {
    
    private final PlaceService placeService;
    private final PlaceDetailsService placeDetailsService;
    
    /**
     * Get a list of places with filtering and pagination
     * Supports filtering by provinceId, category, etc.
     * 
     * @param provinceId Province identifier (e.g., "Ninh Binh") - optional
     * @param category Category filter (attraction, hotel, restaurant, entertainment) - optional
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated list of places
     */
    @GetMapping
    public ResponseEntity<Page<PlaceFeedResponse>> getPlaces(
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("GET /api/v1/places - provinceId: {}, category: {}, page: {}, size: {}", 
                provinceId, category, page, size);
        
        Page<PlaceFeedResponse> result = placeService.getPlaces(provinceId, category, page, size);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get detailed information about a specific place
     * 
     * @param id Place ID (Google Place ID)
     * @return Detailed place information
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlaceDetailResponse> getPlaceById(@PathVariable String id) {
        log.info("GET /api/v1/places/{}", id);
        
        PlaceDetailResponse result = placeService.getPlaceById(id);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get extended details of a place, including content blocks, articles, or heritage info
     * 
     * @param id Place ID (Google Place ID)
     * @return Extended place details
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<PlaceDetailsResponse> getPlaceDetails(@PathVariable String id) {
        log.info("GET /api/v1/places/{}/detail", id);
        
        PlaceDetailsResponse result = placeDetailsService.getPlaceDetailsById(id);
        
        return ResponseEntity.ok(result);
    }
}
