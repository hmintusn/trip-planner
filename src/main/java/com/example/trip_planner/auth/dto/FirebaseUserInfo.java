package com.example.trip_planner.auth.dto;

import lombok.Data;

import java.time.Instant;

/**
 * DTO for Firebase user info from accounts:lookup API
 */
@Data
public class FirebaseUserInfo {
    private String localId;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String photoUrl;
    private Instant createdAt;
    private Instant lastLoginAt;
}
