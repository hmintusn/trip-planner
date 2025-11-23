package com.example.trip_planner.trip.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class TripDetailDTO {

    private UUID id;
    private String ownerLocalId;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String visibility;
    private List<TripMemberDTO> members;
    private List<TripDayDTO> days;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;
}