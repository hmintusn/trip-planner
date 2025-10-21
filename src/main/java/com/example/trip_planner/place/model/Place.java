package com.example.trip_planner.place.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Place entity stored in MongoDB
 * The _id field maps to the Google Place ID for easy lookups
 */
@Document(collection = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
    @CompoundIndex(name = "province_category_score", def = "{'provinceId': 1, 'category': 1, 'score': -1}"),
    @CompoundIndex(name = "category_score", def = "{'category': 1, 'score': -1}")
})
public class Place {
    
    @Id
    @JsonProperty("_id")
    private String id; // Google Place ID (e.g., "ChIJU42tIrB-NjERhcIxUnHsSIs")
    
    private String name;
    private String formattedAddress;
    private List<String> types;
    
    @GeoSpatialIndexed
    private Location location; // GeoJSON Point for geospatial queries
    
    private Double rating;
    private Integer userRatingCount;
    private RegularOpeningHours regularOpeningHours;
    
    @Indexed
    private Integer provinceId;
    private String provinceName;
    
    @Indexed
    private String category; // attraction, hotel, restaurant, entertainment
    
    private Integer priceLevel;
    private String thumbnail;
    private String summary;
    
    @Indexed
    private Double score; // Computed score for ranking
    
    private Instant createdAt;
    private Instant updatedAt;
}
