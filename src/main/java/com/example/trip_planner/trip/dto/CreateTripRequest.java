package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTripRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Name of the trip", example = "Weekend in Hoi An")
    private String name;

    @Size(max = 1000)
    @Schema(description = "Trip description", example = "A relaxing weekend getaway")
    private String description;

    @Schema(description = "Trip notes", example = "Don't forget to bring sunscreen")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Start date of the trip", example = "2026-01-10", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "End date of the trip", example = "2026-01-12", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Trip visibility", example = "PRIVATE", allowableValues = {"PRIVATE", "PUBLIC"})
    private String visibility; // PRIVATE or PUBLIC
}