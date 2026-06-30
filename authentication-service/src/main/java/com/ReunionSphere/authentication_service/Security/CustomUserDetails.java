package com.ReunionSphere.authentication_service.Security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ReunionSphere.authentication_service.Entity.AuthUsers;

public class CustomUserDetails implements UserDetails {

     private final String email;
     private final String password;
     private final List<GrantedAuthority> authorities;

     public CustomUserDetails(AuthUsers credentials) {
          this.email = credentials.getEmail();
          this.password = credentials.getPassword();
          this.authorities = List.of(new SimpleGrantedAuthority(credentials.getRole().name()));
     }

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          return authorities;
     }

     @Override
     public String getPassword() {
          return password;
     }

     @Override
     public String getUsername() {
          return email;
     }

     @Override
     public boolean isAccountNonExpired() {
          return true;
     }

     @Override
     public boolean isAccountNonLocked() {
          return true;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return true;
     }

     @Override
     public boolean isEnabled() {
          return true;
     }
}
