package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotBlank;

/** HR'ın ilan başvurusu statü kodu (APP/WAIT, REVIEW, APPR, REJ). */
public record ApplicationStatusUpdateRequest(
        @NotBlank String statusCode
) {
}
