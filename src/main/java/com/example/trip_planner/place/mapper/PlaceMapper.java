package com.example.trip_planner.place.mapper;

import com.example.trip_planner.place.dto.*;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.model.RegularOpeningHours;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * Mapper utility for converting between entities and DTOs
 */
public class PlaceMapper {
    
    /**
     * Convert Place entity to feed response DTO
     */
    public static PlaceFeedResponse toFeedResponse(Place place) {
        if (place == null) return null;
        
        return PlaceFeedResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .formattedAddress(place.getFormattedAddress())
                .rating(place.getRating())
                .userRatingCount(place.getUserRatingCount())
                .thumbnail(place.getThumbnail())
                .category(place.getCategory())
                .summary(place.getSummary())
                .provinceId(place.getProvinceId())
                .provinceName(place.getProvinceName())
                .score(place.getScore())
                .location(toLocationDTO(place.getLocation()))
                .types(place.getTypes())
                .build();
    }
    
    /**
     * Convert Place entity to detail response DTO
     */
    public static PlaceDetailResponse toDetailResponse(Place place) {
        if (place == null) return null;
        
        return PlaceDetailResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .formattedAddress(place.getFormattedAddress())
                .types(place.getTypes())
                .location(toLocationDTO(place.getLocation()))
                .rating(place.getRating())
                .userRatingCount(place.getUserRatingCount())
                .regularOpeningHours(toRegularOpeningHoursDTO(place.getRegularOpeningHours()))
                .provinceId(place.getProvinceId())
                .provinceName(place.getProvinceName())
                .category(place.getCategory())
                .priceLevel(place.getPriceLevel())
                .thumbnail(place.getThumbnail())
                .summary(place.getSummary())
                .score(place.getScore())
                .createdAt(place.getCreatedAt())
                .updatedAt(place.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert GeoJsonPoint to LocationDTO
     */
    public static LocationDTO toLocationDTO(GeoJsonPoint location) {
        if (location == null) return null;
        return new LocationDTO(location.getY(), location.getX()); // GeoJsonPoint: X=longitude, Y=latitude
    }
    
    /**
     * Convert RegularOpeningHours to DTO
     */
    public static RegularOpeningHoursDTO toRegularOpeningHoursDTO(RegularOpeningHours hours) {
        if (hours == null) return null;
        return new RegularOpeningHoursDTO(hours.getWeekdayDescriptions());
    }
}
