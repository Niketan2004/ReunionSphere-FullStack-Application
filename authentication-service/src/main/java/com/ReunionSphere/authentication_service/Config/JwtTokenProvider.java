package com.ReunionSphere.authentication_service.Config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ReunionSphere.authentication_service.Enums.Roles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Core utility component responsible for lifecycle management of JSON Web Tokens (JWT).
 * <p>
 * This class encapsulates cryptographic signing operations, token generation with custom
 * role claims, extraction of user identity payloads, and robust structural verification.
 */
@Component
@Slf4j
public class JwtTokenProvider {

     @Value("${jwt.secret}")
     private String jwtSecret;

     @Value("${jwt.access-token-expiration}")
     private int jwtExpirationInMs;

     /**
      * Derives a cryptographic {@link SecretKey} from the configured secret key string
      * using the HMAC-SHA algorithm for secure token signing and verification.
      *
      * @return the generated SecretKey instance
      */
     private SecretKey getSigningKey() {
          return Keys.hmacShaKeyFor(jwtSecret.getBytes());
     }

     /**
      * Generates a standard JWT access token based on an active Spring Security {@link Authentication}.
      *
      * @param authentication the active user authentication object
      * @return the signed JWT string
      */
     public String generateToken(Authentication authentication) {
          String userName = authentication.getName();
          Date now = new Date();
          Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

          return Jwts.builder()
                    .subject(userName)
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
     }

     /**
      * Generates a custom JWT access token containing explicit user identity and role claims.
      *
      * @param email the user's email address (token subject)
      * @param role the user's assigned system role
      * @return the signed JWT string
      */
     public String generateToken(String email, Roles role) {
          Date now = new Date();
          Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

          return Jwts.builder()
                    .subject(email)
                    .claim("role", role.name())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
     }

     /**
      * Decrypts and verifies a JWT token to extract the primary subject (user email).
      *
      * @param token the valid JWT string
      * @return the extracted user email address
      */
     public String getUserEmailFromJWT(String token) {
          Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
          return claims.getSubject();
     }

     /**
      * Validates the cryptographic integrity, expiration, and structural validity of a JWT.
      *
      * @param authToken the raw JWT string to be validated
      * @return true if the token is valid, otherwise throws appropriate exceptions
      */
     public Boolean validateToken(String authToken) {
          try {
               Jwts.parser()
                         .verifyWith(getSigningKey())
                         .build()
                         .parseSignedClaims(authToken);
               return true;
          } catch (SecurityException | MalformedJwtException ex) {
               log.error("Invalid JWT signature");
               throw new SecurityException(ex.getMessage());
          } catch (ExpiredJwtException ex) {
               log.error("Expired JWT token");
               throw ex;
          } catch (UnsupportedJwtException ex) {
               log.error("Unsupported JWT token");
               throw ex;
          } catch (IllegalArgumentException ex) {
               log.error("JWT claims string is empty");
               throw ex;
          }
     }
}
