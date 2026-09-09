package com.hireflow.backend.dto;

import java.util.List;
import java.util.UUID;

/** Adayın kendi profilini güncelleme gövdesi. */
public record ProfileUpdateRequest(
        String phone,
        String dept,
        String education,
        String prflPhtUrl,
        String cvUrl,
        List<ExperienceUpdateRequest> experiences,
        List<UUID> skillIds,
        List<UUID> langIds
) {
}
