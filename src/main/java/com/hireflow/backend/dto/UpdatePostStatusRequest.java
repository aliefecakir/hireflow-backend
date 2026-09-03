package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * İlan statüsünü güncelleme isteği (ACTV, PASS, DRFT)
 */
public record UpdatePostStatusRequest(
        @NotBlank String statusCode
) {
}
