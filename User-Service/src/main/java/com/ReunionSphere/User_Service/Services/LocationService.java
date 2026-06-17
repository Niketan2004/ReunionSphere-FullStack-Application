package com.ReunionSphere.User_Service.Services;

import com.ReunionSphere.User_Service.Services.SubscriptionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ReunionSphere.User_Service.Dto.LocationDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.RegisterLocation;
import com.ReunionSphere.User_Service.Entities.Location;
import com.ReunionSphere.User_Service.Exceptions.LocationNotFoundException;
import com.ReunionSphere.User_Service.Repositories.LocationRepo;
import com.ReunionSphere.User_Service.mappers.EntityMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {


     private final LocationRepo locationRepo;
     private final EntityMapper entityMapper;

 
     /**
      * Retrieves a user's location by its unique ID.
      *
      * @param locationId the unique identifier of the location
      * @return a {@link LocationDto} containing the location information
      * @throws LocationNotFoundException if the location cannot be found
      */
     @Cacheable(value = "locations", key = "#locationId")
     public LocationDto getUserLocation(String locationId) {
          log.info("Fetching location details for locationId: {}", locationId);
          Location location = locationRepo.findById(locationId).orElseThrow(
                    () -> {
                         log.error("Location not found with Id {}", locationId);
                         return new LocationNotFoundException("Location not found with Id " + locationId);
                    });
          return entityMapper.mapToLocationDto(location);
     }

     /**
      * Updates an existing user location based on the provided details.
      *
      * @param locationDto the {@link LocationDto} containing the updated information
      * @return the updated {@link LocationDto}
      * @throws IllegalArgumentException if the provided location DTO is null
      * @throws LocationNotFoundException if the location to update does not exist
      */
     @CachePut(value = "locations", key = "#locationDto.locationId")
     public LocationDto updateUserLocationDto(LocationDto locationDto) {
          if (locationDto == null) {
               log.error("Location details provided for update are null");
               throw new IllegalArgumentException("Location details should be provided");
          }
          log.info("Updating location details for locationId: {}", locationDto.getLocationId());
          Location oldLocation = locationRepo.findById(locationDto.getLocationId()).orElseThrow(
                    () -> {
                         log.error("Location does not exist with id {}", locationDto.getLocationId());
                         return new LocationNotFoundException(
                               "Location does not exists with id " + locationDto.getLocationId());
                    });
          oldLocation = entityMapper.mapToLocation(locationDto);
          locationRepo.saveAndFlush(oldLocation);
          log.info("Successfully updated location details for locationId: {}", locationDto.getLocationId());
          return entityMapper.mapToLocationDto(oldLocation);
     }

     /**
      * Creates a new location entry.
      *
      * @param registerLocation the registration details of the new location
      * @return the created {@link LocationDto}
      */
     public LocationDto createLocation(RegisterLocation registerLocation) {
          log.info("Creating a new location");
          Location location = new Location();
          location.setLandMark(registerLocation.getLandMark());
          location.setCity(registerLocation.getCity());
          location.setDistrict(registerLocation.getDistrict());
          location.setPinCode(registerLocation.getPinCode());
          location.setState(registerLocation.getState());
          location.setCountry(registerLocation.getCountry());
          location.setLatitude(registerLocation.getLatitude());
          location.setLongitude(registerLocation.getLongitude());
          location.setDescription(registerLocation.getDescription());
          location = locationRepo.saveAndFlush(location);
          log.info("Location created and updated at  {} and {} ", location.getCreatedAt(), location.getUpdatedAt());

          return entityMapper.mapToLocationDto(location);
     }

     /**
      * Deletes a user location by its unique ID.
      *
      * @param locationId the unique identifier of the location to delete
      * @return {@code true} if the deletion was successful
      * @throws LocationNotFoundException if the location cannot be found
      */
     @CacheEvict(value = "locations", key = "#locationId")
     public Boolean deleteLocation(String locationId) {
          log.info("Deleting location with locationId: {}", locationId);
          if (locationRepo.findById(locationId).isEmpty()) {
               log.error("Failed to delete. Location not found with Id {}", locationId);
               throw new LocationNotFoundException("Location not found with Id " + locationId);
          }
          locationRepo.deleteById(locationId);
          log.info("Successfully deleted location with locationId: {}", locationId);
          return true;
     }
}
