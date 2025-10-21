package com.example.trip_planner.place.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Regular opening hours for a place
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegularOpeningHours {
    private List<String> weekdayDescriptions;
}
