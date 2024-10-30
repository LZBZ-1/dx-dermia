package com.lzbz.patient.repository;

import com.lzbz.patient.model.EmergencyContact;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmergencyContactRepository extends ReactiveCrudRepository<EmergencyContact, Long> {
    Flux<EmergencyContact> findByPatientId(Long patientId);
    Flux<EmergencyContact> findByPatientIdAndContactNameContainingIgnoreCase(Long patientId, String contactName);

    Flux<EmergencyContact> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
