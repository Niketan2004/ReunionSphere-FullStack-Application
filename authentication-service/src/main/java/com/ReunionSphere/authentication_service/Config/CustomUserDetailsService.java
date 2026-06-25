package com.ReunionSphere.authentication_service.Config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ReunionSphere.authentication_service.Entity.AuthUsers;
import com.ReunionSphere.authentication_service.Repository.AuthUsersRepo;

import lombok.RequiredArgsConstructor;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService},
 * acting as the primary bridge between Spring Security's authentication manager
 * and the local persistence layer.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

     private final AuthUsersRepo authUsersRepo;

     /**
      * Retrieves an authentication user record from the database matching the provided username/email.
      * Maps the entity representation into a standard {@link UserDetails} contract via {@link CustomUserDetails}.
      *
      * @param username the primary identity string (user email address)
      * @return UserDetails representing the established user identity and privileges
      * @throws UsernameNotFoundException if no user matching the email address exists in the database
      */
     @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          // Look up user entity by unique email address
          AuthUsers user = authUsersRepo.findByEmail(username);
          if (user == null) {
               throw new UsernameNotFoundException("User not found with email: " + username);
          }
          
          // Encapsulate user entity within Spring Security's UserDetails wrapper contract
          return new CustomUserDetails(user);
     }
}
