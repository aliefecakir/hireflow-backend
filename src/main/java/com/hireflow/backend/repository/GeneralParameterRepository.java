package com.hireflow.backend.repository;

import com.hireflow.backend.entity.GeneralParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** GNL_PARM; kısa kod ile parametre arama. */
@Repository
public interface GeneralParameterRepository extends JpaRepository<GeneralParameter, Long> {

    Optional<GeneralParameter> findByShrtCode(String shrtCode);
}
