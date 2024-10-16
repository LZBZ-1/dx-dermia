package com.lzbz.patient.repository;

import com.lzbz.patient.model.SkinCondition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SkinConditionRepository extends ReactiveCrudRepository<SkinCondition, Long> {
    Flux<SkinCondition> findByPatientId(Long patientId);
}