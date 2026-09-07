package com.hireflow.backend.dto;

/**
 * Organizasyon yanıtı
 */
public record OrganizationResponse(
        Long id,
        String name,
        String descr,
        Short isActv
) {
}
