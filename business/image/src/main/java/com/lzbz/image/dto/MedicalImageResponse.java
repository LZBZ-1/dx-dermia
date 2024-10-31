package com.lzbz.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalImageResponse {
    private Long imageId;
    private Long lesionId;
    private String imageType;
    private String imageUrl;
    private String thumbnailUrl;
    private Double imageQualityScore;
    private ZonedDateTime takenDate;
    private Long takenBy;
    private ZonedDateTime createdAt;
}
