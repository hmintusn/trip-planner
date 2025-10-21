package com.example.trip_planner.common.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.trip_planner.firebase.FirebaseTokenVerifier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filter for Firebase JWT authentication
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
    
    private final FirebaseTokenVerifier tokenVerifier;
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractToken(request);
            
            if (token != null) {
                // Verify token
                DecodedJWT decodedJWT = tokenVerifier.verifyToken(token);
                
                // Extract user information
                String userId = tokenVerifier.extractUserId(decodedJWT);
                String userEmail = tokenVerifier.extractUserEmail(decodedJWT);
                
                // Create authentication object
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userId, 
                        null, 
                        new ArrayList<>()
                );
                
                // Set user details in authentication
                authentication.setAuthenticated(true);
                
                // Set in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Add user info to request attributes for downstream use
                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", userEmail);
                request.setAttribute("decodedJWT", decodedJWT);
                
                log.debug("User authenticated: {} ({})", userId, userEmail);
            }
            
            // Continue filter chain
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("Authentication failed", e);
            
            // Clear security context
            SecurityContextHolder.clearContext();
            
            // Return 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or expired token\"}");
            return;
        }
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * Skip authentication for certain paths
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/public/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/error") ||
               path.startsWith("/api/v1");
    }
}
