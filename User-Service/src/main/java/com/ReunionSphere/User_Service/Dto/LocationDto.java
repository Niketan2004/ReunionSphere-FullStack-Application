package com.ReunionSphere.User_Service.Dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto implements Serializable{
     private String locationId;
     private String landMark;
     private String city;
     private String district;
     private Integer pinCode;
     private String state;
     private String country;
     private String latitude;
     private String longitude;
     private String description;
     private LocalDateTime createdAt;
     private LocalDateTime updatedAt;
}
