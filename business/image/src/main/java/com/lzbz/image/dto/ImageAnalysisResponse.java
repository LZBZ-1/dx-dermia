package com.lzbz.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.JsonNode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisResponse {
    private Long analysisId;
    private Long imageId;
    private Double asymmetryScore;
    private Double borderScore;
    private Double colorVarianceScore;
    private Double diameterMm;
    private Boolean evolutionNoted;
    private ZonedDateTime analyzedAt;
    private String aiModelVersion;
    private JsonNode analysisMetadata;
}