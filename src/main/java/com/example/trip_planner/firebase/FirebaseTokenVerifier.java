package com.example.trip_planner.firebase;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.trip_planner.common.constants.FirebaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Service for verifying Firebase ID tokens using cached JWKS
 */
@Slf4j
@Service
public class FirebaseTokenVerifier {
    
    private final FirebaseJwksService jwksService;
    private final String projectId;
    
    public FirebaseTokenVerifier(
            FirebaseJwksService jwksService,
            @Value("${firebase.project-id}") String projectId) {
        this.jwksService = jwksService;
        this.projectId = projectId;
    }
    
    /**
     * Verify Firebase ID token and return decoded JWT
     */
    public DecodedJWT verifyToken(String token) {
        try {
            log.debug("Verifying Firebase token");
            
            // Decode token header to get key ID
            DecodedJWT decodedToken = JWT.decode(token);
            String keyId = decodedToken.getKeyId();
            
            if (keyId == null) {
                throw new JWTVerificationException("Token missing key ID");
            }
            
            // Get public key for verification
            RSAPublicKey publicKey = getPublicKey(keyId);
            if (publicKey == null) {
                throw new JWTVerificationException("Public key not found for key ID: " + keyId);
            }
            
            // Create verifier
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(getFirebaseIssuer())
                    .withAudience(getFirebaseAudience())
                    .build();
            
            // Verify token
            DecodedJWT verifiedToken = verifier.verify(token);
            
            log.debug("Token verified successfully for user: {}", verifiedToken.getSubject());
            return verifiedToken;
            
        } catch (JWTVerificationException e) {
            log.warn("Token verification failed: {}", e.getMessage());
            
            // If verification fails, try refreshing JWKS and retry once
            if (isSignatureVerificationError(e)) {
                log.info("Signature verification failed, attempting to refresh JWKS");
                jwksService.refreshJwks();
                
                // Retry verification with fresh keys
                try {
                    DecodedJWT decodedToken = JWT.decode(token);
                    String keyId = decodedToken.getKeyId();
                    RSAPublicKey publicKey = getPublicKey(keyId);
                    
                    if (publicKey != null) {
                        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
                        JWTVerifier verifier = JWT.require(algorithm)
                                .withIssuer(getFirebaseIssuer())
                                .withAudience(getFirebaseAudience())
                                .build();
                        
                        return verifier.verify(token);
                    }
                } catch (Exception retryException) {
                    log.error("Token verification failed even after JWKS refresh", retryException);
                }
            }
            
            throw new RuntimeException("Token verification failed", e);
        } catch (Exception e) {
            log.error("Unexpected error during token verification", e);
            throw new RuntimeException("Token verification failed", e);
        }
    }
    
    /**
     * Get public key from JWKS by key ID
     */
    private RSAPublicKey getPublicKey(String keyId) {
        try {
            Map<String, Object> jwks = jwksService.getJwks();
            // JWKS has a 'keys' array
            Object keysObj = jwks.get("keys");
            if (keysObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> keys = (List<Map<String, Object>>) keysObj;
                for (Map<String, Object> key : keys) {
                    Object kidObj = key.get("kid");
                    if (kidObj != null && kidObj.toString().equals(keyId)) {
                        // Expect 'n' (modulus) and 'e' (exponent) in base64url
                        Object nObj = key.get("n");
                        Object eObj = key.get("e");
                        if (nObj instanceof String && eObj instanceof String) {
                            return buildRsaPublicKeyFromModExp((String) nObj, (String) eObj);
                        }
                    }
                }
            }

            log.warn("Public key not found for key ID: {}", keyId);
            return null;
            
        } catch (Exception e) {
            log.error("Failed to get public key for key ID: {}", keyId, e);
            return null;
        }
    }
    
    

    /**
     * Build RSAPublicKey from base64url modulus (n) and exponent (e)
     */
    private RSAPublicKey buildRsaPublicKeyFromModExp(String nB64Url, String eB64Url) {
        try {
            // Base64 URL decoder (no padding)
            Base64.Decoder urlDecoder = Base64.getUrlDecoder();

            byte[] modulusBytes = urlDecoder.decode(nB64Url);
            byte[] exponentBytes = urlDecoder.decode(eB64Url);

            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            log.error("Failed to build RSA public key from modulus/exponent", e);
            return null;
        }
    }
    
    /**
     * Get Firebase issuer URL
     */
    private String getFirebaseIssuer() {
        return FirebaseConstants.FIREBASE_ISSUER_PREFIX + projectId;
    }
    
    /**
     * Get Firebase audience
     */
    private String getFirebaseAudience() {
        return projectId;
    }
    
    /**
     * Check if error is related to signature verification
     */
    private boolean isSignatureVerificationError(JWTVerificationException e) {
        String message = e.getMessage().toLowerCase();
        return message.contains("signature") || 
               message.contains("invalid") || 
               message.contains("key");
    }
    
    /**
     * Extract user ID from verified token
     */
    public String extractUserId(DecodedJWT token) {
        return token.getSubject();
    }
    
    /**
     * Extract user email from verified token
     */
    public String extractUserEmail(DecodedJWT token) {
        return token.getClaim("email").asString();
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(DecodedJWT token) {
        return token.getExpiresAt().before(new java.util.Date());
    }
}
