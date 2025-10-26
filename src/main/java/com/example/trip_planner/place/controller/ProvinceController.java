package com.example.trip_planner.place.controller;

import com.example.trip_planner.place.dto.ProvinceResponse;
import com.example.trip_planner.place.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for province operations (User endpoints)
 */
@RestController
@RequestMapping("/api/v1/provinces")
@RequiredArgsConstructor
@Slf4j
public class ProvinceController {

    private final ProvinceService provinceService;

    /**
     * Get a list of all provinces
     *
     * @return List of all provinces
     */
    @GetMapping
    public ResponseEntity<List<ProvinceResponse>> getAllProvinces() {
        log.info("GET /api/v1/provinces");

        List<ProvinceResponse> provinces = provinceService.getAllProvinces();

        return ResponseEntity.ok(provinces);
    }

    /**
     * Get details of a specific province
     *
     * @param id Province ID
     * @return Province details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProvinceResponse> getProvinceById(@PathVariable Integer id) {
        log.info("GET /api/v1/provinces/{}", id);

        ProvinceResponse province = provinceService.getProvinceById(id);

        return ResponseEntity.ok(province);
    }

    /**
     * Get provinces by region
     *
     * @param region Region name (optional filter)
     * @return List of provinces in the region
     */
    @GetMapping("/region/{region}")
    public ResponseEntity<List<ProvinceResponse>> getProvincesByRegion(@PathVariable String region) {
        log.info("GET /api/v1/provinces/region/{}", region);

        List<ProvinceResponse> provinces = provinceService.getProvincesByRegion(region);

        return ResponseEntity.ok(provinces);
    }
}