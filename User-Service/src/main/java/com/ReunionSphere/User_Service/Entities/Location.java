package com.ReunionSphere.User_Service.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     private String locationId;

     private String landMark;

     private String city;

     private String district;

     private Integer pinCode;

     @NotBlank(message = "State is required")
     private String state;

     @NotBlank(message = "Country is required")
     private String country;

     private String latitude;

     private String longitude;

     private String description;

     @CreationTimestamp
     @Column(updatable = false)
     private  LocalDateTime createdAt;
     @UpdateTimestamp
     private LocalDateTime updatedAt;
}