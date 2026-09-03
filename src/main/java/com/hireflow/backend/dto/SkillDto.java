package com.hireflow.backend.dto;

import java.util.UUID;

public record SkillDto(
        UUID skillId,
        String name,
        String shrtCode
) {
}
