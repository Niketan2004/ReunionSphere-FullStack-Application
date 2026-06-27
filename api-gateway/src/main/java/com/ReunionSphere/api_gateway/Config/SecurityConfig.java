package com.ReunionSphere.api_gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

     @Bean
     SecurityWebFilterChain securityFilterChain(
               ServerHttpSecurity http) {

          return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchange -> exchange
                              .pathMatchers("/api/v1/auth/**")
                              .permitAll()

                              .pathMatchers("/api/v1/admin/**")
                              .hasRole("ADMIN")

                              .pathMatchers("/api/v1/reports/**")
                              .hasAnyRole("USER", "ADMIN")

                              .pathMatchers("/api/v1/users/**")
                              .authenticated()
                              .anyExchange()
                              .authenticated())
                    .build();
     }
}