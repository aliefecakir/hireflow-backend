package com.hireflow.backend.dto;

import java.util.UUID;

/** Profil deneyim satırı (okuma). */
public record ExperienceDto(
        UUID experienceId,
        String corpName,
        String position,
        String descr,
        Short stllWrkg,
        String sdate,
        String edate
) {
}
