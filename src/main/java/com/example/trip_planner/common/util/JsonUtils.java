package com.example.trip_planner.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * Utility class for JSON operations
 */
@Slf4j
public final class JsonUtils {
    
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    private JsonUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Convert object to JSON string
     */
    public static String toJson(Object object) {
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON", e);
            throw new RuntimeException("JSON conversion failed", e);
        }
    }
    
    /**
     * Convert JSON string to object with Type
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        try {
            return GSON.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON with type: {}", json, e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }
}
