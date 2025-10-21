package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for location data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private double lat;
    private double lng;
}
