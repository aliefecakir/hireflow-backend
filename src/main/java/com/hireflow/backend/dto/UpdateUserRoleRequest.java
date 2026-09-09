package com.hireflow.backend.dto;

/** Kullanıcı rolü güncelleme; roleId veya shrtCode yeterlidir. */
public record UpdateUserRoleRequest(
        Long roleId,
        String shrtCode
) {
}
