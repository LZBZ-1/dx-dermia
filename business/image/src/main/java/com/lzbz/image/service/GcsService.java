package com.lzbz.image.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.lzbz.image.config.GcsProperties;
import com.lzbz.image.dto.GcsStorageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsService {

    private final Storage storage;
    private final GcsProperties gcsProperties;

    public Mono<GcsStorageResponse> uploadImage(byte[] imageData, String contentType) {
        return Mono.fromCallable(() -> {
            String filename = UUID.randomUUID().toString();
            String extension = getExtensionFromContentType(contentType);
            String objectName = gcsProperties.getImagesFolder() + filename + "." + extension;

            BlobId blobId = BlobId.of(gcsProperties.getBucketName(), objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            Blob blob = storage.create(blobInfo, imageData);

            return GcsStorageResponse.builder()
                    .bucketName(gcsProperties.getBucketName())
                    .objectName(objectName)
                    .publicUrl(generateGcsUrl(gcsProperties.getBucketName(), objectName))
                    .size(blob.getSize())
                    .contentType(contentType)
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<GcsStorageResponse> generateAndUploadThumbnail(byte[] originalImage, String contentType) {
        return Mono.fromCallable(() -> {
            // Crear thumbnail
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalImage));
            BufferedImage thumbnail = Scalr.resize(img, 200);

            // Convertir thumbnail a bytes
            ByteArrayOutputStream thumbnailOutput = new ByteArrayOutputStream();
            String extension = getExtensionFromContentType(contentType);
            ImageIO.write(thumbnail, extension, thumbnailOutput);
            byte[] thumbnailBytes = thumbnailOutput.toByteArray();

            // Subir thumbnail
            String filename = UUID.randomUUID().toString();
            String objectName = gcsProperties.getThumbnailFolder() + filename + "." + extension;

            BlobId blobId = BlobId.of(gcsProperties.getBucketName(), objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            Blob blob = storage.create(blobInfo, thumbnailBytes);

            return GcsStorageResponse.builder()
                    .bucketName(gcsProperties.getBucketName())
                    .objectName(objectName)
                    .publicUrl(generateGcsUrl(gcsProperties.getBucketName(), objectName))
                    .size(blob.getSize())
                    .contentType(contentType)
                    .build();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String generateGcsUrl(String bucketName, String objectName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new IllegalArgumentException("Unsupported image type: " + contentType);
        };
    }
}