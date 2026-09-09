package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Soru ve şıklarını birlikte oluşturma isteği
 */
public record CreateQuestionRequest(
        @NotBlank String questionText,
        @NotNull Long tpId,
        @NotNull Integer minScore,
        @NotNull Integer maxScore,
        Short isAssmt,
        @Valid List<Choice> choices
) {

    /** Şık metni, puanı, sırası; isOther=1 serbest metin. */
    public record Choice(
            @NotBlank String choiceText,
            Integer score,
            @NotNull Integer ordNo,
            Short isOther
    ) {
    }
}
