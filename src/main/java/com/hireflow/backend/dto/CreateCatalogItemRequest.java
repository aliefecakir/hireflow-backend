package com.hireflow.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Yeni üniversite / bölüm oluşturma isteği
 */
public record CreateCatalogItemRequest(
        @NotBlank String name,

        @NotNull
        @Min(0)
        Integer score
) {
}
