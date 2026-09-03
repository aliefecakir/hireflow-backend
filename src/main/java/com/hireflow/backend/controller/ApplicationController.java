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

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CAND')")
    public ResponseEntity<ApplicationResponse> applyToPost(
            @Valid @RequestBody ApplicationCreateRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ApplicationResponse created = applicationService.applyToPost(currentUserId(jwt), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CAND')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(applicationService.getMyApplications(currentUserId(jwt)));
    }

    @GetMapping("/manage")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @GetMapping("/manage/post/{postId}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByPost(
            @PathVariable("postId") UUID postId
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsByPost(postId));
    }

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

    private UUID currentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
