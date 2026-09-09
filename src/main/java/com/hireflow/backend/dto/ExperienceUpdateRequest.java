package com.hireflow.backend.dto;

import java.util.UUID;

/** Deneyim oluştur/güncelle; experienceId null ise yeni kayıt. */
public record ExperienceUpdateRequest(
        UUID experienceId,
        String corpName,
        String position,
        String descr,
        Short stllWrkg,
        String sdate,
        String edate
) {
}
