package com.lzbz.patient.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("skin_condition")
public class SkinCondition {
    @Id
    private Long id;

    @Column("patient_id")
    private Long patientId;

    @Column("case_id")
    private String caseId;

    private String release;
    private List<String> textures;

    @Column("body_parts")
    private List<String> bodyParts;

    @Column("condition_symptoms")
    private List<String> conditionSymptoms;

    @Column("other_symptoms")
    private List<String> otherSymptoms;

    @Column("related_category")
    private String relatedCategory;

    @Column("condition_duration")
    private String conditionDuration;
}