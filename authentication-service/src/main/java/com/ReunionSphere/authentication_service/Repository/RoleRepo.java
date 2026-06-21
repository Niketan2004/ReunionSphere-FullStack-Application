package com.ReunionSphere.authentication_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ReunionSphere.authentication_service.Entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
     

}
