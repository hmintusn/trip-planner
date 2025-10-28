package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for place recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRecommendationResponse {
    private List<PlaceRecommendationItem> recommendations;
}