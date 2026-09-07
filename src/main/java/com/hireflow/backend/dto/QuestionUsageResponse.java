package com.hireflow.backend.dto;

import java.util.List;

/**
 * Sorunun kullanım durumu
 */
public record QuestionUsageResponse(
        boolean isUsed,
        boolean canDelete,
        boolean canEditContent,
        long formCount,
        long answerCount,
        List<String> formTitles
) {
}
