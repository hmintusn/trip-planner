package com.example.trip_planner;

// import java.io.FileInputStream;
// import java.nio.file.Path;
// import java.util.List;
// import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TripPlannerApplication {

	public static void main(String[] args) {
        SpringApplication.run(TripPlannerApplication.class, args);
        // ApplicationContext context = SpringApplication.run(TripPlannerApplication.class, args);

    	// FirebaseJwksService service = context.getBean(FirebaseJwksService.class);
		// FirebaseTokenVerifier verifier = context.getBean(FirebaseTokenVerifier.class);
		// PlaceRepository repository = context.getBean(PlaceRepository.class);

		// 1. FirebaseJwks get jwks:  
    	// Map<String, Object> jwks = service.getJwks();
		
		// System.out.println("==================");
		// @SuppressWarnings("unchecked")
		// List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
		// for(Map<String, Object> key : keys){
		// 	String kid = (String) key.get("kid");
		// 	System.out.println("kid:" + kid);
		// }


	}

}
