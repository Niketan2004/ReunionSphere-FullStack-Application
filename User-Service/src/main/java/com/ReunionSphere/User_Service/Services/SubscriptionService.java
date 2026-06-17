package com.ReunionSphere.User_Service.Services;

import com.ReunionSphere.User_Service.Services.LocationService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ReunionSphere.User_Service.Dto.SubscriptionDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.CreateSubscriptionDto;
import com.ReunionSphere.User_Service.Entities.Subscriptions;
import com.ReunionSphere.User_Service.Exceptions.SubscriptionNotFoundException;
import com.ReunionSphere.User_Service.Repositories.SubscriptionsRepo;
import com.ReunionSphere.User_Service.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
     private final LocationService locationService;
     private final SubscriptionsRepo subscriptionsRepo;
     private final EntityMapper entityMapper;


     /**
      * Creates a new subscription for a user.
      *
      * @param createSubscription the details required to create the subscription
      * @return the created {@link SubscriptionDto}
      * @throws IllegalArgumentException if the provided DTO is null
      */
     public SubscriptionDto createSubscription(CreateSubscriptionDto createSubscription) {
          log.info("Creating subscription for email: {}", createSubscription != null ? createSubscription.getEmail() : "null");
          if (createSubscription == null) {
               log.error("Subscription could not be null");
               throw new IllegalArgumentException("Subscription could not be null");
          }
          Subscriptions subscriptions = new Subscriptions();
          subscriptions.setUserProfiles(entityMapper.toUserProfileEntity(createSubscription.getUserProfiles()));
          subscriptions.setUserRole(createSubscription.getUserRole());
          subscriptions.setLocation(entityMapper.mapToLocation(createSubscription.getLocation()));
          subscriptions.setEmail(createSubscription.getEmail());
          subscriptions.setPhoneNumber(createSubscription.getPhoneNumber());
          subscriptions.setIsActive(true);
          subscriptions.setIsVerified(createSubscription.getIsVerified());
          subscriptions = subscriptionsRepo.saveAndFlush(subscriptions);
          log.info("Successfully created subscription for email: {}", createSubscription.getEmail());
          return entityMapper.toSubscriptionDto(subscriptions);
     }

     /**
      * Updates an existing subscription based on the provided details.
      *
      * @param subscriptionDto the {@link SubscriptionDto} containing the updated information
      * @return the updated {@link SubscriptionDto}
      * @throws IllegalArgumentException if the provided DTO is null
      * @throws SubscriptionNotFoundException if the subscription does not exist
      */
     @CachePut(value = "subscriptions", key = "#subscriptionDto.subscriptionId")
     public SubscriptionDto updateSubscription(SubscriptionDto subscriptionDto) {
          if (subscriptionDto == null) {
               log.error("Failed to update Subscription as subscriptionDto is null ");
               throw new IllegalArgumentException("Subscription could not be null");
          }
          log.info("Updating subscription with ID: {}", subscriptionDto.getSubscriptionId());
          if (!subscriptionsRepo.existsById(subscriptionDto.getSubscriptionId())
                    || subscriptionsRepo.findByEmail(subscriptionDto.getEmail()) == null) {
               log.error("User not found with id {} or email {}", subscriptionDto.getSubscriptionId(),
                         subscriptionDto.getEmail());
               throw new SubscriptionNotFoundException(
                         "Subscription not found with id " + subscriptionDto.getSubscriptionId());

          }
          Subscriptions subscriptions = subscriptionsRepo.saveAndFlush(entityMapper.toSubscriptionEntity(subscriptionDto));
          locationService.updateUserLocationDto(subscriptionDto.getLocation());
          subscriptionDto.setSubscriptionId(subscriptions.getSubscriptionId());
          log.info("Successfully updated subscription with ID: {}", subscriptionDto.getSubscriptionId());
          return subscriptionDto;
     }

     /**
      * Retrieves a subscription by its unique ID.
      *
      * @param subscriptionId the unique identifier of the subscription
      * @return a {@link SubscriptionDto} containing the subscription information
      * @throws SubscriptionNotFoundException if the subscription cannot be found
      */
     @Cacheable(value = "subscriptions", key = "#subscriptionId")
     public SubscriptionDto findSubscriptionById(String subscriptionId) {
          log.info("Fetching subscription by ID: {}", subscriptionId);
          return entityMapper.toSubscriptionDto(subscriptionsRepo.findById(
                    subscriptionId)
                    .orElseThrow(
                              () -> {
                                   log.error("Subscription not found with id {}", subscriptionId);
                                   return new SubscriptionNotFoundException(
                                        "Subscription not found with id " + subscriptionId);
                              }));
     }

     /**
      * Retrieves a subscription by the associated email address.
      *
      * @param email the email address of the user
      * @return a {@link SubscriptionDto} containing the subscription information
      * @throws SubscriptionNotFoundException if the subscription cannot be found for the email
      */
     public SubscriptionDto findSubscriptionByEmail(String email) {
          log.info("Fetching subscription by email: {}", email);
          String id = subscriptionsRepo.findSubscriptionIdByEmail(email);
          if (id == null) {
               log.error("Subscription not found with email {}", email);
               throw new SubscriptionNotFoundException("Subscription not found with email " + email);

          }
          return findSubscriptionById(id);
     }

     /**
      * Retrieves a subscription by the associated phone number.
      *
      * @param num the phone number of the user
      * @return a {@link SubscriptionDto} containing the subscription information
      * @throws SubscriptionNotFoundException if the subscription cannot be found for the phone number
      */
     public SubscriptionDto findSubscriptionByPhoneNumber(String num) {
          log.info("Fetching subscription by phone number: {}", num);
          String id = subscriptionsRepo.findSubscriptionIdByPhoneNumber(num);
          if (id == null) {
               log.error("Subscription not found having phone number  {}", num);
               throw new SubscriptionNotFoundException("Subscription not found with phone number  " + num);

          }
          return findSubscriptionById(id);
     }

     /**
      * Retrieves all existing subscriptions.
      *
      * @return a list of {@link SubscriptionDto} objects representing all subscriptions
      */
     public List<SubscriptionDto> findAllSubscriptions() {
          return subscriptionsRepo.findAll()
                    .stream()
                    .map(entityMapper::toSubscriptionDto)
                    .toList();
     }

     /**
      * Deletes a subscription by its unique ID.
      *
      * @param subscriptionId the unique identifier of the subscription to delete
      * @return {@code true} if the deletion was successful
      * @throws SubscriptionNotFoundException if the subscription cannot be found
      */
     @CacheEvict(value = "subscriptions", key = "#subscriptionId")
     public Boolean deleteSubscription(String subscriptionId) {
          log.info("Fetching Subscription for id {}", subscriptionId);
          Subscriptions subs = subscriptionsRepo.findById(
                    subscriptionId).orElseThrow(
                              () -> new SubscriptionNotFoundException(
                                        "Subscription not found with Id " + subscriptionId));
          log.info("Found Subscription for Id {} ", subscriptionId);
          subscriptionsRepo.delete(subs);
          log.info("Deleted Subscription for Id {} ", subscriptionId);
          return true;
     }

     /**
      * Deletes a subscription by the associated email address.
      *
      * @param email the email address associated with the subscription to delete
      * @return {@code true} if the deletion was successful
      * @throws IllegalArgumentException if the email is null or empty
      */
     public Boolean deleteSubscriptionByEmail(String email) {
          if (email == null || email.isEmpty()) {
               log.error("Email is Null or empty !");
               throw new IllegalArgumentException("Email should not be empty !");
          }
          log.info("Deleting subscription for email: {}", email);
          String id = subscriptionsRepo.findSubscriptionIdByEmail(email);
          return deleteSubscription(id);
     }
}
