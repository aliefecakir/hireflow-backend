package com.hireflow.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Üniversite / bölüm puan ve aktiflik güncelleme isteği
 */
public record UpdateCatalogItemRequest(
        @NotBlank String name,

        @NotNull
        @Min(0)
        Integer score,

        @NotNull
        @Min(0)
        @Max(1)
        Short isActv
) {
}
