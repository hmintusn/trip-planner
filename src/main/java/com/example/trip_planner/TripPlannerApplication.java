package com.example.trip_planner;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext; 
import com.example.trip_planner.firebase.service.FirebaseJwksService;
@SpringBootApplication
public class TripPlannerApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TripPlannerApplication.class, args);

    	FirebaseJwksService service = context.getBean(FirebaseJwksService.class);

    	Map<String, String> mp = service.getJwks();
	   
		System.out.println(mp);
		System.out.println("======================");
		System.out.println(mp);

	}

}
