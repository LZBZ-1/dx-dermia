package com.lzbz.image.repository;

import com.lzbz.image.model.ImageAnalysis;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ImageAnalysisRepository extends R2dbcRepository<ImageAnalysis, Long> {
    Flux<ImageAnalysis> findByImageId(Long imageId);
    Mono<ImageAnalysis> findFirstByImageIdOrderByAnalyzedAtDesc(Long imageId);
}