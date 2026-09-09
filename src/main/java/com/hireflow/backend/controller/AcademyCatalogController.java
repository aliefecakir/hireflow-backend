package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CatalogLookupResponse;
import com.hireflow.backend.service.AcademyCatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public: akademi başvurusu üniversite / bölüm dropdown. */
@RestController
@RequestMapping("/api/academy")
public class AcademyCatalogController {

    private final AcademyCatalogService academyCatalogService;

    public AcademyCatalogController(AcademyCatalogService academyCatalogService) {
        this.academyCatalogService = academyCatalogService;
    }

    @GetMapping("/universities")
    public ResponseEntity<List<CatalogLookupResponse>> getUniversities() {
        return ResponseEntity.ok(academyCatalogService.getActiveUniversities());
    }

    @GetMapping("/departments")
    public ResponseEntity<List<CatalogLookupResponse>> getDepartments() {
        return ResponseEntity.ok(academyCatalogService.getActiveDepartments());
    }
}
