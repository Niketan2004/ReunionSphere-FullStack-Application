package com.ReunionSphere.authentication_service.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Role {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(unique = true, nullable = false, length = 50)
     private String name; // e.g., "ROLE_USER", "ROLE_ADMIN"
}
