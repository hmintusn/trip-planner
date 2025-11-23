package com.example.trip_planner.trip.dto;

import com.example.trip_planner.trip.model.TripMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMemberRequest {

    @NotBlank(message = "User local ID is required")
    @Schema(description = "User local ID", example = "abc123xyz")
    private String userLocalId;

    @NotNull(message = "Role is required")
    @Schema(description = "Member role", example = "EDITOR")
    private TripMemberRole role;
}