package com.example.trip_planner.place.service;

import com.example.trip_planner.place.dto.PlaceDetailsResponse;
import com.example.trip_planner.place.model.PlaceDetails;

import java.util.List;
import java.util.Map;

/**
 * Service interface for PlaceDetails operations
 */
public interface PlaceDetailsService {

    /**
     * Get place details by ID
     * @param id Place ID
     * @return Place details
     */
    PlaceDetailsResponse getPlaceDetailsById(String id);

    /**
     * Create place details
     * @param placeDetails Place details to create
     * @return Created place details
     */
    PlaceDetailsResponse createPlaceDetails(PlaceDetails placeDetails);

    /**
     * Update place details
     * @param placeDetails Place details with updated data
     * @return Updated place details
     */
    PlaceDetailsResponse updatePlaceDetails(PlaceDetails placeDetails);

    /**
     * Delete place details
     * @param id Place ID to delete details for
     */
    void deletePlaceDetails(String id);

    /**
     * Import place details from list (upsert by place ID)
     * @param placeDetailsList List of place details to import
     * @return map with counts: total, inserted, updated
     */
    Map<String, Integer> importPlaceDetails(List<PlaceDetails> placeDetailsList);

    /**
     * Get place details by province
     * @param provinceId Province ID
     * @return List of place details
     */
    List<PlaceDetailsResponse> getPlaceDetailsByProvince(Integer provinceId);
}