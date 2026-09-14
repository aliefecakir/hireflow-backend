package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CatalogItemResponse;
import com.hireflow.backend.dto.CatalogLookupResponse;
import com.hireflow.backend.dto.CreateCatalogItemRequest;
import com.hireflow.backend.dto.UpdateCatalogItemRequest;
import com.hireflow.backend.entity.Department;
import com.hireflow.backend.entity.University;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.DepartmentRepository;
import com.hireflow.backend.repository.UniversityRepository;
import com.hireflow.backend.service.AcademyCatalogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/** UNIVERSITY / DEPARTMENT: aday listesi (puansız) ve yönetici katalog puanı. */
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

    @Override
    public List<CatalogItemResponse> getUniversities(boolean includeInactive) {
        List<University> rows = includeInactive
                ? universityRepository.findAllByOrderByNameAsc()
                : universityRepository.findByIsActvOrderByNameAsc(ACTIVE);
        return rows.stream().map(this::toResponse).toList();
    }

    @Override
    public List<CatalogItemResponse> getDepartments(boolean includeInactive) {
        List<Department> rows = includeInactive
                ? departmentRepository.findAllByOrderByNameAsc()
                : departmentRepository.findByIsActvOrderByNameAsc(ACTIVE);
        return rows.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CatalogItemResponse createUniversity(CreateCatalogItemRequest request, UUID currentUserId) {
        String name = normalizeName(request.name());
        if (universityRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Bu isimde bir üniversite zaten var.");
        }

        University university = new University();
        university.setName(name);
        university.setScore(request.score());
        university.setIsActv(ACTIVE);
        university.setCuser(currentUserId);
        return toResponse(universityRepository.save(university));
    }

    @Override
    @Transactional
    public CatalogItemResponse createDepartment(CreateCatalogItemRequest request, UUID currentUserId) {
        String name = normalizeName(request.name());
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new BadRequestException("Bu isimde bir bölüm zaten var.");
        }

        Department department = new Department();
        department.setName(name);
        department.setScore(request.score());
        department.setIsActv(ACTIVE);
        department.setCuser(currentUserId);
        return toResponse(departmentRepository.save(department));
    }

    /** Puan yalnızca yeni başvurulara işler; ACADEMY_APP.UNI_SCORE kopyası geçmişe dönük değişmez. */
    @Override
    @Transactional
    public CatalogItemResponse updateUniversity(
            Long universityId,
            UpdateCatalogItemRequest request,
            UUID currentUserId
    ) {
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new NoSuchElementException("Üniversite bulunamadı."));

        String name = normalizeName(request.name());
        if (universityRepository.existsByNameIgnoreCaseAndUniversityIdNot(name, universityId)) {
            throw new BadRequestException("Bu isimde bir üniversite zaten var.");
        }

        university.setName(name);
        university.setScore(request.score());
        university.setIsActv(request.isActv());
        university.setUuser(currentUserId);
        university.setUdate(LocalDateTime.now());
        return toResponse(universityRepository.save(university));
    }

    @Override
    @Transactional
    public CatalogItemResponse updateDepartment(
            Long departmentId,
            UpdateCatalogItemRequest request,
            UUID currentUserId
    ) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("Bölüm bulunamadı."));

        String name = normalizeName(request.name());
        if (departmentRepository.existsByNameIgnoreCaseAndDepartmentIdNot(name, departmentId)) {
            throw new BadRequestException("Bu isimde bir bölüm zaten var.");
        }

        department.setName(name);
        department.setScore(request.score());
        department.setIsActv(request.isActv());
        department.setUuser(currentUserId);
        department.setUdate(LocalDateTime.now());
        return toResponse(departmentRepository.save(department));
    }

    private String normalizeName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("Ad boş olamaz.");
        }
        return trimmed;
    }

    private CatalogItemResponse toResponse(University university) {
        return new CatalogItemResponse(
                university.getUniversityId(),
                university.getName(),
                university.getScore(),
                university.getIsActv()
        );
    }

    private CatalogItemResponse toResponse(Department department) {
        return new CatalogItemResponse(
                department.getDepartmentId(),
                department.getName(),
                department.getScore(),
                department.getIsActv()
        );
    }
}
