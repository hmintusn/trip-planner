package com.example.trip_planner.clustering.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class GenerateTripRequest {
    
    @Schema(description = "List of heritage place IDs to visit", example = "[\"ChIJB89j-xp7NjERo9aySQ7Ikh0\", \"ChIJn60YcKl7NjERHlwGKcFB5Nk\"]")
    private List<String> heritagePlaceIds;
    
    @Schema(description = "Number of days for the trip", example = "2", minimum = "1", maximum = "7")
    private int days;
    
    @Schema(description = "Trip name", example = "Ninh Binh Heritage Tour")
    private String tripName;
    
    @Schema(description = "Trip description", example = "Explore ancient temples and scenic landscapes")
    private String description;
    
    @Schema(description = "Trip start date", example = "2025-01-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Schema(description = "Trip end date", example = "2025-01-16")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Schema(description = "Trip visibility", example = "PRIVATE", allowableValues = {"PRIVATE", "PUBLIC"})
    private String visibility = "PRIVATE";

    @Schema(description = "Hotel location [longitude, latitude]", example = "[105.8650816, 20.2785788]")
    private double[] hotelLocation;

    // Getters and Setters
    public List<String> getHeritagePlaceIds() {
        return heritagePlaceIds;
    }

    public void setHeritagePlaceIds(List<String> heritagePlaceIds) {
        this.heritagePlaceIds = heritagePlaceIds;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public double[] getHotelLocation() {
        return hotelLocation;
    }

    public void setHotelLocation(double[] hotelLocation) {
        this.hotelLocation = hotelLocation;
    }
}
