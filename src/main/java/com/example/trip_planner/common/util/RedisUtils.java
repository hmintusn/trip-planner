package com.example.trip_planner.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for Redis operations
 */
@Slf4j
public final class RedisUtils {
    
    private RedisUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Set value in Redis with TTL
     */
    public static <T> void setWithTTL(RedisTemplate<String, T> redisTemplate, 
                                     String key, 
                                     T value, 
                                     long ttlSeconds) {
        try {
            ValueOperations<String, T> ops = redisTemplate.opsForValue();
            ops.set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Set Redis key: {} with TTL: {} seconds", key, ttlSeconds);
        } catch (Exception e) {
            log.error("Failed to set Redis key: {}", key, e);
            // Don't throw exception, just log it to prevent cache failures from breaking the application
        }
    }
    
    /**
     * Set value in Redis with Duration
     */
    public static <T> void setWithDuration(RedisTemplate<String, T> redisTemplate, 
                                          String key, 
                                          T value, 
                                          Duration duration) {
        try {
            ValueOperations<String, T> ops = redisTemplate.opsForValue();
            ops.set(key, value, duration);
            log.debug("Set Redis key: {} with duration: {}", key, duration);
        } catch (Exception e) {
            log.error("Failed to set Redis key: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
    
    /**
     * Get value from Redis
     */
    public static <T> T get(RedisTemplate<String, T> redisTemplate, String key) {
        try {
            ValueOperations<String, T> ops = redisTemplate.opsForValue();
            T value = ops.get(key);
            log.debug("Retrieved Redis key: {}, found: {}", key, value != null);
            return value;
        } catch (Exception e) {
            log.error("Failed to get Redis key: {}", key, e);
            // Return null instead of throwing to prevent cache failures from breaking the application
            return null;
        }
    }
    
    /**
     * Delete key from Redis
     */
    public static void delete(RedisTemplate<String, ?> redisTemplate, String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Deleted Redis key: {}, success: {}", key, deleted);
        } catch (Exception e) {
            log.error("Failed to delete Redis key: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
    
    /**
     * Check if key exists in Redis
     */
    public static boolean exists(RedisTemplate<String, ?> redisTemplate, String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            log.debug("Checked Redis key existence: {}, exists: {}", key, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check Redis key existence: {}", key, e);
            throw new RuntimeException("Redis operation failed", e);
        }
    }
}
