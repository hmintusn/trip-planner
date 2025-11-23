package com.example.trip_planner.trip.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TripMemberId implements Serializable {

    private UUID tripId;
    private String userLocalId;

    public TripMemberId() {}

    public TripMemberId(UUID tripId, String userLocalId) {
        this.tripId = tripId;
        this.userLocalId = userLocalId;
    }

    // Getters and setters
    public UUID getTripId() { return tripId; }
    public void setTripId(UUID tripId) { this.tripId = tripId; }

    public String getUserLocalId() { return userLocalId; }
    public void setUserLocalId(String userLocalId) { this.userLocalId = userLocalId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripMemberId that = (TripMemberId) o;
        return Objects.equals(tripId, that.tripId) && Objects.equals(userLocalId, that.userLocalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, userLocalId);
    }
}