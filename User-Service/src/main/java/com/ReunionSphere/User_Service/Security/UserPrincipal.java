package com.ReunionSphere.User_Service.Security;

public record UserPrincipal(String userId, String email, String role) {
}
