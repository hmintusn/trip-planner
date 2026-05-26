package com.example.trip_planner.exploration.dto;

import com.example.trip_planner.exploration.model.Exploration.ContentBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for exploration detail view
 * Contains complete exploration information including content blocks
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplorationDetailsResponse {
    private String id;
    private String title;
    private String sourceUrl;
    private List<ContentBlock> contentBlocks;
    private String author;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer provinceId;
    private String provinceName;
    private List<String> tags;
    private String category;
    private String summary;
    private Double score;
}
