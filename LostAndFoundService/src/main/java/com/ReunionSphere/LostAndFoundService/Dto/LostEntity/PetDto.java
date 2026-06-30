package com.ReunionSphere.LostAndFoundService.Dto.LostEntity;

import com.ReunionSphere.LostAndFoundService.Enums.Species;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetDto {
     private Species species;
     private String breed;
     private String name;
     private String gender;
     private String ageGroup;
     private String primaryColor;
     private String secondaryColor;
     private String size;
     private String collorDiscreption;
     private String microChipedId;
     private String other;

}
