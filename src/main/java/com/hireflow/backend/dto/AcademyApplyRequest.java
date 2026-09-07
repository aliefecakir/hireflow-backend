package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Akademi form başvurusu isteği
 */
public record AcademyApplyRequest(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String email,
        @NotBlank String phone,
        @NotNull Long universityId,
        @NotNull Long departmentId,
        LocalDate gradDate,
        Integer uniScore,
        Integer depScore,
        Integer totalScore,
        @Valid List<AnswerRequest> answers
) {

    public record AnswerRequest(
            @NotNull Long questionId,
            Long questionChoiceId,
            String answerText
    ) {
    }
}
