package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreateQuestionRequest;
import com.hireflow.backend.dto.QuestionResponse;
import com.hireflow.backend.dto.QuestionTypeResponse;

import java.util.List;

public interface QuestionService {

    List<QuestionResponse> getActiveQuestions();

    List<QuestionTypeResponse> getActiveQuestionTypes();

    QuestionResponse createQuestion(CreateQuestionRequest request);
}
