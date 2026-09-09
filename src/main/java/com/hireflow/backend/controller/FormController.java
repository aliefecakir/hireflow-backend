package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreateFormRequest;
import com.hireflow.backend.dto.FormDetailResponse;
import com.hireflow.backend.dto.FormQuestionResponse;
import com.hireflow.backend.dto.FormResponse;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.FormService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Akademi form CRUD + aday soru listesi. */
@RestController
@RequestMapping("/api/academy/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    /** Liste: herkese aktif formlar; akademi kadrosu includeInactive=true ile pasifleri de görür. */
    @GetMapping
    public ResponseEntity<List<FormResponse>> getForms(
            @RequestParam(name = "includeInactive", defaultValue = "false") boolean includeInactive,
            Authentication authentication
    ) {
        return ResponseEntity.ok(formService.getForms(includeInactive && AcademyRoles.hasReadAccess(authentication)));
    }

    /** Yönetici form detayı (aday + mülakat soruları). */
    @GetMapping("/{formId}")
    @PreAuthorize(AcademyRoles.READ)
    public ResponseEntity<FormDetailResponse> getFormDetail(
            @PathVariable("formId") Long formId
    ) {
        return ResponseEntity.ok(formService.getFormDetail(formId));
    }

    /** Public: adayın dolduracağı sorular (isAssmt=0). */
    @GetMapping("/{formId}/questions")
    public ResponseEntity<List<FormQuestionResponse>> getCandidateQuestions(
            @PathVariable("formId") Long formId
    ) {
        return ResponseEntity.ok(formService.getCandidateQuestions(formId));
    }

    /** Yeni form + FORM_QUESTION_REL satırları. */
    @PostMapping
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<FormResponse> createForm(@Valid @RequestBody CreateFormRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formService.createForm(request));
    }

    /** Form alanlarını günceller; soru ilişkilerini baştan yazar. */
    @PutMapping("/{formId}")
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<FormResponse> updateForm(
            @PathVariable("formId") Long formId,
            @Valid @RequestBody CreateFormRequest request
    ) {
        return ResponseEntity.ok(formService.updateForm(formId, request));
    }
}
