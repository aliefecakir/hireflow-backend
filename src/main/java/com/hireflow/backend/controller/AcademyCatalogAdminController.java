package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CatalogItemResponse;
import com.hireflow.backend.dto.CreateCatalogItemRequest;
import com.hireflow.backend.dto.UpdateCatalogItemRequest;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.AcademyCatalogService;
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

/** Üniversite / bölüm katalog puanları: yalnızca akademi yöneticisi ve admin. */
@RestController
@RequestMapping("/api/academy/catalog")
@PreAuthorize(AcademyRoles.WRITE)
public class AcademyCatalogAdminController {

    private final AcademyCatalogService academyCatalogService;

    public AcademyCatalogAdminController(AcademyCatalogService academyCatalogService) {
        this.academyCatalogService = academyCatalogService;
    }

    /** includeInactive=true ise pasif üniversiteler de gelir. */
    @GetMapping("/universities")
    public ResponseEntity<List<CatalogItemResponse>> getUniversities(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(academyCatalogService.getUniversities(includeInactive));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<CatalogItemResponse>> getDepartments(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(academyCatalogService.getDepartments(includeInactive));
    }

    /** Yeni üniversite; varsayılan IS_ACTV=1. */
    @PostMapping("/universities")
    public ResponseEntity<CatalogItemResponse> createUniversity(
            @Valid @RequestBody CreateCatalogItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(academyCatalogService.createUniversity(request, currentUserId(jwt)));
    }

    @PostMapping("/departments")
    public ResponseEntity<CatalogItemResponse> createDepartment(
            @Valid @RequestBody CreateCatalogItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(academyCatalogService.createDepartment(request, currentUserId(jwt)));
    }

    /** Ad, puan ve aktiflik günceller; mevcut başvuruların puanı değişmez. */
    @PutMapping("/universities/{universityId}")
    public ResponseEntity<CatalogItemResponse> updateUniversity(
            @PathVariable("universityId") Long universityId,
            @Valid @RequestBody UpdateCatalogItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                academyCatalogService.updateUniversity(universityId, request, currentUserId(jwt))
        );
    }

    @PutMapping("/departments/{departmentId}")
    public ResponseEntity<CatalogItemResponse> updateDepartment(
            @PathVariable("departmentId") Long departmentId,
            @Valid @RequestBody UpdateCatalogItemRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(
                academyCatalogService.updateDepartment(departmentId, request, currentUserId(jwt))
        );
    }

    private UUID currentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalArgumentException("Oturum bilgisi alınamadı.");
        }
        return UUID.fromString(jwt.getSubject());
    }
}
