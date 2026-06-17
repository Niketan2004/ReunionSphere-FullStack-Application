package com.ReunionSphere.User_Service.Dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ReunionSphere.User_Service.Enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto implements Serializable {
     private String subscriptionId;
     private UserProfileDto userProfiles;
     private UserRoles userRole;
     private LocationDto location;
     private String email;
     private String phoneNumber;
     private Boolean isActive;
     private Boolean isVerified;
     private LocalDateTime createdAt;
     private LocalDateTime updatedAt;
}
