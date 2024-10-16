package com.lzbz.patient.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("race_ethnicity")
public class RaceEthnicity {
    @Id
    private Long id;

    @Column("patient_id")
    private Long patientId;

    private String category;
}
