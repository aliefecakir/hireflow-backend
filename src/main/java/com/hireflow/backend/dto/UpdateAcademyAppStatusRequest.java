package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Akademi başvurusunun ST_ID ve STATUS_DESCR güncellemesi
 */
public record UpdateAcademyAppStatusRequest(
        @NotNull UUID stId,
        String statusDescr
) {
}
