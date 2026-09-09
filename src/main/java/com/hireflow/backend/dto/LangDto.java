package com.hireflow.backend.dto;

import java.util.UUID;

/** Dil katalog satırı. */
public record LangDto(
        UUID langId,
        String name,
        String shrtCode
) {
}
