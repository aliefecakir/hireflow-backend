package com.hireflow.backend.repository;

import com.hireflow.backend.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph(attributePaths = "questionChoices")
    @Query("select distinct q from Question q order by q.questionId")
    List<Question> findAllWithChoices();

    @EntityGraph(attributePaths = "questionChoices")
    @Query("select q from Question q where q.questionId = :questionId")
    Optional<Question> findWithChoices(@Param("questionId") Long questionId);
}
