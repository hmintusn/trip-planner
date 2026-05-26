package com.example.trip_planner.clustering.dto;

import com.example.trip_planner.place.model.Place;

import java.util.List;

/**
 * Result of route optimization including places and travel durations
 */
public class OptimizedRouteResult {
    private List<Place> places;
    private List<Double> legDurations; // Duration in minutes for each leg (travel between places)

    public OptimizedRouteResult() {
    }

    public OptimizedRouteResult(List<Place> places, List<Double> legDurations) {
        this.places = places;
        this.legDurations = legDurations;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public List<Double> getLegDurations() {
        return legDurations;
    }

    public void setLegDurations(List<Double> legDurations) {
        this.legDurations = legDurations;
    }
}
