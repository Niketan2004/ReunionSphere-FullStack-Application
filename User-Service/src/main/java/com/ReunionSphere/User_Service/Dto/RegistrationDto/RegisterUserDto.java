package com.ReunionSphere.User_Service.Dto.RegistrationDto;

import com.ReunionSphere.User_Service.Enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {
     private String authUserId;
     private String firstName;
     private String middleName;
     private String lastName;
     private String email;
     private String phoneNumber;
     private UserRoles userRole;
     private RegisterLocation location;
     private String profileImageUrl;
}
