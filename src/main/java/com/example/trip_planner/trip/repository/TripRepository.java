package com.example.trip_planner.trip.repository;

import com.example.trip_planner.trip.model.Trip;
import com.example.trip_planner.trip.model.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    Page<Trip> findByOwnerLocalIdAndStatusNot(String ownerLocalId, TripStatus status, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.ownerLocalId = :ownerLocalId AND t.status != :status AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Trip> findByOwnerLocalIdAndStatusNotAndQuery(String ownerLocalId, TripStatus status, String q, Pageable pageable);

    Optional<Trip> findByIdAndStatusNot(UUID id, TripStatus status);

    List<Trip> findByOwnerLocalId(String ownerLocalId);

    // Admin queries
    Page<Trip> findAll(Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.ownerLocalId = :ownerLocalId")
    Page<Trip> findByOwnerLocalId(@Param("ownerLocalId") String ownerLocalId, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE t.status = :status")
    Page<Trip> findByStatus(@Param("status") TripStatus status, Pageable pageable);

    @Query("SELECT t FROM Trip t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Trip> findByQuery(@Param("q") String q, Pageable pageable);
}