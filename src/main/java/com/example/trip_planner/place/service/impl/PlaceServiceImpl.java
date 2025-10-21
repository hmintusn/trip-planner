package com.example.trip_planner.place.service.impl;

import com.example.trip_planner.place.dto.PlaceDetailResponse;
import com.example.trip_planner.place.dto.PlaceFeedResponse;
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
import java.util.*;

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
}
