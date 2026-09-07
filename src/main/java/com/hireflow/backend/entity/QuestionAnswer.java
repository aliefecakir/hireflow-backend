package com.hireflow.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"QUESTION_ANSWER\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestionAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"QUESTION_ANSWER_ID\"")
    private Long questionAnswerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"ACADEMY_APP_ID\"", referencedColumnName = "\"ACADEMY_APP_ID\"", nullable = false)
    @JsonIgnoreProperties({"questionAnswers", "hibernateLazyInitializer", "handler"})
    private AcademyApp academyApp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"QUESTION_ID\"", referencedColumnName = "\"QUESTION_ID\"", nullable = false)
    @JsonIgnoreProperties({"formQuestionRels", "questionChoices", "questionAnswers", "hibernateLazyInitializer", "handler"})
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"QUESTION_CHOICE_ID\"", referencedColumnName = "\"QUESTION_CHOICE_ID\"")
    @JsonIgnoreProperties({"questionAnswers", "hibernateLazyInitializer", "handler"})
    private QuestionChoice questionChoice;

    @Column(name = "\"ANSWER_TEXT\"", columnDefinition = "text")
    private String answerText;

    @Column(name = "\"SCORE\"")
    private Integer score = 0;
}
