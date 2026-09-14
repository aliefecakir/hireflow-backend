package com.hireflow.backend.service;

import com.hireflow.backend.dto.CatalogItemResponse;
import com.hireflow.backend.dto.CatalogLookupResponse;
import com.hireflow.backend.dto.CreateCatalogItemRequest;
import com.hireflow.backend.dto.UpdateCatalogItemRequest;

import java.util.List;
import java.util.UUID;

/** Aktif üniversite / bölüm listesi (aday başvurusu) ve katalog puan yönetimi. */
public interface AcademyCatalogService {

    List<CatalogLookupResponse> getActiveUniversities();

    List<CatalogLookupResponse> getActiveDepartments();

    List<CatalogItemResponse> getUniversities(boolean includeInactive);

    List<CatalogItemResponse> getDepartments(boolean includeInactive);

    CatalogItemResponse createUniversity(CreateCatalogItemRequest request, UUID currentUserId);

    CatalogItemResponse createDepartment(CreateCatalogItemRequest request, UUID currentUserId);

    CatalogItemResponse updateUniversity(Long universityId, UpdateCatalogItemRequest request, UUID currentUserId);

    CatalogItemResponse updateDepartment(Long departmentId, UpdateCatalogItemRequest request, UUID currentUserId);
}
