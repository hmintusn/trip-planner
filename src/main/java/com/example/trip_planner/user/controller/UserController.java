package com.example.trip_planner.user.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.trip_planner.firebase.FirebaseTokenVerifier;
import com.example.trip_planner.user.dto.UpdateProfileRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User controller for profile management
 * Endpoints: /api/v1/user/*
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FirebaseTokenVerifier tokenVerifier;

    /**
     * GET /api/v1/user/profile
     * Get current user profile (requires token)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestBody TokenRequest request) {
        log.info("GET /api/v1/user/profile");
        
        // Verify token and extract uid
        DecodedJWT decodedToken = tokenVerifier.verifyToken(request.getIdToken());
        String uid = tokenVerifier.extractUserId(decodedToken);
        
        log.info("GET /api/v1/user/profile - uid: {}", uid);
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/v1/user/profile
     * Update user profile (display name, photo, etc.)
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestBody UpdateProfileWithTokenRequest request) {
        log.info("PUT /api/v1/user/profile");
        
        // Verify token and extract uid
        DecodedJWT decodedToken = tokenVerifier.verifyToken(request.getIdToken());
        String uid = tokenVerifier.extractUserId(decodedToken);
        
        log.info("PUT /api/v1/user/profile - uid: {}, request: {}", uid, request.getUpdateRequest());
        userService.updateProfile(uid, request.getUpdateRequest());
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }

    /**
     * DTO for token request
     */
    public static class TokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    /**
     * DTO for update profile with token
     */
    public static class UpdateProfileWithTokenRequest {
        private String idToken;
        private UpdateProfileRequest updateRequest;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }

        public UpdateProfileRequest getUpdateRequest() {
            return updateRequest;
        }

        public void setUpdateRequest(UpdateProfileRequest updateRequest) {
            this.updateRequest = updateRequest;
        }
    }
}
