package com.hireflow.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FormDetailResponse(
        Long formId,
        String title,
        String descr,
        Long organizationId,
        String organizationName,
        Short isActv,
        LocalDateTime sdate,
        LocalDateTime edate,
        List<FormQuestionResponse> questions
) {
}
