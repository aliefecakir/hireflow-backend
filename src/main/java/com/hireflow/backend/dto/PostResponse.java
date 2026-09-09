package com.hireflow.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * İlan bilgilerini ve GNL_ST statüsünü içeren response DTO
 */
public record PostResponse(
        UUID postId,
        String title,
        String descr,
        String reqTech,
        String reqDept,
        Status status,
        boolean applied,
        LocalDateTime cdate,
        LocalDateTime udate
) {

    public record Status(String shrtCode, String name) { // GNL_ST özeti
    }
}
