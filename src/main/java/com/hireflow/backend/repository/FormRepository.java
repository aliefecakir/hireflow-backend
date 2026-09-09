package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Form;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/** FORM listeleri; organization graph. */
@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    @EntityGraph(attributePaths = "organization")
    List<Form> findByIsActvOrderBySdateDesc(Short isActv);

    @EntityGraph(attributePaths = "organization")
    List<Form> findByIsActvAndEdateBefore(Short isActv, LocalDateTime edate); // scheduler: süresi dolanlar

    @EntityGraph(attributePaths = "organization")
    List<Form> findAllByOrderBySdateDesc();
}
