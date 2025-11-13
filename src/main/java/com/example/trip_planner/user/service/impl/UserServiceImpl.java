package com.example.trip_planner.user.service.impl;

import com.example.trip_planner.auth.dto.FirebaseUserInfo;
import com.example.trip_planner.user.dto.AdminUpdateUserRequest;
import com.example.trip_planner.user.dto.UpdateProfileRequest;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.model.User;
import com.example.trip_planner.user.model.UserStatus;
import com.example.trip_planner.user.repository.UserRepository;
import com.example.trip_planner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * User service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User syncFromFirebase(FirebaseUserInfo firebaseUserInfo) {
        User user = userRepository.findByLocalId(firebaseUserInfo.getLocalId())
                .orElseGet(() -> {
                    log.info("Creating new user from Firebase: {}", firebaseUserInfo.getLocalId());
                    User newUser = new User();
                    newUser.setLocalId(firebaseUserInfo.getLocalId());
                    newUser.setStatus(UserStatus.ACTIVE); // Default status
                    newUser.setCreatedAt(firebaseUserInfo.getCreatedAt() != null ? 
                        firebaseUserInfo.getCreatedAt() : Instant.now());
                    return newUser;
                });

        // Update fields from Firebase
        user.setEmail(firebaseUserInfo.getEmail());
        user.setEmailVerified(firebaseUserInfo.isEmailVerified());
        user.setDisplayName(firebaseUserInfo.getDisplayName());
        user.setPhotoUrl(firebaseUserInfo.getPhotoUrl());
        user.setLastLoginAt(firebaseUserInfo.getLastLoginAt() != null ? 
            firebaseUserInfo.getLastLoginAt() : Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepository.save(user);
    }

    @Override
    public User getUserByLocalId(String localId) {
        return userRepository.findByLocalId(localId)
                .orElseThrow(() -> new RuntimeException("User not found: " + localId));
    }

    @Override
    public UserProfileDTO getUserProfile(String localId) {
        User user = getUserByLocalId(localId);
        return mapToDTO(user);
    }

    @Override
    public User updateProfile(String localId, UpdateProfileRequest request) {
        User user = getUserByLocalId(localId);
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getPhotoUrl() != null) {
            user.setPhotoUrl(request.getPhotoUrl());
        }
        user.setUpdatedAt(Instant.now());
        
        return userRepository.save(user);
    }

    @Override
    public Page<UserProfileDTO> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public User adminUpdateUser(String localId, AdminUpdateUserRequest request) {
        User user = getUserByLocalId(localId);
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getPhotoUrl() != null) {
            user.setPhotoUrl(request.getPhotoUrl());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setUpdatedAt(Instant.now());
        
        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(String localId, UserStatus status) {
        User user = getUserByLocalId(localId);
        user.setStatus(status);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    private UserProfileDTO mapToDTO(User user) {
        return UserProfileDTO.builder()
                .localId(user.getLocalId())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .displayName(user.getDisplayName())
                .photoUrl(user.getPhotoUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
