package com.ReunionSphere.api_gateway.Utils;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * Determines which routes are public (no JWT required) vs secured (JWT required).
 */
@Component
public class RouteValidator {

     public static final List<String> openApiEndpoints = List.of(
               "/api/v1/auth/login",
               "/api/v1/auth/register",
               "/api/v1/auth/google",
               "/v3/api-docs",
               "/swagger-ui",
               "/actuator",
               "/eureka"
     );

     /**
      * Public GET endpoints — these are accessible without authentication.
      * Note: POST/PUT/DELETE on /api/v1/reports still requires authentication
      * because they won't match these GET-only paths at the gateway level.
      * The actual enforcement is done by each downstream service's Spring Security config.
      */
     public static final List<String> publicGetEndpoints = List.of(
               "/api/v1/reports"
     );

     public Predicate<ServerHttpRequest> isSecured = request -> {
          String path = request.getURI().getPath();
          String method = request.getMethod().name();

          // Check if it's an always-public endpoint (any method)
          boolean isOpenEndpoint = openApiEndpoints.stream()
                    .anyMatch(path::contains);
          if (isOpenEndpoint) return false;

          // Check if it's a public GET endpoint
          if ("GET".equalsIgnoreCase(method)) {
               boolean isPublicGet = publicGetEndpoints.stream()
                         .anyMatch(path::startsWith);
               if (isPublicGet) return false;
          }

          // Everything else requires authentication
          return true;
     };
}