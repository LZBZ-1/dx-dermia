package com.lzbz.patient.controller;

import com.lzbz.patient.client.AuthServiceClient;
import com.lzbz.patient.model.EmergencyContact;
import com.lzbz.patient.service.EmergencyContactService;
import com.lzbz.patient.service.PatientDemographicsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/patients/emergency-contacts")
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;
    private final PatientDemographicsService patientService;
    private final AuthServiceClient authServiceClient;

    @PostMapping
    public Mono<ResponseEntity<EmergencyContact>> addEmergencyContact(
            @Valid @RequestBody EmergencyContact contact,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> {
                    contact.setPatientId(patient.getPatientId());
                    return emergencyContactService.addEmergencyContact(contact);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .doOnError(e -> log.error("Error adding emergency contact: {}", e.getMessage()));
    }

    @GetMapping
    public Flux<EmergencyContact> getMyEmergencyContacts(Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMapMany(patient -> emergencyContactService.getEmergencyContacts(patient.getPatientId()));
    }

    @DeleteMapping("/{contactId}")
    public Mono<ResponseEntity<Void>> deleteEmergencyContact(
            @PathVariable Long contactId,
            Authentication authentication) {
        return authServiceClient.getUserIdFromUsername(authentication.getName())
                .flatMap(patientService::findByUserId)
                .flatMap(patient -> emergencyContactService.deleteEmergencyContact(contactId))
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}