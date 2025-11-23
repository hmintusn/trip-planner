package com.example.trip_planner.trip.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class TripActivityDTO {

    private UUID id;
    private String placeId;
    private String startTime;
    private String endTime;
    private Integer orderIndex;
    private String notes;
    private PlacePreviewDTO placePreview;
}