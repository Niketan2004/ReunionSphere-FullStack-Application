package com.ReunionSphere.authentication_service.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginRequest {
     @NotBlank(message = "Token cannot be blank")
     private String idToken;

     // Only required if the user doesn't exist yet and needs to be registered
     private String role;
}