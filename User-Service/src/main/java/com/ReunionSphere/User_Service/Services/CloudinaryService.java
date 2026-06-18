package com.ReunionSphere.User_Service.Services;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
     // Injecting cloudinary config
     private final Cloudinary cloudinary;
// Uploading Image to te Cloudinary
     public String uploadProfileImage(MultipartFile multipartFile) {
          try {
               Map<?, ?> result = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
               return result.get("secure_url").toString();
          } catch (Exception e) {
               throw new RuntimeException(e.getMessage());
          }

     }
}
