package com.lzbz.patient.controller;

import com.lzbz.patient.dto.RaceEthnicityRequest;
import com.lzbz.patient.dto.RaceEthnicityResponse;
import com.lzbz.patient.service.RaceEthnicityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/race-ethnicities")
public class RaceEthnicityController {

    private final RaceEthnicityService raceEthnicityService;

    public RaceEthnicityController(RaceEthnicityService raceEthnicityService) {
        this.raceEthnicityService = raceEthnicityService;
    }

    @GetMapping
    public Flux<RaceEthnicityResponse> getAllRaceEthnicities() {
        return raceEthnicityService.getAllRaceEthnicities();
    }

    @GetMapping("/{id}")
    public Mono<RaceEthnicityResponse> getRaceEthnicity(@PathVariable Long id) {
        return raceEthnicityService.getRaceEthnicity(id);
    }

    @GetMapping("/patient/{patientId}")
    public Flux<RaceEthnicityResponse> getRaceEthnicitiesByPatientId(@PathVariable Long patientId) {
        return raceEthnicityService.getRaceEthnicitiesByPatientId(patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RaceEthnicityResponse> createRaceEthnicity(@RequestBody RaceEthnicityRequest request) {
        return raceEthnicityService.createRaceEthnicity(request);
    }

    @PutMapping("/{id}")
    public Mono<RaceEthnicityResponse> updateRaceEthnicity(@PathVariable Long id, @RequestBody RaceEthnicityRequest request) {
        return raceEthnicityService.updateRaceEthnicity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteRaceEthnicity(@PathVariable Long id) {
        return raceEthnicityService.deleteRaceEthnicity(id);
    }
}