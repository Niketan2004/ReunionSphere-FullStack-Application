package com.ReunionSphere.LostAndFoundService.Dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import com.ReunionSphere.LostAndFoundService.Enums.EntityType;
import com.ReunionSphere.LostAndFoundService.Enums.Status;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RegisterReportDto implements Serializable{
     private String reporterId;
     private EntityType entityType;
     private Status status;
     private LocationDto lostLocation; // This is the Location details of lost one
     private Map<String, Object> customAddress; // this is Custom address details given by users
     private LocalDateTime incidentDate;
     private List<String> imageUrls;
     private List<String> publicId;
     private Map<String, Object> lostEntityDetails;
}
