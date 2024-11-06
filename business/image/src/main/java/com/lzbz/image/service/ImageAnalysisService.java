package com.lzbz.image.service;

import com.lzbz.image.model.ImageAnalysis;
import com.lzbz.image.repository.ImageAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageAnalysisService {
    private final ImageAnalysisRepository imageAnalysisRepository;

    public Mono<ImageAnalysis> createAnalysis(ImageAnalysis analysis) {
        analysis.setAnalyzedAt(ZonedDateTime.now());
        return imageAnalysisRepository.save(analysis)
                .doOnSuccess(a -> log.info("Created analysis for image ID: {}", a.getImageId()))
                .doOnError(e -> log.error("Error creating analysis: {}", e.getMessage()));
    }

    public Flux<ImageAnalysis> findByImageId(Long imageId) {
        return imageAnalysisRepository.findByImageId(imageId)
                .doOnComplete(() -> log.debug("Retrieved all analyses for image ID: {}", imageId));
    }

    public Mono<ImageAnalysis> getLatestAnalysis(Long imageId) {
        return imageAnalysisRepository.findFirstByImageIdOrderByAnalyzedAtDesc(imageId)
                .doOnSuccess(analysis -> log.debug("Retrieved latest analysis for image ID: {}", imageId));
    }
}
