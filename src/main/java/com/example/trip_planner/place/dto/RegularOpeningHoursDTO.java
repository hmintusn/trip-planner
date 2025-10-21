package com.example.trip_planner.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for opening hours
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegularOpeningHoursDTO {
    private List<String> weekdayDescriptions;
}
