package com.hireflow.backend.dto;

import java.util.UUID;

/**
 * Aktif soru tipi (GNL_TP, isActv = 1)
 */
public record QuestionTypeResponse(
        UUID id,
        String name,
        String shrtCode,
        String entCodeName
) {
}
