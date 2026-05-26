package com.example.trip_planner.user.dto;

import com.example.trip_planner.user.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * User profile response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String localId;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String photoUrl;
    private UserStatus status;
    private Instant createdAt;
    private Instant lastLoginAt;
    private Instant updatedAt;
}
