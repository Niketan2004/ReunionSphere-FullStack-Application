package com.ReunionSphere.User_Service.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ReunionSphere.User_Service.Dto.LocationDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.RegisterLocation;
import com.ReunionSphere.User_Service.Services.LocationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

     private final LocationService locationService;

     // Getting user location by id
     @GetMapping("/{locationId}")
     public ResponseEntity<LocationDto> getLocationById(@PathVariable String locationId) {
          return ResponseEntity.ok(locationService.getUserLocation(locationId));
     }

     // updating user location
     @PutMapping("/{locationId}")
     public ResponseEntity<LocationDto> updateLocation(@PathVariable String locationId,
               @RequestBody LocationDto locationDto) {
          return ResponseEntity.ok(locationService.updateUserLocationDto(locationDto));
     }

     // creating user location
     @PostMapping("/")
     public ResponseEntity<LocationDto> createLocation(@RequestBody RegisterLocation registeredLocation) {
          return ResponseEntity.ok(locationService.createLocation(registeredLocation));
     }

     // deleting user location
     @DeleteMapping("/{locationId}")
     public ResponseEntity<Boolean> deleteLocation(@PathVariable String id) {
          return ResponseEntity.ok(locationService.deleteLocation(id));
     }
}
