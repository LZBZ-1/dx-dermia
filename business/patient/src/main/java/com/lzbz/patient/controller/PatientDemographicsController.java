package com.lzbz.patient.controller;

import com.lzbz.patient.client.AuthServiceClient;
import com.lzbz.patient.model.PatientDemographics;
import com.lzbz.patient.service.PatientDemographicsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientDemographicsController {

    private final PatientDemographicsService patientService;
    private final AuthServiceClient authServiceClient;

    @PostMapping
    public Mono<ResponseEntity<PatientDemographics>> createPatient(
            @Valid @RequestBody PatientDemographics patient,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(userId -> {
                    patient.setUserId(userId);
                    return patientService.createPatient(patient);
                })
                .map(ResponseEntity::ok)
                .doOnSuccess(p -> log.info("Created patient profile"))
                .onErrorResume(e -> {
                    log.error("Error creating patient: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<PatientDemographics>> getMyProfile(
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{patientId}")
    public Mono<ResponseEntity<?>> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientDemographics patient,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(userId -> patientService.findByUserId(userId)
                        .flatMap(existing -> {
                            if (!existing.getPatientId().equals(patientId)) {
                                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
                            }
                            return patientService.updatePatient(patientId, patient)
                                    .map(ResponseEntity::ok);
                        }))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<PatientDemographics> searchPatients(@RequestParam String lastName) {
        return patientService.searchByLastName(lastName);
    }
}