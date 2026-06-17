package com.ReunionSphere.User_Service.Dto.RegistrationDto;

import java.time.LocalDateTime;

import com.ReunionSphere.User_Service.Dto.LocationDto;
import com.ReunionSphere.User_Service.Dto.UserProfileDto;
import com.ReunionSphere.User_Service.Enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateSubscriptionDto {
     private UserProfileDto userProfiles;
     private UserRoles userRole;
     private LocationDto location;
     private String email;
     private String phoneNumber;
     private Boolean isActive;
     private Boolean isVerified;
}
