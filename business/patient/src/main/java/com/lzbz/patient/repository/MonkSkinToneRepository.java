package com.lzbz.patient.repository;

import com.lzbz.patient.model.MonkSkinTone;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MonkSkinToneRepository extends ReactiveCrudRepository<MonkSkinTone, Long> {
    Flux<MonkSkinTone> findByPatientId(Long patientId);
}


