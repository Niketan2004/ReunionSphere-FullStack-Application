package com.ReunionSphere.authentication_service.Config;

import java.net.Authenticator;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.proc.ExpiredJWTException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
     @Value("${app.jwt.secret}")
     private String jwtSecret; // e.g., defined in application.yml

     @Value("${app.jwt.expiration-ms}")
     private int jwtExpirationInMs;

     private Key getSigningKey() {
          return Keys.hmacShaKeyFor(jwtSecret.getBytes());

     }

     private String generateToken(Authentication authentication) {
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

     public String getUserEmailFromJWT(String token) {
          Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
          return claims.getSubject();
     }

     public Boolean validateToken(String authToken) throws ExpiredJWTException {
          try {
               Jwts.parser()
                         .verifyWith((SecretKey) getSigningKey())
                         .build()
                         .parseSignedClaims(authToken);
               return true;
          } catch (SecurityException | MalformedJwtException ex) {
               log.error("Invalid JWT signature");
               throw new SecurityException(ex.getMessage());
          } catch (ExpiredJwtException ex) {
               log.error("Expired JWT token");
               throw new ExpiredJWTException(ex.getMessage());
          } catch (UnsupportedJwtException ex) {
               log.error("Unsupported JWT token");
               throw new UnsupportedJwtException(ex.getMessage());
          } catch (IllegalArgumentException ex) {
               log.error("JWT claims string is empty");
               throw new IllegalArgumentException(ex.getMessage());
          }

     }
}
