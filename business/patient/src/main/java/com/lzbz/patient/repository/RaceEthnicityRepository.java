package com.lzbz.patient.repository;

import com.lzbz.patient.model.RaceEthnicity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RaceEthnicityRepository extends ReactiveCrudRepository<RaceEthnicity, Long> {
    Flux<RaceEthnicity> findByPatientId(Long patientId);
}

