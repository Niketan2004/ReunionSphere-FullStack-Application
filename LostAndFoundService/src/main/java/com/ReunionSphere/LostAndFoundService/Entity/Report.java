package com.ReunionSphere.LostAndFoundService.Entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ReunionSphere.LostAndFoundService.Enums.EntityType;
import com.ReunionSphere.LostAndFoundService.Enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lost_reports")
public class Report {
     @Id
     private String reportId;
     // This id links to the user from user Service
     @NotBlank
     private String reporterId;

     @NotNull
     private EntityType entityType;

     @NotNull
     private Status status;

     @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
     @NotNull
     private GeoJsonPoint lostLocation; // This is the Location details of lost one

     private Map<String, Object> customAddress; // this is Custom address details given by users

     @NotNull
     private LocalDateTime incidentDate;

     @NotNull
     private List<String> imageUrls;

     @NotNull
     private List<String> publicId;

     // Polymorphic map to store specific attributes (e.g., pet breed, vehicle
     // license plate)
     @NotNull
     private Map<String, Object> lostEntityDetails;
     

     @CreatedDate
     private LocalDateTime createdAt;

     @LastModifiedDate
     private LocalDateTime updatedAt;

}
