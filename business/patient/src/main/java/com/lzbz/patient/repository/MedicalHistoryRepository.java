package com.lzbz.patient.repository;

import com.lzbz.patient.model.MedicalHistory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface MedicalHistoryRepository extends ReactiveCrudRepository<MedicalHistory, Long> {
    Mono<MedicalHistory> findByPatientId(Long patientId);

    @Query("SELECT * FROM medical_history WHERE patient_id = :patientId AND family_history_melanoma = true")
    Flux<MedicalHistory> findByPatientIdWithMelanomaHistory(Long patientId);

    Flux<MedicalHistory> findByPatientIdAndSkinType(Long patientId, String skinType);

    @Query("SELECT * FROM medical_history WHERE patient_id = :patientId ORDER BY created_at DESC LIMIT 1")
    Mono<MedicalHistory> findLatestByPatientId(Long patientId);
}