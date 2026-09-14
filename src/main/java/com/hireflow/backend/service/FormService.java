package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreateFormRequest;
import com.hireflow.backend.dto.FormDetailResponse;
import com.hireflow.backend.dto.FormQuestionResponse;
import com.hireflow.backend.dto.FormResponse;

import java.util.List;
import java.util.UUID;

/** Akademi FORM kayıtları, soru bağlama ve süre doldurma. */
public interface FormService {

    List<FormResponse> getForms(boolean includeInactive);

    FormDetailResponse getFormDetail(Long formId);

    List<FormQuestionResponse> getCandidateQuestions(Long formId);

    FormResponse createForm(CreateFormRequest request, UUID currentUserId);

    FormResponse updateForm(Long formId, CreateFormRequest request, UUID currentUserId);

    void deactivateExpiredForms(); // EDATE geçmiş aktif formları pasife çeker
}
