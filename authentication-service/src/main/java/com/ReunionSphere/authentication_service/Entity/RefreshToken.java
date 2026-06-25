package com.ReunionSphere.authentication_service.Entity;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

     @Id
     @GeneratedValue(strategy = GenerationType.UUID)
     @Column(updatable = false, nullable = false)
     private String tokenId;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "user_id", nullable = false)
     @ToString.Exclude
     @EqualsAndHashCode.Exclude
     private AuthUsers authUser;

     @Column(nullable = false, unique = true, length = 512)
     private String token;

     @Column(name = "expiry_date", nullable = false)
     private Instant expiryDate;
}