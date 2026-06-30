package com.ReunionSphere.api_gateway.Utils;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Role-based access validator for the API Gateway.
 * <p>
 * Only enforces role checks for admin-level endpoints at the gateway.
 * Report and user access control is handled by each downstream service's
 * Spring Security configuration.
 */
@Component
public class RoleValidator {

     private final Map<String, List<String>> roleMappings = Map.of(
               "/api/v1/admin",
               List.of("ROLE_ADMIN")
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