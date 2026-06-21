package com.ReunionSphere.authentication_service.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

     @Value("${}")
     private String userServiceBaseUrl;

     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder(12);
     }

     @Bean
     public RestClient userServiceRestClient() {
          return RestClient.builder()
                    .baseUrl("http://user-service:8081")
                    .build();
     }
}
