package com.hireflow.backend.dto;

/**
 * GNL_PARM kaydı (portal ve benzeri anahtarlar).
 */
public record GeneralParameterResponse(
        Long gnlParmId,
        String name,
        String shrtCode,
        Long val,
        Short isActv
) {
}
