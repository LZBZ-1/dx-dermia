package com.lzbz.patient.service;

import com.lzbz.patient.model.PatientDemographics;
import com.lzbz.patient.repository.PatientDemographicsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientDemographicsService {
    private final PatientDemographicsRepository patientDemographicsRepository;

    public Mono<PatientDemographics> createPatient(PatientDemographics patient) {
        return patientDemographicsRepository.existsByNationalId(patient.getNationalId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new RuntimeException("Patient with this National ID already exists"));
                    }
                    patient.setCreatedAt(ZonedDateTime.now());
                    patient.setUpdatedAt(ZonedDateTime.now());
                    return patientDemographicsRepository.save(patient);
                })
                .doOnSuccess(p -> log.info("Created patient with ID: {}", p.getPatientId()))
                .doOnError(e -> log.error("Error creating patient: {}", e.getMessage()));
    }

    public Mono<PatientDemographics> updatePatient(Long patientId, PatientDemographics patient) {
        return patientDemographicsRepository.findById(patientId)
                .flatMap(existing -> {
                    patient.setPatientId(patientId);
                    patient.setCreatedAt(existing.getCreatedAt());
                    patient.setUpdatedAt(ZonedDateTime.now());
                    return patientDemographicsRepository.save(patient);
                })
                .doOnSuccess(p -> log.info("Updated patient with ID: {}", p.getPatientId()))
                .doOnError(e -> log.error("Error updating patient: {}", e.getMessage()));
    }

    public Mono<PatientDemographics> findByUserId(Long userId) {
        return patientDemographicsRepository.findByUserId(userId)
                .doOnSuccess(p -> log.debug("Found patient for user ID: {}", userId));
    }

    public Flux<PatientDemographics> searchByLastName(String lastName) {
        return patientDemographicsRepository.findByLastNameContainingIgnoreCase(lastName)
                .doOnComplete(() -> log.debug("Completed search for lastName: {}", lastName));
    }
}
