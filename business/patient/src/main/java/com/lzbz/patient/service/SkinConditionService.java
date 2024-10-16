package com.lzbz.patient.service;

import com.lzbz.patient.model.SkinCondition;
import com.lzbz.patient.dto.SkinConditionRequest;
import com.lzbz.patient.dto.SkinConditionResponse;
import com.lzbz.patient.repository.SkinConditionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SkinConditionService {

    private final SkinConditionRepository skinConditionRepository;

    public SkinConditionService(SkinConditionRepository skinConditionRepository) {
        this.skinConditionRepository = skinConditionRepository;
    }

    public Flux<SkinConditionResponse> getAllSkinConditions() {
        return skinConditionRepository.findAll()
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching all skin conditions: " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch all skin conditions", e));
                });
    }

    public Mono<SkinConditionResponse> getSkinCondition(Long id) {
        return skinConditionRepository.findById(id)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching skin condition with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch skin condition with id " + id, e));
                });
    }

    public Flux<SkinConditionResponse> getSkinConditionsByPatientId(Long patientId) {
        return skinConditionRepository.findByPatientId(patientId)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error fetching skin conditions for patient id " + patientId + ": " + e.getMessage());
                    return Flux.error(new RuntimeException("Failed to fetch skin conditions for patient id " + patientId, e));
                });
    }

    public Mono<SkinConditionResponse> createSkinCondition(SkinConditionRequest request) {
        return Mono.just(request)
                .map(this::mapToSkinCondition)
                .flatMap(skinConditionRepository::save)
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error creating skin condition: " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to create skin condition", e));
                });
    }

    public Mono<SkinConditionResponse> updateSkinCondition(Long id, SkinConditionRequest request) {
        return skinConditionRepository.findById(id)
                .flatMap(existingSkinCondition -> {
                    existingSkinCondition.setPatientId(request.getPatientId());
                    existingSkinCondition.setCaseId(request.getCaseId());
                    existingSkinCondition.setRelease(request.getRelease());
                    existingSkinCondition.setTextures(request.getTextures());
                    existingSkinCondition.setBodyParts(request.getBodyParts());
                    existingSkinCondition.setConditionSymptoms(request.getConditionSymptoms());
                    existingSkinCondition.setOtherSymptoms(request.getOtherSymptoms());
                    existingSkinCondition.setRelatedCategory(request.getRelatedCategory());
                    existingSkinCondition.setConditionDuration(request.getConditionDuration());
                    return skinConditionRepository.save(existingSkinCondition);
                })
                .map(this::mapToResponse)
                .onErrorResume(e -> {
                    System.err.println("Error updating skin condition with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to update skin condition with id " + id, e));
                });
    }

    public Mono<Void> deleteSkinCondition(Long id) {
        return skinConditionRepository.deleteById(id)
                .onErrorResume(e -> {
                    System.err.println("Error deleting skin condition with id " + id + ": " + e.getMessage());
                    return Mono.error(new RuntimeException("Failed to delete skin condition with id " + id, e));
                });
    }

    private SkinCondition mapToSkinCondition(SkinConditionRequest request) {
        return SkinCondition.builder()
                .patientId(request.getPatientId())
                .caseId(request.getCaseId())
                .release(request.getRelease())
                .textures(request.getTextures())
                .bodyParts(request.getBodyParts())
                .conditionSymptoms(request.getConditionSymptoms())
                .otherSymptoms(request.getOtherSymptoms())
                .relatedCategory(request.getRelatedCategory())
                .conditionDuration(request.getConditionDuration())
                .build();
    }

    private SkinConditionResponse mapToResponse(SkinCondition skinCondition) {
        return SkinConditionResponse.builder()
                .id(skinCondition.getId())
                .patientId(skinCondition.getPatientId())
                .caseId(skinCondition.getCaseId())
                .release(skinCondition.getRelease())
                .textures(skinCondition.getTextures())
                .bodyParts(skinCondition.getBodyParts())
                .conditionSymptoms(skinCondition.getConditionSymptoms())
                .otherSymptoms(skinCondition.getOtherSymptoms())
                .relatedCategory(skinCondition.getRelatedCategory())
                .conditionDuration(skinCondition.getConditionDuration())
                .build();
    }
}