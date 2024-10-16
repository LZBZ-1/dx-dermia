package com.lzbz.patient.controller;

import com.lzbz.patient.dto.MonkSkinToneRequest;
import com.lzbz.patient.dto.MonkSkinToneResponse;
import com.lzbz.patient.service.MonkSkinToneService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/monk-skin-tones")
public class MonkSkinToneController {

    private final MonkSkinToneService monkSkinToneService;

    public MonkSkinToneController(MonkSkinToneService monkSkinToneService) {
        this.monkSkinToneService = monkSkinToneService;
    }

    @GetMapping
    public Flux<MonkSkinToneResponse> getAllMonkSkinTones() {
        return monkSkinToneService.getAllMonkSkinTones();
    }

    @GetMapping("/{id}")
    public Mono<MonkSkinToneResponse> getMonkSkinTone(@PathVariable Long id) {
        return monkSkinToneService.getMonkSkinTone(id);
    }

    @GetMapping("/patient/{patientId}")
    public Flux<MonkSkinToneResponse> getMonkSkinTonesByPatientId(@PathVariable Long patientId) {
        return monkSkinToneService.getMonkSkinTonesByPatientId(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MonkSkinToneResponse> createMonkSkinTone(@RequestBody MonkSkinToneRequest request) {
        return monkSkinToneService.createMonkSkinTone(request);
    }

    @PutMapping("/{id}")
    public Mono<MonkSkinToneResponse> updateMonkSkinTone(@PathVariable Long id, @RequestBody MonkSkinToneRequest request) {
        return monkSkinToneService.updateMonkSkinTone(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMonkSkinTone(@PathVariable Long id) {
        return monkSkinToneService.deleteMonkSkinTone(id);
    }
}