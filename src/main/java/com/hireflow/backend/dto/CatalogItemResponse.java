package com.hireflow.backend.dto;

/**
 * Katalog yönetimi: id + name + SCORE + IS_ACTV.
 */
public record CatalogItemResponse(
        Long id,
        String name,
        Integer score,
        Short isActv
) {
}
