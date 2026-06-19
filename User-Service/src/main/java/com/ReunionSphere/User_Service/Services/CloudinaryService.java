package com.ReunionSphere.User_Service.Services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.User_Service.Dto.CloudinaryResponse;
import com.ReunionSphere.User_Service.Exceptions.ResourceNotFoundException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
     // Injecting cloudinary config
     private final Cloudinary cloudinary;

     // Uploading Image to te Cloudinary
     public CloudinaryResponse uploadProfileImage(MultipartFile multipartFile) {
          try {
               Map<?, ?> result = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.asMap(
                         "folder", "ReunionSphere_UserService_ProfileImage"));
               ObjectMapper mapper = new ObjectMapper();
               return mapper.convertValue(result, CloudinaryResponse.class);
          } catch (Exception e) {
               throw new RuntimeException(e.getMessage());
          }

     }

     // Deleting image from cloudinary
     public void deleteProfileImage(String publicId) {
          try {
               cloudinary.uploader().destroy(publicId,
                         ObjectUtils.asMap("folder", "ReunionSphere_UserService_ProfileImage"));
               
          } catch (Exception e) {
               throw new ResourceNotFoundException(e.getMessage());
          }
     }
}
