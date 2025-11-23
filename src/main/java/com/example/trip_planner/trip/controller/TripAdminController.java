package com.example.trip_planner.trip.controller;

import com.example.trip_planner.trip.dto.TripDetailDTO;
import com.example.trip_planner.trip.dto.TripSummaryDTO;
import com.example.trip_planner.trip.service.TripService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
// @Tag(name = "Admin - Trip Management", description = "Admin APIs for managing all trips")
public class TripAdminController {

    private final TripService tripService;

    @GetMapping("/trips")
    public ResponseEntity<Page<TripSummaryDTO>> getAllTrips(@RequestParam(required = false) String ownerLocalId,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(required = false) String q,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/admin/trips - filters: owner={}, status={}, q={}", ownerLocalId, status, q);

        Pageable pageable = PageRequest.of(page, size);
        Page<TripSummaryDTO> trips = tripService.getAllTrips(ownerLocalId, status, q, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDetailDTO> getTripDetail(@PathVariable UUID tripId) {
        log.info("GET /api/v1/admin/trips/{}", tripId);

        TripDetailDTO trip = tripService.getTripDetailAdmin(tripId);
        return ResponseEntity.ok(trip);
    }

    @PatchMapping("/trips/{tripId}/status")
    public ResponseEntity<Void> updateTripStatus(@PathVariable UUID tripId,
                                                 @RequestParam String status) {
        log.info("PATCH /api/v1/admin/trips/{}/status - status={}", tripId, status);

        tripService.updateTripStatus(tripId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<Void> hardDeleteTrip(@PathVariable UUID tripId) {
        log.info("DELETE /api/v1/admin/trips/{}", tripId);

        // Assume hard delete is implemented in service
        // For now, use update to DELETED, but admin can hard delete
        tripService.updateTripStatus(tripId, "DELETED");
        return ResponseEntity.noContent().build();
    }
}