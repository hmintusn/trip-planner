package com.example.trip_planner.exploration.dto;

import com.example.trip_planner.exploration.model.Exploration.ContentBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Request DTO for creating a new exploration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplorationCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private Integer provinceId;
    
    private String provinceName;
    
    private String sourceUrl;
    
    private String summary;
    
    private List<ContentBlock> contentBlocks;
    
    private List<String> tags;
    
    private String category;
    
    private Double score;
    
    private String author;
}
