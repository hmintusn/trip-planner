package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Individual recommendation item in the response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceRecommendationItem {
    private String _id;
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
    private String priceLevel;
    private String thumbnail;
    private String summary;
    private Double score;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Integer distanceMeters;
}