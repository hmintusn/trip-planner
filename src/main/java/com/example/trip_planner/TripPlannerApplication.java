package com.example.trip_planner;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.trip_planner.firebase.FirebaseTokenVerifier;
import com.example.trip_planner.common.util.JsonUtils;
import com.example.trip_planner.firebase.FirebaseJwksService;
import com.example.trip_planner.place.model.Place;
import com.example.trip_planner.place.repository.PlaceRepository;
import com.fasterxml.jackson.core.type.TypeReference;

@SpringBootApplication
public class TripPlannerApplication {

	public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TripPlannerApplication.class, args);

    	FirebaseJwksService service = context.getBean(FirebaseJwksService.class);
		FirebaseTokenVerifier verifier = context.getBean(FirebaseTokenVerifier.class);
		PlaceRepository repository = context.getBean(PlaceRepository.class);

		// 1. FirebaseJwks get jwks:  
    	// Map<String, Object> jwks = service.getJwks();
		
		// System.out.println("==================");
		// @SuppressWarnings("unchecked")
		// List<Map<String, Object>> keys = (List<Map<String, Object>>) jwks.get("keys");
		// for(Map<String, Object> key : keys){
		// 	String kid = (String) key.get("kid");
		// 	System.out.println("kid:" + kid);
		// }

		// 2. Verify token 
		System.out.println("==================");
		String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjlkMjEzMGZlZjAyNTg3ZmQ4ODYxODg2OTgyMjczNGVmNzZhMTExNjUiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vdHJpcC0yNmVkMSIsImF1ZCI6InRyaXAtMjZlZDEiLCJhdXRoX3RpbWUiOjE3NjA4NjQwMjQsInVzZXJfaWQiOiJBR29GSTZDQ0V6TjQ0UldYY3NMNWFNSlZFSUcyIiwic3ViIjoiQUdvRkk2Q0NFek40NFJXWGNzTDVhTUpWRUlHMiIsImlhdCI6MTc2MDg2NDAyNCwiZXhwIjoxNzYwODY3NjI0LCJlbWFpbCI6InRlc3RAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInRlc3RAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.rDWKDHwoRI_7iUws1E7drl3wh2SZznDta6AD1IBIH90WfnN9487pduOUWR0liInPxLkYD0Pqa6A4nUwaDohq58cDQkmufvHHEgiQjEURI_-kh6o1WGA6NLdjnhsWkcpvAtLC3dWx9aKTjpolefNn64GORALW9obP27y2i0PrTEyFSGfBIJc8CJ4oL7SjBK-YbP4ipgtzDafXTjHl1YHoG274RIYEEdfMUE2syY9Z2osJcv20N34yQgoQYylUDi2NEdfqJjZpChC2lXIZcA2fiMwbmQJwShfwMecciDraHki44w2EgeB8Jf3De_eKYnZ0lhBDHd1ejiZxMpQmaP_1sQ";
		// DecodedJWT decodedJWT = verifier.verifyToken(idToken);
		// System.out.println("user email: " + verifier.extractUserEmail(decodedJWT));
		System.out.println("==================");
		
		// 3. Parse places.json
		try {
			// 1. Point to your file
			Path filePath = Path.of("C:\\Users\\84328\\nguhoc\\Desktop\\Trip Planner\\crawl-data\\places_sorted.json");
	
			// 2. Create a MultipartFile from it
			FileInputStream inputStream = new FileInputStream(filePath.toFile());
			// MultipartFile file = new MockMultipartFile(
			//         "file",                               // form field name
			//         filePath.getFileName().toString(),    // original filename
			//         "application/json",                   // content type
			//         inputStream                           // file content
			// );
	
			// 3. Parse JSON into List<Place>
			List<Place> places = JsonUtils.fromJson(
					inputStream,
					new TypeReference<List<Place>>() {}
			);
	
			
			System.out.println("Parsed places: " + places.size());
			if (!places.isEmpty()) {
				System.out.println("Location: " + places.get(0).getLocation());
				System.out.println("Data types" + places.get(0).getLocation().getClass().getName());
			}
			System.out.println("==================");
		} catch (Exception e) {
            throw new RuntimeException("JSON parsing failed", e);
		}

		// Place place = Place.builder().build();
		// repository.insert();


	}

}
