package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreateOrganizationRequest;
import com.hireflow.backend.dto.OrganizationResponse;
import com.hireflow.backend.dto.UpdateOrganizationRequest;
import com.hireflow.backend.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academy/organizations")
@PreAuthorize("hasRole('ACADEMY_MNGR')")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> getOrganizations(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(organizationService.getOrganizations(includeInactive));
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrganization(request));
    }

    @PutMapping("/{organizationId}")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable("organizationId") Long organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.updateOrganization(organizationId, request));
    }
}
