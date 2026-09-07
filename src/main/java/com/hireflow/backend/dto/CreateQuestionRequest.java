package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Soru ve şıklarını birlikte oluşturma isteği
 */
public record CreateQuestionRequest(
        @NotBlank String questionText,
        @NotNull UUID tpId,
        @NotNull Integer minScore,
        @NotNull Integer maxScore,
        Short isAssmt,
        @Valid List<Choice> choices
) {

    public record Choice(
            @NotBlank String choiceText,
            Integer score,
            @NotNull Integer ordNo,
            Short isOther
    ) {
    }
}
