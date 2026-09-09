package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** İlan başvurusu: hangi POST'a. */
public record ApplicationCreateRequest(
        @NotNull UUID postId
) {
}
