package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Yeni ilan oluşturma isteği
 */
public record CreatePostRequest(
        @NotBlank String title,
        @NotBlank String descr,
        String reqTech,
        String reqDept,
        @NotBlank String statusCode
) {
}
