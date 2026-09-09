package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreateFormRequest;
import com.hireflow.backend.dto.FormDetailResponse;
import com.hireflow.backend.dto.FormQuestionResponse;
import com.hireflow.backend.dto.FormResponse;

import java.util.List;

/** Akademi FORM kayıtları, soru bağlama ve süre doldurma. */
public interface FormService {

    List<FormResponse> getForms(boolean includeInactive);

    FormDetailResponse getFormDetail(Long formId);

    List<FormQuestionResponse> getCandidateQuestions(Long formId);

    FormResponse createForm(CreateFormRequest request);

    FormResponse updateForm(Long formId, CreateFormRequest request);

    void deactivateExpiredForms(); // EDATE geçmiş aktif formları pasife çeker
}
