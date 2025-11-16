package com.example.trip_planner.user.controller;

import com.example.trip_planner.user.dto.UpdateProfileRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Get current user profile (requires Bearer token)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/user/profile - uid: {}", userId);

        UserProfileDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/v1/user/profile
     * Update user profile (display name, photo, etc.)
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestBody UpdateProfileRequest request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/user/profile - uid: {}, request: {}", userId, request);

        userService.updateProfile(userId, request);
        UserProfileDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
