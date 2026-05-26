package com.example.trip_planner.user.dto;

import com.example.trip_planner.user.model.UserStatus;
import lombok.Data;

/**
 * Request DTO for admin to update user
 */
@Data
public class AdminUpdateUserRequest {
    private String displayName;
    private String photoUrl;
    private UserStatus status;
}
