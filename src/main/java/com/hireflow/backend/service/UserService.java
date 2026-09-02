package com.hireflow.backend.service;

import com.hireflow.backend.dto.UserProfileResponse;

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
}
