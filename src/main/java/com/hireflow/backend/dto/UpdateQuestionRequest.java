package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Soru güncelleme isteği.
 * Soru kullanımdaysa sadece şık puanları güncellenebilir.
 * Kullanımda değilse her şey güncellenebilir.
 */
public record UpdateQuestionRequest(
        String questionText,
        Long tpId,
        Integer minScore,
        Integer maxScore,
        Short isAssmt,
        @Valid List<ChoiceUpdate> choices
) {

    public record ChoiceUpdate(
            Long id, // Mevcut şık için zorunlu; yeni şık için null
            String choiceText,
            Integer score,
            Integer ordNo,
            Short isOther
    ) {
    }
}
