package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for place list (feed)
 * Contains only essential fields for list display
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceFeedResponse {
    private String id;
    private String name;
    private String formattedAddress;
    private Double rating;
    private Integer userRatingCount;
    private String thumbnail;
    private String category;
    private Integer provinceId;
    private String provinceName;
    private Double score;
    private LocationDTO location;
    private List<String> types;
}
