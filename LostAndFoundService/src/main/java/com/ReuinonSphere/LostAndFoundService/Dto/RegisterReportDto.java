package com.ReuinonSphere.LostAndFoundService.Dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import com.ReuinonSphere.LostAndFoundService.Enums.EntityType;
import com.ReuinonSphere.LostAndFoundService.Enums.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RegisterReportDto {
     private String reporterId;
     private EntityType entityType;
     private Status status;
     private GeoJsonPoint lostLocation; // This is the Location details of lost one
     private Map<String, Object> customAddress; // this is Custom address details given by users
     private LocalDateTime incidentDate;
     private List<String> imageUrls;
     private List<String> publicId;
     private Map<String, Object> details;
}
