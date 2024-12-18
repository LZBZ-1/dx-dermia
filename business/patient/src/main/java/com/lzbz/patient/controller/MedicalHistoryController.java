package com.lzbz.patient.controller;

import com.lzbz.patient.client.AuthServiceClient;
import com.lzbz.patient.model.MedicalHistory;
import com.lzbz.patient.service.MedicalHistoryService;
import com.lzbz.patient.service.PatientDemographicsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/patients/medical-history")
@RequiredArgsConstructor
@Slf4j
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;
    private final PatientDemographicsService patientService;
    private final AuthServiceClient authServiceClient;

    @PostMapping
    public Mono<ResponseEntity<MedicalHistory>> createMedicalHistory(
            @Valid @RequestBody MedicalHistory history,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> {
                    history.setPatientId(patient.getPatientId());
                    return medicalHistoryService.createMedicalHistory(history);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<MedicalHistory>> getMyMedicalHistory(Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> medicalHistoryService.getLatestMedicalHistory(patient.getPatientId()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public Mono<ResponseEntity<MedicalHistory>> updateMyMedicalHistory(
            @Valid @RequestBody MedicalHistory history,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> medicalHistoryService.updateMedicalHistory(patient.getPatientId(), history))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}