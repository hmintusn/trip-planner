package com.example.trip_planner.auth.controller;

import com.example.trip_planner.auth.dto.SigninRequest;
import com.example.trip_planner.auth.service.AuthService;
import com.example.trip_planner.user.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth controller for signin
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/signin
     * First sign-in: verify ID Token & sync user info
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequest request) {
        try {
            log.info("POST /api/v1/signin");
            UserProfileDTO profile = authService.signin(request.getIdToken());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            log.error("Signin failed", e);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
