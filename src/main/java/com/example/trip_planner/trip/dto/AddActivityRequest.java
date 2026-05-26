package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalTime;

@Data
public class AddActivityRequest {

    @NotBlank(message = "Place ID is required")
    @Schema(description = "Google Place ID", example = "ChIJLfyY2E4rQjERCq-pDhpe4hU")
    private String placeId;

    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Start time", example = "09:00", type = "string", format = "time", pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @Schema(description = "End time", example = "12:00", type = "string", format = "time", pattern = "HH:mm")
    private LocalTime endTime;
    
    @Schema(description = "Order index", example = "0")
    private Integer orderIndex;
    
    @Schema(description = "Activity notes", example = "Morning visit")
    private String notes;
}