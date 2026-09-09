package com.hireflow.backend.service;

import com.hireflow.backend.dto.SystemRoleResponse;
import com.hireflow.backend.dto.UpdateUserRoleRequest;
import com.hireflow.backend.dto.UserProfileResponse;
import com.hireflow.backend.dto.UserRoleRowResponse;

import java.util.List;
import java.util.UUID;

/**
 * Kullanıcı işlemleri için servis interface
 */
public interface UserService {
    
    /**
     * Email adresine göre kullanıcı profil bilgilerini getirir
     * 
     * @param email Kullanıcının email adresi
     * @return UserProfileResponse - Kullanıcı profil bilgileri
     * @throws RuntimeException Kullanıcı bulunamazsa
     */
    UserProfileResponse getUserProfile(String email);

    List<UserRoleRowResponse> listUserRoles();

    List<SystemRoleResponse> listActiveRoles();

    UserRoleRowResponse updateUserRole(UUID userId, UpdateUserRoleRequest request, UUID actorUserId);
}
