package com.hireflow.backend.dto;

import java.util.UUID;

/** Yetenek katalog satırı. */
public record SkillDto(
        UUID skillId,
        String name,
        String shrtCode
) {
}
