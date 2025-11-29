package com.example.trip_planner.exploration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for exploration list (feed)
 * Contains only essential fields for list display
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplorationFeedResponse {
    private String id;
    private String title;
    private String summary;
    private Integer provinceId;
    private String provinceName;
    private List<String> tags;
    private String category;
    private Double score;
    private Instant createdAt;
}
