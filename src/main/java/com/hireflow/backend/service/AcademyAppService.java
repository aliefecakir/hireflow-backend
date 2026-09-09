package com.hireflow.backend.service;

import com.hireflow.backend.dto.AcademyAppStatusResponse;
import com.hireflow.backend.dto.AcademyApplyRequest;
import com.hireflow.backend.dto.AcademyApplyResponse;
import com.hireflow.backend.dto.FormApplicationResponse;
import com.hireflow.backend.dto.UpdateAcademyAppStatusRequest;

import java.util.List;
import java.util.UUID;

/** Akademi formuna başvuru, form başvuru listesi ve durum güncelleme. */
public interface AcademyAppService {

    AcademyApplyResponse applyToForm(Long formId, AcademyApplyRequest request); // PUBLIC başvuru + otomatik puan

    List<FormApplicationResponse> getFormApplications(Long formId); // yönetici liste

    List<AcademyAppStatusResponse> getApplicationStatuses(); // GNL_ST / ACADEMY_APP

    FormApplicationResponse updateApplicationStatus(Long appId, UpdateAcademyAppStatusRequest request, UUID evaluatorId);
}
