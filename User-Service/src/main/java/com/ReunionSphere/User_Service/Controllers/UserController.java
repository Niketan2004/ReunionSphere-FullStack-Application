package com.ReunionSphere.User_Service.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ReunionSphere.User_Service.Dto.UserProfileDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.RegisterUserDto;
import com.ReunionSphere.User_Service.Services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
     // Injecting User Service
     private final UserService userService;

     // Geting all Users
     @GetMapping("/")
     public ResponseEntity<List<UserProfileDto>> getAllUserProfiles() {
          return ResponseEntity.ok(userService.getAllUsers());
     }

     // Getting User profiles by Id
     @GetMapping("/id/{userId}")
     public ResponseEntity<UserProfileDto> getUserById(@PathVariable String userId) {
          return ResponseEntity.ok(userService.getUserProfile(userId));
     }

     // Getting User profiles by email
     @GetMapping("/email/{email}")
     public ResponseEntity<UserProfileDto> getUserByIEmail(@PathVariable String email) {
          return ResponseEntity.ok(userService.getUserProfileByEmail(email));
     }

     // Updating User Profile
     @PutMapping("/{userId}")
     public ResponseEntity<UserProfileDto> updateUser(@PathVariable String userId,
               @RequestBody UserProfileDto userProfileDto) {
          return ResponseEntity.ok(userService.updateUserProfile(userProfileDto));
     }

     // Creating User Profile
     @PostMapping("/")
     public ResponseEntity<UserProfileDto> createUser(@RequestBody RegisterUserDto registerUserDto) {
          return ResponseEntity.ok(userService.createUserProfile(registerUserDto));
     }

     // Creating User Profile
     @DeleteMapping("/{userId}")
     public ResponseEntity<Boolean> deleteUser(@PathVariable String userId) {
          return ResponseEntity.ok(userService.deleteUser(userId));
     }

}
