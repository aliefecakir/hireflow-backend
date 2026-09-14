package com.hireflow.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/** ACADEMY_APP_ST_HSTR satırı; durum adları çözülmüş. */
public record AcademyAppStatusHistoryResponse(
        Long academyAppStHstrId,
        Long academyAppId,
        Long stId,
        String statusName,
        Long prevStId,
        String prevStatusName,
        String changeReason,
        LocalDateTime changedAt,
        UUID changedByUserId,
        String changedByName
) {
}
