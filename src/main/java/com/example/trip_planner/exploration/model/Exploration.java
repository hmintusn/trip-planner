package com.example.trip_planner.exploration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Exploration entity stored in MongoDB
 * Contains exploration content with structured content blocks
 */
@Document(collection = "explorations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exploration {

    @Id
    @JsonProperty("_id")
    private String id; // UUID or provided ID

    private String title;

    @Indexed
    private Integer provinceId;

    private String provinceName;

    private String sourceUrl;

    private String summary;

    private List<ContentBlock> contentBlocks;

    private List<String> tags;

    @Indexed
    private String category;

    @Indexed
    private Double score; // For ranking/sorting

    private String author;

    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Content block representing different types of content
     * Reuses the same structure as PlaceDetails.ContentBlock
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentBlock {
        private String type; // heading, paragraph, image, list, etc.
        private String content; // text content
        private String url; // for images/links
        private String caption; // for images
        private List<String> items; // for lists
    }
}
