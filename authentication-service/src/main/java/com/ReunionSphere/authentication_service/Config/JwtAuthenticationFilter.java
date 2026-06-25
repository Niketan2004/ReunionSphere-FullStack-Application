package com.ReunionSphere.authentication_service.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter class executed once per HTTP request to intercept, extract, and validate
 * incoming JWT authorization headers.
 * <p>
 * If a valid JWT is identified, this filter establishes the authenticated user's
 * identity and authorization privileges within the active {@link SecurityContextHolder}.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

     private final JwtTokenProvider tokenProvider;
     private final UserDetailsService customUserDetailsService;

     /**
      * Core filtering method executing inspection of the incoming HTTP request.
      * Extracts the Bearer token, verifies cryptographic validity, loads user privileges,
      * and injects the authentication token into the Spring Security context.
      *
      * @param request the incoming HttpServletRequest
      * @param response the outgoing HttpServletResponse
      * @param filterChain the remaining filter chain to be executed
      * @throws ServletException if filter execution encounters a servlet error
      * @throws IOException if I/O operations fail during filtering
      */
     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
               throws ServletException, IOException {

          try {
               String jwt = getJwtFromRequest(request);
               if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                    // Extract subject identity from valid token payload
                    String userEmail = tokenProvider.getUserEmailFromJWT(jwt);
                    
                    // Retrieve comprehensive user details and authority claims from the database
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                    
                    // Construct authentication token representing the verified user
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                              userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Register the established authentication object within the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
               }
          } catch (Exception ex) {
               logger.error("Could not set user authentication in security context", ex);
          }
          
          // Continue standard execution of downstream security and application filters
          filterChain.doFilter(request, response);
     }

     /**
      * Inspects the HTTP Authorization header to isolate and return the raw JWT Bearer token string.
      *
      * @param request the incoming HttpServletRequest
      * @return the isolated JWT string, or null if the header is absent or improperly formatted
      */
     private String getJwtFromRequest(HttpServletRequest request) {
          String bearerToken = request.getHeader("Authorization");
          if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
               return bearerToken.substring(7);
          }
          return null;
     }
}