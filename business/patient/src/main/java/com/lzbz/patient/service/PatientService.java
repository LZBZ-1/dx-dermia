package com.lzbz.patient.service;

import com.lzbz.patient.model.Patient;
import com.lzbz.patient.dto.PatientRequest;
import com.lzbz.patient.dto.PatientResponse;
import com.lzbz.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Flux<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching all patients: " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch all patients", e));
                });
    }

    public Mono<PatientResponse> getPatient(Long id) {
        return patientRepository.findById(id)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching patient with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch patient with id " + id, e));
                });
    }

    public Mono<PatientResponse> createPatient(PatientRequest request) {
        return Mono.just(request)
                .map(this::mapToPatient)
                .flatMap(patient -> {
                    patient.setCreatedAt(LocalDateTime.now());
                    patient.setUpdatedAt(LocalDateTime.now());
                    return patientRepository.save(patient);
                })
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error creating patient: " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to create patient", e));
                });
    }

    public Mono<PatientResponse> updatePatient(Long id, PatientRequest request) {
        return patientRepository.findById(id)
                .flatMap(existingPatient -> {
                    // Actualizar los campos del paciente existente con los nuevos datos
                    existingPatient.setUserId(request.getUserId());
                    existingPatient.setSource(request.getSource());
                    existingPatient.setYear(request.getYear());
                    existingPatient.setAgeGroup(request.getAgeGroup());
                    existingPatient.setSexAtBirth(request.getSexAtBirth());
                    existingPatient.setFitzpatrickSkinType(request.getFitzpatrickSkinType());
                    existingPatient.setUpdatedAt(LocalDateTime.now());
                    return patientRepository.save(existingPatient);
                })
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error updating patient with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to update patient with id " + id, e));
                });
    }

    public Mono<Void> deletePatient(Long id) {
        return patientRepository.deleteById(id)
                .onErrorResume(e -> {
                    System.err.println("Error deleting patient with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to delete patient with id " + id, e));
                });
    }

    private Patient mapToPatient(PatientRequest request) {
        return Patient.builder()
                .userId(request.getUserId())
                .source(request.getSource())
                .year(request.getYear())
                .ageGroup(request.getAgeGroup())
                .sexAtBirth(request.getSexAtBirth())
                .fitzpatrickSkinType(request.getFitzpatrickSkinType())
                .build();
    }

    private PatientResponse mapToResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .userId(patient.getUserId())
                .source(patient.getSource())
                .year(patient.getYear())
                .ageGroup(patient.getAgeGroup())
                .sexAtBirth(patient.getSexAtBirth())
                .fitzpatrickSkinType(patient.getFitzpatrickSkinType())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}