package com.hireflow.backend.service;

import com.hireflow.backend.dto.ApplicationCreateRequest;
import com.hireflow.backend.dto.ApplicationResponse;

import java.util.List;
import java.util.UUID;

/** İş ilanı başvuruları (APP tablosu). */
public interface ApplicationService {

    ApplicationResponse applyToPost(UUID currentUserId, ApplicationCreateRequest request);

    List<ApplicationResponse> getMyApplications(UUID currentUserId);

    List<ApplicationResponse> getAllApplications();

    List<ApplicationResponse> getApplicationsByPost(UUID postId);

    ApplicationResponse updateApplicationStatus(UUID appId, String statusCode, UUID hrUserId);
}
