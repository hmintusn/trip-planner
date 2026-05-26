package com.example.trip_planner.clustering.service;

import com.example.trip_planner.clustering.model.PlacePoint;
import com.example.trip_planner.place.model.Place;

import java.util.List;

public class ClusterWithRestaurant {
    private int clusterId;
    private List<PlacePoint> places;
    private Place restaurant;
    private double[] centroid;

    public ClusterWithRestaurant(int clusterId, List<PlacePoint> places, Place restaurant, double[] centroid) {
        this.clusterId = clusterId;
        this.places = places;
        this.restaurant = restaurant;
        this.centroid = centroid;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public List<PlacePoint> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlacePoint> places) {
        this.places = places;
    }

    public Place getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Place restaurant) {
        this.restaurant = restaurant;
    }

    public double[] getCentroid() {
        return centroid;
    }

    public void setCentroid(double[] centroid) {
        this.centroid = centroid;
    }
}
