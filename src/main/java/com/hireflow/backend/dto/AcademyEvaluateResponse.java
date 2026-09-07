package com.hireflow.backend.dto;

/**
 * Mülakat değerlendirme sonucu
 */
public record AcademyEvaluateResponse(
        Long academyAppId,
        Integer interviewScore,
        String statusDescr
) {
}
