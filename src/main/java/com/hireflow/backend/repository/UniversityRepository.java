package com.hireflow.backend.repository;

import com.hireflow.backend.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** UNIVERSITY; JPA CRUD (akademi puanı). */
@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {

    List<University> findByIsActvOrderByNameAsc(Short isActv);
}
