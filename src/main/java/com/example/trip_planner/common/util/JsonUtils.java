package com.example.trip_planner.common.util;

import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;


/**
 * Utility class for JSON operations
 */
@Slf4j
public final class JsonUtils {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());

        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    /**
     * Convert object to JSON string
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to write JSON: {}", object, e);
            throw new RuntimeException("JSON writing failed", e);
        }
    }
    
    /**
     * Deserialize JSON content from given JSON content String.
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", json, e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }

    /**
     * Deserialize JSON content from given InputStream.
     */
    public static <T> T fromJson(InputStream inputStream, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, typeRef);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", inputStream, e);
            throw new RuntimeException("JSON parsing failed", e);
        }
    }
}
