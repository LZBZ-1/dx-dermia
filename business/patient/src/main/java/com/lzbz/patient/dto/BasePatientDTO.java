package com.lzbz.patient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BasePatientDTO {
    private Integer userId;
    private String source;
    private Integer year;
    private String ageGroup;
    private String sexAtBirth;
    private String fitzpatrickSkinType;
}