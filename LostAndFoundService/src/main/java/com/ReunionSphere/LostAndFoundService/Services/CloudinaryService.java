package com.ReunionSphere.LostAndFoundService.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.LostAndFoundService.Dto.CloudinaryResponse;
import com.ReunionSphere.LostAndFoundService.Exceptions.ResourceNotFoundException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

     /**
      * Cloudinary client used for image upload, deletion,
      * and media asset management.
      */
     private final Cloudinary cloudinary;

     /**
      * Uploads multiple images to Cloudinary and returns metadata
      * required by the application such as secure URLs and public IDs.
      *
      * All images are stored inside the dedicated
      * "ReunionSphere_LostImages" folder to simplify media management.
      *
      * @param images list of images received from the client
      * @return list of uploaded image metadata
      * @throws RuntimeException when one or more uploads fail
      */
     public List<CloudinaryResponse> uploadImages(List<MultipartFile> images) {

          log.info("Received request to upload {} image(s) to Cloudinary",
                    images.size());
          List<CloudinaryResponse> uploadResponse = new ArrayList<>();
          ObjectMapper mapper = new ObjectMapper();

          for (MultipartFile image : images) {
               try {
                    log.debug("Uploading image: filename={}, size={} bytes",
                              image.getOriginalFilename(),
                              image.getSize());
                    Map<?, ?> result = cloudinary.uploader().upload(
                              image.getBytes(),
                              ObjectUtils.asMap(
                                        "folder",
                                        "ReunionSphere_LostImages"));
                    uploadResponse.add(
                              mapper.convertValue(
                                        result,
                                        CloudinaryResponse.class));
                    log.debug("Successfully uploaded image: filename={}",
                              image.getOriginalFilename());
               } catch (Exception e) {
                    log.error(
                              "Image upload failed. filename={}",
                              image.getOriginalFilename(),
                              e);
                    throw new RuntimeException(
                              "Failed to upload image(s) to Cloudinary");
               }
          }
          log.info("Successfully uploaded {} image(s) to Cloudinary",
                    uploadResponse.size());
          return uploadResponse;
     }

     /**
      * Deletes multiple images from Cloudinary using their public IDs.
      *
      * This operation is typically executed when:
      * - A report is deleted.
      * - Existing report images are replaced.
      * - Media cleanup is required.
      *
      * @param publicIds Cloudinary public identifiers of images
      *                  that should be removed
      * @throws ResourceNotFoundException when deletion fails
      */
     public void deleteProfileImages(List<String> publicIds) {
          log.info("Received request to delete {} image(s) from Cloudinary",
                    publicIds.size());
          try {
               cloudinary.api().deleteResources(
                         publicIds,
                         ObjectUtils.emptyMap());
               log.info(
                         "Successfully deleted {} image(s) from Cloudinary",
                         publicIds.size());
          } catch (Exception e) {
               log.error(
                         "Failed to delete image(s) from Cloudinary. publicIds={}",
                         publicIds,
                         e);
               throw new ResourceNotFoundException(
                         "Failed to delete images from Cloudinary");
          }
     }
}
