package com.hireflow.backend.repository;

import com.hireflow.backend.entity.GeneralType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralTypeRepository extends JpaRepository<GeneralType, Long> {

    List<GeneralType> findByIsActvOrderByNameAsc(Short isActv);
    
    /**
     * Kısa kod ile GeneralType bulur
     * @param shrtCode Kısa kod
     * @return Optional<GeneralType>
     */
    Optional<GeneralType> findByShrtCode(String shrtCode);
    
    /**
     * Entity code name ile GeneralTyp bulur
     * @param entCodeName Entity code name
     * @return Optional<GeneralType>
     */
    Optional<GeneralType> findByEntCodeName(String entCodeName);
}
