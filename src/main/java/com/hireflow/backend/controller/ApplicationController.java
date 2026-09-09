package com.hireflow.backend.controller;

import com.hireflow.backend.dto.ApplicationCreateRequest;
import com.hireflow.backend.dto.ApplicationResponse;
import com.hireflow.backend.dto.ApplicationStatusUpdateRequest;
import com.hireflow.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** İş ilanı (POST) başvuruları: aday başvurusu, HR liste/statü. */
@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /** Adayın aktif bir ilana başvurusu (çift başvuru engellenir). */
    @PostMapping
    @PreAuthorize("hasRole('CAND')")
    public ResponseEntity<ApplicationResponse> applyToPost(
            @Valid @RequestBody ApplicationCreateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApplicationResponse created = applicationService.applyToPost(currentUserId(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Oturumdaki adayın kendi başvuruları. */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CAND')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(applicationService.getMyApplications(currentUserId(jwt)));
    }

    /** HR: tüm ilan başvuruları. */
    @GetMapping("/manage")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    /** HR: tek bir ilanın başvuruları. */
    @GetMapping("/manage/post/{postId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByPost(
            @PathVariable("postId") UUID postId
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsByPost(postId));
    }

    /** HR: başvuru statüsünü GNL_ST kodu ile değiştirir. */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                applicationService.updateApplicationStatus(id, request.statusCode(), currentUserId(jwt))
        );
    }

    /** JWT subject = USER_ID (UUID). */
    private UUID currentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
