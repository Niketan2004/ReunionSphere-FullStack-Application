package com.ReunionSphere.authentication_service.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ReunionSphere.authentication_service.Dto.AuthRequest;
import com.ReunionSphere.authentication_service.Dto.AuthResponse;
import com.ReunionSphere.authentication_service.Dto.GoogleLoginRequest;
import com.ReunionSphere.authentication_service.Dto.RegisterResponse;
import com.ReunionSphere.authentication_service.Dto.RegisterUserRequest;
import com.ReunionSphere.authentication_service.Services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {

     private final AuthenticationService authenticationService;

     @PostMapping("/register")
     public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterUserRequest request) {
          log.info("Received registration request for email: {}", request.getEmail());
          RegisterResponse response = authenticationService.registerUser(request);
          log.info("Registration completed for email: {}", request.getEmail());
          return ResponseEntity.status(HttpStatus.CREATED).body(response);
     }

     @PostMapping("/login")
     public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
          log.info("Received login request for email: {}", request.getEmail());
          AuthResponse response = authenticationService.login(request);
          log.info("Login successful for email: {}", request.getEmail());
          return ResponseEntity.ok(response);
     }

     @PostMapping("/google")
     public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
          log.info("Received Google OAuth login request");
          AuthResponse response = authenticationService.googleLogin(request);
          return ResponseEntity.ok(response);
     }
}
