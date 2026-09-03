package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplicationStatusUpdateRequest(
        @NotBlank String statusCode
) {
}
