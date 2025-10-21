package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for place detail
 * Contains all fields including opening hours
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailResponse {
    private String id;
    private String name;
    private String formattedAddress;
    private List<String> types;
    private LocationDTO location;
    private Double rating;
    private Integer userRatingCount;
    private RegularOpeningHoursDTO regularOpeningHours;
    private Integer provinceId;
    private String provinceName;
    private String category;
    private Integer priceLevel;
    private String thumbnail;
    private String summary;
    private Double score;
    private Instant createdAt;
    private Instant updatedAt;
}
