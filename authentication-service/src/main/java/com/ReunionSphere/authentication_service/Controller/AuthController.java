package com.ReunionSphere.authentication_service.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ReunionSphere.authentication_service.Dto.AuthRequest;
import com.ReunionSphere.authentication_service.Dto.AuthResponse;
import com.ReunionSphere.authentication_service.Dto.GoogleLoginRequest;
import com.ReunionSphere.authentication_service.Dto.RegisterUserRequest;
import com.ReunionSphere.authentication_service.Services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

     private final AuthenticationService authenticationService;

     @PostMapping("/register")
     public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterUserRequest request) {
          return ResponseEntity.ok(authenticationService.registerUser(request));
     }

     @PostMapping("/login")
     public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
          return ResponseEntity.ok(authenticationService.login(request));
     }

     @PostMapping("/google")
     public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
          return ResponseEntity.ok(authenticationService.googleLogin(request));
     }
}
