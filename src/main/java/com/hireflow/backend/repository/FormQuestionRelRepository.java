package com.hireflow.backend.repository;

import com.hireflow.backend.entity.FormQuestionRel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormQuestionRelRepository extends JpaRepository<FormQuestionRel, Long> {

    @EntityGraph(attributePaths = {"question", "question.questionChoices"})
    List<FormQuestionRel> findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(Long formId, Short isAssmt);

    @EntityGraph(attributePaths = {"question", "question.questionChoices"})
    List<FormQuestionRel> findByForm_FormIdOrderByOrdNoAsc(Long formId);

    void deleteByForm_FormId(Long formId);
}
