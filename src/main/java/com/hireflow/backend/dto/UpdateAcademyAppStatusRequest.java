package com.hireflow.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Akademi başvurusunun ST_ID ve STATUS_DESCR güncellemesi
 */
public record UpdateAcademyAppStatusRequest(
        @NotNull Long stId,
        String statusDescr
) {
}
