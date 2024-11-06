package com.lzbz.image.controller;

import com.lzbz.image.dto.MedicalImageRequest;
import com.lzbz.image.dto.MedicalImageResponse;
import com.lzbz.image.service.MedicalImageService;
import com.lzbz.image.client.AuthServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
public class MedicalImageController {

    private final MedicalImageService medicalImageService;
    private final AuthServiceClient authServiceClient;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MedicalImageResponse> uploadImage(
            @RequestPart("file") FilePart file,
            @RequestPart("lesionId") String lesionId,
            @RequestPart(value = "takenDate", required = false) String takenDate,
            Authentication auth) {

        return authServiceClient.getUserIdFromUsername(auth.getName())
                .flatMap(userId -> DataBufferUtils.join(file.content())
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return bytes;
                        })
                        .map(bytes -> MedicalImageRequest.builder()
                                .imageData(bytes)
                                .imageType(getContentType(file.filename()))
                                .lesionId(Long.parseLong(lesionId))
                                .takenBy(userId)  // Aquí usamos el userId obtenido
                                .takenDate(takenDate != null ? ZonedDateTime.parse(takenDate) : ZonedDateTime.now())
                                .build())
                        .flatMap(medicalImageService::uploadMedicalImage))
                .doOnSuccess(response -> log.info("Successfully uploaded image for lesion ID: {}", lesionId))
                .doOnError(e -> log.error("Error uploading image: {}", e.getMessage()));
    }


    @GetMapping("/lesion/{lesionId}")
    public Flux<MedicalImageResponse> getImagesByLesionId(@PathVariable Long lesionId) {
        return medicalImageService.findByLesionId(lesionId)
                .doOnComplete(() -> log.debug("Retrieved images for lesion ID: {}", lesionId));
    }

    @GetMapping("/user/{userId}")
    public Flux<MedicalImageResponse> getImagesByUser(@PathVariable Long userId) {
        return medicalImageService.findByTakenBy(userId)
                .doOnComplete(() -> log.debug("Retrieved images for user ID: {}", userId));
    }

    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> throw new IllegalArgumentException("Unsupported file type: " + extension);
        };
    }
}