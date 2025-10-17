package com.example.trip_planner.common.constants;

/**
 * Firebase-related constants
 */
public final class FirebaseConstants {
    
    public static final String FIREBASE_ISSUER_PREFIX = "https://securetoken.google.com/";
    public static final String FIREBASE_AUDIENCE_SUFFIX = ".firebaseapp.com";
    public static final String ALGORITHM_RS256 = "RS256";
    
    private FirebaseConstants() {
        // Utility class - prevent instantiation
    }
}
