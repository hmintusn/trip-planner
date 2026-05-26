package com.example.trip_planner.trip.repository;

import com.example.trip_planner.trip.model.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripDayRepository extends JpaRepository<TripDay, UUID> {

    List<TripDay> findByTripIdOrderByOrderIndex(UUID tripId);
}