package com.ReunionSphere.api_gateway.Filters;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ReunionSphere.api_gateway.Services.JwtService;
import com.ReunionSphere.api_gateway.Utils.RoleValidator;
import com.ReunionSphere.api_gateway.Utils.RouteValidator;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
     private final JwtService jwtService;
     private final RouteValidator routeValidator;
     private final RoleValidator roleValidator;

     @Override
     public Mono<Void> filter(ServerWebExchange exchange,
               GatewayFilterChain chain) {
          // Check if this endpoint requires authentication
          if (!routeValidator.isSecured.test(exchange.getRequest())) {
               return chain.filter(exchange);
          }

          // Read Authorization header
          String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               exchange.getResponse()
                         .setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
          }

          // Remove "Bearer "
          String token = authHeader.substring(7);
          // Validate JWT
          if (!jwtService.validateToken(token)) {
               exchange.getResponse()
                         .setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
          }

          Claims claims = jwtService.getClaims(token);
          String email = claims.getSubject();
          String userId = claims.get("userId", String.class);
          String role = claims.get("role", String.class);
          String path = exchange.getRequest()
                    .getURI()
                    .getPath();

          if (!roleValidator.hasAccess(path, role)) {
               exchange.getResponse()
                         .setStatusCode(HttpStatus.FORBIDDEN);
               return exchange.getResponse().setComplete();
          }

          ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .header("X-User-Id", userId)
                    .build();

          ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
          // Continue to next filter
          return chain.filter(modifiedExchange);
     }

     @Override
     public int getOrder() {
          return -1;
     }
}
