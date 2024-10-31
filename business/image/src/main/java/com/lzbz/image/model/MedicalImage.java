package com.lzbz.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("medical_images")
public class MedicalImage {
    @Id
    @Column("image_id")
    private Long imageId;

    @Column("lesion_id")
    private Long lesionId;

    @Column("image_type")
    private String imageType;

    @Column("image_path")
    private String imagePath;

    @Column("thumbnail_path")
    private String thumbnailPath;

    @Column("image_quality_score")
    private Double imageQualityScore;

    @Column("taken_date")
    private ZonedDateTime takenDate;

    @Column("taken_by")
    private Long takenBy;

    @Column("created_at")
    private ZonedDateTime createdAt;
}
