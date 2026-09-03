package com.hireflow.backend.controller;

import com.hireflow.backend.dto.LangDto;
import com.hireflow.backend.dto.ProfileDetailResponse;
import com.hireflow.backend.dto.ProfileUpdateRequest;
import com.hireflow.backend.dto.SkillDto;
import com.hireflow.backend.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDetailResponse> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(profileService.getMyProfile(currentUserId(jwt)));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileDetailResponse> updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(profileService.updateMyProfile(currentUserId(jwt), request));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ProfileDetailResponse> getProfileByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<SkillDto>> getSkills(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(profileService.getActiveSkills(q, limit));
    }

    @GetMapping("/languages")
    public ResponseEntity<List<LangDto>> getLanguages() {
        return ResponseEntity.ok(profileService.getActiveLanguages());
    }

    private UUID currentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
