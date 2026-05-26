package com.example.trip_planner.trip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "trip_days")
@Getter
@Setter
@NoArgsConstructor
public class TripDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "day_date")
    private java.time.LocalDate dayDate;

    @NotNull
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;
}