package com.example.trip_planner.user.controller;

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

    /**
     * GET /api/v1/user/profile
     * Get current user profile (requires token)
     * 
     * TODO: Extract localId from Firebase token in Authorization header
     * For now, using uid from query param for testing
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestParam String uid) {
        log.info("GET /api/v1/user/profile - uid: {}", uid);
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/v1/user/profile
     * Update user profile (display name, photo, etc.)
     * 
     * TODO: Extract localId from Firebase token in Authorization header
     * For now, using uid from query param for testing
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestParam String uid,
            @RequestBody UpdateProfileRequest request) {
        log.info("PUT /api/v1/user/profile - uid: {}, request: {}", uid, request);
        userService.updateProfile(uid, request);
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }
}
