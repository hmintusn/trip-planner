package com.example.trip_planner.clustering.dto;

public class GeneratedActivityDTO {
    private String placeId;
    private String placeName;
    private String placeType; // "heritage_site" or "restaurant"
    private String session; // "MORNING" or "AFTERNOON"
    private Double travelDurationMinutes; // Travel time from previous location in minutes
    private String startTime; // HH:mm format
    private String endTime;   // HH:mm format
    private int orderIndex;
    private String notes;

    // Getters and Setters
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Double getTravelDurationMinutes() {
        return travelDurationMinutes;
    }

    public void setTravelDurationMinutes(Double travelDurationMinutes) {
        this.travelDurationMinutes = travelDurationMinutes;
    }
}
