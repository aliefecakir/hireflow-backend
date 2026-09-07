package com.hireflow.backend.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Forma ait aday başvurusunun liste satırı
 */
public record FormApplicationResponse(
        Long academyAppId,
        String name,
        String surname,
        String universityName,
        String departmentName,
        LocalDate gradDate,
        Integer totalScore,
        Integer interviewScore,
        UUID stId,
        String statusName,
        String statusShrtCode,
        String statusDescr
) {
}
