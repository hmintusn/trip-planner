package com.example.trip_planner.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Request to create a full trip with all details in one go")
public class CreateFullTripRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Name of the trip", example = "Weekend in Hoi An")
    private String name;

    @Size(max = 1000)
    @Schema(description = "Trip description", example = "A relaxing weekend getaway")
    private String description;

    @Schema(description = "Trip notes", example = "Don't forget to bring sunscreen")
    private String notes;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "Start date of the trip", example = "2026-01-10", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Schema(description = "End date of the trip", example = "2026-01-12", type = "string", format = "date", pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    @Schema(description = "Trip visibility", example = "PRIVATE", allowableValues = {"PRIVATE", "PUBLIC"})
    private String visibility;

    @Valid
    @Schema(description = "List of members to add (excluding owner)")
    private List<AddMemberRequest> members;

    @Valid
    @Schema(description = "List of days with activities")
    private List<CreateDayWithActivitiesRequest> days;
}
