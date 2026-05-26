package com.example.trip_planner.clustering.service;

import com.example.trip_planner.clustering.dto.MapboxOptimizedTripsResponse;
import com.example.trip_planner.clustering.dto.OptimizedRouteResult;
import com.example.trip_planner.common.config.MapboxConfig;
import com.example.trip_planner.place.model.Place;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapboxService {

    private static final Logger log = LoggerFactory.getLogger(MapboxService.class);

    @Autowired
    private MapboxConfig mapboxConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Optimizes route order for a list of places
     * @param startLocation Starting point [longitude, latitude]
     * @param places List of places to visit
     * @param endLocation Ending point [longitude, latitude] (can be null to return to start)
     * @return OptimizedRouteResult with reordered places and travel durations
     */
    public OptimizedRouteResult optimizeRoute(double[] startLocation, List<Place> places, double[] endLocation) {
        if (places == null || places.isEmpty()) {
            log.warn("No places to optimize");
            return new OptimizedRouteResult(places, new ArrayList<>());
        }

        if (places.size() == 1) {
            log.debug("Only one place, no optimization needed");
            List<Double> durations = new ArrayList<>();
            durations.add(30.0); // Default 30 min from start to first place
            if (endLocation != null) {
                durations.add(30.0); // Default 30 min from first place to end
            }
            return new OptimizedRouteResult(places, durations);
        }

        try {
            // Build coordinates string: start;place1;place2;...;end
            StringBuilder coordinates = new StringBuilder();
            
            // Add start location
            coordinates.append(String.format("%.6f,%.6f", startLocation[0], startLocation[1]));
            
            // Add all places (as intermediate waypoints)
            for (Place place : places) {
                coordinates.append(";");
                coordinates.append(String.format("%.6f,%.6f", 
                    place.getLocation().getX(), // longitude
                    place.getLocation().getY()  // latitude
                ));
            }
            
            // Add end location if different from start
            if (endLocation != null) {
                coordinates.append(";");
                coordinates.append(String.format("%.6f,%.6f", endLocation[0], endLocation[1]));
            }

            // Build API URL
            String url = String.format("%s/mapbox/%s/%s?access_token=%s&source=first&destination=%s",
                mapboxConfig.getApiUrl(),
                mapboxConfig.getProfile(),
                coordinates.toString(),
                mapboxConfig.getAccessToken(),
                endLocation != null ? "last" : "first"  // Return to start if no end location
            );

            log.debug("Calling Mapbox API: {}", url.replace(mapboxConfig.getAccessToken(), "***"));

            // Call Mapbox API
            MapboxOptimizedTripsResponse response = restTemplate.getForObject(url, MapboxOptimizedTripsResponse.class);

            if (response == null || !"Ok".equals(response.getCode())) {
                log.error("Mapbox API returned error: {}", response != null ? response.getCode() : "null response");
                // Return original order with default durations if API fails
                List<Double> defaultDurations = new ArrayList<>();
                for (int i = 0; i <= places.size(); i++) {
                    defaultDurations.add(30.0); // Default 30 min per leg
                }
                return new OptimizedRouteResult(places, defaultDurations);
            }

            // Reorder places based on waypoint indices
            List<Place> optimizedPlaces = new ArrayList<>();
            List<MapboxOptimizedTripsResponse.Waypoint> waypoints = response.getWaypoints();
            
            // Skip first waypoint (start location) and last if it's the end location
            int skipLast = endLocation != null ? 1 : 0;
            for (int i = 1; i < waypoints.size() - skipLast; i++) {
                MapboxOptimizedTripsResponse.Waypoint waypoint = waypoints.get(i);
                // waypoint_index refers to the original position in our input
                // We need to map it back to the place (subtract 1 for start location)
                int originalIndex = waypoint.getWaypoint_index() - 1;
                if (originalIndex >= 0 && originalIndex < places.size()) {
                    optimizedPlaces.add(places.get(originalIndex));
                }
            }

            // Extract leg durations from the trip (convert seconds to minutes)
            List<Double> legDurations = new ArrayList<>();
            if (response.getTrips() != null && !response.getTrips().isEmpty()) {
                MapboxOptimizedTripsResponse.Trip trip = response.getTrips().get(0);
                if (trip.getLegs() != null) {
                    for (MapboxOptimizedTripsResponse.Trip.Leg leg : trip.getLegs()) {
                        legDurations.add(leg.getDuration() / 60.0); // Convert seconds to minutes
                    }
                }
            }

            log.info("Optimized route: {} places reordered with {} leg durations", 
                     optimizedPlaces.size(), legDurations.size());
            return new OptimizedRouteResult(optimizedPlaces, legDurations);

        } catch (Exception e) {
            log.error("Error calling Mapbox API: {}", e.getMessage(), e);
            // Return original order with default durations if any error occurs
            List<Double> defaultDurations = new ArrayList<>();
            for (int i = 0; i <= places.size(); i++) {
                defaultDurations.add(30.0); // Default 30 min per leg
            }
            return new OptimizedRouteResult(places, defaultDurations);
        }
    }

    /**
     * Get optimized route duration in seconds
     */
    public double getRouteDuration(double[] startLocation, List<Place> places, double[] endLocation) {
        // For simplicity, return estimated time based on number of places
        // In a real implementation, you'd extract this from the Mapbox response
        int totalPlaces = places.size();
        return totalPlaces * (90 * 60 + 30 * 60); // 1.5h visit + 30min travel per place
    }
}
