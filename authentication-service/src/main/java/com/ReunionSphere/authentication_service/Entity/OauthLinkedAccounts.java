package com.ReunionSphere.authentication_service.Entity;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.Collate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthLinkedAccounts {
     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     @Column(updatable = false, nullable = false)
     private String oAuthId;
     
     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "auth_id", nullable = false)
     @ToString.Exclude
     @EqualsAndHashCode.Exclude
     private AuthUsers authUsers;

     @Column(nullable = false, length = 50)
     private String provideName;

     @Column(nullable = false)
     private String providerUserId;

     @CreationTimestamp
     @Column(updatable = false,nullable = false)
     private Instant linkedAt;

     @UpdateTimestamp
     private LocalDateTime updatedAt;
}
