package com.example.trip_planner.clustering;

import java.util.List;

public class ClusterRequest {
    private List<String> placeIds;
    private int days;

    public ClusterRequest() {}

    public List<String> getPlaceIds() { return placeIds; }
    public void setPlaceIds(List<String> placeIds) { this.placeIds = placeIds; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
}