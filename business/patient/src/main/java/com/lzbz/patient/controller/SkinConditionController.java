package com.lzbz.patient.controller;

import com.lzbz.patient.dto.SkinConditionRequest;
import com.lzbz.patient.dto.SkinConditionResponse;
import com.lzbz.patient.service.SkinConditionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/skin-conditions")
public class SkinConditionController {

    private final SkinConditionService skinConditionService;

    public SkinConditionController(SkinConditionService skinConditionService) {
        this.skinConditionService = skinConditionService;
    }

    @GetMapping
    public Flux<SkinConditionResponse> getAllSkinConditions() {
        return skinConditionService.getAllSkinConditions();
    }

    @GetMapping("/{id}")
    public Mono<SkinConditionResponse> getSkinCondition(@PathVariable Long id) {
        return skinConditionService.getSkinCondition(id);
    }

    @GetMapping("/patient/{patientId}")
    public Flux<SkinConditionResponse> getSkinConditionsByPatientId(@PathVariable Long patientId) {
        return skinConditionService.getSkinConditionsByPatientId(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SkinConditionResponse> createSkinCondition(@RequestBody SkinConditionRequest request) {
        return skinConditionService.createSkinCondition(request);
    }

    @PutMapping("/{id}")
    public Mono<SkinConditionResponse> updateSkinCondition(@PathVariable Long id, @RequestBody SkinConditionRequest request) {
        return skinConditionService.updateSkinCondition(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSkinCondition(@PathVariable Long id) {
        return skinConditionService.deleteSkinCondition(id);
    }
}