package com.example.trip_planner.firebase;

import com.example.trip_planner.auth.dto.FirebaseUserInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

/**
 * Service to call Firebase Identity Toolkit REST API (accounts:lookup)
 * to retrieve user information from Firebase by idToken
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseUserInfoService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${firebase.api-key:}")
    private String apiKey;

    /**
     * Lookup Firebase user by idToken
     * Calls POST https://identitytoolkit.googleapis.com/v1/accounts:lookup
     */
    public FirebaseUserInfo lookupByIdToken(String idToken) throws Exception {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("idToken is required");
        }
        
        String url = String.format("https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=%s", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String body = objectMapper.writeValueAsString(new java.util.HashMap<String, String>() {{ 
            put("idToken", idToken); 
        }});
        
        HttpEntity<String> req = new HttpEntity<>(body, headers);
        String resp = restTemplate.postForObject(url, req, String.class);
        
        if (resp == null) {
            throw new RuntimeException("Firebase API returned null response");
        }
        
        JsonNode root = objectMapper.readTree(resp);
        JsonNode users = root.path("users");
        
        if (!users.isArray() || users.size() == 0) {
            throw new RuntimeException("User not found in Firebase");
        }
        
        JsonNode u = users.get(0);
        FirebaseUserInfo dto = new FirebaseUserInfo();
        dto.setLocalId(u.path("localId").asText(null));
        dto.setEmail(u.path("email").asText(null));
        dto.setEmailVerified(u.path("emailVerified").asBoolean(false));
        dto.setDisplayName(u.path("displayName").asText(null));
        dto.setPhotoUrl(u.path("photoUrl").asText(null));
        
        // Parse createdAt & lastLoginAt (epoch millis as string)
        try {
            if (u.has("createdAt")) {
                String created = u.get("createdAt").asText();
                long ms = Long.parseLong(created);
                dto.setCreatedAt(Instant.ofEpochMilli(ms));
            }
        } catch (Exception e) {
            log.warn("Failed to parse createdAt: {}", e.getMessage());
        }
        
        try {
            if (u.has("lastLoginAt")) {
                String last = u.get("lastLoginAt").asText();
                long ms = Long.parseLong(last);
                dto.setLastLoginAt(Instant.ofEpochMilli(ms));
            }
        } catch (Exception e) {
            log.warn("Failed to parse lastLoginAt: {}", e.getMessage());
        }
        
        log.info("Successfully retrieved Firebase user info for localId: {}", dto.getLocalId());
        return dto;
    }
}
