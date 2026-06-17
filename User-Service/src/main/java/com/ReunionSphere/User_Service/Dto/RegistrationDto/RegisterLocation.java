package com.ReunionSphere.User_Service.Dto.RegistrationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLocation {
     private String landMark;
     private String city;
     private String district;
     private Integer pinCode;
     private String state;
     private String country;
     private String latitude;
     private String longitude;
     private String description;
}
