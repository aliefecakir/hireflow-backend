package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreateOrganizationRequest;
import com.hireflow.backend.dto.OrganizationResponse;
import com.hireflow.backend.dto.UpdateOrganizationRequest;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Akademi organizasyonları: liste, oluşturma, aktif/pasif. */
@RestController
@RequestMapping("/api/academy/organizations")
@PreAuthorize(AcademyRoles.WRITE)
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /** includeInactive=true ise pasif organizasyonlar da gelir. */
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(organizationService.getOrganizations(includeInactive));
    }

    /** Yeni organizasyon; varsayılan IS_ACTV=1. */
    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrganization(request, currentUserId(jwt)));
    }

    /** Sadece aktiflik bayrağını günceller (0/1). */
    @PutMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable("organizationId") Long organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                organizationService.updateOrganization(organizationId, request, currentUserId(jwt))
        );
    }

    private UUID currentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
