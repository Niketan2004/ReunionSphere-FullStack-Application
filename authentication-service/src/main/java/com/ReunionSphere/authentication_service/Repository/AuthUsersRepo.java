package com.ReunionSphere.authentication_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ReunionSphere.authentication_service.Entity.AuthUsers;

public interface AuthUsersRepo extends JpaRepository<AuthUsers,String> {
     
}
