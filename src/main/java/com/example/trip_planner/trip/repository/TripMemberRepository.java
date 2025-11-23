package com.example.trip_planner.trip.repository;

import com.example.trip_planner.trip.model.TripMember;
import com.example.trip_planner.trip.model.TripMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripMemberRepository extends JpaRepository<TripMember, TripMemberId> {

    List<TripMember> findByTripId(UUID tripId);

    boolean existsByTripIdAndUserLocalId(UUID tripId, String userLocalId);

    void deleteByTripIdAndUserLocalId(UUID tripId, String userLocalId);
}