package com.example.trip_planner.user.service;

import com.example.trip_planner.auth.dto.FirebaseUserInfo;
import com.example.trip_planner.user.dto.UpdateProfileRequest;
import com.example.trip_planner.user.dto.AdminUpdateUserRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.model.User;
import com.example.trip_planner.user.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * User service interface
 */
public interface UserService {
    /**
     * Sync user from Firebase info and upsert into DB
     */
    User syncFromFirebase(FirebaseUserInfo firebaseUserInfo);
    
    /**
     * Get user by localId
     */
    User getUserByLocalId(String localId);
    
    /**
     * Get user profile DTO
     */
    UserProfileDTO getUserProfile(String localId);
    
    /**
     * Update user profile (user can only update their own)
     */
    User updateProfile(String localId, UpdateProfileRequest request);
    
    /**
     * List all users with pagination (admin)
     */
    Page<UserProfileDTO> listUsers(Pageable pageable);
    
    /**
     * Admin update user
     */
    User adminUpdateUser(String localId, AdminUpdateUserRequest request);
    
    /**
     * Admin update user status
     */
    User updateUserStatus(String localId, UserStatus status);
}
