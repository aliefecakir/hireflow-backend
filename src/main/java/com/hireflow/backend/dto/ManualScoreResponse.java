package com.hireflow.backend.dto;

/**
 * Tek bir yönetici puanının kayıt sonucu
 */
public record ManualScoreResponse(
        Long academyAppId,
        Long questionId,
        Integer score,
        Integer totalScore
) {
}
