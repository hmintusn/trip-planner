package com.example.trip_planner.trip.service;

import com.example.trip_planner.trip.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TripService {

    TripDetailDTO createTrip(String ownerLocalId, CreateTripRequest request);

    TripDetailDTO createFullTrip(String ownerLocalId, CreateFullTripRequest request);

    Page<TripSummaryDTO> getUserTrips(String userLocalId, String status, String q, Pageable pageable);

    TripDetailDTO getTripDetail(UUID tripId, String userLocalId);

    TripDetailDTO updateTrip(UUID tripId, String userLocalId, UpdateTripRequest request);

    void deleteTrip(UUID tripId, String userLocalId);

    TripMemberDTO addMember(UUID tripId, String ownerLocalId, AddMemberRequest request);

    void removeMember(UUID tripId, String ownerLocalId, String memberLocalId);

    List<TripMemberDTO> getTripMembers(UUID tripId);

    TripDayDTO addDay(UUID tripId, String userLocalId, AddDayRequest request);

    TripActivityDTO addActivity(UUID tripId, UUID dayId, String userLocalId, AddActivityRequest request);

    TripActivityDTO updateActivity(UUID tripId, UUID dayId, UUID activityId, String userLocalId, UpdateActivityRequest request);

    void removeActivity(UUID tripId, UUID dayId, UUID activityId, String userLocalId);

    // Admin methods
    Page<TripSummaryDTO> getAllTrips(String ownerLocalId, String status, String q, Pageable pageable);

    TripDetailDTO getTripDetailAdmin(UUID tripId);

    void updateTripStatus(UUID tripId, String status);
}