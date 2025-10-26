package com.example.trip_planner.place.controller;

import com.example.trip_planner.place.dto.ProvinceResponse;
import com.example.trip_planner.place.model.Province;
import com.example.trip_planner.place.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for province management
 * Handles CRUD operations for provinces (admin only)
 */
@RestController
@RequestMapping("/api/v1/admin/provinces")
@RequiredArgsConstructor
@Slf4j
public class ProvinceAdminController {

    private final ProvinceService provinceService;

    /**
     * Create a new province (Admin only)
     *
     * @param province Province data to create
     * @return Created province details
     */
    @PostMapping
    public ResponseEntity<ProvinceResponse> createProvince(@RequestBody Province province) {
        log.info("POST /api/v1/admin/provinces");

        ProvinceResponse created = provinceService.createProvince(province);

        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing province (Admin only)
     *
     * @param id Province ID to update
     * @param province Updated province data
     * @return Updated province details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProvinceResponse> updateProvince(
            @PathVariable Integer id,
            @RequestBody Province province) {
        log.info("PUT /api/v1/admin/provinces/{}", id);

        province.setId(id); // Ensure ID is set from path
        ProvinceResponse updated = provinceService.updateProvince(province);

        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a province (Admin only)
     *
     * @param id Province ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvince(@PathVariable Integer id) {
        log.info("DELETE /api/v1/admin/provinces/{}", id);

        provinceService.deleteProvince(id);

        return ResponseEntity.noContent().build();
    }
}