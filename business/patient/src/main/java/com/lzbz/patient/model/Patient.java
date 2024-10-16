package com.lzbz.patient.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("patients")
public class Patient {
    @Id
    private Long id;

    @Column("user_id")
    private Integer userId;

    private String source;
    private Integer year;

    @Column("age_group")
    private String ageGroup;

    @Column("sex_at_birth")
    private String sexAtBirth;

    @Column("fitzpatrick_skin_type")
    private String fitzpatrickSkinType;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
