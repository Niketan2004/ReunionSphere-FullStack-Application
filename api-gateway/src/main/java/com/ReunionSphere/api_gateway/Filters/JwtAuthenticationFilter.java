package com.ReunionSphere.api_gateway.Filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ReunionSphere.api_gateway.Services.JwtService;
import com.ReunionSphere.api_gateway.Utils.RouteValidator;

import reactor.core.publisher.Mono;

/**
 * Global gateway filter that performs early JWT validation.
 * 
 * The gateway only validates token integrity for early rejection of invalid tokens.
 * It forwards the original Authorization header as-is to downstream services,
 * which independently validate the JWT and populate their own SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
     private final JwtService jwtService;
     private final RouteValidator routeValidator;

     @Override
     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
          String path = exchange.getRequest().getURI().getPath();

          // Check if this endpoint requires authentication
          if (!routeValidator.isSecured.test(exchange.getRequest())) {
               log.debug("Public route accessed: {}", path);
               return chain.filter(exchange);
          }

          // Read Authorization header
          String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               log.warn("Missing or invalid Authorization header for secured route: {}", path);
               exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
          }

          // Remove "Bearer " prefix
          String token = authHeader.substring(7);

          // Validate JWT for early rejection — downstream services will validate again independently
          if (!jwtService.validateToken(token)) {
               log.warn("JWT validation failed at gateway for route: {}", path);
               exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
          }

          log.debug("JWT validated at gateway. Forwarding request to downstream service: {}", path);

          // Forward the request as-is — the Authorization header flows through naturally
          // Downstream services will validate the JWT independently and populate SecurityContext
          return chain.filter(exchange);
     }

     @Override
     public int getOrder() {
          return -1;
     }
}
