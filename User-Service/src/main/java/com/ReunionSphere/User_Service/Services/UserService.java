package com.ReunionSphere.User_Service.Services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.User_Service.Dto.CloudinaryResponse;
import com.ReunionSphere.User_Service.Dto.UserProfileDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.CreateSubscriptionDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.RegisterUserDto;
import com.ReunionSphere.User_Service.Entities.Location;
import com.ReunionSphere.User_Service.Entities.UserProfiles;
import com.ReunionSphere.User_Service.Exceptions.UserAlreadyExistsException;
import com.ReunionSphere.User_Service.Exceptions.UserNotfoundException;
import com.ReunionSphere.User_Service.Repositories.UserProfilesRepo;
import com.ReunionSphere.User_Service.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

     private final UserProfilesRepo userProfilesRepo;
     private final EntityMapper entityMapper;
     private final LocationService locationService;
     private final SubscriptionService subscriptionService;
     private final CloudinaryService cloudinaryService;

     /**
      * Retrieves a user profile by their unique user ID.
      *
      * @param userId the unique identifier of the user
      * @return a {@link UserProfileDto} containing the user's profile information
      * @throws UserNotfoundException if the user cannot be found
      */
     @Cacheable(value = "users", key = "#userId")
     public UserProfileDto getUserProfile(String userId) {
          log.info("Fetching user profile for userId: {}", userId);
          UserProfiles userProfiles = userProfilesRepo.findById(userId).orElseThrow(
                    () -> {
                         log.error("User not found with Id {}", userId);
                         return new UserNotfoundException("User not found with Id " + userId);
                    });
          return entityMapper.toUserProfileDto(userProfiles);
     }

     /**
      * Retrieves a user profile by their email address.
      *
      * @param email the email address of the user
      * @return a {@link UserProfileDto} containing the user's profile information
      * @throws UserNotfoundException if no user is found with the given email
      */
     public UserProfileDto getUserProfileByEmail(String email) {
          log.info("Fetching user profile by email: {}", email);
          // finding userId in Db
          String userId = userProfilesRepo.findUserIdByEmail(email);
          if (userId == null) {
               log.error("User not found with email {}", email);
               throw new UserNotfoundException("User not found with email " + email);
          }
          return getUserProfile(userId);
     }

     /**
      * Retrieves all existing user profiles.
      *
      * @return a list of {@link UserProfileDto} objects representing all users
      */
     public List<UserProfileDto> getAllUsers() {
          return userProfilesRepo.findAll()
                    .stream()
                    .map(entityMapper::toUserProfileDto)
                    .toList();
     }

     /**
      * Updates an existing user profile based on the provided details.
      *
      * @param user the {@link UserProfileDto} containing the updated information
      * @return the updated {@link UserProfileDto}
      * @throws IllegalArgumentException if the provided user DTO is null
      * @throws UserNotfoundException    if the user to update does not exist
      */
     @CachePut(value = "users", key = "#user.userId")
     @Transactional
     public UserProfileDto updateUserProfile(MultipartFile image, UserProfileDto user) {
          if (user == null) {
               log.error("Failed to update user profile as user is null");
               throw new IllegalArgumentException("User details should be provided");
          }
          log.info("Updating user profile for userId: {}", user.getUserId());
          UserProfiles oldUser = userProfilesRepo.findById(user.getUserId()).orElseThrow(
                    () -> {
                         log.error("User does not exist with id {}", user.getUserId());
                         return new UserNotfoundException("User does not exists with id " + user.getUserId());
                    });
          // Uploading images to cloudinary
          if (image != null) {  
               log.debug("Uploading Profile Image to cloudinary");
               CloudinaryResponse response = uploadImage(image);
               user.setProfileImageUrl(response.getSecureUrl());
               user.setProfileImagePublicId(response.getPublicId());
               log.info("Profile Picture saved successfully");
          }

          entityMapper.updateUserProfileFromDto(user, oldUser);
          locationService.updateUserLocationDto(user.getLocation());
          userProfilesRepo.saveAndFlush(oldUser);
          log.info("Successfully updated user profile for userId: {}", user.getUserId());
          return entityMapper.toUserProfileDto(oldUser);
     }

     /**
      * Creates a new user profile, including their location and initial
      * subscription.
      *
      * @param registerUser the registration details of the new user
      * @return the created {@link UserProfileDto}
      * @throws IllegalArgumentException   if the registration details or auth ID are
      *                                    invalid
      * @throws UserAlreadyExistsException if a user with the same email or phone
      *                                    number already exists
      */
     @Transactional
     public UserProfileDto createUserProfile(RegisterUserDto registerUser) {
          log.info("Creating user profile for email: {}", registerUser != null ? registerUser.getEmail() : "null");
          if (registerUser == null) {
               log.error("Register user DTO is null");
               throw new IllegalArgumentException("User details should be provided");
          }

          if (userProfilesRepo.findUserIdByPhoneNumber(registerUser.getPhoneNumber()) != null
                    || userProfilesRepo.findByEmail(registerUser.getEmail()).isPresent()) {
               log.error("User Already Exists with email: {} or phone: {}", registerUser.getEmail(),
                         registerUser.getPhoneNumber());
               throw new UserAlreadyExistsException(
                         "User Already Exists please try with different email or phone Number");
          }
          if (registerUser.getAuthUserId() == null) {
               log.error("User is not Authenticated. AuthUserId is null");
               throw new IllegalArgumentException("User is not Authenticated !");
          }

          log.debug("Creating location for the user");
          Location savedLocation = entityMapper.mapToLocation(
                    locationService.createLocation(registerUser.getLocation()));

          UserProfiles userProfiles = new UserProfiles();
          

          userProfiles.setAuthUserId(registerUser.getAuthUserId());
          userProfiles.setFirstName(registerUser.getFirstName());
          userProfiles.setMiddleName(registerUser.getMiddleName());
          userProfiles.setLastName(registerUser.getLastName());
          userProfiles.setEmail(registerUser.getEmail());
          userProfiles.setPhoneNumber(registerUser.getPhoneNumber());
          userProfiles.setUserRole(registerUser.getUserRole());
          userProfiles.setLocation(savedLocation);

          userProfiles = userProfilesRepo.saveAndFlush(userProfiles);
          UserProfileDto userProfileDto = entityMapper.toUserProfileDto(userProfiles);

          CreateSubscriptionDto createSubscriptionDto = new CreateSubscriptionDto();
          createSubscriptionDto.setUserProfiles(userProfileDto);
          createSubscriptionDto.setEmail(registerUser.getEmail());
          createSubscriptionDto.setLocation(locationService.getUserLocation(savedLocation.getLocationId()));
          createSubscriptionDto.setPhoneNumber(registerUser.getPhoneNumber());
          createSubscriptionDto.setUserRole(registerUser.getUserRole());
          createSubscriptionDto.setIsActive(true);
          createSubscriptionDto.setIsVerified(true);

          subscriptionService.createSubscription(createSubscriptionDto);

          log.info("Successfully created user profile and subscription for email: {}", registerUser.getEmail());
          return userProfileDto;

     }

     /**
      * Deletes a user profile by their unique user ID.
      *
      * @param userId the unique identifier of the user to delete
      * @return {@code true} if the deletion was successful
      * @throws UserNotfoundException if the user cannot be found
      */
     @CacheEvict(value = "users", key = "#userId")
     @Transactional
     public Boolean deleteUser(String userId) {
          log.info("Deleting user profile for userId: {}", userId);
          UserProfiles user = userProfilesRepo.findById(userId).orElseThrow(
                    () -> {
                         log.error("User Not Found with User Id {}", userId);
                         return new UserNotfoundException("User Not Found with User Id " + userId);
                    });
          // Deleting Profile Image from cloudinary
          cloudinaryService.deleteProfileImage(user.getProfileImagePublicId());
          log.info("Deleted profile image from cloudinary");
          userProfilesRepo.deleteById(userId);
          log.info("Successfully deleted user profile for userId: {}", userId);
          return true;
     }

     // Uploading image to the cloudinary
     public CloudinaryResponse uploadImage(MultipartFile multipartFile) {
          if (multipartFile != null) {
               log.info("User has uploaded profile image. Setting it as profile picture ");
               return cloudinaryService.uploadProfileImage(multipartFile);
          } else {
               log.info("No profile Image found! Setting Default profile image");
               CloudinaryResponse cloudinaryResponse = new CloudinaryResponse();
               cloudinaryResponse.setPublicId("default pic");
               cloudinaryResponse.setSecureUrl(
                         "https://res.cloudinary.com/db0v87xw1/image/upload/v1781782898/default%20pic.jpg");
               return cloudinaryResponse;

          }

     }

}
