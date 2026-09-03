package com.hireflow.backend.dto;

import java.util.UUID;

public record LangDto(
        UUID langId,
        String name,
        String shrtCode
) {
}
