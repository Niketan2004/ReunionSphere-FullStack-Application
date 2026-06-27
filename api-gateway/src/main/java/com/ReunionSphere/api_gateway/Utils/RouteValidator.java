package com.ReunionSphere.api_gateway.Utils;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

     public static final List<String> openApiEndpoints = List.of(
               "/api/v1/auth/login",
               "/api/v1/auth/register",
               "/v3/api-docs",
               "/swagger-ui",
               "/actuator",
               "/eureka"
     );

     public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
               .stream()
               .noneMatch(uri -> request.getURI()
                         .getPath()
                         .contains(uri));

}