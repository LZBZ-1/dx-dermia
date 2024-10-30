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
@Table("emergency_contacts")
public class EmergencyContact {
    @Id
    private Long emergencyContactId;
    private Long patientId;
    private String contactName;
    private String relationship;
    private String phone;
    private ZonedDateTime createdAt;
}
