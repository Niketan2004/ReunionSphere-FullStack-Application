package com.ReunionSphere.User_Service.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ReunionSphere.User_Service.Dto.SubscriptionDto;
import com.ReunionSphere.User_Service.Dto.RegistrationDto.CreateSubscriptionDto;
import com.ReunionSphere.User_Service.Services.SubscriptionService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
     private final SubscriptionService subscriptionService;

     // GETTING SUBSCRIPTION BY ID
     @GetMapping("/{subscriptionId}")
     public ResponseEntity<SubscriptionDto> getSubscriptionById(@PathVariable String subscriptionId) {
          return ResponseEntity.status(HttpStatus.FOUND)
                    .body(subscriptionService.findSubscriptionById(subscriptionId));
     }

     // GETTING SUBSCRIPTION BY EMAIL
     @GetMapping("/email/{emailId}")
     public ResponseEntity<SubscriptionDto> getSubscriptionByEmail(@PathVariable String emailId) {
          return ResponseEntity.status(HttpStatus.FOUND)
                    .body(subscriptionService.findSubscriptionByEmail(emailId));
     }

     // GETTING SUBSCRIPTION BY PHONE NUMBER
     @GetMapping("/phoneNumber/{phoneNumber}")
     public ResponseEntity<SubscriptionDto> getSubscriptionByPhoneNumber(@PathVariable String phoneNumber) {
          return ResponseEntity.status(HttpStatus.FOUND)
                    .body(subscriptionService.findSubscriptionByPhoneNumber(phoneNumber));
     }

     // GETTING ALL SUBSCRIPTIONS
     @GetMapping("/")
     public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
          return ResponseEntity.status(HttpStatus.FOUND).body(subscriptionService.findAllSubscriptions());
     }

     // UPDATING SUBSCRIPTION
     @PutMapping("/{id}")
     public ResponseEntity<SubscriptionDto> updateSubscription(@PathVariable String id,
               @RequestBody SubscriptionDto subscriptionDto) {
          return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(subscriptionService.updateSubscription(subscriptionDto));
     }

     // CREATING SUBSCRIPTION
     @PostMapping("/")
     public ResponseEntity<SubscriptionDto> createSubscription(@RequestBody CreateSubscriptionDto subscriptionDto) {
          return ResponseEntity.status(HttpStatus.CREATED)
                    .body(subscriptionService.createSubscription(subscriptionDto));
     }

     // DELETING SUBSCRIPTION BY ID
     @DeleteMapping("/{subscriptionId}")
     public ResponseEntity<Boolean> deleteSubscriptionById(@PathVariable String subscriptionId) {
          return ResponseEntity.status(HttpStatus.FOUND)
                    .body(subscriptionService.deleteSubscription(subscriptionId));
     }

     // DELETING SUBSCRIPTION BY EMAIL
     @DeleteMapping("/{emailId}")
     public ResponseEntity<Boolean> deleteSubscriptionByEmail(@PathVariable String emailId) {
          return ResponseEntity.status(HttpStatus.FOUND)
                    .body(subscriptionService.deleteSubscriptionByEmail(emailId));
     }

}
