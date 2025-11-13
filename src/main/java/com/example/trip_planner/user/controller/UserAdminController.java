package com.example.trip_planner.user.controller;

import com.example.trip_planner.user.dto.AdminUpdateUserRequest;
import com.example.trip_planner.user.dto.UpdateStatusRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin user management controller
 * Endpoints: /api/v1/admin/users/*
 * 
 * TODO: Add admin role check via Firebase custom claims
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    /**
     * GET /api/v1/admin/users
     * List all users (with pagination, filters)
     */
    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> listUsers(Pageable pageable) {
        log.info("GET /api/v1/admin/users - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileDTO> users = userService.listUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/admin/users/:uid
     * Get detailed user info
     */
    @GetMapping("/{uid}")
    public ResponseEntity<UserProfileDTO> getUser(@PathVariable String uid) {
        log.info("GET /api/v1/admin/users/{}", uid);
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/v1/admin/users/:uid
     * Update user (role, display name, etc.)
     */
    @PutMapping("/{uid}")
    public ResponseEntity<UserProfileDTO> updateUser(
            @PathVariable String uid,
            @RequestBody AdminUpdateUserRequest request) {
        log.info("PUT /api/v1/admin/users/{} - request: {}", uid, request);
        userService.adminUpdateUser(uid, request);
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }

    /**
     * PATCH /api/v1/admin/users/:uid/status
     * Activate / Deactivate account (soft disable)
     */
    @PatchMapping("/{uid}/status")
    public ResponseEntity<UserProfileDTO> updateStatus(
            @PathVariable String uid,
            @RequestBody UpdateStatusRequest request) {
        log.info("PATCH /api/v1/admin/users/{}/status - status: {}", uid, request.getStatus());
        userService.updateUserStatus(uid, request.getStatus());
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }
}
