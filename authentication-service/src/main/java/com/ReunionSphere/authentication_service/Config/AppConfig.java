package com.ReunionSphere.authentication_service.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

/**
 * Centralized Spring Application Configuration initializing shared cross-cutting Beans
 * utilized throughout the microservice infrastructure.
 * <p>
 * Configures foundational dependencies including cryptographic password encoders
 * and HTTP client abstractions for external microservice integration.
 */
@Configuration
public class AppConfig {

     @Value("${user.service.base-url:http://user-service:8081}")
     private String userServiceBaseUrl;

     /**
      * Establishes a cryptographic {@link PasswordEncoder} Bean utilizing the BCrypt hashing
      * algorithm to ensure secure storage and comparison of user passwords.
      *
      * @return PasswordEncoder instance configured with BCrypt
      */
     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
     }

     /**
      * Provisions a configured Spring {@link RestClient} Bean pre-wired with the target
      * base URL of the user-service microservice for streamlined cross-service communication.
      *
      * @return RestClient configured for user-service integration
      */
     @Bean
     public RestClient userServiceRestClient() {
          return RestClient.builder()
                    .baseUrl(userServiceBaseUrl)
                    .build();
     }
}
