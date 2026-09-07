package com.hireflow.backend.service;

import com.hireflow.backend.dto.AcademyAppDetailsResponse;
import com.hireflow.backend.dto.AcademyEvaluateResponse;
import com.hireflow.backend.dto.EvaluateAcademyAppRequest;

import java.util.UUID;

public interface AcademyEvaluationService {

    AcademyAppDetailsResponse getApplicationDetails(Long appId);

    AcademyEvaluateResponse evaluateApplication(Long appId, EvaluateAcademyAppRequest request, UUID evaluatorId);
}
