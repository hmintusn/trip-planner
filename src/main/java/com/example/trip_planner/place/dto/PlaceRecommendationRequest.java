package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for place recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRecommendationRequest {
    private List<String> placeIds;
    private List<String> categories;
    private Integer radius;
}