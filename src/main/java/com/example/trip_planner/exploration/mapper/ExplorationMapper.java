package com.example.trip_planner.exploration.mapper;

import com.example.trip_planner.exploration.dto.ExplorationCreateRequest;
import com.example.trip_planner.exploration.dto.ExplorationDetailsResponse;
import com.example.trip_planner.exploration.dto.ExplorationFeedResponse;
import com.example.trip_planner.exploration.dto.ExplorationUpdateRequest;
import com.example.trip_planner.exploration.model.Exploration;

import java.time.Instant;
import java.util.UUID;

/**
 * Mapper utility for converting between Exploration entities and DTOs
 */
public class ExplorationMapper {
    
    /**
     * Convert Exploration entity to feed response DTO
     */
    public static ExplorationFeedResponse toFeedResponse(Exploration exploration) {
        if (exploration == null) return null;
        
        return ExplorationFeedResponse.builder()
                .id(exploration.getId())
                .title(exploration.getTitle())
                .summary(exploration.getSummary())
                .provinceId(exploration.getProvinceId())
                .provinceName(exploration.getProvinceName())
                .tags(exploration.getTags())
                .category(exploration.getCategory())
                .score(exploration.getScore())
                .createdAt(exploration.getCreatedAt())
                .thumbnail(exploration.getSourceUrl())
                .build();
    }
    
    /**
     * Convert Exploration entity to details response DTO
     */
    public static ExplorationDetailsResponse toDetailsResponse(Exploration exploration) {
        if (exploration == null) return null;
        
        return ExplorationDetailsResponse.builder()
                .id(exploration.getId())
                .title(exploration.getTitle())
                .sourceUrl(exploration.getSourceUrl())
                .contentBlocks(exploration.getContentBlocks())
                .author(exploration.getAuthor())
                .createdAt(exploration.getCreatedAt())
                .updatedAt(exploration.getUpdatedAt())
                .provinceId(exploration.getProvinceId())
                .provinceName(exploration.getProvinceName())
                .tags(exploration.getTags())
                .category(exploration.getCategory())
                .summary(exploration.getSummary())
                .score(exploration.getScore())
                .build();
    }
    
    /**
     * Convert ExplorationCreateRequest to Exploration entity
     * Always generates a new UUID for the ID
     */
    public static Exploration toEntity(ExplorationCreateRequest request) {
        if (request == null) return null;
        
        Instant now = Instant.now();
        
        return Exploration.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .provinceId(request.getProvinceId())
                .provinceName(request.getProvinceName())
                .sourceUrl(request.getSourceUrl())
                .summary(request.getSummary())
                .contentBlocks(request.getContentBlocks())
                .tags(request.getTags())
                .category(request.getCategory())
                .score(request.getScore())
                .author(request.getAuthor())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    /**
     * Update existing Exploration entity with data from UpdateRequest
     */
    public static void updateEntity(Exploration exploration, ExplorationUpdateRequest request) {
        if (exploration == null || request == null) return;
        
        exploration.setTitle(request.getTitle());
        exploration.setProvinceId(request.getProvinceId());
        exploration.setProvinceName(request.getProvinceName());
        exploration.setSourceUrl(request.getSourceUrl());
        exploration.setSummary(request.getSummary());
        exploration.setContentBlocks(request.getContentBlocks());
        exploration.setTags(request.getTags());
        exploration.setCategory(request.getCategory());
        exploration.setScore(request.getScore());
        exploration.setAuthor(request.getAuthor());
        exploration.setUpdatedAt(Instant.now());
    }
}
