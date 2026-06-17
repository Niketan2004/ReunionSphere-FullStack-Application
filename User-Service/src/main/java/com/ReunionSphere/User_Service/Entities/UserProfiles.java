package com.ReunionSphere.User_Service.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ReunionSphere.User_Service.Enums.UserRoles;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfiles {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String userId;

  @NotBlank(message = "Auth id should not be blank")
  @Column(nullable = false)
  private String authUserId;

  @NotBlank(message = "First Name should not be blank")
  @Size(max = 20)
  @Column(nullable = false)
  private String firstName;

  @Size(max = 20)
  private String middleName;

  @Size(max = 20)
  private String lastName;

  @Email
  @NotBlank(message = "Email should not be blank")
  @Column(nullable = false, unique = true)
  private String email;

  @NotBlank(message = "Phone Number should not be blank")
  @Size(min = 10, max = 10)
  @Column(nullable = false)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  private UserRoles userRole;

  @Builder.Default
  private Boolean isActive = true;

  @Builder.Default
  private Boolean isVerified = false;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Location location;

  @OneToOne(mappedBy = "userProfiles", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Subscriptions subscriptions;

  private String profileImageUrl;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}