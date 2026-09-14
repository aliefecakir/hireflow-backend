package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** DEPARTMENT; JPA CRUD (akademi puanı). */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByIsActvOrderByNameAsc(Short isActv);

    List<Department> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndDepartmentIdNot(String name, Long departmentId);
}
