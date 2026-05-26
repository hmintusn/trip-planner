package com.example.trip_planner.user.dto;

import com.example.trip_planner.user.model.UserStatus;
import lombok.Data;

/**
 * Request DTO for admin to update user status
 */
@Data
public class UpdateStatusRequest {
    private UserStatus status;
}
