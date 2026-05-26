package com.example.trip_planner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TripPlannerApplicationTest {

    @Test
    void applicationClassExists() {
        // Simple test to verify the main application class exists
        assertNotNull(TripPlannerApplication.class);
    }
    
    @Test
    void mainMethodExists() throws NoSuchMethodException {
        // Verify the main method exists with correct signature
        var mainMethod = TripPlannerApplication.class.getMethod("main", String[].class);
        assertNotNull(mainMethod);
    }
}
