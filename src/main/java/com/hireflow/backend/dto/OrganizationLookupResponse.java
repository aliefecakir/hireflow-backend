package com.hireflow.backend.dto;

/**
 * Aktif organizasyon listesi için sade yanıt
 */
public record OrganizationLookupResponse(
        Long id,
        String name
) {
}
