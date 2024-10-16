package com.lzbz.patient.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseSkinConditionDTO {
    private Long patientId;
    private String caseId;
    private String release;
    private List<String> textures;
    private List<String> bodyParts;
    private List<String> conditionSymptoms;
    private List<String> otherSymptoms;
    private String relatedCategory;
    private String conditionDuration;
}