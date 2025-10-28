package com.example.trip_planner.place.service;

import com.example.trip_planner.place.dto.PlaceDetailResponse;
import com.example.trip_planner.place.dto.PlaceFeedResponse;
import com.example.trip_planner.place.dto.PlaceRecommendationRequest;
import com.example.trip_planner.place.dto.PlaceRecommendationResponse;
import com.example.trip_planner.place.model.Place;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Service interface for Place operations
 */
public interface PlaceService {
    
    /**
     * Get paginated places with optional filters
     * @param provinceId Optional province filter
     * @param category Optional category filter
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Paginated list of places
     */
    Page<PlaceFeedResponse> getPlaces(Integer provinceId, String category, int page, int size);
    
    /**
     * Get place detail by ID
     * @param id Place ID
     * @return Place detail
     */
    PlaceDetailResponse getPlaceById(String id);
    
    /**
     * Create a new place (Admin)
     * @param place Place to create
     * @return Created place detail
     */
    PlaceDetailResponse createPlace(Place place);
    
    /**
     * Update an existing place (Admin)
     * @param place Place with updated data
     * @return Updated place detail
     */
    PlaceDetailResponse updatePlace(Place place);
    
    /**
     * Delete a place (Admin)
     * @param id Place ID to delete
     */
    void deletePlace(String id);
    
    /**
     * Import places from list (upsert by place ID) (Admin)
     * @param places List of places to import
     * @return map with counts: total, inserted, updated
     */
    Map<String, Integer> importPlaces(List<Place> places);
    
    /**
     * Get place recommendations based on selected heritage places
     * @param request Recommendation request with placeIds, categories, and radius
     * @return List of recommended places with distance information
     */
    PlaceRecommendationResponse getPlaceRecommendations(PlaceRecommendationRequest request);
}
