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
@Table("monk_skin_tone")
public class MonkSkinTone {
    @Id
    private Long id;

    @Column("patient_id")
    private Long patientId;

    @Column("gradable_india")
    private Boolean gradableIndia;

    @Column("gradable_us")
    private Boolean gradableUs;

    @Column("label_india")
    private Integer labelIndia;

    @Column("label_us")
    private Integer labelUs;
}