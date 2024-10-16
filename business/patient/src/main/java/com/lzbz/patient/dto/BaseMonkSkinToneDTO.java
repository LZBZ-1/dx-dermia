package com.lzbz.patient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseMonkSkinToneDTO {
    private Long patientId;
    private Boolean gradableIndia;
    private Boolean gradableUs;
    private Integer labelIndia;
    private Integer labelUs;
}
