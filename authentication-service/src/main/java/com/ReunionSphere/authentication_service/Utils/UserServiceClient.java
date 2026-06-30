package com.ReunionSphere.authentication_service.Utils;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ReunionSphere.authentication_service.Dto.UserProfileDto;


@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

     @PostMapping("/api/v1/users/")
     UserProfileDto createUser(@RequestBody UserProfileDto dto);
}