package com.hireflow.backend.dto;

/**
 * Aday dropdown'u: yalnızca id + name (SCORE yok).
 */
public record CatalogLookupResponse(
        Long id,
        String name
) {
}
