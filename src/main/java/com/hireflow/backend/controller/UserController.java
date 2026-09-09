package com.hireflow.backend.controller;

import com.hireflow.backend.dto.UpdateUserRoleRequest;
import com.hireflow.backend.dto.UserProfileResponse;
import com.hireflow.backend.dto.UserRoleRowResponse;
import com.hireflow.backend.entity.User;
import com.hireflow.backend.repository.UserRepository;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Kullanıcı işlemleri için REST controller
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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
        
        // USER + USER_ROLE + GNL_TP.shrtCode
        UserProfileResponse profile = userService.getUserProfile(email);
        
        System.out.println("Profile retrieved: " + profile.email() + ", Roles: " + profile.roles());
        System.out.println("============================");
        
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @PreAuthorize(AcademyRoles.ADMIN)
    public ResponseEntity<List<UserRoleRowResponse>> listUserRoles() {
        return ResponseEntity.ok(userService.listUserRoles());
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize(AcademyRoles.ADMIN)
    public ResponseEntity<UserRoleRowResponse> updateUserRole(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateUserRoleRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(userService.updateUserRole(userId, request, currentUserId(jwt)));
    }

    private UUID currentUserId(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
        }

        if (jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
