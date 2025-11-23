package com.example.trip_planner.trip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "trip_activities")
@Getter
@Setter
@NoArgsConstructor
public class TripActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "trip_day_id", nullable = false)
    private UUID tripDayId;

    @NotBlank
    @Column(name = "place_id", nullable = false)
    private String placeId;

    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    @Column(name = "end_time")
    private java.time.LocalTime endTime;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "place_snapshot", columnDefinition = "JSONB")
    private String placeSnapshot; // JSON string for place preview
}