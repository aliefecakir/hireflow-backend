package com.hireflow.backend.controller;

import com.hireflow.backend.dto.AcademyApplyRequest;
import com.hireflow.backend.dto.AcademyApplyResponse;
import com.hireflow.backend.dto.FormApplicationResponse;
import com.hireflow.backend.service.AcademyAppService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academy/forms")
public class AcademyApplicationController {

    private final AcademyAppService academyAppService;

    public AcademyApplicationController(AcademyAppService academyAppService) {
        this.academyAppService = academyAppService;
    }

    @GetMapping("/{formId}/applications")
    @PreAuthorize("hasRole('ACADEMY_MNGR')")
    public ResponseEntity<List<FormApplicationResponse>> getFormApplications(
            @PathVariable("formId") Long formId
    ) {
        return ResponseEntity.ok(academyAppService.getFormApplications(formId));
    }

    @PostMapping("/{formId}/apply")
    public ResponseEntity<AcademyApplyResponse> applyToForm(
            @PathVariable("formId") Long formId,
            @Valid @RequestBody AcademyApplyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(academyAppService.applyToForm(formId, request));
    }
}
