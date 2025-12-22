package com.example.trip_planner.clustering;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class PlacePoint implements Clusterable {
    private final String id;
    private final double lat;
    private final double lon;

    // điểm sau khi convert sang meter
    private double[] point;

    public PlacePoint(String id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.point = new double[] { 0.0, 0.0 }; // set later
    }

    public void setPointInMeters(double x, double y) {
        this.point = new double[] { x, y };
    }

    @Override
    public double[] getPoint() {
        return point;
    }

    public String getId() { return id; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
}
