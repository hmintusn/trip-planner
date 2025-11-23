package com.example.trip_planner.trip.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trip_members")
@IdClass(TripMemberId.class)
@Getter
@Setter
@NoArgsConstructor
public class TripMember {

    @Id
    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Id
    @NotBlank
    @Column(name = "user_local_id", nullable = false)
    private String userLocalId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripMemberRole role = TripMemberRole.VIEWER;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;
}