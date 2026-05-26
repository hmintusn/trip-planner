package com.example.trip_planner.firebase;

import com.example.trip_planner.common.constants.RedisKeys;
import com.example.trip_planner.common.util.JsonUtils;
import com.example.trip_planner.common.util.RedisUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service for fetching and caching Firebase JWKS (JSON Web Key Sets)
 */
@Slf4j
@Service
public class FirebaseJwksService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private final String jwksUrl;
    private final long defaultTtlSeconds;
    
    public FirebaseJwksService(
            RedisTemplate<String, String> redisTemplate,
            @Value("${firebase.jwks-url}") String jwksUrl,
            @Value("${firebase.cache-ttl-seconds}") long defaultTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = new RestTemplate();
        this.jwksUrl = jwksUrl;
        this.defaultTtlSeconds = defaultTtlSeconds;
    }
    
    /**
     * Get JWKS from cache or fetch from Firebase if not cached or expired
     */
    public Map<String, Object> getJwks() {
        try {
            // Try to get from cache first
            String cachedJwks = RedisUtils.get(redisTemplate, RedisKeys.FIREBASE_JWKS);
            if (cachedJwks != null) {
                // parse as Map<String, Object> because JWKS contains nested structures (arrays/objects)
                Map<String, Object> parsed = JsonUtils.fromJson(cachedJwks, new TypeReference<Map<String, Object>>() {});
                return parsed;
            }
            // Fetch from Firebase
            return fetchAndCacheJwks();
            
        } catch (Exception e) {
            log.error("Failed to get JWKS", e);
            throw new RuntimeException("JWKS retrieval failed", e);
        }
    }
    
    /**
     * Force refresh JWKS from Firebase
     */
    public Map<String, Object> refreshJwks() {
        log.info("Force refreshing JWKS from Firebase");
        return fetchAndCacheJwks();
    }
    
    /**
     * Fetch JWKS from Firebase and cache it
     */
    private Map<String, Object> fetchAndCacheJwks() {
        try {
            log.info("Fetching JWKS from Firebase: {}", jwksUrl);
            
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            jwksUrl,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {
            }
        );
        Map<String, Object> jwks = (Map<String, Object>) response.getBody();
            if (jwks == null || jwks.isEmpty()) {
                throw new RuntimeException("Received empty JWKS from Firebase");
            }
            
            // Extract TTL from Cache-Control header
            String cacheControl = response.getHeaders().getFirst("Cache-Control");
            String etag = response.getHeaders().getFirst("ETag");
            long ttlSeconds = extractTtlFromCacheControl(cacheControl);
            
            // Cache the JWKS
            cacheJwks(jwks, ttlSeconds, cacheControl, etag);
            
            log.info("Successfully fetched and cached {} JWKS keys", jwks.size());
            return jwks;
            
        } catch (Exception e) {
            log.error("Failed to fetch JWKS from Firebase", e);
            throw new RuntimeException("JWKS fetch failed", e);
        }
    }
    
    /**
     * Cache JWKS in Redis
     */
    private void cacheJwks(Map<String, Object> jwks, long ttlSeconds, String cacheControl, String etag) {
        try {
            // Cache the JWKS
            String jwksJson = JsonUtils.toJson(jwks);
            RedisUtils.setWithTTL(redisTemplate, RedisKeys.FIREBASE_JWKS, jwksJson, ttlSeconds);
            
            log.debug("Cached JWKS with TTL: {} seconds", ttlSeconds);
            
        } catch (Exception e) {
            log.error("Failed to cache JWKS", e);
            throw new RuntimeException("JWKS caching failed", e);
        }
    }
    
    /**
     * Extract TTL from Cache-Control header
     * Example: "public, max-age=3600" -> 3600 seconds
     */
    private long extractTtlFromCacheControl(String cacheControl) {
        if (cacheControl == null || cacheControl.isEmpty()) {
            return defaultTtlSeconds;
        }
        
        try {
            // Look for max-age directive
            String[] directives = cacheControl.split(",");
            for (String directive : directives) {
                directive = directive.trim();
                if (directive.startsWith("max-age=")) {
                    String maxAge = directive.substring("max-age=".length()).trim();
                    return Long.parseLong(maxAge);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse Cache-Control header: {}", cacheControl, e);
        }
        
        return defaultTtlSeconds;
    }
    
    /**
     * Clear JWKS cache
     */
    public void clearCache() {
        try {
            RedisUtils.delete(redisTemplate, RedisKeys.FIREBASE_JWKS);
            log.info("Cleared JWKS cache");
        } catch (Exception e) {
            log.error("Failed to clear JWKS cache", e);
            throw new RuntimeException("Cache clearing failed", e);
        }
    }
}
