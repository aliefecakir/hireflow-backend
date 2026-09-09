package com.hireflow.backend.service;

import com.hireflow.backend.dto.AcademyAppDetailsResponse;
import com.hireflow.backend.dto.AcademyEvaluateResponse;
import com.hireflow.backend.dto.EvaluateAcademyAppRequest;
import com.hireflow.backend.dto.ManualScoreResponse;

import java.util.UUID;

/** Akademi başvurusu detay, mülakat puanlama ve manuel skor. */
public interface AcademyEvaluationService {

    AcademyAppDetailsResponse getApplicationDetails(Long appId); // aday cevap + mülakat kriterleri

    AcademyEvaluateResponse evaluateApplication(Long appId, EvaluateAcademyAppRequest request, UUID evaluatorId);

    ManualScoreResponse saveManualScore(Long appId, EvaluateAcademyAppRequest.ManualScoreRequest request, UUID evaluatorId);
}
