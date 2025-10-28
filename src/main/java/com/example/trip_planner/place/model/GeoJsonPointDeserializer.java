package com.example.trip_planner.place.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.io.IOException;

/**
 * Custom Jackson deserializer for GeoJSON Point format to GeoJsonPoint
 */
public class GeoJsonPointDeserializer extends JsonDeserializer<GeoJsonPoint> {

    @Override
    public GeoJsonPoint deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isNull()) {
            return null;
        }

        // Check if it's already a GeoJsonPoint format (shouldn't happen but safety check)
        if (node.has("x") && node.has("y")) {
            double x = node.get("x").asDouble();
            double y = node.get("y").asDouble();
            return new GeoJsonPoint(x, y);
        }

        // Handle GeoJSON format: {"type": "Point", "coordinates": [lng, lat]}
        if (node.has("coordinates") && node.get("coordinates").isArray()) {
            JsonNode coords = node.get("coordinates");
            if (coords.size() >= 2) {
                double lng = coords.get(0).asDouble();
                double lat = coords.get(1).asDouble();
                return new GeoJsonPoint(lng, lat);
            }
        }

        throw new IOException("Invalid GeoJSON Point format");
    }
}