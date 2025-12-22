package com.example.trip_planner.clustering;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClusterService {

    public List<CentroidCluster<PlacePoint>> cluster(
            List<PlacePoint> places,
            int days,
            int maxIter,
            int runs
    ) {
        int K = Math.min(places.size(), days * 2); 

        // Convert lat/lon → meters
        GeoUtils.projectToMeters(places);

        double bestScore = Double.POSITIVE_INFINITY;
        List<CentroidCluster<PlacePoint>> bestClusters = null;

        // Commons Math random generator
        RandomGenerator rnd = new JDKRandomGenerator();
        rnd.setSeed(System.currentTimeMillis());

        for (int run = 0; run < runs; run++) {

            KMeansPlusPlusClusterer<PlacePoint> clusterer =
                    new KMeansPlusPlusClusterer<>(
                            K,
                            maxIter,
                            new EuclideanDistance(),
                            rnd
                    );

            List<CentroidCluster<PlacePoint>> clusters = clusterer.cluster(places);
            
            // Balance the clusters by redistributing points
            balanceClusters(clusters);

            double inertia = computeInertia(clusters);
            double sizePenalty = computeSizeVariancePenalty(clusters);
            double score = inertia + sizePenalty * 100000; // Increased weight

            if (score < bestScore) {
                bestScore = score;
                bestClusters = clusters;
            }
        }

        return bestClusters;
    }

    private double computeInertia(List<CentroidCluster<PlacePoint>> clusters) {
        double total = 0.0;

        for (CentroidCluster<PlacePoint> c : clusters) {
            List<PlacePoint> pts = c.getPoints();
            if (pts.isEmpty()) continue;

            // Centroid theo trung bình arithmetic
            double cx = pts.stream().mapToDouble(p -> p.getPoint()[0]).average().orElse(0);
            double cy = pts.stream().mapToDouble(p -> p.getPoint()[1]).average().orElse(0);

            for (PlacePoint p : pts) {
                double dx = p.getPoint()[0] - cx;
                double dy = p.getPoint()[1] - cy;
                total += dx * dx + dy * dy;
            }
        }

        return total;
    }

    private double computeSizeVariancePenalty(List<CentroidCluster<PlacePoint>> clusters) {
        int n = clusters.size();
        if (n <= 1) return 0.0;

        double meanSize = clusters.stream().mapToInt(c -> c.getPoints().size()).average().orElse(0.0);
        double variance = clusters.stream()
            .mapToDouble(c -> {
                double diff = c.getPoints().size() - meanSize;
                return diff * diff;
            })
            .sum() / n;

        return variance;
    }

    /**
     * Balance clusters by moving points from oversized clusters to undersized ones
     * while minimizing distance increase
     */
    private void balanceClusters(List<CentroidCluster<PlacePoint>> clusters) {
        int totalPoints = clusters.stream().mapToInt(c -> c.getPoints().size()).sum();
        int targetSize = totalPoints / clusters.size();
        int remainder = totalPoints % clusters.size();
        
        // Calculate target sizes (some clusters get +1 to handle remainder)
        List<Integer> targetSizes = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            targetSizes.add(i < remainder ? targetSize + 1 : targetSize);
        }
        
        // Iteratively balance clusters
        for (int iter = 0; iter < 10; iter++) {
            boolean changed = false;
            
            for (int i = 0; i < clusters.size(); i++) {
                CentroidCluster<PlacePoint> cluster = clusters.get(i);
                int currentSize = cluster.getPoints().size();
                int target = targetSizes.get(i);
                
                // If cluster is too large, move points to smaller clusters
                while (currentSize > target) {
                    PlacePoint pointToMove = findBestPointToMove(cluster, clusters, i);
                    if (pointToMove == null) break;
                    
                    int bestTargetCluster = findBestTargetCluster(pointToMove, clusters, i, targetSizes);
                    if (bestTargetCluster == -1) break;
                    
                    // Move the point
                    cluster.getPoints().remove(pointToMove);
                    clusters.get(bestTargetCluster).getPoints().add(pointToMove);
                    
                    currentSize--;
                    changed = true;
                }
            }
            
            if (!changed) break;
        }
    }
    
    /**
     * Find the best point to move from an oversized cluster
     * (the point farthest from its centroid)
     */
    private PlacePoint findBestPointToMove(CentroidCluster<PlacePoint> cluster, 
                                           List<CentroidCluster<PlacePoint>> allClusters,
                                           int clusterIndex) {
        if (cluster.getPoints().isEmpty()) return null;
        
        double[] centroid = computeCentroid(cluster);
        PlacePoint farthest = null;
        double maxDist = -1;
        
        for (PlacePoint p : cluster.getPoints()) {
            double dist = euclideanDistance(p.getPoint(), centroid);
            if (dist > maxDist) {
                maxDist = dist;
                farthest = p;
            }
        }
        
        return farthest;
    }
    
    /**
     * Find the best target cluster for a point (closest centroid among undersized clusters)
     */
    private int findBestTargetCluster(PlacePoint point, 
                                      List<CentroidCluster<PlacePoint>> clusters,
                                      int sourceCluster,
                                      List<Integer> targetSizes) {
        int bestCluster = -1;
        double minDist = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < clusters.size(); i++) {
            if (i == sourceCluster) continue;
            
            CentroidCluster<PlacePoint> cluster = clusters.get(i);
            int currentSize = cluster.getPoints().size();
            int target = targetSizes.get(i);
            
            // Only consider clusters that need more points
            if (currentSize >= target) continue;
            
            double[] centroid = computeCentroid(cluster);
            double dist = euclideanDistance(point.getPoint(), centroid);
            
            if (dist < minDist) {
                minDist = dist;
                bestCluster = i;
            }
        }
        
        return bestCluster;
    }
    
    private double[] computeCentroid(CentroidCluster<PlacePoint> cluster) {
        List<PlacePoint> points = cluster.getPoints();
        if (points.isEmpty()) return new double[]{0, 0};
        
        double cx = points.stream().mapToDouble(p -> p.getPoint()[0]).average().orElse(0);
        double cy = points.stream().mapToDouble(p -> p.getPoint()[1]).average().orElse(0);
        
        return new double[]{cx, cy};
    }
    
    private double euclideanDistance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        return Math.sqrt(dx * dx + dy * dy);
    }
}
