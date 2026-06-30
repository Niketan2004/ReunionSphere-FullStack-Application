package com.ReunionSphere.LostAndFoundService.Dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto implements Serializable{
     private Double longitude;
     private Double latitude;
}
