package com.hireflow.backend.dto;

import java.util.List;
import java.util.UUID;

/** Profil detayı + tamamlanma yüzdesi + ilişkili listeler. */
public record ProfileDetailResponse(
        UUID userId,
        String name,
        String surname,
        String email,
        UUID profileId,
        String phone,
        String dept,
        String education,
        String prflPhtUrl,
        String cvUrl,
        Short isCmpltd,
        int completionPercentage,
        List<ExperienceDto> experiences,
        List<SkillDto> skills,
        List<LangDto> languages
) {
}
