package com.ReuinonSphere.LostAndFoundService.Dto.LostEntity;


import com.ReuinonSphere.LostAndFoundService.Enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleDto {
     private VehicleType vehicleType;
     private String make;
     private String model;
     private Integer year;
     private String color;
     private String licensePlate;
     private String vinOrChassisNumber;
     private String enginNumber;
     private String identifyingFeature;
     private String other;

}
