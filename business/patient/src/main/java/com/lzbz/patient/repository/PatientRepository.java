package com.lzbz.patient.repository;

import com.lzbz.patient.model.Patient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PatientRepository extends ReactiveCrudRepository<Patient, Long> {
    Mono<Patient> findByUserId(Integer userId);
}