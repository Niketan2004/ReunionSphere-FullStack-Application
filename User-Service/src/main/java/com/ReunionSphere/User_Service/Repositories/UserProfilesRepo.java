package com.ReunionSphere.User_Service.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ReunionSphere.User_Service.Entities.UserProfiles;

public interface UserProfilesRepo extends JpaRepository<UserProfiles, String> {
    Optional<UserProfiles> findByEmail(String email);

    @Query("SELECT u.userId FROM UserProfiles u WHERE u.email = :email")
    String findUserIdByEmail(@Param("email") String email);

    @Query("SELECT u.userId FROM UserProfiles u WHERE u.phoneNumber = :phoneNumber")
    String findUserIdByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}
