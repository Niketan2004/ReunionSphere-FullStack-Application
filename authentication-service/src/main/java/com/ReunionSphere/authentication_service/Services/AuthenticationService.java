package com.ReunionSphere.authentication_service.Services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.ReunionSphere.authentication_service.Config.JwtTokenProvider;
import com.ReunionSphere.authentication_service.Dto.AuthRequest;
import com.ReunionSphere.authentication_service.Dto.AuthResponse;
import com.ReunionSphere.authentication_service.Dto.GoogleLoginRequest;
import com.ReunionSphere.authentication_service.Dto.RegisterUserRequest;
import com.ReunionSphere.authentication_service.Dto.UserProfileDto;
import com.ReunionSphere.authentication_service.Entity.AuthUsers;
import com.ReunionSphere.authentication_service.Entity.OauthLinkedAccounts;
import com.ReunionSphere.authentication_service.Enums.Roles;
import com.ReunionSphere.authentication_service.Exceptions.UserAlreadyExistsException;
import com.ReunionSphere.authentication_service.Repository.AuthUsersRepo;
import com.ReunionSphere.authentication_service.Repository.OauthLinkedAccountsRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for handling user authentication, registration,
 * and OAuth2 integration workflows within the authentication microservice.
 * <p>
 * This service coordinates local database transactions for credential storage
 * and orchestrates inter-service communication with the user-service to maintain
 * user profile synchronization.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

     @Value("${spring.security.oauth2.client.registration.google.client-id:GOOGLE_CLIENT_ID}")
     private String googleClientId;

     private final AuthUsersRepo authUsersRepo;
     private final OauthLinkedAccountsRepo oauthLinkedAccountsRepo;
     private final JwtTokenProvider jwtTokenProvider;
     private final PasswordEncoder passwordEncoder;
     private final AuthenticationManager authenticationManager;
     private final RestClient userServiceRestClient;
  

     /**
      * Registers a new user with standard email and password credentials.
      * Ensures that duplicate registrations are prevented and synchronizes the newly
      * created user profile with the standalone user-service microservice.
      *
      * @param registerUserRequest the DTO containing user registration details
      * @return AuthResponse containing the generated JSON Web Token (JWT)
      * @throws UserAlreadyExistsException if the email is already registered
      */
     @Transactional
     public AuthResponse registerUser(RegisterUserRequest registerUserRequest) {
          log.info("Processing Register Request for user {}", registerUserRequest.getEmail());
          
          // Verify whether the user already exists in the local database
          if (authUsersRepo.findByEmail(registerUserRequest.getEmail()) != null) {
               log.error("User already registered with email {}", registerUserRequest.getEmail());
               throw new UserAlreadyExistsException("User already exists with email: " + registerUserRequest.getEmail());
          }

          // Determine the appropriate role, defaulting to standard user privileges
          Roles role = registerUserRequest.getUserRole() != null ? registerUserRequest.getUserRole() : Roles.ROLE_USER;
          
          // Persist the new authentication credentials with an encrypted password
          AuthUsers authUser = AuthUsers.builder()
                    .email(registerUserRequest.getEmail())
                    .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                    .role(role)
                    .isEnabled(true)
                    .isVerified(false)
                    .accountLocked(false)
                    .build();
          
          authUser = authUsersRepo.save(authUser);
          log.info("User saved successfully in auth repository with ID: {}", authUser.getAuthId());

          // Asynchronously/synchronously publish user profile information to user-service
          syncUserProfileWithUserService(authUser.getAuthId(), registerUserRequest, role);

          // Generate and return a secure JWT representing the authenticated session
          String token = jwtTokenProvider.generateToken(authUser.getAuthId(),authUser.getEmail(), authUser.getRole());
          return new AuthResponse(token);
     }

     /**
      * Authenticates a user's standard credentials (email and password) against
      * the Spring Security authentication manager and returns a JWT access token.
      *
      * @param authRequest the DTO containing login credentials
      * @return AuthResponse containing the generated JWT
      */
     public AuthResponse login(AuthRequest authRequest) {
          log.info("Processing Login Request for user {}", authRequest.getEmail());
          
          // Delegate authentication validation to Spring Security's AuthenticationManager
          Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
          );
          SecurityContextHolder.getContext().setAuthentication(authentication);
          
          // Retrieve user entity to extract accurate role information for token generation
          AuthUsers user = authUsersRepo.findByEmail(authRequest.getEmail());
          if (user == null) {
               throw new IllegalArgumentException("User not found after successful authentication");
          }

          // Generate secure JWT containing user identity and role claims
          String token = jwtTokenProvider.generateToken(user.getAuthId(),user.getEmail(), user.getRole());
          return new AuthResponse(token);
     }

     /**
      * Validates a Google OAuth2 ID token, identifies or registers the corresponding user,
      * links the OAuth account, and generates a unified JWT access token.
      *
      * @param request the DTO containing the Google ID token and optional target role
      * @return AuthResponse containing the generated JWT
      */
     @Transactional
     public AuthResponse googleLogin(GoogleLoginRequest request) {
          log.info("Processing Google Login Request");
          
          // Perform verification of the Google ID Token with Google's public tokeninfo endpoint
          Map<String, Object> tokenInfo = verifyGoogleIdToken(request.getIdToken());
          
          String email = (String) tokenInfo.get("email");
          String sub = (String) tokenInfo.get("sub");
          String givenName = (String) tokenInfo.get("given_name");
          String familyName = (String) tokenInfo.get("family_name");
          String picture = (String) tokenInfo.get("picture");
          
          // Check if the user already exists in the system
          AuthUsers user = authUsersRepo.findByEmail(email);
          if (user == null) {
               log.info("New Google user detected, creating auth and profile records for email: {}", email);
               // Provision local authentication entity and linked account record
               user = createGoogleAuthUser(email, sub, request.getRole());
               // Synchronize profile metadata with the dedicated user-service
               syncGoogleUserProfileWithUserService(user.getAuthId(), email, givenName, familyName, picture, user.getRole());
          } else {
               log.info("Existing user logged in via Google OAuth: {}", email);
          }
          
          // Issue a unified application JWT for downstream microservice authentication
          String token = jwtTokenProvider.generateToken(user.getAuthId(),user.getEmail(), user.getRole());
          return new AuthResponse(token);
     }

     /**
      * Invokes the Google OAuth2 public verification endpoint via RestClient to confirm
      * token authenticity and extract profile claims.
      *
      * @param idToken the raw Google ID token
      * @return Map containing the token claims and user profile details
      */
     private Map<String, Object> verifyGoogleIdToken(String idToken) {
          try {
               RestClient restClient = RestClient.create();
               Map<String, Object> tokenInfo = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
               
               if (tokenInfo == null || !tokenInfo.containsKey("email")) {
                    throw new IllegalArgumentException("Invalid Google ID Token structure");
               }
               return tokenInfo;
          } catch (Exception e) {
               log.error("Google token verification failed", e);
               throw new IllegalArgumentException("Google token verification failed: " + e.getMessage(), e);
          }
     }

     /**
      * Creates a new authentication record and OAuth linked account entity for users
      * registering via Google OAuth2.
      *
      * @param email the user's primary email address
      * @param sub the unique Google subject identifier
      * @param requestedRole the optional requested system role
      * @return the persisted AuthUsers entity
      */
     private AuthUsers createGoogleAuthUser(String email, String sub, String requestedRole) {
          Roles role = requestedRole != null ? Roles.valueOf(requestedRole) : Roles.ROLE_USER;
          AuthUsers user = AuthUsers.builder()
                    .email(email)
                    .role(role)
                    .isVerified(true)
                    .isEnabled(true)
                    .accountLocked(false)
                    .build();
          user = authUsersRepo.save(user);
          
          OauthLinkedAccounts linkedAccount = OauthLinkedAccounts.builder()
                    .authUsers(user)
                    .providerName("google")
                    .providerUserId(sub)
                    .build();
          oauthLinkedAccountsRepo.save(linkedAccount);
          return user;
     }

     /**
      * Performs inter-service communication to transmit standard user profile data
      * to the standalone user-service microservice.
      *
      * @param authId the unique authentication identity UUID
      * @param request the original registration payload
      * @param role the assigned system role
      */
     private void syncUserProfileWithUserService(String authId, RegisterUserRequest request, Roles role) {
          try {
               userServiceRestClient.post()
                    .uri("/api/v1/users")
                    .body(UserProfileDto.builder()
                         .authId(authId)
                         .firstName(request.getFirstName())
                         .middleName(request.getMiddleName())
                         .lastName(request.getLastName())
                         .email(request.getEmail())
                         .phoneNumber(request.getPhoneNumber())
                         .userRole(role)
                         .location(request.getLocation())
                         .profileImageUrl(request.getProfileImageUrl())
                         .build())
                    .retrieve()
                    .toBodilessEntity();
               log.info("Successfully synced user profile with user-service for authId: {}", authId);
          } catch (Exception e) {
               log.error("Failed to sync user profile with user-service for authId: {}", authId, e);
          }
     }

     /**
      * Performs inter-service communication to transmit Google OAuth user profile data
      * to the standalone user-service microservice.
      *
      * @param authId the unique authentication identity UUID
      * @param email the user's email address
      * @param givenName the user's first name
      * @param familyName the user's last name
      * @param picture the user's profile image URL
      * @param role the assigned system role
      */
     private void syncGoogleUserProfileWithUserService(String authId, String email, String givenName, String familyName, String picture, Roles role) {
          try {
               userServiceRestClient.post()
                    .uri("/api/v1/users")
                    .body(UserProfileDto.builder()
                         .authId(authId)
                         .firstName(givenName)
                         .lastName(familyName)
                         .email(email)
                         .userRole(role)
                         .profileImageUrl(picture)
                         .build())
                    .retrieve()
                    .toBodilessEntity();
               log.info("Successfully synced Google user profile with user-service for authId: {}", authId);
          } catch (Exception e) {
               log.error("Failed to sync Google user profile with user-service for authId: {}", authId, e);
          }
     }
}
