package com.hireflow.backend.repository;

import com.hireflow.backend.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Akademi soru cevapları; başvuru/soru filtreleri. */
@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    @EntityGraph(attributePaths = {"question", "questionChoice"})
    List<QuestionAnswer> findByAcademyApp_AcademyAppIdAndQuestion_IsAssmt(
            Long academyAppId,
            Short isAssmt
    );

    void deleteByAcademyApp_AcademyAppIdAndQuestion_IsAssmt(Long academyAppId, Short isAssmt);

    // Belirli bir başvurunun belirli bir soruya verdiği cevapları getir
    @EntityGraph(attributePaths = {"question", "questionChoice"})
    List<QuestionAnswer> findByAcademyApp_AcademyAppIdAndQuestion_QuestionId(
            Long academyAppId,
            Long questionId
    );

    // Bir başvurunun tüm cevaplarını getir (toplam puan hesabı için)
    @EntityGraph(attributePaths = {"question", "questionChoice"})
    List<QuestionAnswer> findByAcademyApp_AcademyAppId(Long academyAppId);

    // Bir soruya kaç cevap verildiğini say
    long countByQuestion_QuestionId(Long questionId);
}
