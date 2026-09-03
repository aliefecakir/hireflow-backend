package com.hireflow.backend.dto;

import java.util.UUID;

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
