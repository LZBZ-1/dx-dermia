package com.lzbz.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.databind.JsonNode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("image_analysis")
public class ImageAnalysis {
    @Id
    @Column("analysis_id")
    private Long analysisId;

    @Column("image_id")
    private Long imageId;

    @Column("asymmetry_score")
    private Double asymmetryScore;

    @Column("border_score")
    private Double borderScore;

    @Column("color_variance_score")
    private Double colorVarianceScore;

    @Column("diameter_mm")
    private Double diameterMm;

    @Column("evolution_noted")
    private Boolean evolutionNoted;

    @Column("analyzed_at")
    private ZonedDateTime analyzedAt;

    @Column("ai_model_version")
    private String aiModelVersion;

    @Column("analysis_metadata")
    private JsonNode analysisMetadata;
}