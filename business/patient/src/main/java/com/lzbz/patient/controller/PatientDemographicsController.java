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

import java.time.ZonedDateTime;

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

    @PutMapping("/me")
    public Mono<ResponseEntity<PatientDemographics>> updateMyPatientInfo(
            @Valid @RequestBody PatientDemographics patient,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(userId -> patientService.findByUserId(userId))
                .flatMap(existingPatient -> {
                    // Mantenemos los campos que no deben cambiar
                    patient.setPatientId(existingPatient.getPatientId());
                    patient.setUserId(existingPatient.getUserId());  // Importante: mantener el userId
                    patient.setCreatedAt(existingPatient.getCreatedAt());
                    patient.setUpdatedAt(ZonedDateTime.now());

                    return patientService.updatePatient(existingPatient.getPatientId(), patient);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public Flux<PatientDemographics> searchPatients(@RequestParam String lastName) {
        return patientService.searchByLastName(lastName);
    }
}