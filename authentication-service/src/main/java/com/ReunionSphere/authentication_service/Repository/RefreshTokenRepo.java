package com.ReunionSphere.authentication_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ReunionSphere.authentication_service.Entity.RefreshToken;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, String> {
     
}
