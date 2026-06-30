package com.ReunionSphere.authentication_service.Dto;

import com.ReunionSphere.authentication_service.Enums.Roles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {
     private String userId;
     private String authUserId;
     private String firstName;
     private String middleName;
     private String lastName;
     private String email;
     private String phoneNumber;
     private Roles userRole;
     private LocationDto location;
     private String profileImageUrl;
}
