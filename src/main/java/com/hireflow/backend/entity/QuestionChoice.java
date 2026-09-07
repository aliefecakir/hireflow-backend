package com.hireflow.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"QUESTION_CHOICE\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestionChoice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"QUESTION_CHOICE_ID\"")
    private Long questionChoiceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"QUESTION_ID\"", referencedColumnName = "\"QUESTION_ID\"", nullable = false)
    @JsonIgnoreProperties({"formQuestionRels", "questionChoices", "questionAnswers", "hibernateLazyInitializer", "handler"})
    private Question question;

    @Column(name = "\"CHOICE_TEXT\"", nullable = false)
    private String choiceText;

    @Column(name = "\"SCORE\"")
    private Integer score = 0;

    @Column(name = "\"ORD_NO\"", nullable = false)
    private Integer ordNo;

    @Column(name = "\"IS_OTHER\"", nullable = false, columnDefinition = "int2")
    private Short isOther = (short) 0;

    @OneToMany(mappedBy = "questionChoice", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();
}
