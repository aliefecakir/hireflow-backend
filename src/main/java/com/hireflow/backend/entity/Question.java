package com.hireflow.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

/** QUESTION: soru bankası; IS_ASSMT=0 aday, 1 mülakat. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"QUESTION\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"QUESTION_ID\"")
    private Long questionId;

    @Column(name = "\"QUESTION_TEXT\"", nullable = false)
    private String questionText;

    @Column(name = "\"TP_ID\"", nullable = false)
    private Long tpId; // GNL_TP soru tipi

    @Column(name = "\"MIN_SCORE\"", nullable = false)
    private Integer minScore;

    @Column(name = "\"MAX_SCORE\"", nullable = false)
    private Integer maxScore;

    @Column(name = "\"IS_ASSMT\"", nullable = false, columnDefinition = "int2")
    private Short isAssmt = (short) 0;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FormQuestionRel> formQuestionRels = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionChoice> questionChoices = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();
}
