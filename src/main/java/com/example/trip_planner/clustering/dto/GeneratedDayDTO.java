package com.example.trip_planner.clustering.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class GeneratedDayDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dayDate;
    
    private int orderIndex;
    private String notes;
    private List<GeneratedActivityDTO> activities;

    // Getters and Setters
    public LocalDate getDayDate() {
        return dayDate;
    }

    public void setDayDate(LocalDate dayDate) {
        this.dayDate = dayDate;
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

    public List<GeneratedActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<GeneratedActivityDTO> activities) {
        this.activities = activities;
    }
}
