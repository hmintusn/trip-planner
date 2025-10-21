package com.example.trip_planner.place.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GeoJSON Point for MongoDB geospatial queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String type = "Point"; // GeoJSON type
    private double[] coordinates; // [longitude, latitude]
    
    public Location(double longitude, double latitude) {
        this.type = "Point";
        this.coordinates = new double[]{longitude, latitude};
    }
    
    public double getLat() {
        return coordinates != null && coordinates.length > 1 ? coordinates[1] : 0;
    }
    
    public double getLng() {
        return coordinates != null && coordinates.length > 0 ? coordinates[0] : 0;
    }
    
    public void setLat(double lat) {
        if (coordinates == null || coordinates.length < 2) {
            coordinates = new double[2];
        }
        coordinates[1] = lat;
    }
    
    public void setLng(double lng) {
        if (coordinates == null || coordinates.length < 2) {
            coordinates = new double[2];
        }
        coordinates[0] = lng;
    }
}
