package com.hireflow.backend.dto;

import java.util.UUID;

/** USER_ROLE satırı: kullanıcı + aktif rol. */
public record UserRoleRowResponse(
        UUID userId,
        UUID userRoleId,
        String email,
        String firstName,
        String lastName,
        Long roleId,
        String roleName,
        String roleShrtCode,
        Short isActv
) {
}
