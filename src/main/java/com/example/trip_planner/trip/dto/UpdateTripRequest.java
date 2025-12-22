package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTripRequest {

    @Schema(description = "Updated trip name", example = "Extended Weekend in Hoi An")
    private String name;

    @Schema(description = "Updated description", example = "Extended relaxing weekend")
    private String description;

    @Schema(description = "Updated notes", example = "Remember to pack warm clothes")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Updated start date", example = "2026-01-10", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Updated end date", example = "2026-01-15", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Updated visibility", example = "PUBLIC", allowableValues = {"PRIVATE", "PUBLIC"})
    private String visibility;

    @NotNull
    @Schema(description = "Version for optimistic locking", example = "0")
    private Integer version;
}