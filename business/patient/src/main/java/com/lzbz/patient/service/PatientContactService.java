package com.lzbz.patient.service;

import com.lzbz.patient.model.PatientContactInfo;
import com.lzbz.patient.repository.PatientContactInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientContactService {
    private final PatientContactInfoRepository contactInfoRepository;

    public Mono<PatientContactInfo> createContactInfo(PatientContactInfo contactInfo) {
        contactInfo.setCreatedAt(ZonedDateTime.now());
        contactInfo.setUpdatedAt(ZonedDateTime.now());
        return contactInfoRepository.save(contactInfo)
                .doOnSuccess(c -> log.info("Created contact info for patient ID: {}", c.getPatientId()))
                .doOnError(e -> log.error("Error creating contact info: {}", e.getMessage()));
    }

    public Mono<PatientContactInfo> updateContactInfo(Long patientId, PatientContactInfo contactInfo) {
        return contactInfoRepository.findByPatientId(patientId)
                .flatMap(existing -> {
                    contactInfo.setContactId(existing.getContactId());
                    contactInfo.setCreatedAt(existing.getCreatedAt());
                    contactInfo.setUpdatedAt(ZonedDateTime.now());
                    return contactInfoRepository.save(contactInfo);
                })
                .doOnSuccess(c -> log.info("Updated contact info for patient ID: {}", patientId));
    }

    public Mono<PatientContactInfo> getContactInfo(Long patientId) {
        return contactInfoRepository.findByPatientId(patientId)
                .doOnSuccess(c -> log.debug("Retrieved contact info for patient ID: {}", patientId));
    }
}
