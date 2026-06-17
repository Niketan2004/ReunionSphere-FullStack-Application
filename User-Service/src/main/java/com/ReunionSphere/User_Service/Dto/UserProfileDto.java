package com.ReunionSphere.User_Service.Dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ReunionSphere.User_Service.Entities.Location;
import com.ReunionSphere.User_Service.Enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto implements Serializable {

     private String userId;
     private String authUserId;
     private String firstName;
     private String middleName;
     private String lastName;
     private String email;
     private String phoneNumber;
     private UserRoles userRole;
     private Boolean isActive;
     private Boolean isVerified;
     private LocationDto location;
     private String profileImageUrl;
     private LocalDateTime createdAt;
     private LocalDateTime updatedAt;

}
