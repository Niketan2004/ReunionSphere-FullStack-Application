package com.ReunionSphere.api_gateway.Services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

     @Value("${jwt.secret}")
     private String jwtSecret;

     private SecretKey getSigningKey() {
          return Keys.hmacShaKeyFor(jwtSecret.getBytes());
     }

     /**
      * Extract all claims from JWT
      */
     public Claims extractAllClaims(String token) {
          return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
     }

     /**
      * Extract email (subject)
      */
     public String extractEmail(String token) {
          return extractAllClaims(token).getSubject();
     }

     /**
      * Extract userId
      */
     public String extractUserId(String token) {
          return extractAllClaims(token)
                    .get("userId", String.class);
     }

     /**
      * Extract role
      */
     public String extractRole(String token) {
          return extractAllClaims(token)
                    .get("role", String.class);
     }

     /**
      * Check expiration
      */
     public boolean isTokenExpired(String token) {
          Date expiration = extractAllClaims(token)
                    .getExpiration();
          return expiration.before(new Date());
     }
     
     public Claims getClaims(String token) {

          return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
     }

     /**
      * Validate JWT
      */
     public boolean validateToken(String token) {
          try {
               return !isTokenExpired(token);
          } catch (JwtException ex) {
               return false;
          } catch (Exception ex) {
               return false;
          }
     }

}