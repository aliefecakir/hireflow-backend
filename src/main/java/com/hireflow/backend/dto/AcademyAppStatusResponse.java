package com.hireflow.backend.dto;

import java.util.UUID;

/**
 * ACADEMY_APP için GNL_ST durum kaydı
 */
public record AcademyAppStatusResponse(
        UUID stId,
        String name,
        String descr,
        String shrtCode
) {
}
