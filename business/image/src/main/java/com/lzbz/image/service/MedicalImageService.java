package com.lzbz.image.service;

import com.lzbz.image.model.MedicalImage;
import com.lzbz.image.dto.MedicalImageRequest;
import com.lzbz.image.dto.GcsStorageResponse;
import com.lzbz.image.dto.MedicalImageResponse;
import com.lzbz.image.repository.MedicalImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalImageService {
    private final MedicalImageRepository medicalImageRepository;
    private final GcsService gcsService;

    public Mono<MedicalImageResponse> uploadMedicalImage(MedicalImageRequest request) {
        return gcsService.uploadImage(request.getImageData(), request.getImageType())
                .flatMap(imageResponse ->
                        gcsService.generateAndUploadThumbnail(request.getImageData(), request.getImageType())
                                .flatMap(thumbnailResponse -> {
                                    MedicalImage image = MedicalImage.builder()
                                            .lesionId(request.getLesionId())
                                            .imageType(request.getImageType())
                                            .imagePath(imageResponse.getObjectName())
                                            .thumbnailPath(thumbnailResponse.getObjectName())
                                            .takenDate(request.getTakenDate())
                                            .takenBy(request.getTakenBy())
                                            .createdAt(ZonedDateTime.now())
                                            .build();

                                    return medicalImageRepository.save(image)
                                            .map(savedImage -> mapToResponse(savedImage, imageResponse, thumbnailResponse));
                                }))
                .doOnSuccess(response -> log.info("Successfully uploaded image for lesion ID: {}", request.getLesionId()))
                .doOnError(e -> log.error("Error uploading image: {}", e.getMessage()));
    }

    public Flux<MedicalImageResponse> findByLesionId(Long lesionId) {
        return medicalImageRepository.findByLesionId(lesionId)
                .map(this::mapToResponse)
                .doOnComplete(() -> log.debug("Retrieved all images for lesion ID: {}", lesionId));
    }

    public Flux<MedicalImageResponse> findByTakenBy(Long userId) {
        return medicalImageRepository.findByTakenBy(userId)
                .map(this::mapToResponse)
                .doOnComplete(() -> log.debug("Retrieved all images taken by user ID: {}", userId));
    }

    private MedicalImageResponse mapToResponse(MedicalImage image) {
        String imageUrl = generateGcsUrl(image.getImagePath());
        String thumbnailUrl = generateGcsUrl(image.getThumbnailPath());

        return MedicalImageResponse.builder()
                .imageId(image.getImageId())
                .lesionId(image.getLesionId())
                .imageType(image.getImageType())
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .imageQualityScore(image.getImageQualityScore())
                .takenDate(image.getTakenDate())
                .takenBy(image.getTakenBy())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private MedicalImageResponse mapToResponse(MedicalImage image, GcsStorageResponse imageResponse, GcsStorageResponse thumbnailResponse) {
        return MedicalImageResponse.builder()
                .imageId(image.getImageId())
                .lesionId(image.getLesionId())
                .imageType(image.getImageType())
                .imageUrl(imageResponse.getPublicUrl())
                .thumbnailUrl(thumbnailResponse.getPublicUrl())
                .imageQualityScore(image.getImageQualityScore())
                .takenDate(image.getTakenDate())
                .takenBy(image.getTakenBy())
                .createdAt(image.getCreatedAt())
                .build();
    }

    private String generateGcsUrl(String objectName) {
        return String.format("https://storage.googleapis.com/%s/%s", "dx-derm-ia-bucket", objectName);
    }
}
