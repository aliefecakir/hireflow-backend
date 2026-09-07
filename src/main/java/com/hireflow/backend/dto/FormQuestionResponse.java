package com.hireflow.backend.dto;

import java.util.List;
import java.util.UUID;

/**
 * Adayın dolduracağı form sorusu (isAssmt = 0)
 */
public record FormQuestionResponse(
        Long questionId,
        String questionText,
        UUID tpId,
        Integer minScore,
        Integer maxScore,
        Integer ordNo,
        Short isReq,
        Short isAssmt,
        List<Choice> choices
) {

    public record Choice(
            Long questionChoiceId,
            String choiceText,
            Integer score,
            Integer ordNo,
            Short isOther
    ) {
    }
}
