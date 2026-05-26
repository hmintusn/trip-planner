package com.example.trip_planner.trip.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class TripMemberDTO {

    private String userLocalId;
    private String role;
    private Instant joinedAt;
}