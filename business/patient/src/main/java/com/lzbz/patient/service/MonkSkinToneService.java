package com.lzbz.patient.service;

import com.lzbz.patient.model.MonkSkinTone;
import com.lzbz.patient.dto.MonkSkinToneRequest;
import com.lzbz.patient.dto.MonkSkinToneResponse;
import com.lzbz.patient.repository.MonkSkinToneRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MonkSkinToneService {

    private final MonkSkinToneRepository monkSkinToneRepository;

    public MonkSkinToneService(MonkSkinToneRepository monkSkinToneRepository) {
        this.monkSkinToneRepository = monkSkinToneRepository;
    }

    public Flux<MonkSkinToneResponse> getAllMonkSkinTones() {
        return monkSkinToneRepository.findAll()
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching all Monk skin tones: " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch all Monk skin tones", e));
                });
    }

    public Mono<MonkSkinToneResponse> getMonkSkinTone(Long id) {
        return monkSkinToneRepository.findById(id)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching Monk skin tone with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch Monk skin tone with id " + id, e));
                });
    }

    public Flux<MonkSkinToneResponse> getMonkSkinTonesByPatientId(Long patientId) {
        return monkSkinToneRepository.findByPatientId(patientId)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching Monk skin tones for patient id " + patientId + ": " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch Monk skin tones for patient id " + patientId, e));
                });
    }

    public Mono<MonkSkinToneResponse> createMonkSkinTone(MonkSkinToneRequest request) {
        return Mono.just(request)
                .map(this::mapToMonkSkinTone)
                .flatMap(monkSkinToneRepository::save)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error creating Monk skin tone: " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to create Monk skin tone", e));
                });
    }

    public Mono<MonkSkinToneResponse> updateMonkSkinTone(Long id, MonkSkinToneRequest request) {
        return monkSkinToneRepository.findById(id)
                .flatMap(existingMonkSkinTone -> {
                    existingMonkSkinTone.setPatientId(request.getPatientId());
                    existingMonkSkinTone.setGradableIndia(request.getGradableIndia());
                    existingMonkSkinTone.setGradableUs(request.getGradableUs());
                    existingMonkSkinTone.setLabelIndia(request.getLabelIndia());
                    existingMonkSkinTone.setLabelUs(request.getLabelUs());
                    return monkSkinToneRepository.save(existingMonkSkinTone);
                })
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error updating Monk skin tone with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to update Monk skin tone with id " + id, e));
                });
    }

    public Mono<Void> deleteMonkSkinTone(Long id) {
        return monkSkinToneRepository.deleteById(id)
                .onErrorResume(e -> {
                    System.err.println("Error deleting Monk skin tone with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to delete Monk skin tone with id " + id, e));
                });
    }

    private MonkSkinTone mapToMonkSkinTone(MonkSkinToneRequest request) {
        return MonkSkinTone.builder()
                .patientId(request.getPatientId())
                .gradableIndia(request.getGradableIndia())
                .gradableUs(request.getGradableUs())
                .labelIndia(request.getLabelIndia())
                .labelUs(request.getLabelUs())
                .build();
    }

    private MonkSkinToneResponse mapToResponse(MonkSkinTone monkSkinTone) {
        return MonkSkinToneResponse.builder()
                .id(monkSkinTone.getId())
                .patientId(monkSkinTone.getPatientId())
                .gradableIndia(monkSkinTone.getGradableIndia())
                .gradableUs(monkSkinTone.getGradableUs())
                .labelIndia(monkSkinTone.getLabelIndia())
                .labelUs(monkSkinTone.getLabelUs())
                .build();
    }
}