package com.lzbz.image.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GcsConfig {
    private final GcsProperties gcsProperties;

    @Bean
    public Storage storage() {
        try {
            // Usa Resource para manejar el archivo de manera más robusta
            String credentialsPath = gcsProperties.getCredentialsPath();
            log.info("Loading GCP credentials from: {}", credentialsPath);

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

            return StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            log.error("Error loading GCP credentials: {}", e.getMessage());
            throw new RuntimeException("Could not initialize GCS Storage", e);
        }
    }
}