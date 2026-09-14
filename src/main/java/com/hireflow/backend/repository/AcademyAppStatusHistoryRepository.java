package com.hireflow.backend.repository;

import com.hireflow.backend.entity.AcademyAppStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** ACADEMY_APP_ST_HSTR; başvuru bazlı tarihçe. */
@Repository
public interface AcademyAppStatusHistoryRepository extends JpaRepository<AcademyAppStatusHistory, Long> {

    List<AcademyAppStatusHistory> findByAcademyAppIdOrderByCdateDescAcademyAppStHstrIdDesc(Long academyAppId);
}
