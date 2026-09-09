package com.hireflow.backend.dto;

import java.time.LocalDateTime;

/**
 * Form listesi / oluşturma / güncelleme yanıtı
 */
public record FormResponse(
        Long formId,
        String title,
        String descr,
        Long organizationId,
        String organizationName,
        Short isActv,
        LocalDateTime sdate,
        LocalDateTime edate,
        Long applicationCount
) {
}
