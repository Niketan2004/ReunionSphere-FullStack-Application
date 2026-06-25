package com.ReunionSphere.authentication_service.Entity;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ReunionSphere.authentication_service.Enums.Roles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUsers {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     @Column(nullable = false, updatable = false)
     private String authId;
     @Email
     @NotBlank(message = "Email should not be blank")
     @Column(unique = true, nullable = false)
     private String email;

     private String password; // Nullable for loging via oauth

     @Builder.Default
     @Column(name = "is_enabled", nullable = false)
     private Boolean isEnabled = true;
     @Builder.Default
     @Column(name = "is_verified", nullable = false)
     private Boolean isVerified = false;
     @Builder.Default
     @Column(name = "account_locked", nullable = false)
     private Boolean accountLocked = false;

     // @ManyToMany(fetch = FetchType.EAGER)
     // @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "authId"),
     // inverseJoinColumns = @JoinColumn(name = "role_id"))
     // @Builder.Default
     // @ToString.Exclude
     // @EqualsAndHashCode.Exclude
     @Enumerated(EnumType.STRING)
     private Roles role;
     // private Set<Role> roles = new HashSet<>();

     @OneToMany(mappedBy = "authUsers", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<OauthLinkedAccounts> linkedAccounts;

     @CreationTimestamp
     @Column(updatable = false, nullable = false)
     private LocalDateTime createdAt;

     @UpdateTimestamp
     private LocalDateTime updatedAt;

}
