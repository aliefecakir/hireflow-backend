package com.hireflow.backend.controller;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;
import com.hireflow.backend.dto.QuestionUsageResponse;
import com.hireflow.backend.dto.UpdateQuestionRequest;
import com.hireflow.backend.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academy/questions")
@PreAuthorize("hasRole('ACADEMY_MNGR')")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getActiveQuestions() {
        return ResponseEntity.ok(questionService.getActiveQuestions());
    }

    @GetMapping("/types")
    public ResponseEntity<List<QuestionTypeResponse>> getActiveQuestionTypes() {
        return ResponseEntity.ok(questionService.getActiveQuestionTypes());
    }

    @GetMapping("/{id}/usage")
    public ResponseEntity<QuestionUsageResponse> getQuestionUsage(@PathVariable("id") Long questionId) {
        return ResponseEntity.ok(questionService.getQuestionUsage(questionId));
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.createQuestion(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable("id") Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request
    ) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable("id") Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
