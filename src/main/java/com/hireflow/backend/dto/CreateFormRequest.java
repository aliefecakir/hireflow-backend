package com.hireflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Yeni form ve soru ilişkilerini oluşturma / güncelleme isteği
 */
public record CreateFormRequest(
        @NotNull Long organizationId,
        @NotBlank String title,
        String descr,
        @NotNull LocalDateTime sdate,
        @NotNull LocalDateTime edate,
        Short isActv,
        @Valid List<FormQuestionRequest> questions
) {

    /** Forma bağlanacak soru (sıra serviste yeniden yazılır). */
    public record FormQuestionRequest(
            @NotNull Long questionId,
            Integer ordNo,
            Short isReq
    ) {
    }
}
