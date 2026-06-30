package com.ReunionSphere.LostAndFoundService.Dto.LostEntity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
     private String fullName;
     private String gender;
     private Integer age;
     private Float height;
     private String hairColor;
     private String eyeColor;
     private String clothingLastSeen;
     private String identifyingFeatures;
     private String medicalConditions;
     private List<String> languagesSpoken;
     private String otherDescription;
     
}
