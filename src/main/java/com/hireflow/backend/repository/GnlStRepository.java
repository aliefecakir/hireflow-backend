package com.hireflow.backend.repository;

import com.hireflow.backend.entity.GnlSt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GnlStRepository extends JpaRepository<GnlSt, UUID> {

    Optional<GnlSt> findByEntCodeNameAndShrtCode(String entCodeName, String shrtCode);
}
