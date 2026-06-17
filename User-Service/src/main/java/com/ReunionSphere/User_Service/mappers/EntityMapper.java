package com.ReunionSphere.User_Service.mappers;

import org.springframework.stereotype.Component;

import com.ReunionSphere.User_Service.Dto.LocationDto;
import com.ReunionSphere.User_Service.Dto.SubscriptionDto;
import com.ReunionSphere.User_Service.Dto.UserProfileDto;
import com.ReunionSphere.User_Service.Entities.Location;
import com.ReunionSphere.User_Service.Entities.Subscriptions;
import com.ReunionSphere.User_Service.Entities.UserProfiles;

@Component
public class EntityMapper {

     // ==========================
     // UserProfile Entity -> DTO
     // ==========================
     public UserProfileDto toUserProfileDto(UserProfiles userProfiles) {

          if (userProfiles == null) {
               return null;
          }

          UserProfileDto dto = new UserProfileDto();

          dto.setUserId(userProfiles.getUserId());
          dto.setAuthUserId(userProfiles.getAuthUserId());
          dto.setFirstName(userProfiles.getFirstName());
          dto.setMiddleName(userProfiles.getMiddleName());
          dto.setLastName(userProfiles.getLastName());
          dto.setEmail(userProfiles.getEmail());
          dto.setPhoneNumber(userProfiles.getPhoneNumber());
          dto.setUserRole(userProfiles.getUserRole());
          dto.setIsActive(userProfiles.getIsActive());
          dto.setIsVerified(userProfiles.getIsVerified());
          dto.setLocation(mapToLocationDto(userProfiles.getLocation()));
          dto.setProfileImageUrl(userProfiles.getProfileImageUrl());
          dto.setCreatedAt(userProfiles.getCreatedAt());
          dto.setUpdatedAt(userProfiles.getUpdatedAt());

          return dto;
     }

     // ==========================
     // UserProfile DTO -> Entity
     // ==========================
     public UserProfiles toUserProfileEntity(UserProfileDto dto) {

          if (dto == null) {
               return null;
          }

          UserProfiles user = new UserProfiles();

          user.setUserId(dto.getUserId());
          user.setAuthUserId(dto.getAuthUserId());
          user.setFirstName(dto.getFirstName());
          user.setMiddleName(dto.getMiddleName());
          user.setLastName(dto.getLastName());
          user.setEmail(dto.getEmail());
          user.setPhoneNumber(dto.getPhoneNumber());
          user.setUserRole(dto.getUserRole());
          user.setIsActive(dto.getIsActive());
          user.setIsVerified(dto.getIsVerified());
          user.setProfileImageUrl(dto.getProfileImageUrl());
          user.setLocation(mapToLocation(dto.getLocation()));

          return user;
     }

     // =====================================
     // Update Existing UserProfile Entity
     // =====================================
     public UserProfiles toUserProfiles(UserProfiles oldUser, UserProfileDto userDto) {

          oldUser.setFirstName(userDto.getFirstName());
          oldUser.setMiddleName(userDto.getMiddleName());
          oldUser.setLastName(userDto.getLastName());
          oldUser.setEmail(userDto.getEmail());
          oldUser.setPhoneNumber(userDto.getPhoneNumber());
          oldUser.setUserRole(userDto.getUserRole());
          oldUser.setIsActive(userDto.getIsActive());
          oldUser.setIsVerified(userDto.getIsVerified());
          oldUser.setLocation(mapToLocation(userDto.getLocation()));
          oldUser.setProfileImageUrl(userDto.getProfileImageUrl());

          return oldUser;
     }

     // ==========================
     // Location Entity -> DTO
     // ==========================
     public LocationDto mapToLocationDto(Location location) {

          if (location == null) {
               return null;
          }

          LocationDto dto = new LocationDto();

          dto.setLocationId(location.getLocationId());
          dto.setLandMark(location.getLandMark());
          dto.setCity(location.getCity());
          dto.setDistrict(location.getDistrict());
          dto.setPinCode(location.getPinCode());
          dto.setState(location.getState());
          dto.setCountry(location.getCountry());
          dto.setLatitude(location.getLatitude());
          dto.setLongitude(location.getLongitude());
          dto.setDescription(location.getDescription());
          dto.setCreatedAt(location.getCreatedAt());
          dto.setUpdatedAt(location.getUpdatedAt());

          return dto;
     }

     // ==========================
     // Location DTO -> Entity
     // ==========================
     public Location mapToLocation(LocationDto dto) {

          if (dto == null) {
               return null;
          }
          Location location = new Location();
          if (dto.getLocationId() != null) {
               location.setLocationId(dto.getLocationId());
          }
          location.setLandMark(dto.getLandMark());
          location.setCity(dto.getCity());
          location.setDistrict(dto.getDistrict());
          location.setPinCode(dto.getPinCode());
          location.setState(dto.getState());
          location.setCountry(dto.getCountry());
          location.setLatitude(dto.getLatitude());
          location.setLongitude(dto.getLongitude());
          location.setDescription(dto.getDescription());
          location.setCreatedAt(dto.getCreatedAt());
          location.setUpdatedAt(dto.getUpdatedAt());

          return location;
     }

     // ==========================
     // Subscription Entity -> DTO
     // ==========================
     public SubscriptionDto toSubscriptionDto(Subscriptions subscription) {

          if (subscription == null) {
               return null;
          }

          SubscriptionDto dto = new SubscriptionDto();

          dto.setSubscriptionId(subscription.getSubscriptionId());
          dto.setUserProfiles(toUserProfileDto(subscription.getUserProfiles()));
          dto.setUserRole(subscription.getUserRole());
          dto.setLocation(mapToLocationDto(subscription.getLocation()));
          dto.setEmail(subscription.getEmail());
          dto.setPhoneNumber(subscription.getPhoneNumber());
          dto.setIsActive(subscription.getIsActive());
          dto.setIsVerified(subscription.getIsVerified());
          dto.setCreatedAt(subscription.getCreatedAt());
          dto.setUpdatedAt(subscription.getUpdatedAt());

          return dto;
     }

     // ==========================
     // Subscription DTO -> Entity
     // ==========================
     public Subscriptions toSubscriptionEntity(SubscriptionDto dto) {

          if (dto == null) {
               return null;
          }

          Subscriptions subscription = new Subscriptions();

          subscription.setSubscriptionId(dto.getSubscriptionId());
          subscription.setUserProfiles(toUserProfileEntity(dto.getUserProfiles()));
          subscription.setUserRole(dto.getUserRole());
          subscription.setLocation(mapToLocation(dto.getLocation()));
          subscription.setEmail(dto.getEmail());
          subscription.setPhoneNumber(dto.getPhoneNumber());
          subscription.setIsActive(dto.getIsActive());
          subscription.setIsVerified(dto.getIsVerified());
          subscription.setCreatedAt(dto.getCreatedAt());
          subscription.setUpdatedAt(dto.getUpdatedAt());

          return subscription;
     }
}