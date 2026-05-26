package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Day with activities for full trip creation")
public class CreateDayWithActivitiesRequest {

    @NotNull(message = "Day date is required")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Day date", example = "2026-01-11", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate dayDate;

    @Schema(description = "Order index", example = "0")
    private Integer orderIndex;

    @Schema(description = "Notes for the day", example = "City walking tour")
    private String notes;

    @Valid
    @Schema(description = "List of activities for this day")
    private List<AddActivityRequest> activities;
}
