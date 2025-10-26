package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for PlaceDetails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetailsResponse {

    private String id;
    private String title;
    private Integer provinceId;
    private String sourceUrl;
    private List<ContentBlock> contentBlocks;
    private Instant createdAt;
    private Instant updatedAt;
    private String author;

    /**
     * Content block DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentBlock {
        private String type;
        private String content;
        private String url;
        private String caption;
        private List<String> items;
    }
}