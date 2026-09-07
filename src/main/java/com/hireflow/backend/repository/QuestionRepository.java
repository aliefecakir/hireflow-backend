package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "questionChoices")
    @Query("select distinct q from Question q order by q.questionId")
    List<Question> findAllWithChoices();
}
