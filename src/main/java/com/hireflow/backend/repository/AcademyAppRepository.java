package com.hireflow.backend.repository;

import com.hireflow.backend.entity.AcademyApp;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** ACADEMY_APP sorguları; üniversite/bölüm/form graph fetch. */
@Repository
public interface AcademyAppRepository extends JpaRepository<AcademyApp, Long> {

    @EntityGraph(attributePaths = {"university", "department", "form"})
    @Query("select a from AcademyApp a where a.academyAppId = :appId")
    Optional<AcademyApp> findDetailedById(@Param("appId") Long appId);

    @EntityGraph(attributePaths = {"university", "department"})
    List<AcademyApp> findByForm_FormIdOrderByAcademyAppIdDesc(Long formId);

    @EntityGraph(attributePaths = {"university", "department"})
    List<AcademyApp> findByForm_FormIdAndStIdInOrderByAcademyAppIdDesc(Long formId, Collection<Long> stIds);

    long countByForm_FormId(Long formId);

    @Query("select a.form.formId, count(a) from AcademyApp a group by a.form.formId")
    List<Object[]> countGroupedByFormId();
}
