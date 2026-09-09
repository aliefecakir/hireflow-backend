package com.hireflow.backend.dto;

/** Aktif sistem rolü (GNL_TP, SHRT_CODE). */
public record SystemRoleResponse(
        Long roleId,
        String name,
        String descr,
        String shrtCode,
        Short isActv
) {
}
