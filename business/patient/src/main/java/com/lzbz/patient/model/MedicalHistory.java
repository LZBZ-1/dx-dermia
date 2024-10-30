package com.lzbz.patient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("medical_history")
public class MedicalHistory {
    @Id
    private Long historyId;
    private Long patientId;
    private String skinType;
    private Boolean familyHistoryMelanoma;
    private String previousSkinConditions;
    private String sunExposureHistory;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}