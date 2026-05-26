package com.example.trip_planner.trip.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TripDayDTO {

    private UUID id;
    private String dayDate;
    private Integer orderIndex;
    private String notes;
    private List<TripActivityDTO> activities;
}