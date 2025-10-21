package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for import operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private int totalPlaces;
    private int insertedPlaces;
    private int updatedPlaces;
    private String message;
    
    public static ImportResponse success(int total, int inserted, int updated) {
        return new ImportResponse(
            total, 
            inserted, 
            updated, 
            String.format("Successfully imported %d places (%d new, %d updated)", total, inserted, updated)
        );
    }
}
