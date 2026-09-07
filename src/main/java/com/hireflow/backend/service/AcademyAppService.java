package com.hireflow.backend.service;

import com.hireflow.backend.dto.AcademyAppStatusResponse;
import com.hireflow.backend.dto.AcademyApplyRequest;
import com.hireflow.backend.dto.AcademyApplyResponse;
import com.hireflow.backend.dto.FormApplicationResponse;
import com.hireflow.backend.dto.UpdateAcademyAppStatusRequest;

import java.util.List;
import java.util.UUID;

public interface AcademyAppService {

    AcademyApplyResponse applyToForm(Long formId, AcademyApplyRequest request);

    List<FormApplicationResponse> getFormApplications(Long formId);

    List<AcademyAppStatusResponse> getApplicationStatuses();

    FormApplicationResponse updateApplicationStatus(Long appId, UpdateAcademyAppStatusRequest request, UUID evaluatorId);
}
