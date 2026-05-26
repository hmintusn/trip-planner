package com.example.trip_planner.user.dto;

import lombok.Data;

/**
 * Request DTO for updating user profile
 */
@Data
public class UpdateProfileRequest {
    private String displayName;
    private String photoUrl;
}
