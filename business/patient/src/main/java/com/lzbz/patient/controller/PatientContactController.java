package com.lzbz.patient.controller;

import com.lzbz.patient.model.PatientContactInfo;
import com.lzbz.patient.service.PatientContactService;
import com.lzbz.patient.service.PatientDemographicsService;
import com.lzbz.patient.client.AuthServiceClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/patients/contacts")
@RequiredArgsConstructor
@Slf4j
public class PatientContactController {

    private final PatientContactService contactService;
    private final PatientDemographicsService patientService;
    private final AuthServiceClient authServiceClient;

    @PostMapping
    public Mono<ResponseEntity<PatientContactInfo>> createContactInfo(
            @Valid @RequestBody PatientContactInfo contactInfo,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(userId -> patientService.findByUserId(userId))
                .flatMap(patient -> {
                    contactInfo.setPatientId(patient.getPatientId());
                    return contactService.createContactInfo(contactInfo);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .doOnError(e -> log.error("Error creating contact info: {}", e.getMessage()));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<PatientContactInfo>> getMyContactInfo(Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> contactService.getContactInfo(patient.getPatientId()))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public Mono<ResponseEntity<PatientContactInfo>> updateMyContactInfo(
            @Valid @RequestBody PatientContactInfo contactInfo,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> contactService.updateContactInfo(patient.getPatientId(), contactInfo))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}