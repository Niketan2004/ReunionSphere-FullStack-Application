package com.ReunionSphere.User_Service.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ReunionSphere.User_Service.Enums.UserRoles;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscriptions {

     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     
     private String subscriptionId;

     // @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
     @OneToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "user_id")
     @ToString.Exclude
     @EqualsAndHashCode.Exclude
     private UserProfiles userProfiles;

     @Enumerated(EnumType.STRING)
     private UserRoles userRole;

     @OneToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "location_id")
     @ToString.Exclude
     @EqualsAndHashCode.Exclude
     private Location location;

     @Email
     @NotBlank(message = "Email is required")
     private String email;

     private String phoneNumber;

     private Boolean isActive;

     private Boolean isVerified;

     @CreationTimestamp
     @Column(updatable = false)
     private LocalDateTime createdAt;

     @UpdateTimestamp
     private LocalDateTime updatedAt;
}