package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Yeni organizasyon oluşturma isteği
 */
public record CreateOrganizationRequest(
        @NotBlank String name,
        String descr
) {
}
