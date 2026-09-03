package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Mevcut ilanı güncelleme isteği
 */
public record UpdatePostRequest(
        @NotBlank String title,
        @NotBlank String descr,
        String reqTech,
        String reqDept,
        @NotBlank String statusCode
) {
}
