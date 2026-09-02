package com.hireflow.backend.repository;

import com.hireflow.backend.entity.GeneralType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneralTypeRepository extends JpaRepository<GeneralType, UUID> {
    
    /**
     * Kısa kod ile GeneralType bulur
     * @param shrtCode Kısa kod
     * @return Optional<GeneralType>
     */
    Optional<GeneralType> findByShrtCode(String shrtCode);
    
    /**
     * Entity code name ile GeneralType bulur
     * @param entCodeName Entity code name
     * @return Optional<GeneralType>
     */
    Optional<GeneralType> findByEntCodeName(String entCodeName);
}
