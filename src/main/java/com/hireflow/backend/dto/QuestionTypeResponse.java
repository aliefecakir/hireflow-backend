package com.hireflow.backend.dto;

/**
 * Aktif soru tipi (GNL_TP, isActv = 1)
 */
public record QuestionTypeResponse(
        Long id,
        String name,
        String shrtCode,
        String entCodeName
) {
}
