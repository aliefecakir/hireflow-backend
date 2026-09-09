package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/** LANG katalog. */
@Repository
public interface LangRepository extends JpaRepository<Lang, UUID> {

    List<Lang> findByIsActvOrderByNameAsc(Short isActv);

    List<Lang> findByNameIn(Collection<String> names);

    List<Lang> findAllByOrderByNameAsc();
}
