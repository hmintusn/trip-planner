package com.example.trip_planner.place.service.impl;

import com.example.trip_planner.place.dto.PlaceDetailsResponse;
import com.example.trip_planner.place.mapper.PlaceDetailsMapper;
import com.example.trip_planner.place.model.PlaceDetails;
import com.example.trip_planner.place.repository.PlaceDetailsRepository;
import com.example.trip_planner.place.service.PlaceDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of PlaceDetailsService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceDetailsServiceImpl implements PlaceDetailsService {

    private final PlaceDetailsRepository placeDetailsRepository;

    @Override
    public PlaceDetailsResponse getPlaceDetailsById(String id) {
        log.debug("Getting place details for ID: {}", id);

        PlaceDetails placeDetails = placeDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place details not found for ID: " + id));

        return PlaceDetailsMapper.toResponse(placeDetails);
    }

    @Override
    public PlaceDetailsResponse createPlaceDetails(PlaceDetails placeDetails) {
        log.info("Creating place details for ID: {}", placeDetails.getId());

        placeDetails.setCreatedAt(Instant.now());
        placeDetails.setUpdatedAt(Instant.now());

        PlaceDetails saved = placeDetailsRepository.save(placeDetails);
        return PlaceDetailsMapper.toResponse(saved);
    }

    @Override
    public PlaceDetailsResponse updatePlaceDetails(PlaceDetails placeDetails) {
        log.info("Updating place details for ID: {}", placeDetails.getId());

        // Check if exists
        if (!placeDetailsRepository.existsById(placeDetails.getId())) {
            throw new RuntimeException("Place details not found for ID: " + placeDetails.getId());
        }

        placeDetails.setUpdatedAt(Instant.now());
        PlaceDetails saved = placeDetailsRepository.save(placeDetails);
        return PlaceDetailsMapper.toResponse(saved);
    }

    @Override
    public void deletePlaceDetails(String id) {
        log.info("Deleting place details for ID: {}", id);

        if (!placeDetailsRepository.existsById(id)) {
            throw new RuntimeException("Place details not found for ID: " + id);
        }

        placeDetailsRepository.deleteById(id);
    }

    @Override
    public Map<String, Integer> importPlaceDetails(List<PlaceDetails> placeDetailsList) {
        log.info("Importing {} place details", placeDetailsList.size());

        int inserted = 0;
        int updated = 0;
        Instant now = Instant.now();

        for (PlaceDetails placeDetails : placeDetailsList) {
            // Check if exists
            boolean exists = placeDetailsRepository.existsById(placeDetails.getId());

            // Set timestamps
            if (!exists) {
                placeDetails.setCreatedAt(now);
                inserted++;
            } else {
                updated++;
            }
            placeDetails.setUpdatedAt(now);

            // Upsert
            placeDetailsRepository.save(placeDetails);
        }

        log.info("Import completed: {} total, {} new, {} updated", placeDetailsList.size(), inserted, updated);

        Map<String, Integer> result = new HashMap<>();
        result.put("total", placeDetailsList.size());
        result.put("inserted", inserted);
        result.put("updated", updated);

        return result;
    }

    @Override
    public List<PlaceDetailsResponse> getPlaceDetailsByProvince(Integer provinceId) {
        log.debug("Getting place details for province: {}", provinceId);

        List<PlaceDetails> placeDetails = placeDetailsRepository.findByProvinceId(provinceId);
        return placeDetails.stream()
                .map(PlaceDetailsMapper::toResponse)
                .collect(Collectors.toList());
    }
}