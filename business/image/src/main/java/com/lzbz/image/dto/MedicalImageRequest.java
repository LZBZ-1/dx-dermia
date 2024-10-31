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
public class MedicalImageRequest {
    private Long lesionId;
    private String imageType;
    private ZonedDateTime takenDate;
    private Long takenBy;
    private byte[] imageData;
}