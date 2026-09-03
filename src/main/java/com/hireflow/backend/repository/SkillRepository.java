package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    List<Skill> findByIsActvOrderByNameAsc(Short isActv);

    List<Skill> findByIsActvAndNameIn(Short isActv, Collection<String> names);

    List<Skill> findByIsActvAndNameContainingIgnoreCaseOrderByNameAsc(Short isActv, String name);
}
