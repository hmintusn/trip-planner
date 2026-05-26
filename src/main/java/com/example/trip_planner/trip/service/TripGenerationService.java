package com.example.trip_planner.trip.service;

import com.example.trip_planner.clustering.dto.GenerateTripRequest;
import com.example.trip_planner.clustering.dto.GeneratedActivityDTO;
import com.example.trip_planner.clustering.dto.GeneratedDayDTO;
import com.example.trip_planner.clustering.dto.GeneratedTripDTO;
import com.example.trip_planner.clustering.dto.OptimizedRouteResult;
import com.example.trip_planner.clustering.model.PlacePoint;
import com.example.trip_planner.clustering.service.ClusterOptimizationService;
import com.example.trip_planner.clustering.service.ClusterService;
import com.example.trip_planner.clustering.service.ClusterWithRestaurant;
import com.example.trip_planner.clustering.service.MapboxService;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.repository.PlaceRepository;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TripGenerationService {

    private static final Logger log = LoggerFactory.getLogger(TripGenerationService.class);

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClusterOptimizationService optimizationService;

    @Autowired
    private MapboxService mapboxService;

    /**
     * Generate a complete trip from heritage place IDs
     * NOTE: This only generates and returns the trip structure - does NOT save to database
     * User will edit the generated trip before saving
     */
    public GeneratedTripDTO generateTrip(GenerateTripRequest request) {
        log.info("Generating trip: {} days, {} places", request.getDays(), request.getHeritagePlaceIds().size());
        
        try {
            // 1. Fetch heritage places from database
            List<Place> heritagePlaces = placeRepository.findAllById(request.getHeritagePlaceIds());
            log.info("Found {} heritage places in database", heritagePlaces.size());
            
            if (heritagePlaces.size() < request.getDays() * 2) {
                throw new IllegalArgumentException(
                    "Not enough heritage places. Need at least " + (request.getDays() * 2) + 
                    " places for " + request.getDays() + " days. Found: " + heritagePlaces.size());
            }

            // 2. Convert to PlacePoints for clustering
            List<PlacePoint> placePoints = heritagePlaces.stream()
                    .map(p -> new PlacePoint(p.getId(), p.getLocation().getY(), p.getLocation().getX()))
                    .collect(Collectors.toList());
            log.info("Converted to {} place points", placePoints.size());

            // 3. Cluster places by days (K = days * 2, but actual K is calculated inside)
            // We pass days parameter, ClusterService will compute K = days * 2
            List<CentroidCluster<PlacePoint>> clusters = clusterService.cluster(
                    placePoints, request.getDays(), 100, 10);
            log.info("Created {} clusters", clusters.size());

            // 4. Find restaurants and optimize cluster order
            List<ClusterWithRestaurant> optimizedClusters = optimizationService.optimizeClusters(clusters);
            log.info("Optimized cluster order with {} clusters", optimizedClusters.size());

            // 5. Build trip structure (does NOT save to database)
            GeneratedTripDTO trip = buildTripStructure(request, optimizedClusters, heritagePlaces);
            log.info("Generated trip with {} days", trip.getDays().size());
            
            return trip;
        } catch (Exception e) {
            log.error("Error generating trip: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate trip: " + e.getMessage(), e);
        }
    }

    /**
     * Build complete trip structure with days and activities
     */
    private GeneratedTripDTO buildTripStructure(
            GenerateTripRequest request,
            List<ClusterWithRestaurant> orderedClusters,
            List<Place> allPlaces) {

        GeneratedTripDTO trip = new GeneratedTripDTO();
        trip.setName(request.getTripName());
        trip.setDescription(request.getDescription() != null ? request.getDescription() : "Auto-generated optimized trip");
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setVisibility(request.getVisibility() != null ? request.getVisibility() : "PRIVATE");

        List<GeneratedDayDTO> days = new ArrayList<>();
        LocalDate currentDate = request.getStartDate();

        // Map place IDs to Place objects for quick lookup
        Map<String, Place> placeMap = allPlaces.stream()
                .collect(Collectors.toMap(Place::getId, p -> p));
        
        // Find best hotel near the first heritage place
        Place bestHotel = null;
        if (!allPlaces.isEmpty()) {
            Place firstPlace = allPlaces.get(0);
            double lat = firstPlace.getLocation().getY();
            double lon = firstPlace.getLocation().getX();
            List<Place> nearbyHotels = placeRepository.findNearbyHotels(lat, lon, 5000.0); // 5km radius
            
            // Select best by rating
            bestHotel = nearbyHotels.stream()
                    .max(Comparator.comparingDouble(p -> p.getScore() != null ? p.getScore() : 0.0))
                    .orElse(null);
            log.info("Found best hotel: {}", bestHotel != null ? bestHotel.getName() : "None");
        }

        // Assign 2 clusters per day (morning + afternoon)
        // Each day will have: Morning heritage sites → Lunch restaurant → Afternoon heritage sites → Dinner restaurant
        final Place hotelForActivities = bestHotel; // Make effectively final for lambda
        for (int i = 0; i < orderedClusters.size(); i += 2) {
            GeneratedDayDTO day = new GeneratedDayDTO();
            day.setDayDate(currentDate);
            day.setOrderIndex(i / 2);
            day.setNotes("Day " + (i / 2 + 1) + " - Morning & Afternoon");

            List<GeneratedActivityDTO> activities = new ArrayList<>();
            int activityOrder = 0;
            
            // Add hotel as first activity on first day only
            if (i == 0 && hotelForActivities != null) {
                GeneratedActivityDTO hotelActivity = new GeneratedActivityDTO();
                hotelActivity.setPlaceId(hotelForActivities.getId());
                hotelActivity.setPlaceName(hotelForActivities.getName());
                hotelActivity.setPlaceType("hotel");
                hotelActivity.setSession("MORNING");
                hotelActivity.setTravelDurationMinutes(null); // Starting point, no travel
                hotelActivity.setStartTime(LocalTime.of(8, 0).toString());
                hotelActivity.setEndTime(LocalTime.of(9, 0).toString());
                hotelActivity.setOrderIndex(activityOrder++);
                hotelActivity.setNotes("Hotel check-in / Start point");
                activities.add(hotelActivity);
                log.info("Added hotel activity: {}", hotelForActivities.getName());
            }

            // Morning session (cluster i) - ends with LUNCH restaurant
            if (i < orderedClusters.size()) {
                ClusterWithRestaurant morningCluster = orderedClusters.get(i);
                Place lunchRestaurant = morningCluster.getRestaurant();
                
                // Get hotel location as starting point (use best hotel if found, otherwise request or default)
                double[] hotelLocation = hotelForActivities != null 
                    ? new double[]{hotelForActivities.getLocation().getX(), hotelForActivities.getLocation().getY()}
                        : new double[]{105.8650816, 20.2785788}; // Default location
                
                activities.addAll(createActivitiesForCluster(
                        morningCluster, 
                        LocalTime.of(9, 0), 
                        activityOrder,
                        placeMap,
                        true,  // isMorning = true → adds lunch restaurant
                        "MORNING",  // Session indicator
                        hotelLocation,  // Start from hotel
                        lunchRestaurant != null ? new double[]{
                            lunchRestaurant.getLocation().getX(),
                            lunchRestaurant.getLocation().getY()
                        } : null  // End at lunch restaurant
                ));
                activityOrder += morningCluster.getPlaces().size() + 1; // +1 for restaurant
            }

            // Afternoon session (cluster i+1) - ends with DINNER restaurant
            if (i + 1 < orderedClusters.size()) {
                ClusterWithRestaurant afternoonCluster = orderedClusters.get(i + 1);
                Place dinnerRestaurant = afternoonCluster.getRestaurant();
                
                // Get lunch restaurant location as starting point
                Place lunchRestaurant = i < orderedClusters.size() ? orderedClusters.get(i).getRestaurant() : null;
                double[] lunchLocation = lunchRestaurant != null 
                    ? new double[]{lunchRestaurant.getLocation().getX(), lunchRestaurant.getLocation().getY()}
                    : new double[]{105.8650816, 20.2785788};
                
                activities.addAll(createActivitiesForCluster(
                        afternoonCluster, 
                        LocalTime.of(14, 0), 
                        activityOrder,
                        placeMap,
                        false,  // isMorning = false → adds dinner restaurant
                        "AFTERNOON",  // Session indicator
                        lunchLocation,  // Start from lunch restaurant
                        dinnerRestaurant != null ? new double[]{
                            dinnerRestaurant.getLocation().getX(),
                            dinnerRestaurant.getLocation().getY()
                        } : null  // End at dinner restaurant
                ));
            }

            day.setActivities(activities);
            days.add(day);
            currentDate = currentDate.plusDays(1);
        }

        trip.setDays(days);
        return trip;
    }

    /**
     * Create activities for a single cluster (heritage sites + restaurant)
     * Each cluster adds heritage sites followed by either:
     * - Lunch restaurant (1h) if isMorning=true
     * - Dinner restaurant (1.5h) if isMorning=false
     * Uses Mapbox to optimize the visiting order
     */
    private List<GeneratedActivityDTO> createActivitiesForCluster(
            ClusterWithRestaurant cluster,
            LocalTime startTime,
            int startOrder,
            Map<String, Place> placeMap,
            boolean isMorning,
            String session,  // "MORNING" or "AFTERNOON"
            double[] startLocation,  // Starting point (hotel or lunch restaurant)
            double[] endLocation) {  // Ending point (lunch or dinner restaurant)

        List<GeneratedActivityDTO> activities = new ArrayList<>();
        LocalTime currentTime = startTime;

        // Get heritage places from cluster
        List<Place> heritagePlaces = cluster.getPlaces().stream()
                .map(point -> placeMap.get(point.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Optimize route using Mapbox
        OptimizedRouteResult routeResult = mapboxService.optimizeRoute(startLocation, heritagePlaces, endLocation);
        List<Place> optimizedPlaces = routeResult.getPlaces();
        List<Double> legDurations = routeResult.getLegDurations();
        log.debug("Optimized {} places using Mapbox for {} session with {} leg durations", 
                  optimizedPlaces.size(), isMorning ? "morning" : "afternoon", legDurations.size());

        // Add heritage sites in optimized order (1.5 hours each + actual travel time)
        for (int i = 0; i < optimizedPlaces.size(); i++) {
            Place place = optimizedPlaces.get(i);

            // Get travel duration to this place (legDurations[i] is from previous to current)
            Double travelMinutes = i < legDurations.size() ? legDurations.get(i) : null;
            
            // Add travel time to reach this place
            if (travelMinutes != null) {
                currentTime = currentTime.plusMinutes((long) Math.ceil(travelMinutes));
            }

            GeneratedActivityDTO activity = new GeneratedActivityDTO();
            activity.setPlaceId(place.getId());
            activity.setPlaceName(place.getName());
            activity.setPlaceType("heritage_site");
            activity.setSession(session);
            activity.setTravelDurationMinutes(travelMinutes);
            activity.setStartTime(currentTime.toString());
            currentTime = currentTime.plusMinutes(90); // 1.5 hours visit
            activity.setEndTime(currentTime.toString());
            activity.setOrderIndex(startOrder++);
            String travelNote = travelMinutes != null 
                ? String.format("Travel from previous location: %.1f min | Visit: 1.5 hours", travelMinutes)
                : "Visit duration: 1.5 hours";
            activity.setNotes(travelNote);

            activities.add(activity);
        }

        // Add restaurant at the end
        if (cluster.getRestaurant() != null) {
            // Travel duration from last heritage site to restaurant
            // legDurations has: [start->place0, place0->place1, ..., lastPlace->restaurant]
            Double travelToRestaurant = null;
            if (legDurations.size() > optimizedPlaces.size()) {
                travelToRestaurant = legDurations.get(optimizedPlaces.size());
            }
            
            // Add travel time to reach restaurant
            if (travelToRestaurant != null) {
                currentTime = currentTime.plusMinutes((long) Math.ceil(travelToRestaurant));
            }

            GeneratedActivityDTO meal = new GeneratedActivityDTO();
            meal.setPlaceId(cluster.getRestaurant().getId());
            meal.setPlaceName(cluster.getRestaurant().getName());
            meal.setPlaceType("restaurant");
            meal.setSession(session);
            meal.setTravelDurationMinutes(travelToRestaurant);
            meal.setStartTime(currentTime.toString());
            currentTime = currentTime.plusMinutes(isMorning ? 60 : 90); // 1h lunch / 1.5h dinner
            meal.setEndTime(currentTime.toString());
            meal.setOrderIndex(startOrder);
            String mealType = isMorning ? "Lunch break" : "Dinner";
            String mealNote = travelToRestaurant != null 
                ? String.format("Travel from previous location: %.1f min | %s", travelToRestaurant, mealType)
                : mealType;
            meal.setNotes(mealNote);

            activities.add(meal);
        }

        return activities;
    }
}
