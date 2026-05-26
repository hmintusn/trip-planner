package com.example.trip_planner.clustering.service;

import com.example.trip_planner.clustering.model.PlacePoint;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.repository.PlaceRepository;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ClusterOptimizationService {

    @Autowired
    private PlaceRepository placeRepository;

    /**
     * Find restaurants for each cluster and optimize cluster order
     */
    public List<ClusterWithRestaurant> optimizeClusters(
            List<CentroidCluster<PlacePoint>> clusters) {

        // Find restaurants for each cluster
        List<ClusterWithRestaurant> clustersWithRestaurants = new ArrayList<>();
        
        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<PlacePoint> cluster = clusters.get(i);
            
            // Calculate centroid
            double[] centroid = computeCentroid(cluster);
            double lat = convertYToLat(centroid[1]);
            double lon = convertXToLon(centroid[0], lat);
            
            // Find nearby restaurants (2km radius)
            List<Place> nearbyRestaurants = placeRepository.findNearbyRestaurants(lat, lon, 5000.0);
            
            // Select best by rating
            Place bestRestaurant = nearbyRestaurants.stream()
                    .max(Comparator.comparingDouble(p -> p.getScore() != null ? p.getScore() : 0.0))
                    .orElse(null);
            
            clustersWithRestaurants.add(new ClusterWithRestaurant(i, cluster.getPoints(), bestRestaurant, centroid));
        }

        // Optimize order using nearest neighbor
        return optimizeOrder(clustersWithRestaurants);
    }

    /**
     * Compute centroid of a cluster
     */
    private double[] computeCentroid(CentroidCluster<PlacePoint> cluster) {
        List<PlacePoint> points = cluster.getPoints();
        if (points.isEmpty()) return new double[]{0, 0};

        double sumX = 0, sumY = 0;
        for (PlacePoint p : points) {
            sumX += p.getPoint()[0];
            sumY += p.getPoint()[1];
        }
        return new double[]{sumX / points.size(), sumY / points.size()};
    }

    /**
     * Convert Y coordinate (meters) back to latitude
     */
    private double convertYToLat(double y) {
        return Math.toDegrees(y / 6371000.0);
    }

    /**
     * Convert X coordinate (meters) back to longitude
     */
    private double convertXToLon(double x, double lat) {
        return Math.toDegrees(x / (6371000.0 * Math.cos(Math.toRadians(lat))));
    }

    /**
     * Optimize cluster order using greedy nearest neighbor
     */
    private List<ClusterWithRestaurant> optimizeOrder(List<ClusterWithRestaurant> clusters) {
        if (clusters.size() <= 1) return clusters;

        List<ClusterWithRestaurant> ordered = new ArrayList<>();
        List<ClusterWithRestaurant> remaining = new ArrayList<>(clusters);

        // Start with first cluster
        ordered.add(remaining.remove(0));

        // Greedy nearest neighbor
        while (!remaining.isEmpty()) {
            ClusterWithRestaurant last = ordered.get(ordered.size() - 1);
            double[] lastCentroid = last.getCentroid();

            ClusterWithRestaurant nearest = remaining.stream()
                    .min(Comparator.comparingDouble(c -> 
                            euclideanDistance(lastCentroid, c.getCentroid())))
                    .orElse(remaining.get(0));

            ordered.add(nearest);
            remaining.remove(nearest);
        }

        return ordered;
    }

    /**
     * Calculate Euclidean distance between two points
     */
    private double euclideanDistance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        return Math.sqrt(dx * dx + dy * dy);
    }
}
