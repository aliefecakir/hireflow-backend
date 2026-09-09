package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Yönetici mülakat puanlama isteği
 */
public record EvaluateAcademyAppRequest(
        @Valid List<EvaluationAnswerRequest> answers,
        @Valid List<ManualScoreRequest> manualScores // Açık uçlu sorular için manuel puanlar
) {

    /** Mülakat şıkkı seçimi veya serbest metin. */
    public record EvaluationAnswerRequest(
            @NotNull Long questionId,
            Long questionChoiceId,
            String answerText
    ) {
    }

    /** Açık uçlu / "diğer" cevaba yönetici puanı. */
    public record ManualScoreRequest(
            @NotNull Long questionId,
            @NotNull Integer score
    ) {
    }
}
