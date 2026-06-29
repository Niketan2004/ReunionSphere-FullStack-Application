package com.ReunionSphere.authentication_service.Security.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Centralized Spring Application Configuration initializing shared
 * cross-cutting Beans
 * utilized throughout the microservice infrastructure.
 * <p>
 * Configures foundational dependencies including cryptographic password
 * encoders
 * and HTTP client abstractions for external microservice integration.
 */
@Configuration
public class AppConfig {


     /**
      * Establishes a cryptographic {@link PasswordEncoder} Bean utilizing the BCrypt
      * hashing
      * algorithm to ensure secure storage and comparison of user passwords.
      *
      * @return PasswordEncoder instance configured with BCrypt
      */
     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
     }

  
}
