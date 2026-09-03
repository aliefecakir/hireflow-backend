package com.hireflow.backend.controller;

import com.hireflow.backend.dto.UserProfileResponse;
import com.hireflow.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kullanıcı işlemleri için REST controller
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Oturum açmış kullanıcının profil bilgilerini getirir
     * 
     * @param jwt JWT token
     * @return UserProfileResponse - Kullanıcı profil bilgileri
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        System.out.println("=== GET /api/v1/users/me ===");

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        
        // JWT'den email claim'ini al
        String email = jwt.getClaimAsString("email");
        
        // Email yoksa subject (UUID) kullan
        if (email == null || email.isEmpty()) {
            email = jwt.getSubject();
        }
        
        System.out.println("Email from JWT: " + email);
        
        // Kullanıcı profilini getir
        UserProfileResponse profile = userService.getUserProfile(email);
        
        System.out.println("Profile retrieved: " + profile.email() + ", Roles: " + profile.roles());
        System.out.println("============================");
        
        return ResponseEntity.ok(profile);
    }
}
