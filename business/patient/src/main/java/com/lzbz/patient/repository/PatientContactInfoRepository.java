package com.lzbz.patient.repository;

import com.lzbz.patient.model.PatientContactInfo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PatientContactInfoRepository extends ReactiveCrudRepository<PatientContactInfo, Long> {
    Mono<PatientContactInfo> findByPatientId(Long patientId);
    Mono<PatientContactInfo> findByEmail(String email);
    Mono<PatientContactInfo> findByPhone(String phone);

    Mono<Boolean> existsByPatientId(Long patientId);
    Mono<Boolean> existsByEmailAndPatientIdNot(String email, Long patientId);
}
