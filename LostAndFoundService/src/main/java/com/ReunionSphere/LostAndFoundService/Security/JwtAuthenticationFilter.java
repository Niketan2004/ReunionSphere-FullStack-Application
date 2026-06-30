package com.ReunionSphere.LostAndFoundService.Security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

     private final JwtService jwtService;

     @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
               throws ServletException, IOException {

          String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               filterChain.doFilter(request, response);
               return;
          }

          String token = authHeader.substring(7);

          if (jwtService.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
               String userId = jwtService.extractUserId(token);
               String email = jwtService.extractEmail(token);
               String role = jwtService.extractRole(token);

               UserPrincipal principal = new UserPrincipal(userId, email, role);

               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                         principal, null, List.of(new SimpleGrantedAuthority(role)));
               
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);
          }

          filterChain.doFilter(request, response);
     }
}
