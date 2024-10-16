package com.lzbz.patient.service;

import com.lzbz.patient.model.RaceEthnicity;
import com.lzbz.patient.dto.RaceEthnicityRequest;
import com.lzbz.patient.dto.RaceEthnicityResponse;
import com.lzbz.patient.repository.RaceEthnicityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RaceEthnicityService {

    private final RaceEthnicityRepository raceEthnicityRepository;

    public RaceEthnicityService(RaceEthnicityRepository raceEthnicityRepository) {
        this.raceEthnicityRepository = raceEthnicityRepository;
    }

    public Flux<RaceEthnicityResponse> getAllRaceEthnicities() {
        return raceEthnicityRepository.findAll()
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching all race ethnicities: " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch all race ethnicities", e));
                });
    }

    public Mono<RaceEthnicityResponse> getRaceEthnicity(Long id) {
        return raceEthnicityRepository.findById(id)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching race ethnicity with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch race ethnicity with id " + id, e));
                });
    }

    public Flux<RaceEthnicityResponse> getRaceEthnicitiesByPatientId(Long patientId) {
        return raceEthnicityRepository.findByPatientId(patientId)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching race ethnicities for patient id " + patientId + ": " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch race ethnicities for patient id " + patientId, e));
                });
    }

    public Mono<RaceEthnicityResponse> createRaceEthnicity(RaceEthnicityRequest request) {
        return Mono.just(request)
                .map(this::mapToRaceEthnicity)
                .flatMap(raceEthnicityRepository::save)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error creating race ethnicity: " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to create race ethnicity", e));
                });
    }

    public Mono<RaceEthnicityResponse> updateRaceEthnicity(Long id, RaceEthnicityRequest request) {
        return raceEthnicityRepository.findById(id)
                .flatMap(existingRaceEthnicity -> {
                    existingRaceEthnicity.setPatientId(request.getPatientId());
                    existingRaceEthnicity.setCategory(request.getCategory());
                    return raceEthnicityRepository.save(existingRaceEthnicity);
                })
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error updating race ethnicity with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to update race ethnicity with id " + id, e));
                });
    }

    public Mono<Void> deleteRaceEthnicity(Long id) {
        return raceEthnicityRepository.deleteById(id)
                .onErrorResume(e -> {
                    System.err.println("Error deleting race ethnicity with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to delete race ethnicity with id " + id, e));
                });
    }

    private RaceEthnicity mapToRaceEthnicity(RaceEthnicityRequest request) {
        return RaceEthnicity.builder()
                .patientId(request.getPatientId())
                .category(request.getCategory())
                .build();
    }

    private RaceEthnicityResponse mapToResponse(RaceEthnicity raceEthnicity) {
        return RaceEthnicityResponse.builder()
                .id(raceEthnicity.getId())
                .patientId(raceEthnicity.getPatientId())
                .category(raceEthnicity.getCategory())
                .build();
    }
}