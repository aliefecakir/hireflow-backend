package com.hireflow.backend.repository;

import com.hireflow.backend.entity.FormQuestionRel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Form-soru köprüsü; şıklarla birlikte yükler. */
@Repository
public interface FormQuestionRelRepository extends JpaRepository<FormQuestionRel, Long> {

    @EntityGraph(attributePaths = {"question", "question.questionChoices"})
    List<FormQuestionRel> findByForm_FormIdAndQuestion_IsAssmtOrderByOrdNoAsc(Long formId, Short isAssmt);

    @EntityGraph(attributePaths = {"question", "question.questionChoices"})
    List<FormQuestionRel> findByForm_FormIdOrderByOrdNoAsc(Long formId);

    void deleteByForm_FormId(Long formId);

    long countByQuestion_QuestionId(Long questionId);

    @EntityGraph(attributePaths = {"form"})
    List<FormQuestionRel> findByQuestion_QuestionId(Long questionId);

    @Query("select r.question.questionId, count(r) from FormQuestionRel r group by r.question.questionId")
    List<Object[]> countGroupedByQuestionId(); // soru listesinde formCount
}
