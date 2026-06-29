package com.ReunionSphere.User_Service.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.User_Service.Dto.CloudinaryResponse;
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
          return ResponseEntity.status(HttpStatus.FOUND).body(userService.getAllUsers());
     }

     // Getting User profiles by Id
     @GetMapping("/id/{userId}")
     public ResponseEntity<UserProfileDto> getUserById(@PathVariable String userId) {
          return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserProfile(userId));
     }

     // Getting User profiles by email
     @GetMapping("/email/{email}")
     public ResponseEntity<UserProfileDto> getUserByIEmail(@PathVariable String email) {
          return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserProfileByEmail(email));
     }

     // Updating User Profile
     @PutMapping("/{userId}")
     public ResponseEntity<UserProfileDto> updateUser(@PathVariable String userId,
               @RequestPart(value = "profileImage", required = false) MultipartFile image,
               @RequestPart(value = "registerUserDto") UserProfileDto userProfileDto) {
          return ResponseEntity.status(HttpStatus.FOUND).body(userService.updateUserProfile(image, userProfileDto));
     }

     // Creating User Profile
     @PostMapping("/")
     public ResponseEntity<UserProfileDto> createUser(
            @RequestBody RegisterUserDto registerUserDto) {
          return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUserProfile(registerUserDto));
     }

     // Creating User Profile
     @DeleteMapping("/{userId}")
     public ResponseEntity<Boolean> deleteUser(@PathVariable String userId) {
          return ResponseEntity.ok(userService.deleteUser(userId));
     }

     // Image uploading
     @PostMapping("/profile-image")
     public ResponseEntity<CloudinaryResponse> updateProfileImage(@RequestParam MultipartFile imageFile) {
          return ResponseEntity.ok(userService.uploadImage(imageFile));
     }

}
