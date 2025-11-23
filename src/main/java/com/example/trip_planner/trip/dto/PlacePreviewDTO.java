package com.example.trip_planner.trip.dto;

import lombok.Data;

@Data
public class PlacePreviewDTO {

    private String name;
    private String thumbnail;
    private String category;
    private Double latitude;
    private Double longitude;
}