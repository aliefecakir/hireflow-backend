package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;
import com.hireflow.backend.dto.QuestionUsageResponse;
import com.hireflow.backend.dto.UpdateQuestionRequest;

import java.util.List;

public interface QuestionService {

    List<QuestionResponse> getActiveQuestions();

    List<QuestionTypeResponse> getActiveQuestionTypes();

    QuestionResponse createQuestion(CreateQuestionRequest request);

    QuestionUsageResponse getQuestionUsage(Long questionId);

    QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);

    void deleteQuestion(Long questionId);
}
