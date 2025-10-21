package com.example.trip_planner.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Firebase JWT authentication
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final Environment environment;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> {
                // For dev/test profiles, allow all requests
                if (isDevOrTestProfile()) {
                    authz.anyRequest().permitAll();
                } else {
                    // Production configuration
                    authz
                        .requestMatchers("/api/public/**", "/actuator/**", "/", "/error", "/api/v1/**").permitAll()
                        .anyRequest().authenticated();
                }
            });
            // .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    private boolean isDevOrTestProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile) || "local".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
