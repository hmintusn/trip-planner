package com.example.trip_planner.trip.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class TripSummaryDTO {

    private UUID id;
    private String name;
    private String startDate;
    private String endDate;
    private String status;
    private Instant updatedAt;
}