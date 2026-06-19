package com.ReunionSphere.User_Service.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.ReunionSphere.User_Service.Dto.LocationDto;
import com.ReunionSphere.User_Service.Dto.SubscriptionDto;
import com.ReunionSphere.User_Service.Dto.UserProfileDto;
import com.ReunionSphere.User_Service.Entities.Location;
import com.ReunionSphere.User_Service.Entities.Subscriptions;
import com.ReunionSphere.User_Service.Entities.UserProfiles;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMapper {

     // ==========================
     // UserProfile
     // ==========================

     UserProfileDto toUserProfileDto(UserProfiles userProfiles);

     UserProfiles toUserProfileEntity(UserProfileDto dto);

     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     void updateUserProfileFromDto(
               UserProfileDto dto,
               @MappingTarget UserProfiles entity);

     // ==========================
     // Location
     // ==========================

     LocationDto mapToLocationDto(Location location);

     Location mapToLocation(LocationDto dto);

     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     void updateLocationFromDto(
               LocationDto dto,
               @MappingTarget Location entity);

     // ==========================
     // Subscription
     // ==========================

     SubscriptionDto toSubscriptionDto(Subscriptions subscription);

     Subscriptions toSubscriptionEntity(SubscriptionDto dto);

     @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     void updateSubscriptionFromDto(
               SubscriptionDto dto,
               @MappingTarget Subscriptions entity);
}