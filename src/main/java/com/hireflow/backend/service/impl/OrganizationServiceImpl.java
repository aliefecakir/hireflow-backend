package com.hireflow.backend.service.impl;

import com.hireflow.backend.dto.CreateOrganizationRequest;
import com.hireflow.backend.dto.OrganizationLookupResponse;
import com.hireflow.backend.dto.OrganizationResponse;
import com.hireflow.backend.dto.UpdateOrganizationRequest;
import com.hireflow.backend.entity.Organization;
import com.hireflow.backend.exception.BadRequestException;
import com.hireflow.backend.repository.OrganizationRepository;
import com.hireflow.backend.service.OrganizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/** Organizasyon listeleme, oluşturma, aktif/pasif. */
@Service
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    private static final UUID SYSTEM_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Short ACTIVE = 1; // IS_ACTV
    private static final Short INACTIVE = 0;

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public List<OrganizationLookupResponse> getActiveOrganizations() {
        // id + name (dropdown)
        return organizationRepository.findByIsActvOrderByNameAsc(ACTIVE).stream()
                .map(organization -> new OrganizationLookupResponse(
                        organization.getOrganizationId(),
                        organization.getName()
                ))
                .toList();
    }

    @Override
    public List<OrganizationResponse> getOrganizations(boolean includeInactive) {
        List<Organization> rows = includeInactive
                ? organizationRepository.findAllByOrderByNameAsc()
                : organizationRepository.findByIsActvOrderByNameAsc(ACTIVE);
        return rows.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request) {
        Organization organization = new Organization();
        organization.setName(request.name());
        organization.setDescr(request.descr());
        organization.setIsActv(ACTIVE);
        organization.setCuser(SYSTEM_USER_ID);

        Organization saved = organizationRepository.save(organization);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(Long organizationId, UpdateOrganizationRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NoSuchElementException("Organizasyon bulunamadı."));

        Short isActv = request.isActv();
        if (!ACTIVE.equals(isActv) && !INACTIVE.equals(isActv)) {
            throw new BadRequestException("Organizasyon durumu 0 veya 1 olmalıdır.");
        }

        organization.setIsActv(isActv);
        organization.setUuser(SYSTEM_USER_ID);
        organization.setUdate(LocalDateTime.now());
        return toResponse(organizationRepository.save(organization));
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getOrganizationId(),
                organization.getName(),
                organization.getDescr(),
                organization.getIsActv()
        );
    }
}
