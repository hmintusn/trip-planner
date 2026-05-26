package com.example.trip_planner.place.service;

import com.example.trip_planner.place.dto.ProvinceResponse;
import com.example.trip_planner.place.model.Province;

import java.util.List;

/**
 * Service interface for Province operations
 */
public interface ProvinceService {

    /**
     * Get all provinces
     * @return List of all provinces
     */
    List<ProvinceResponse> getAllProvinces();

    /**
     * Get province by ID
     * @param id Province ID
     * @return Province details
     */
    ProvinceResponse getProvinceById(Integer id);

    /**
     * Get provinces by region
     * @param region Region name
     * @return List of provinces in the region
     */
    List<ProvinceResponse> getProvincesByRegion(String region);

    /**
     * Create a new province (Admin)
     * @param province Province to create
     * @return Created province
     */
    ProvinceResponse createProvince(Province province);

    /**
     * Update an existing province (Admin)
     * @param province Province with updated data
     * @return Updated province
     */
    ProvinceResponse updateProvince(Province province);

    /**
     * Delete a province (Admin)
     * @param id Province ID to delete
     */
    void deleteProvince(Integer id);
}