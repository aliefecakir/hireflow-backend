package com.hireflow.backend.dto;

/**
 * ACADEMY_APP için GNL_ST durum kaydı
 */
public record AcademyAppStatusResponse(
        Long stId,
        String name,
        String descr,
        String shrtCode
) {
}
