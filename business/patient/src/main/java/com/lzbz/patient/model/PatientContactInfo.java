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
@Table("patient_contact_info")
public class PatientContactInfo {
    @Id
    private Long contactId;
    private Long patientId;
    private String address;
    private String phone;
    private String email;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}