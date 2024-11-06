package com.lzbz.image.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gcp.storage")
public class GcsProperties {
    private String bucketName;
    private String credentialsPath;
    private String imagesFolder;
    private String thumbnailFolder;
}