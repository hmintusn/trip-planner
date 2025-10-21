package com.example.trip_planner.common.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

/**
 * Simple health check endpoint for monitoring
 */
@RestController
@RequestMapping("/api/v1/health")
@Slf4j
public class HealthController {

    /**
     * Basic health check endpoint.
     * Used by monitoring tools or for verifying deployment.
     *
     * @return JSON response containing app status and timestamp
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("GET /api/v1/health - Health check requested");

        Map<String, Object> response = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "service", "trip-planner",
                "message", "Service is healthy"
        );

        log.info("Health check OK - {}", response);
        return ResponseEntity.ok(response);
    }
}
