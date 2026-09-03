package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ApplicationCreateRequest(
        @NotNull UUID postId
) {
}
