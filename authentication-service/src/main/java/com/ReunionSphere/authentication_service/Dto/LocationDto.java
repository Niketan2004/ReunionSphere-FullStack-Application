package com.ReunionSphere.authentication_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {
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
