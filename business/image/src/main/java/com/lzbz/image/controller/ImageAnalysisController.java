// ImageAnalysisController.java
package com.lzbz.image.controller;

import com.lzbz.image.model.ImageAnalysis;
import com.lzbz.image.service.ImageAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Slf4j
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ImageAnalysis> createAnalysis(@RequestBody ImageAnalysis analysis) {
        return imageAnalysisService.createAnalysis(analysis)
                .doOnSuccess(a -> log.info("Created analysis for image ID: {}", a.getImageId()));
    }

    @GetMapping("/image/{imageId}")
    public Flux<ImageAnalysis> getAnalysesByImageId(@PathVariable Long imageId) {
        return imageAnalysisService.findByImageId(imageId)
                .doOnComplete(() -> log.debug("Retrieved analyses for image ID: {}", imageId));
    }

    @GetMapping("/image/{imageId}/latest")
    public Mono<ImageAnalysis> getLatestAnalysis(@PathVariable Long imageId) {
        return imageAnalysisService.getLatestAnalysis(imageId)
                .doOnSuccess(analysis -> log.debug("Retrieved latest analysis for image ID: {}", imageId));
    }
}