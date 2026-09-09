package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;
import com.hireflow.backend.dto.QuestionUsageResponse;
import com.hireflow.backend.dto.UpdateQuestionRequest;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Soru bankası: tip listesi, kullanım kontrolü, CRUD. */
@RestController
@RequestMapping("/api/academy/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /** Tüm sorular + şıklar + kaç formda kullanıldığı. */
    @GetMapping
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<List<QuestionResponse>> getActiveQuestions() {
        return ResponseEntity.ok(questionService.getActiveQuestions());
    }

    /** Public: GNL_TP'den soru tipleri (SNGL/MULT/OPEN...). */
    @GetMapping("/types")
    public ResponseEntity<List<QuestionTypeResponse>> getActiveQuestionTypes() {
        return ResponseEntity.ok(questionService.getActiveQuestionTypes());
    }

    /** Soru form/cevap kullanımında mı; silinebilir mi. */
    @GetMapping("/{id}/usage")
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<QuestionUsageResponse> getQuestionUsage(@PathVariable("id") Long questionId) {
        return ResponseEntity.ok(questionService.getQuestionUsage(questionId));
    }

    /** Soru + şıkları oluşturur. */
    @PostMapping
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(request));
    }

    /** Kullanımdaysa sınırlı güncelleme; değilse tam güncelleme. */
    @PutMapping("/{id}")
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable("id") Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, request));
    }

    /** Kullanılmayan soruyu ve şıklarını siler. */
    @DeleteMapping("/{id}")
    @PreAuthorize(AcademyRoles.WRITE)
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
