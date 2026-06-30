package com.ReunionSphere.authentication_service.Services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import com.ReunionSphere.authentication_service.Dto.AuthRequest;
import com.ReunionSphere.authentication_service.Dto.AuthResponse;
import com.ReunionSphere.authentication_service.Dto.GoogleLoginRequest;
import com.ReunionSphere.authentication_service.Dto.RegisterResponse;
import com.ReunionSphere.authentication_service.Dto.RegisterUserRequest;
import com.ReunionSphere.authentication_service.Dto.UserProfileDto;
import com.ReunionSphere.authentication_service.Entity.AuthUsers;
import com.ReunionSphere.authentication_service.Entity.OauthLinkedAccounts;
import com.ReunionSphere.authentication_service.Enums.Roles;
import com.ReunionSphere.authentication_service.Exceptions.UserAlreadyExistsException;
import com.ReunionSphere.authentication_service.Repository.AuthUsersRepo;
import com.ReunionSphere.authentication_service.Repository.OauthLinkedAccountsRepo;
import com.ReunionSphere.authentication_service.Security.Jwt.JwtTokenProvider;
import com.ReunionSphere.authentication_service.Utils.UserServiceClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for handling user authentication, registration,
 * and OAuth2 integration workflows within the authentication microservice.
 * <p>
 * This service coordinates local database transactions for credential storage
 * and orchestrates inter-service communication with the user-service to
 * maintain user profile synchronization.
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
     private final UserServiceClient userServiceClient;

     /**
      * Registers a new user with standard email and password credentials.
      * <p>
      * Registration only creates the user account — no JWT token is issued.
      * The client must call the login endpoint separately to obtain a token.
      *
      * @param registerUserRequest the DTO containing user registration details
      * @return RegisterResponse containing authId, userId, and email
      * @throws UserAlreadyExistsException if the email is already registered
      */
     @Transactional
     public RegisterResponse registerUser(RegisterUserRequest registerUserRequest) {
          log.info("Processing registration request for email: {}", registerUserRequest.getEmail());

          // Verify whether the user already exists in the local database
          if (authUsersRepo.findByEmail(registerUserRequest.getEmail()) != null) {
               log.warn("Registration rejected — email already exists: {}", registerUserRequest.getEmail());
               throw new UserAlreadyExistsException(
                         "User already exists with email: " + registerUserRequest.getEmail());
          }

          // Determine the appropriate role, defaulting to standard user privileges
          Roles role = registerUserRequest.getUserRole() != null
                    ? registerUserRequest.getUserRole()
                    : Roles.ROLE_USER;

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
          log.info("Auth credentials saved successfully with authId: {}", authUser.getAuthId());

          // Synchronize user profile with user-service and retrieve the generated userId
          UserProfileDto createdProfile = syncUserProfileWithUserService(
                    authUser.getAuthId(), registerUserRequest, role);

          log.info("Registration completed successfully for email: {} | authId: {} | userId: {}",
                    registerUserRequest.getEmail(), authUser.getAuthId(), createdProfile.getUserId());

          return RegisterResponse.builder()
                    .authId(authUser.getAuthId())
                    .userId(createdProfile.getUserId())
                    .email(authUser.getEmail())
                    .message("Registration successful. Please login to obtain an access token.")
                    .build();
     }

     /**
      * Authenticates a user's standard credentials (email and password) against
      * the Spring Security authentication manager and returns a JWT access token.
      *
      * @param authRequest the DTO containing login credentials
      * @return AuthResponse containing the generated JWT
      */
     public AuthResponse login(AuthRequest authRequest) {
          log.info("Processing login request for email: {}", authRequest.getEmail());

          // Delegate authentication validation to Spring Security's AuthenticationManager
          Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.debug("Authentication successful for email: {}", authRequest.getEmail());

          // Retrieve user entity to extract accurate role information for token generation
          AuthUsers user = authUsersRepo.findByEmail(authRequest.getEmail());
          if (user == null) {
               log.error("User not found in database after successful authentication: {}", authRequest.getEmail());
               throw new IllegalArgumentException("User not found after successful authentication");
          }

          // Generate secure JWT containing user identity and role claims
          String token = jwtTokenProvider.generateToken(user.getAuthId(), user.getEmail(), user.getRole());
          log.info("JWT token issued successfully for email: {}", authRequest.getEmail());
          return new AuthResponse(token);
     }

     /**
      * Validates a Google OAuth2 ID token, identifies or registers the corresponding
      * user, links the OAuth account, and generates a unified JWT access token.
      *
      * @param request the DTO containing the Google ID token and optional target role
      * @return AuthResponse containing the generated JWT
      */
     @Transactional
     public AuthResponse googleLogin(GoogleLoginRequest request) {
          log.info("Processing Google OAuth login request");

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
               syncGoogleUserProfileWithUserService(user.getAuthId(), email, givenName, familyName, picture,
                         user.getRole());
          } else {
               log.info("Existing user logged in via Google OAuth: {}", email);
          }

          // Issue a unified application JWT for downstream microservice authentication
          String token = jwtTokenProvider.generateToken(user.getAuthId(), user.getEmail(), user.getRole());
          log.info("JWT token issued for Google OAuth user: {}", email);
          return new AuthResponse(token);
     }

     /**
      * Invokes the Google OAuth2 public verification endpoint via RestClient to
      * confirm token authenticity and extract profile claims.
      *
      * @param idToken the raw Google ID token
      * @return Map containing the token claims and user profile details
      */
     private Map<String, Object> verifyGoogleIdToken(String idToken) {
          log.debug("Verifying Google ID token with Google tokeninfo endpoint");
          try {
               RestClient restClient = RestClient.create();
               Map<String, Object> tokenInfo = restClient.get()
                         .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken)
                         .retrieve()
                         .body(new ParameterizedTypeReference<Map<String, Object>>() {
                         });

               if (tokenInfo == null || !tokenInfo.containsKey("email")) {
                    throw new IllegalArgumentException("Invalid Google ID Token structure");
               }
               log.debug("Google ID token verified successfully for email: {}", tokenInfo.get("email"));
               return tokenInfo;
          } catch (Exception e) {
               log.error("Google token verification failed: {}", e.getMessage());
               throw new IllegalArgumentException("Google token verification failed: " + e.getMessage(), e);
          }
     }

     /**
      * Creates a new authentication record and OAuth linked account entity for users
      * registering via Google OAuth2.
      *
      * @param email         the user's primary email address
      * @param sub           the unique Google subject identifier
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
          log.info("Google auth user created with authId: {}", user.getAuthId());

          OauthLinkedAccounts linkedAccount = OauthLinkedAccounts.builder()
                    .authUsers(user)
                    .providerName("google")
                    .providerUserId(sub)
                    .build();
          oauthLinkedAccountsRepo.save(linkedAccount);
          log.debug("Google OAuth linked account saved for authId: {}", user.getAuthId());
          return user;
     }

     /**
      * Performs inter-service communication to transmit standard user profile data
      * to the standalone user-service microservice.
      *
      * @param authId  the unique authentication identity UUID
      * @param request the original registration payload
      * @param role    the assigned system role
      * @return the created UserProfileDto from user-service (contains generated userId)
      */
     private UserProfileDto syncUserProfileWithUserService(String authId, RegisterUserRequest request, Roles role) {
          log.info("Syncing user profile with user-service for authId: {}", authId);
          try {
               UserProfileDto userProfileDto = UserProfileDto.builder()
                         .authUserId(authId)
                         .firstName(request.getFirstName())
                         .middleName(request.getMiddleName())
                         .lastName(request.getLastName())
                         .email(request.getEmail())
                         .phoneNumber(request.getPhoneNumber())
                         .userRole(role)
                         .location(request.getLocation())
                         .build();

               log.debug("Sending user profile to user-service: {}", userProfileDto);
               UserProfileDto createdProfile = userServiceClient.createUser(userProfileDto);

               log.info("User profile synced successfully with user-service | authId: {} | userId: {}",
                         authId, createdProfile.getUserId());
               return createdProfile;
          } catch (Exception e) {
               log.error("Failed to sync user profile with user-service for authId: {} — {}", authId, e.getMessage());
               throw new RuntimeException("Failed to create user profile in user-service: " + e.getMessage(), e);
          }
     }

     /**
      * Performs inter-service communication to transmit Google OAuth user profile
      * data to the standalone user-service microservice.
      *
      * @param authId     the unique authentication identity UUID
      * @param email      the user's email address
      * @param givenName  the user's first name
      * @param familyName the user's last name
      * @param picture    the user's profile image URL
      * @param role       the assigned system role
      */
     private void syncGoogleUserProfileWithUserService(String authId, String email, String givenName, String familyName,
               String picture, Roles role) {
          log.info("Syncing Google user profile with user-service for authId: {}", authId);
          try {
               userServiceClient.createUser(UserProfileDto.builder()
                         .authUserId(authId)
                         .firstName(givenName)
                         .lastName(familyName)
                         .email(email)
                         .userRole(role)
                         .profileImageUrl(picture)
                         .build());
               log.info("Google user profile synced successfully with user-service for authId: {}", authId);
          } catch (Exception e) {
               log.error("Failed to sync Google user profile with user-service for authId: {} — {}", authId,
                         e.getMessage());
          }
     }
}
