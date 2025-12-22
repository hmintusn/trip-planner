package com.example.trip_planner.trip.service.impl;

import com.example.trip_planner.trip.dto.*;
import com.example.trip_planner.trip.model.*;
import com.example.trip_planner.trip.repository.*;
import com.example.trip_planner.trip.service.TripService;
import com.example.trip_planner.user.model.User;
import com.example.trip_planner.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripDayRepository tripDayRepository;
    private final TripActivityRepository tripActivityRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // For JSON handling

    @Override
    @Transactional
    public TripDetailDTO createTrip(String ownerLocalId, CreateTripRequest request) {
        // Validate user exists
        User owner = userRepository.findByLocalId(ownerLocalId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = new Trip();
        trip.setOwnerLocalId(ownerLocalId);
        trip.setName(request.getName());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        if (request.getVisibility() != null) {
            trip.setVisibility(TripVisibility.valueOf(request.getVisibility().toUpperCase()));
        }

        Trip savedTrip = tripRepository.save(trip);

        // Add owner as member
        TripMember ownerMember = new TripMember();
        ownerMember.setTripId(savedTrip.getId());
        ownerMember.setUserLocalId(ownerLocalId);
        ownerMember.setRole(TripMemberRole.OWNER);
        tripMemberRepository.save(ownerMember);

        return mapToDetailDTO(savedTrip);
    }

    @Override
    @Transactional
    public TripDetailDTO createFullTrip(String ownerLocalId, CreateFullTripRequest request) {
        // Validate user exists
        User owner = userRepository.findByLocalId(ownerLocalId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create basic trip
        Trip trip = new Trip();
        trip.setOwnerLocalId(ownerLocalId);
        trip.setName(request.getName());
        trip.setDescription(request.getDescription());
        trip.setNotes(request.getNotes());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        if (request.getVisibility() != null) {
            trip.setVisibility(TripVisibility.valueOf(request.getVisibility().toUpperCase()));
        }
        trip.setStatus(TripStatus.ACTIVE);

        Trip savedTrip = tripRepository.save(trip);

        // Add owner as member
        TripMember ownerMember = new TripMember();
        ownerMember.setTripId(savedTrip.getId());
        ownerMember.setUserLocalId(ownerLocalId);
        ownerMember.setRole(TripMemberRole.OWNER);
        tripMemberRepository.save(ownerMember);

        // Add members if provided
        if (request.getMembers() != null && !request.getMembers().isEmpty()) {
            for (AddMemberRequest memberReq : request.getMembers()) {
                // Validate member user exists
                userRepository.findByLocalId(memberReq.getUserLocalId())
                    .orElseThrow(() -> new RuntimeException("Member user not found: " + memberReq.getUserLocalId()));

                TripMember member = new TripMember();
                member.setTripId(savedTrip.getId());
                member.setUserLocalId(memberReq.getUserLocalId());
                member.setRole(memberReq.getRole() != null ? memberReq.getRole() : TripMemberRole.VIEWER);
                tripMemberRepository.save(member);
            }
        }

        // Add days and activities if provided
        if (request.getDays() != null && !request.getDays().isEmpty()) {
            for (CreateDayWithActivitiesRequest dayReq : request.getDays()) {
                TripDay day = new TripDay();
                day.setTripId(savedTrip.getId());
                day.setDayDate(dayReq.getDayDate());
                day.setOrderIndex(dayReq.getOrderIndex() != null ? dayReq.getOrderIndex() : 0);
                day.setNotes(dayReq.getNotes());
                TripDay savedDay = tripDayRepository.save(day);

                // Add activities for this day if provided
                if (dayReq.getActivities() != null && !dayReq.getActivities().isEmpty()) {
                    for (AddActivityRequest activityReq : dayReq.getActivities()) {
                        TripActivity activity = new TripActivity();
                        activity.setTripDayId(savedDay.getId());
                        activity.setPlaceId(activityReq.getPlaceId());
                        activity.setStartTime(activityReq.getStartTime());
                        activity.setEndTime(activityReq.getEndTime());
                        activity.setOrderIndex(activityReq.getOrderIndex() != null ? activityReq.getOrderIndex() : 0);
                        activity.setNotes(activityReq.getNotes());
                        // TODO: Fetch place preview from Mongo and set placeSnapshot
                        tripActivityRepository.save(activity);
                    }
                }
            }
        }

        return mapToDetailDTO(savedTrip);
    }

    @Override
    public Page<TripSummaryDTO> getUserTrips(String userLocalId, String status, String q, Pageable pageable) {
        TripStatus statusEnum = status != null ? TripStatus.valueOf(status.toUpperCase()) : TripStatus.DELETED;
        Page<Trip> trips;
        if (q != null && !q.trim().isEmpty()) {
            trips = tripRepository.findByOwnerLocalIdAndStatusNotAndQuery(userLocalId, statusEnum, q, pageable);
        } else {
            trips = tripRepository.findByOwnerLocalIdAndStatusNot(userLocalId, statusEnum, pageable);
        }
        return trips.map(this::mapToSummaryDTO);
    }

    @Override
    public TripDetailDTO getTripDetail(UUID tripId, String userLocalId) {
        Trip trip = tripRepository.findByIdAndStatusNot(tripId, TripStatus.DELETED)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Check access: owner, member, or public
        if (!trip.getOwnerLocalId().equals(userLocalId) &&
            !tripMemberRepository.existsByTripIdAndUserLocalId(tripId, userLocalId) &&
            trip.getVisibility() != TripVisibility.PUBLIC) {
            throw new RuntimeException("Access denied");
        }

        return mapToDetailDTO(trip);
    }

    @Override
    public TripDetailDTO updateTrip(UUID tripId, String userLocalId, UpdateTripRequest request) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getOwnerLocalId().equals(userLocalId)) {
            throw new RuntimeException("Only owner can update trip");
        }

        if (!trip.getVersion().equals(request.getVersion())) {
            throw new RuntimeException("Version mismatch");
        }

        if (request.getName() != null) trip.setName(request.getName());
        if (request.getDescription() != null) trip.setDescription(request.getDescription());
        if (request.getNotes() != null) trip.setNotes(request.getNotes());
        if (request.getStartDate() != null) trip.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) trip.setEndDate(request.getEndDate());
        if (request.getVisibility() != null) trip.setVisibility(TripVisibility.valueOf(request.getVisibility().toUpperCase()));

        Trip saved = tripRepository.save(trip);
        return mapToDetailDTO(saved);
    }

    @Override
    public void deleteTrip(UUID tripId, String userLocalId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getOwnerLocalId().equals(userLocalId)) {
            throw new RuntimeException("Only owner can delete trip");
        }

        trip.setStatus(TripStatus.DELETED);
        tripRepository.save(trip);
    }

    @Override
    public TripMemberDTO addMember(UUID tripId, String ownerLocalId, AddMemberRequest request) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getOwnerLocalId().equals(ownerLocalId)) {
            throw new RuntimeException("Only owner can add members");
        }

        // Validate user exists
        userRepository.findByLocalId(request.getUserLocalId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        TripMember member = new TripMember();
        member.setTripId(tripId);
        member.setUserLocalId(request.getUserLocalId());
        member.setRole(request.getRole() != null ? request.getRole() : TripMemberRole.VIEWER);

        TripMember saved = tripMemberRepository.save(member);
        return mapToMemberDTO(saved);
    }

    @Override
    public void removeMember(UUID tripId, String ownerLocalId, String memberLocalId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getOwnerLocalId().equals(ownerLocalId)) {
            throw new RuntimeException("Only owner can remove members");
        }

        tripMemberRepository.deleteByTripIdAndUserLocalId(tripId, memberLocalId);
    }

    @Override
    public List<TripMemberDTO> getTripMembers(UUID tripId) {
        return tripMemberRepository.findByTripId(tripId).stream()
            .map(this::mapToMemberDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TripDayDTO addDay(UUID tripId, String userLocalId, AddDayRequest request) {
        // Check permission
        checkEditPermission(tripId, userLocalId);

        TripDay day = new TripDay();
        day.setTripId(tripId);
        day.setDayDate(request.getDayDate());
        day.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        day.setNotes(request.getNotes());

        TripDay saved = tripDayRepository.save(day);
        return mapToDayDTO(saved);
    }

    @Override
    public TripActivityDTO addActivity(UUID tripId, UUID dayId, String userLocalId, AddActivityRequest request) {
        checkEditPermission(tripId, userLocalId);

        TripActivity activity = new TripActivity();
        activity.setTripDayId(dayId);
        activity.setPlaceId(request.getPlaceId());
        activity.setStartTime(request.getStartTime());
        activity.setEndTime(request.getEndTime());
        activity.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        activity.setNotes(request.getNotes());
        TripActivity saved = tripActivityRepository.save(activity);
        return mapToActivityDTO(saved);
    }

    @Override
    public TripActivityDTO updateActivity(UUID tripId, UUID dayId, UUID activityId, String userLocalId, UpdateActivityRequest request) {
        checkEditPermission(tripId, userLocalId);

        TripActivity activity = tripActivityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getTripDayId().equals(dayId)) {
            throw new RuntimeException("Activity not in this day");
        }

        if (request.getStartTime() != null) {
            activity.setStartTime(LocalTime.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null) {
            activity.setEndTime(LocalTime.parse(request.getEndTime()));
        }
        if (request.getOrderIndex() != null) {
            activity.setOrderIndex(request.getOrderIndex());
        }
        if (request.getNotes() != null) {
            activity.setNotes(request.getNotes());
        }

        TripActivity saved = tripActivityRepository.save(activity);
        return mapToActivityDTO(saved);
    }

    @Override
    public void removeActivity(UUID tripId, UUID dayId, UUID activityId, String userLocalId) {
        checkEditPermission(tripId, userLocalId);

        TripActivity activity = tripActivityRepository.findById(activityId)
            .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (!activity.getTripDayId().equals(dayId)) {
            throw new RuntimeException("Activity not in this day");
        }

        tripActivityRepository.delete(activity);
    }

    // Admin methods
    @Override
    public Page<TripSummaryDTO> getAllTrips(String ownerLocalId, String status, String q, Pageable pageable) {
        Page<Trip> trips;
        if (ownerLocalId != null) {
            trips = tripRepository.findByOwnerLocalId(ownerLocalId, pageable);
        } else if (status != null) {
            trips = tripRepository.findByStatus(TripStatus.valueOf(status.toUpperCase()), pageable);
        } else if (q != null) {
            trips = tripRepository.findByQuery(q, pageable);
        } else {
            trips = tripRepository.findAll(pageable);
        }
        return trips.map(this::mapToSummaryDTO);
    }

    @Override
    public TripDetailDTO getTripDetailAdmin(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));
        return mapToDetailDTO(trip);
    }

    @Override
    public void updateTripStatus(UUID tripId, String status) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(TripStatus.valueOf(status.toUpperCase()));
        tripRepository.save(trip);
    }

    // Helper methods
    private void checkEditPermission(UUID tripId, String userLocalId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

        if (!trip.getOwnerLocalId().equals(userLocalId)) {
            TripMember member = tripMemberRepository.findById(new TripMemberId(tripId, userLocalId))
                .orElseThrow(() -> new RuntimeException("Not a member"));
            if (member.getRole() != TripMemberRole.EDITOR && member.getRole() != TripMemberRole.OWNER) {
                throw new RuntimeException("No edit permission");
            }
        }
    }

    private TripSummaryDTO mapToSummaryDTO(Trip trip) {
        TripSummaryDTO dto = new TripSummaryDTO();
        dto.setId(trip.getId());
        dto.setName(trip.getName());
        dto.setStartDate(trip.getStartDate() != null ? trip.getStartDate().toString() : null);
        dto.setEndDate(trip.getEndDate() != null ? trip.getEndDate().toString() : null);
        dto.setStatus(trip.getStatus().name());
        dto.setUpdatedAt(trip.getUpdatedAt());
        return dto;
    }

    private TripDetailDTO mapToDetailDTO(Trip trip) {
        TripDetailDTO dto = new TripDetailDTO();
        dto.setId(trip.getId());
        dto.setOwnerLocalId(trip.getOwnerLocalId());
        dto.setName(trip.getName());
        dto.setDescription(trip.getDescription());
        dto.setNotes(trip.getNotes());
        dto.setStartDate(trip.getStartDate() != null ? trip.getStartDate().toString() : null);
        dto.setEndDate(trip.getEndDate() != null ? trip.getEndDate().toString() : null);
        dto.setVisibility(trip.getVisibility().name());
        dto.setMembers(getTripMembers(trip.getId()));
        dto.setDays(tripDayRepository.findByTripIdOrderByOrderIndex(trip.getId()).stream()
            .map(this::mapToDayDTO)
            .collect(Collectors.toList()));
        dto.setCreatedAt(trip.getCreatedAt());
        dto.setUpdatedAt(trip.getUpdatedAt());
        dto.setVersion(trip.getVersion());
        return dto;
    }

    private TripMemberDTO mapToMemberDTO(TripMember member) {
        TripMemberDTO dto = new TripMemberDTO();
        dto.setUserLocalId(member.getUserLocalId());
        dto.setRole(member.getRole().name());
        dto.setJoinedAt(member.getJoinedAt());
        return dto;
    }

    private TripDayDTO mapToDayDTO(TripDay day) {
        TripDayDTO dto = new TripDayDTO();
        dto.setId(day.getId());
        dto.setDayDate(day.getDayDate() != null ? day.getDayDate().toString() : null);
        dto.setOrderIndex(day.getOrderIndex());
        dto.setNotes(day.getNotes());
        dto.setActivities(tripActivityRepository.findByTripDayIdOrderByOrderIndex(day.getId()).stream()
            .map(this::mapToActivityDTO)
            .collect(Collectors.toList()));
        return dto;
    }

    private TripActivityDTO mapToActivityDTO(TripActivity activity) {
        TripActivityDTO dto = new TripActivityDTO();
        dto.setId(activity.getId());
        dto.setPlaceId(activity.getPlaceId());
        dto.setStartTime(activity.getStartTime() != null ? activity.getStartTime().toString() : null);
        dto.setEndTime(activity.getEndTime() != null ? activity.getEndTime().toString() : null);
        dto.setOrderIndex(activity.getOrderIndex());
        dto.setNotes(activity.getNotes());
        // TODO: Parse placeSnapshot JSON to PlacePreviewDTO
        return dto;
    }
}