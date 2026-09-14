package com.hireflow.backend.service;

import com.hireflow.backend.dto.CreateOrganizationRequest;
import com.hireflow.backend.dto.OrganizationLookupResponse;
import com.hireflow.backend.dto.OrganizationResponse;
import com.hireflow.backend.dto.UpdateOrganizationRequest;

import java.util.List;
import java.util.UUID;

/** Akademi ORGANIZATION CRUD. */
public interface OrganizationService {

    List<OrganizationLookupResponse> getActiveOrganizations();

    List<OrganizationResponse> getOrganizations(boolean includeInactive);

    OrganizationResponse createOrganization(CreateOrganizationRequest request, UUID currentUserId);

    OrganizationResponse updateOrganization(
            Long organizationId,
            UpdateOrganizationRequest request,
            UUID currentUserId
    );
}
