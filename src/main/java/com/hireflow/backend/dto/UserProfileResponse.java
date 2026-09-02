package com.hireflow.backend.dto;

import java.util.List;
import java.util.UUID;

/**
 * Kullanıcı profil bilgilerini içeren response DTO
 */
public record UserProfileResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        String primaryRole
) {
}
