package com.lzbz.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GcsStorageResponse {
    private String bucketName;
    private String objectName;
    private String publicUrl;
    private Long size;
    private String contentType;
}
