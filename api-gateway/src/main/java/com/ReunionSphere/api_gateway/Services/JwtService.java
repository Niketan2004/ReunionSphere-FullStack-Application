package com.ReunionSphere.api_gateway.Services;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT service for the API Gateway.
 * <p>
 * Only validates token integrity for early rejection.
 * Does NOT extract claims — downstream services handle that independently.
 */
@Service
@Slf4j
public class JwtService {

     @Value("${jwt.secret}")
     private String jwtSecret;

     private SecretKey getSigningKey() {
          return Keys.hmacShaKeyFor(jwtSecret.getBytes());
     }

     /**
      * Validates the JWT token's cryptographic signature and expiration.
      * Uses clock skew tolerance for distributed microservice environments.
      *
      * @param token the raw JWT string
      * @return true if valid, false otherwise
      */
     public boolean validateToken(String token) {
          try {
               Jwts.parser()
                         .verifyWith(getSigningKey())
                         .clockSkewSeconds(60)
                         .build()
                         .parseSignedClaims(token);
               return true;
          } catch (Exception ex) {
               log.warn("JWT validation failed at gateway: {}", ex.getMessage());
               return false;
          }
     }
}