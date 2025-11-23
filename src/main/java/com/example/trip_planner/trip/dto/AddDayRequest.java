package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AddDayRequest {

    @NotNull(message = "Day date is required")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Day date", example = "2024-12-25", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate dayDate;
    
    @Schema(description = "Order index", example = "0")
    private Integer orderIndex;
    
    @Schema(description = "Notes for the day", example = "Visit old quarter")
    private String notes;
}