package com.ReunionSphere.LostAndFoundService.Dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CloudinaryResponse implements Serializable {

     @JsonProperty("asset_id")
     private String assetId;

     @JsonProperty("public_id")
     private String publicId;

     private Long version;

     @JsonProperty("version_id")
     private String versionId;

     private String signature;

     private Integer width;

     private Integer height;

     private String format;

     @JsonProperty("resource_type")
     private String resourceType;

     @JsonProperty("created_at")
     private String createdAt;

     private List<String> tags;

     private Long bytes;

     private String type;

     private String etag;

     private Boolean placeholder;

     private String url;

     @JsonProperty("secure_url")
     private String secureUrl;

     @JsonProperty("asset_folder")
     private String assetFolder;

     @JsonProperty("display_name")
     private String displayName;

     @JsonProperty("original_filename")
     private String originalFilename;

     @JsonProperty("api_key")
     private String apiKey;
}
