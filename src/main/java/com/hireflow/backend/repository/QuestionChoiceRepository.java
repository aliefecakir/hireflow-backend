package com.hireflow.backend.repository;

import com.hireflow.backend.entity.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Soru şıkları; N+1 için IN sorgusu. */
@Repository
public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, Long> {

    List<QuestionChoice> findByQuestion_QuestionIdOrderByOrdNoAsc(Long questionId);
    
    // Performans optimizasyonu: Birden fazla sorunun tüm şıklarını tek sorguda çek
    List<QuestionChoice> findByQuestion_QuestionIdIn(List<Long> questionIds);
}
