package com.example.trip_planner.trip.controller;

import com.example.trip_planner.clustering.dto.GenerateTripRequest;
import com.example.trip_planner.clustering.dto.GeneratedTripDTO;
import com.example.trip_planner.trip.dto.*;
import com.example.trip_planner.trip.service.TripGenerationService;
import com.example.trip_planner.trip.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
// @Tag(name = "Trip Management", description = "APIs for managing trips, days, activities, and members")
public class TripController {

    private final TripService tripService;
    private final TripGenerationService tripGenerationService;

    @PostMapping("/trips/generate-optimized")
    @Operation(
        summary = "Generate optimized trip (READ-ONLY)", 
        description = "Generates a complete trip itinerary by clustering heritage sites and finding optimal restaurants. " +
                      "**IMPORTANT: This endpoint does NOT save anything to the database.** " +
                      "It only returns a trip structure for the user to review and edit. " +
                      "Use POST /trips to actually save the trip after editing."
    )
    public ResponseEntity<GeneratedTripDTO> generateOptimizedTrip(@Valid @RequestBody GenerateTripRequest request) {
        // Validate: need at least days * 2 places (2 clusters per day for morning + afternoon)
        int requiredPlaces = request.getDays() * 2;
        if (request.getHeritagePlaceIds().size() < requiredPlaces) {
            log.warn("Insufficient places: need at least {} places for {} days ({} clusters), got {} places", 
                     requiredPlaces, request.getDays(), requiredPlaces, request.getHeritagePlaceIds().size());
            return ResponseEntity.badRequest().build();
        }

        try {
            // Generate trip (DOES NOT SAVE to database - user will review/edit before saving via POST /trips)
            GeneratedTripDTO trip = tripGenerationService.generateTrip(request);
            log.info("Successfully generated trip with {} days", trip.getDays().size());
            return ResponseEntity.ok(trip);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating trip: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/trips")
    public ResponseEntity<TripDetailDTO> createTrip(@Valid @RequestBody CreateFullTripRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("POST /api/v1/trips - uid: {}", userLocalId);

        TripDetailDTO trip = tripService.createFullTrip(userLocalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(trip);
    }

    @GetMapping("/trips")
    public ResponseEntity<Page<TripSummaryDTO>> getUserTrips(@RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String q,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/trips - uid: {}", userLocalId);

        Pageable pageable = PageRequest.of(page, size);
        Page<TripSummaryDTO> trips = tripService.getUserTrips(userLocalId, status, q, pageable);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<TripDetailDTO> getTripDetail(@PathVariable UUID tripId) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/trips/{} - uid: {}", tripId, userLocalId);

        TripDetailDTO trip = tripService.getTripDetail(tripId, userLocalId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/trips/{tripId}")
    public ResponseEntity<TripDetailDTO> updateTrip(@PathVariable UUID tripId,
                                                    @Valid @RequestBody UpdateTripRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/trips/{} - uid: {}", tripId, userLocalId);

        TripDetailDTO trip = tripService.updateTrip(tripId, userLocalId, request);
        return ResponseEntity.ok(trip);
    }

    @DeleteMapping("/trips/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID tripId) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("DELETE /api/v1/trips/{} - uid: {}", tripId, userLocalId);

        tripService.deleteTrip(tripId, userLocalId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/trips/{tripId}/members")
    public ResponseEntity<TripMemberDTO> addMember(@PathVariable UUID tripId,
                                                   @Valid @RequestBody AddMemberRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("POST /api/v1/trips/{}/members - uid: {}", tripId, userLocalId);

        TripMemberDTO member = tripService.addMember(tripId, userLocalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @GetMapping("/trips/{tripId}/members")
    public ResponseEntity<List<TripMemberDTO>> getTripMembers(@PathVariable UUID tripId) {
        log.info("GET /api/v1/trips/{}/members", tripId);

        List<TripMemberDTO> members = tripService.getTripMembers(tripId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/trips/{tripId}/members/{memberLocalId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID tripId,
                                             @PathVariable String memberLocalId) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("DELETE /api/v1/trips/{}/members/{} - uid: {}", tripId, memberLocalId, userLocalId);

        tripService.removeMember(tripId, userLocalId, memberLocalId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/trips/{tripId}/days")
    public ResponseEntity<TripDayDTO> addDay(@PathVariable UUID tripId,
                                             @Valid @RequestBody AddDayRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("POST /api/v1/trips/{}/days - uid: {}", tripId, userLocalId);

        TripDayDTO day = tripService.addDay(tripId, userLocalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(day);
    }

    @PostMapping("/trips/{tripId}/days/{dayId}/activities")
    public ResponseEntity<TripActivityDTO> addActivity(@PathVariable UUID tripId,
                                                       @PathVariable UUID dayId,
                                                       @Valid @RequestBody AddActivityRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("POST /api/v1/trips/{}/days/{}/activities - uid: {}", tripId, dayId, userLocalId);

        TripActivityDTO activity = tripService.addActivity(tripId, dayId, userLocalId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    @PutMapping("/trips/{tripId}/days/{dayId}/activities/{activityId}")
    public ResponseEntity<TripActivityDTO> updateActivity(@PathVariable UUID tripId,
                                                          @PathVariable UUID dayId,
                                                          @PathVariable UUID activityId,
                                                          @Valid @RequestBody UpdateActivityRequest request) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/trips/{}/days/{}/activities/{} - uid: {}", tripId, dayId, activityId, userLocalId);

        TripActivityDTO activity = tripService.updateActivity(tripId, dayId, activityId, userLocalId, request);
        return ResponseEntity.ok(activity);
    }

    @DeleteMapping("/trips/{tripId}/days/{dayId}/activities/{activityId}")
    public ResponseEntity<Void> removeActivity(@PathVariable UUID tripId,
                                               @PathVariable UUID dayId,
                                               @PathVariable UUID activityId) {
        String userLocalId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("DELETE /api/v1/trips/{}/days/{}/activities/{} - uid: {}", tripId, dayId, activityId, userLocalId);

        tripService.removeActivity(tripId, dayId, activityId, userLocalId);
        return ResponseEntity.noContent().build();
    }
}