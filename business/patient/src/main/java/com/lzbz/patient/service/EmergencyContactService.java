package com.lzbz.patient.service;

import com.lzbz.patient.model.EmergencyContact;
import com.lzbz.patient.repository.EmergencyContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactService {
    private final EmergencyContactRepository emergencyContactRepository;

    public Mono<EmergencyContact> addEmergencyContact(EmergencyContact contact) {
        contact.setCreatedAt(ZonedDateTime.now());
        return emergencyContactRepository.save(contact)
                .doOnSuccess(c -> log.info("Added emergency contact for patient ID: {}", c.getPatientId()));
    }

    public Flux<EmergencyContact> getEmergencyContacts(Long patientId) {
        return emergencyContactRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .doOnComplete(() -> log.debug("Retrieved emergency contacts for patient ID: {}", patientId));
    }

    public Mono<Void> deleteEmergencyContact(Long contactId) {
        return emergencyContactRepository.deleteById(contactId)
                .doOnSuccess(v -> log.info("Deleted emergency contact ID: {}", contactId));
    }
}
