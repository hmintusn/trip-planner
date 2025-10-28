package com.example.trip_planner.place.service.impl;

import com.example.trip_planner.place.dto.PlaceDetailResponse;
import com.example.trip_planner.place.dto.PlaceFeedResponse;
import com.example.trip_planner.place.dto.PlaceRecommendationItem;
import com.example.trip_planner.place.dto.PlaceRecommendationRequest;
import com.example.trip_planner.place.dto.PlaceRecommendationResponse;
import com.example.trip_planner.place.mapper.PlaceMapper;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.repository.PlaceRepository;
import com.example.trip_planner.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PlaceService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {
    
    private final PlaceRepository placeRepository;
    
    @Override
    public Page<PlaceFeedResponse> getPlaces(Integer provinceId, String category, int page, int size) {
        log.debug("Getting places - provinceId: {}, category: {}, page: {}", provinceId, category, page);
        
        // Sort by score descending, then by id for deterministic pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score").and(Sort.by("id")));
        
        Page<Place> placePage;
        
        // Apply filters dynamically
        if (provinceId != null && category != null) {
            placePage = placeRepository.findByProvinceIdAndCategory(provinceId, category, pageable);
        } else if (provinceId != null) {
            placePage = placeRepository.findByProvinceId(provinceId, pageable);
        } else if (category != null) {
            placePage = placeRepository.findByCategory(category, pageable);
        } else {
            placePage = placeRepository.findAll(pageable);
        }
        
        return placePage.map(PlaceMapper::toFeedResponse);
    }
    
    @Override
    public PlaceDetailResponse getPlaceById(String id) {
        log.debug("Getting place detail for id: {}", id);
        
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place not found: " + id));
        
        return PlaceMapper.toDetailResponse(place);
    }
    
    @Override
    public PlaceDetailResponse createPlace(Place place) {
        log.info("Creating new place: {}", place.getName());
        
        Instant now = Instant.now();
        place.setCreatedAt(now);
        place.setUpdatedAt(now);
        
        Place savedPlace = placeRepository.save(place);
        
        log.info("Place created successfully with id: {}", savedPlace.getId());
        return PlaceMapper.toDetailResponse(savedPlace);
    }
    
    @Override
    public PlaceDetailResponse updatePlace(Place place) {
        log.info("Updating place with id: {}", place.getId());
        
        // Check if place exists
        Place existingPlace = placeRepository.findById(place.getId())
                .orElseThrow(() -> new RuntimeException("Place not found: " + place.getId()));
        
        // Preserve createdAt, update updatedAt
        place.setCreatedAt(existingPlace.getCreatedAt());
        place.setUpdatedAt(Instant.now());
        
        Place savedPlace = placeRepository.save(place);
        
        log.info("Place updated successfully: {}", savedPlace.getId());
        return PlaceMapper.toDetailResponse(savedPlace);
    }
    
    @Override
    public void deletePlace(String id) {
        log.info("Deleting place with id: {}", id);
        
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place not found: " + id);
        }
        
        placeRepository.deleteById(id);
        
        log.info("Place deleted successfully: {}", id);
    }
    
    @Override
    public Map<String, Integer> importPlaces(List<Place> places) {
        log.info("Importing {} places", places.size());
        
        int inserted = 0;
        int updated = 0;
        Instant now = Instant.now();
        
        for (Place place : places) {
            // Debug: Log the location object type
            if (place.getLocation() != null) {
                log.debug("Place {} location type: {}", place.getName(), place.getLocation().getClass().getName());
                log.debug("Location value: X={}, Y={}", place.getLocation().getX(), place.getLocation().getY());
            }
            
            // Check if place exists
            boolean exists = placeRepository.existsById(place.getId());
            
            // Set timestamps
            if (!exists) {
                place.setCreatedAt(now);
                inserted++;
            } else {
                updated++;
            }
            place.setUpdatedAt(now);
            
            // Upsert (insert or update)
            placeRepository.save(place);
        }
        
        log.info("Import completed: {} total, {} new, {} updated", places.size(), inserted, updated);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("total", places.size());
        result.put("inserted", inserted);
        result.put("updated", updated);
        
        return result;
    }
    
    @Override
    public PlaceRecommendationResponse getPlaceRecommendations(PlaceRecommendationRequest request) {
        log.debug("Getting place recommendations for placeIds: {}, categories: {}, radius: {}", 
                request.getPlaceIds(), request.getCategories(), request.getRadius());
        
        // Validate request
        if (request.getPlaceIds() == null || request.getPlaceIds().isEmpty()) {
            throw new IllegalArgumentException("placeIds cannot be null or empty");
        }
        
        // Get all places by IDs to calculate center point
        List<Place> selectedPlaces = placeRepository.findAllById(request.getPlaceIds());
        if (selectedPlaces.isEmpty()) {
            return PlaceRecommendationResponse.builder()
                    .recommendations(Collections.emptyList())
                    .build();
        }
        
        // Calculate average center point
        double totalLat = 0.0;
        double totalLng = 0.0;
        for (Place place : selectedPlaces) {
            if (place.getLocation() != null) {
                totalLat += place.getLocation().getY(); // GeoJsonPoint: Y = latitude
                totalLng += place.getLocation().getX(); // GeoJsonPoint: X = longitude
            }
        }
        double centerLat = totalLat / selectedPlaces.size();
        double centerLng = totalLng / selectedPlaces.size();
        
        // Default radius is 2000 meters
        double radiusMeters = request.getRadius() != null ? request.getRadius() : 2000.0;
        
        // Query places within radius
        List<Place> nearbyPlaces;
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            nearbyPlaces = placeRepository.findByLocationNearWithCategories(
                    centerLng, centerLat, radiusMeters, request.getCategories());
        } else {
            nearbyPlaces = placeRepository.findByLocationNearAllCategories(
                    centerLng, centerLat, radiusMeters);
        }
        
        // Remove the original selected places from recommendations
        nearbyPlaces = nearbyPlaces.stream()
                .filter(place -> !request.getPlaceIds().contains(place.getId()))
                .collect(Collectors.toList());
        
        // Calculate distances and create recommendation items
        List<PlaceRecommendationItem> recommendations = new ArrayList<>();
        for (Place place : nearbyPlaces) {
            // Calculate distance in meters
            double distance = calculateDistance(centerLat, centerLng, 
                    place.getLocation().getY(), place.getLocation().getX()); // GeoJsonPoint: Y=lat, X=lng
            
            PlaceRecommendationItem item = PlaceRecommendationItem.builder()
                    ._id(place.getId())
                    .name(place.getName())
                    .formattedAddress(place.getFormattedAddress())
                    .types(place.getTypes())
                    .location(PlaceMapper.toLocationDTO(place.getLocation()))
                    .rating(place.getRating())
                    .userRatingCount(place.getUserRatingCount())
                    .regularOpeningHours(PlaceMapper.toRegularOpeningHoursDTO(place.getRegularOpeningHours()))
                    .provinceId(place.getProvinceId())
                    .provinceName(place.getProvinceName())
                    .category(place.getCategory())
                    .priceLevel(place.getPriceLevel())
                    .thumbnail(place.getThumbnail())
                    .summary(place.getSummary())
                    .score(place.getScore())
                    .createdAt(place.getCreatedAt() != null ? 
                            ZonedDateTime.ofInstant(place.getCreatedAt(), ZoneId.systemDefault()) : null)
                    .updatedAt(place.getUpdatedAt() != null ? 
                            ZonedDateTime.ofInstant(place.getUpdatedAt(), ZoneId.systemDefault()) : null)
                    .distanceMeters((int) Math.round(distance))
                    .build();
            
            recommendations.add(item);
        }
        
        // Sort by distance (ascending), then by score (descending)
        recommendations.sort((a, b) -> {
            int distanceCompare = Integer.compare(a.getDistanceMeters(), b.getDistanceMeters());
            if (distanceCompare != 0) {
                return distanceCompare;
            }
            return Double.compare(b.getScore() != null ? b.getScore() : 0.0, 
                                a.getScore() != null ? a.getScore() : 0.0);
        });
        
        return PlaceRecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371000; // meters
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}
