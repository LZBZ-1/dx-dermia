package com.lzbz.image.repository;

import com.lzbz.image.model.MedicalImage;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MedicalImageRepository extends R2dbcRepository<MedicalImage, Long> {
    Flux<MedicalImage> findByLesionId(Long lesionId);
    Flux<MedicalImage> findByTakenBy(Long userId);
}
