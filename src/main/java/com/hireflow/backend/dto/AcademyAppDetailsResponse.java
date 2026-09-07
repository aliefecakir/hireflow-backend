package com.hireflow.backend.dto;

import java.util.List;
import java.util.UUID;

/**
 * Aday profili, form cevapları ve boş mülakat formu
 */
public record AcademyAppDetailsResponse(
        Long academyAppId,
        String name,
        String surname,
        String email,
        String phone,
        String universityName,
        String departmentName,
        Integer uniScore,
        Integer depScore,
        Integer totalScore,
        Integer interviewScore,
        UUID stId,
        String statusName,
        String statusDescr,
        List<CandidateAnswer> answers,
        List<InterviewCriterion> interviewCriteria
) {

    public record CandidateAnswer(
            Long questionId,
            String questionText,
            Long selectedChoiceId,
            List<Long> selectedChoiceIds,
            String answerText,
            UUID tpId,
            Integer minScore,
            Integer maxScore,
            Integer score,
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

    public record InterviewCriterion(
            Long questionId,
            String questionText,
            Integer minScore,
            Integer maxScore,
            Integer ordNo,
            UUID tpId,
            String tpShrtCode,
            String answerText,
            Long selectedChoiceId,
            List<Choice> choices
    ) {

        public record Choice(
                Long questionChoiceId,
                String choiceText,
                Integer score,
                Integer ordNo
        ) {
        }
    }
}
