package com.hireflow.backend.service;

import com.hireflow.backend.dto.CatalogLookupResponse;

import java.util.List;

/** Aktif üniversite / bölüm listesi (aday başvurusu). */
public interface AcademyCatalogService {

    List<CatalogLookupResponse> getActiveUniversities();

    List<CatalogLookupResponse> getActiveDepartments();
}
