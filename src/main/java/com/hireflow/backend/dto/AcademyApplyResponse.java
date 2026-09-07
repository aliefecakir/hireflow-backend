package com.hireflow.backend.dto;

/**
 * Akademi form başvurusu yanıtı
 */
public record AcademyApplyResponse(
        Long academyAppId,
        Long formId,
        String statusDescr
) {
}
