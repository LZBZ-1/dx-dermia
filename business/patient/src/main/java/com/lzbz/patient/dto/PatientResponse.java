package com.lzbz.patient.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PatientResponse extends BasePatientDTO {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

