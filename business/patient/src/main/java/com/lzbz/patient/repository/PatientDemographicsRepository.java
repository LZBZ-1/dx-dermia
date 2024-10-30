package com.lzbz.patient.repository;

import com.lzbz.patient.model.PatientDemographics;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface PatientDemographicsRepository extends ReactiveCrudRepository<PatientDemographics, Long> {
    Mono<PatientDemographics> findByUserId(Long userId);
    Mono<PatientDemographics> findByNationalId(String nationalId);
    Flux<PatientDemographics> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT * FROM patient_demographics WHERE user_id = :userId AND national_id = :nationalId")
    Mono<PatientDemographics> findByUserIdAndNationalId(Long userId, String nationalId);

    Mono<Boolean> existsByNationalId(String nationalId);
    Mono<Boolean> existsByUserId(Long userId);
}