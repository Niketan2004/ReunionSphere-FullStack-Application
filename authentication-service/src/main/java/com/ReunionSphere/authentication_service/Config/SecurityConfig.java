package com.ReunionSphere.authentication_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Centralized Spring Security configuration class responsible for establishing
 * a stateless, JWT-driven security architecture across the microservice.
 * <p>
 * This configuration disables default session management (CSRF/Cookies) in
 * favor
 * of token-based authentication, secures application endpoints, and registers
 * the
 * custom {@link JwtAuthenticationFilter} within the security filter chain.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

     private final JwtAuthenticationFilter jwtAuthenticationFilter;

     /**
      * Exposes the primary Spring Security {@link AuthenticationManager} as a Bean
      * to allow explicit authentication operations within service layers.
      *
      * @param authenticationConfiguration the autowired authentication configuration
      * @return AuthenticationManager instance
      * @throws Exception if manager initialization fails
      */
     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
               throws Exception {
          return authenticationConfiguration.getAuthenticationManager();
     }

     /**
      * Constructs and configures the primary {@link SecurityFilterChain},
      * establishing
      * HTTP security rules, stateless session policies, and custom filter ordering.
      *
      * @param http the HttpSecurity builder object
      * @return the fully configured SecurityFilterChain
      * @throws Exception if filter chain configuration fails
      */
     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
                    // Disable CSRF protection since tokens are used and sessions are stateless
                    .csrf(csrf -> csrf.disable())

                    // Configure session management to enforce completely stateless execution
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                    // Establish access control rules for public authentication endpoints and API
                    // documentation
                    .authorizeHttpRequests(auth -> auth
                              .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/actuator/**")
                              .permitAll()
                              .anyRequest().authenticated())

                    // Register the custom JWT verification filter prior to standard
                    // username/password authentication
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

          return http.build();
     }
}
