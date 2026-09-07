package com.hireflow.backend.repository;

import com.hireflow.backend.entity.GnlSt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GnlStRepository extends JpaRepository<GnlSt, Long> {

    Optional<GnlSt> findByEntCodeNameAndShrtCode(String entCodeName, String shrtCode);

    Optional<GnlSt> findFirstByEntCodeNameIgnoreCaseAndNameIgnoreCase(String entCodeName, String name);

    List<GnlSt> findByEntCodeNameIgnoreCaseOrderByNameAsc(String entCodeName);
}
