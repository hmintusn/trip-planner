package com.example.trip_planner.auth.service;

import com.example.trip_planner.user.dto.UserProfileDTO;

/**
 * Auth service interface
 */
public interface AuthService {
    /**
     * Sign in user by verifying Firebase idToken and syncing user info
     * Returns user profile
     */
    UserProfileDTO signin(String idToken) throws Exception;
}
