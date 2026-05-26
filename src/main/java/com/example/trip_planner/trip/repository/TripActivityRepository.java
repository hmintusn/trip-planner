package com.example.trip_planner.trip.repository;

import com.example.trip_planner.trip.model.TripActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripActivityRepository extends JpaRepository<TripActivity, UUID> {

    List<TripActivity> findByTripDayIdOrderByOrderIndex(UUID tripDayId);
}