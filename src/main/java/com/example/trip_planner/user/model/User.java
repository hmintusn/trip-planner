package com.example.trip_planner.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * User entity stored in PostgreSQL
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "local_id", nullable = false, unique = true)
    private String localId; // Firebase localId (uid)

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status; // ACTIVE or DISABLED
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "last_login_at")
    private Instant lastLoginAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
}
