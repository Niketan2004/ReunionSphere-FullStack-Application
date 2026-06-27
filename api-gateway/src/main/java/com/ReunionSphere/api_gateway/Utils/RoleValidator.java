package com.ReunionSphere.api_gateway.Utils;


import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RoleValidator {

     private final Map<String, List<String>> roleMappings = Map.of(

               "/api/v1/admin",
               List.of("ROLE_ADMIN"),

               "/api/v1/users",
               List.of("ROLE_USER", "ROLE_ADMIN"),

               "/api/v1/reports",
               List.of("ROLE_USER", "ROLE_ADMIN")

     );

     public boolean hasAccess(String path, String role) {
          for (Map.Entry<String, List<String>> entry : roleMappings.entrySet()) {
               if (path.startsWith(entry.getKey())) {
                    return entry.getValue().contains(role);
               }
          }
          return true;
     }

}