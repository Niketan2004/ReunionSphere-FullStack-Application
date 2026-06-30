package com.ReunionSphere.LostAndFoundService.Security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

     @Value("${jwt.secret}")
     private String jwtSecret;

     private SecretKey getSigningKey() {
          return Keys.hmacShaKeyFor(jwtSecret.getBytes());
     }

     public Claims extractAllClaims(String token) {
          return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
     }

     public String extractUserId(String token) {
          return extractAllClaims(token).get("userId", String.class);
     }

     public String extractEmail(String token) {
          return extractAllClaims(token).getSubject();
     }

     public String extractRole(String token) {
          return extractAllClaims(token).get("role", String.class);
     }

     public boolean validateToken(String token) {
          try {
               extractAllClaims(token);
               return true;
          } catch (Exception ex) {
               log.error("JWT validation failed: {}", ex.getMessage());
               return false;
          }
     }
}
