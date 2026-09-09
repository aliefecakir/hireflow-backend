package com.hireflow.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/** İlan başvurusu yanıtı: ilan + aday + GNL_ST. */
public record ApplicationResponse(
        UUID appId,
        UUID postId,
        String postTitle,
        String postDescr,
        String postReqTech,
        String postReqDept,
        UUID cndtId,
        String candidateName,
        String candidateSurname,
        String candidateEmail,
        Status status,
        LocalDateTime appliedDate
) {

    public record Status(String shrtCode, String name, String descr) { // APP GNL_ST
    }
}
