package com.example.trip_planner.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * User entity stored in MongoDB
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String localId; // Firebase localId (uid)

    @Indexed
    private String email;

    private boolean emailVerified;
    
    private String displayName;
    
    private String photoUrl;
    
    private UserStatus status; // ACTIVE or DISABLED
    
    private Instant createdAt;
    
    private Instant lastLoginAt;
    
    private Instant updatedAt;
}
