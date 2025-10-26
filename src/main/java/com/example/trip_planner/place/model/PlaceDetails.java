package com.example.trip_planner.place.model;

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
 * PlaceDetails entity stored in MongoDB
 * Contains extended information about places including content blocks, articles, heritage info
 */
@Document(collection = "place-details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDetails {

    @Id
    @JsonProperty("_id")
    private String id; // Same as Place ID (Google Place ID)

    private String title;

    @Indexed
    private Integer provinceId;

    private String sourceUrl;

    private List<ContentBlock> contentBlocks;

    private Instant createdAt;

    private Instant updatedAt;

    private String author;

    /**
     * Content block representing different types of content
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