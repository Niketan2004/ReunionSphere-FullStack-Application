package com.ReunionSphere.authentication_service.Config;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetailsService implements UserDetails {
     

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          // TODO Auto-generated method stub
          throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
     }

     @Override
     public @Nullable String getPassword() {
          // TODO Auto-generated method stub
          throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
     }

     @Override
     public String getUsername() {
          // TODO Auto-generated method stub
          throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
     }
     
}
