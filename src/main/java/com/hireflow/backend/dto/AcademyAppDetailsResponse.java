package com.hireflow.backend.dto;

import java.util.List;

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
        Long stId,
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
            Long tpId,
            Integer minScore,
            Integer maxScore,
            Integer score,
            Integer ordNo,
            Boolean manuallyScored,
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
            Long tpId,
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
