package com.example.trip_planner.clustering.util;
import java.util.List;

import com.example.trip_planner.clustering.model.PlacePoint;

public class GeoUtils {
    private static final double R = 6371000.0; // meters

    public static void projectToMeters(List<PlacePoint> pts) {
        double meanLat = pts.stream().mapToDouble(PlacePoint::getLat).average().orElse(0.0);
        double meanLatRad = Math.toRadians(meanLat);

        for (PlacePoint p : pts) {
            double latRad = Math.toRadians(p.getLat());
            double lonRad = Math.toRadians(p.getLon());
            double x = R * lonRad * Math.cos(meanLatRad);
            double y = R * latRad;
            p.setPointInMeters(x, y);
        }
    }

    // Optional: haversine distance between two lat/lon (meters)
    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dphi = Math.toRadians(lat2 - lat1);
        double dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi/2)*Math.sin(dphi/2)
                 + Math.cos(phi1)*Math.cos(phi2)*Math.sin(dlambda/2)*Math.sin(dlambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
