package com.lzbz.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_roles")
@Slf4j
public class UserRole {
    @Id
    private Long userRoleId;
    private Long userId;
    private Long roleId;
    private ZonedDateTime assignedAt;
}
