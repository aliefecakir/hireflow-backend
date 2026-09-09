package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CatalogLookupResponse;
import com.hireflow.backend.repository.DepartmentRepository;
import com.hireflow.backend.repository.UniversityRepository;
import com.hireflow.backend.service.AcademyCatalogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** UNIVERSITY / DEPARTMENT: aktif kayıtlar, puan alanı yok. */
@Service
@Transactional(readOnly = true)
public class AcademyCatalogServiceImpl implements AcademyCatalogService {

    private static final Short ACTIVE = 1;

    private final UniversityRepository universityRepository;
    private final DepartmentRepository departmentRepository;

    public AcademyCatalogServiceImpl(
            UniversityRepository universityRepository,
            DepartmentRepository departmentRepository
    ) {
        this.universityRepository = universityRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<CatalogLookupResponse> getActiveUniversities() {
        return universityRepository.findByIsActvOrderByNameAsc(ACTIVE).stream()
                .map(university -> new CatalogLookupResponse(university.getUniversityId(), university.getName()))
                .toList();
    }

    @Override
    public List<CatalogLookupResponse> getActiveDepartments() {
        return departmentRepository.findByIsActvOrderByNameAsc(ACTIVE).stream()
                .map(department -> new CatalogLookupResponse(department.getDepartmentId(), department.getName()))
                .toList();
    }
}
