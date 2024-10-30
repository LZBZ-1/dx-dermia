package com.lzbz.patient.service;

import com.lzbz.patient.model.MedicalHistory;
import com.lzbz.patient.repository.MedicalHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalHistoryService {
    private final MedicalHistoryRepository medicalHistoryRepository;

    public Mono<MedicalHistory> createMedicalHistory(MedicalHistory history) {
        history.setCreatedAt(ZonedDateTime.now());
        history.setUpdatedAt(ZonedDateTime.now());
        return medicalHistoryRepository.save(history)
                .doOnSuccess(h -> log.info("Created medical history for patient ID: {}", h.getPatientId()));
    }

    public Mono<MedicalHistory> updateMedicalHistory(Long patientId, MedicalHistory history) {
        return medicalHistoryRepository.findByPatientId(patientId)
                .flatMap(existing -> {
                    history.setHistoryId(existing.getHistoryId());
                    history.setCreatedAt(existing.getCreatedAt());
                    history.setUpdatedAt(ZonedDateTime.now());
                    return medicalHistoryRepository.save(history);
                })
                .doOnSuccess(h -> log.info("Updated medical history for patient ID: {}", patientId));
    }

    public Mono<MedicalHistory> getLatestMedicalHistory(Long patientId) {
        return medicalHistoryRepository.findLatestByPatientId(patientId)
                .doOnSuccess(h -> log.debug("Retrieved latest medical history for patient ID: {}", patientId));
    }
}