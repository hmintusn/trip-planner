package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Province
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceResponse {

    private Integer id;
    private String name;
    private String region;
    private String description;
    private String thumbnail;
}