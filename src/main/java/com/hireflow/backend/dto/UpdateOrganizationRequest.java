package com.hireflow.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Organizasyon aktiflik güncelleme isteği
 */
public record UpdateOrganizationRequest(
        @NotNull
        @Min(0)
        @Max(1)
        Short isActv
) {
}
