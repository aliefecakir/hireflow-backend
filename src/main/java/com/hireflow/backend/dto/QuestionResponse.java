package com.hireflow.backend.dto;

import java.util.List;

/**
 * Soru ve şıklarını içeren yanıt
 */
public record QuestionResponse(
        Long id,
        String questionText,
        Long tpId,
        Integer minScore,
        Integer maxScore,
        Short isAssmt,
        List<Choice> choices,
        Long formCount
) {

    public record Choice(
            Long id,
            String choiceText,
            Integer score,
            Integer ordNo,
            Short isOther
    ) {
    }
}
