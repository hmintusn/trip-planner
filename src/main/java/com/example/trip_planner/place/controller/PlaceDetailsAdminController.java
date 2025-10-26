package com.example.trip_planner.place.controller;

import com.example.trip_planner.common.util.JsonUtils;
import com.example.trip_planner.place.dto.ImportResponse;
import com.example.trip_planner.place.dto.PlaceDetailsResponse;
import com.example.trip_planner.place.model.PlaceDetails;
import com.example.trip_planner.place.service.PlaceDetailsService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST controller for place details admin operations
 */
@RestController
@RequestMapping("/api/v1/admin/places")
@RequiredArgsConstructor
@Slf4j
public class PlaceDetailsAdminController {

    private final PlaceDetailsService placeDetailsService;

    /**
     * Create place details
     * @param placeDetails Place details to create
     * @return Created place details
     */
    @PostMapping("/{id}/details")
    public ResponseEntity<PlaceDetailsResponse> createPlaceDetails(@PathVariable String id, @RequestBody PlaceDetails placeDetails) {
        log.info("POST /api/v1/admin/places/{}/details", id);

        // Ensure ID matches path variable
        placeDetails.setId(id);

        PlaceDetailsResponse result = placeDetailsService.createPlaceDetails(placeDetails);
        return ResponseEntity.ok(result);
    }

    /**
     * Update place details
     * @param id Place ID
     * @param placeDetails Updated place details
     * @return Updated place details
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<PlaceDetailsResponse> updatePlaceDetails(@PathVariable String id, @RequestBody PlaceDetails placeDetails) {
        log.info("PUT /api/v1/admin/places/{}/details", id);

        placeDetails.setId(id);

        PlaceDetailsResponse result = placeDetailsService.updatePlaceDetails(placeDetails);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete place details
     * @param id Place ID
     * @return Success message
     */
    @DeleteMapping("/{id}/details")
    public ResponseEntity<Map<String, String>> deletePlaceDetails(@PathVariable String id) {
        log.info("DELETE /api/v1/admin/places/{}/details", id);

        placeDetailsService.deletePlaceDetails(id);
        return ResponseEntity.ok(Map.of("message", "Place details deleted successfully", "id", id));
    }

    /**
     * Import place details from JSON file
     * @param file JSON file containing place details
     * @return Import statistics
     */
    @PostMapping("/details/import")
    public ResponseEntity<ImportResponse> importPlaceDetails(@RequestParam("file") MultipartFile file) {
        log.info("POST /api/v1/admin/places/details/import");

        try {
            // Parse JSON file into list of PlaceDetails objects
            List<PlaceDetails> placeDetailsList = JsonUtils.fromJson(
                    file.getInputStream(),
                    new TypeReference<List<PlaceDetails>>() {}
            );

            log.info("Parsed {} place details from file", placeDetailsList.size());

            Map<String, Integer> stats = placeDetailsService.importPlaceDetails(placeDetailsList);

            ImportResponse response = ImportResponse.builder()
                    .totalPlaces(stats.get("total"))
                    .insertedPlaces(stats.get("inserted"))
                    .updatedPlaces(stats.get("updated"))
                    .message(String.format("Successfully imported %d place details (%d new, %d updated)",
                            stats.get("total"), stats.get("inserted"), stats.get("updated")))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error importing place details", e);
            ImportResponse errorResponse = ImportResponse.builder()
                    .message("Error importing place details: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}