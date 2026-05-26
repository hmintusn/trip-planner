package com.example.trip_planner.clustering.dto;

import java.util.List;

public class ClusterDTO {
    private int clusterId;
    private List<PlaceDTO> places;

    public ClusterDTO() {}

    public ClusterDTO(int clusterId, List<PlaceDTO> places) {
        this.clusterId = clusterId;
        this.places = places;
    }

    public int getClusterId() { return clusterId; }
    public void setClusterId(int clusterId) { this.clusterId = clusterId; }

    public List<PlaceDTO> getPlaces() { return places; }
    public void setPlaces(List<PlaceDTO> places) { this.places = places; }
}