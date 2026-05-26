package com.example.trip_planner.user.controller;

import com.example.trip_planner.user.dto.AdminUpdateUserRequest;
import com.example.trip_planner.user.dto.UpdateStatusRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
@SecurityRequirement(name = "bearerAuth")
public class UserAdminController {

    private final UserService userService;

    /**
     * GET /api/v1/admin/users
     * List all users (with pagination, filters)
     */
    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> listUsers(Pageable pageable) {
        String adminUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/admin/users - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        log.info("Admin access by uid: {}", adminUserId);
        Page<UserProfileDTO> users = userService.listUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/admin/users/:uid
     * Get detailed user info
     */
    @GetMapping("/{uid}")
    public ResponseEntity<UserProfileDTO> getUser(@PathVariable String uid) {
        String adminUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("GET /api/v1/admin/users/{}", uid);

        log.info("Admin access by uid: {} for user: {}", adminUserId, uid);
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
        String adminUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PUT /api/v1/admin/users/{} - request: {}", uid, request);

        log.info("Admin update by uid: {} for user: {}", adminUserId, uid);
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
        String adminUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("PATCH /api/v1/admin/users/{}/status - status: {}", uid, request.getStatus());

        log.info("Admin status update by uid: {} for user: {}", adminUserId, uid);
        userService.updateUserStatus(uid, request.getStatus());
        UserProfileDTO profile = userService.getUserProfile(uid);
        return ResponseEntity.ok(profile);
    }
}
