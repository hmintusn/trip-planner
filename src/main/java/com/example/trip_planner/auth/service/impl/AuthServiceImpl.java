package com.example.trip_planner.auth.service.impl;

import com.example.trip_planner.auth.service.AuthService;
import com.example.trip_planner.firebase.FirebaseUserInfoService;
import com.example.trip_planner.auth.dto.FirebaseUserInfo;
import com.example.trip_planner.user.dto.UserProfileDTO;
import com.example.trip_planner.user.model.User;
import com.example.trip_planner.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Auth service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final FirebaseUserInfoService firebaseUserInfoService;
    private final UserService userService;

    @Override
    public UserProfileDTO signin(String idToken) throws Exception {
        log.info("Processing signin with Firebase idToken");
        
        // Verify token and get user info from Firebase
        FirebaseUserInfo firebaseUserInfo = firebaseUserInfoService.lookupByIdToken(idToken);
        
        // Sync/upsert user in our DB
        User user = userService.syncFromFirebase(firebaseUserInfo);
        
        log.info("User signed in successfully: {}", user.getLocalId());
        
        // Return user profile
        return userService.getUserProfile(user.getLocalId());
    }
}
