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

/** FORM_QUESTION_REL: formu soruya bağlar (sıra + zorunluluk). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"FORM_QUESTION_REL\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FormQuestionRel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"FORM_QUESTION_REL_ID\"")
    private Long formQuestionRelId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"FORM_ID\"", referencedColumnName = "\"FORM_ID\"", nullable = false)
    @JsonIgnoreProperties({"formQuestionRels", "academyApps", "hibernateLazyInitializer", "handler"})
    private Form form;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"QUESTION_ID\"", referencedColumnName = "\"QUESTION_ID\"", nullable = false)
    @JsonIgnoreProperties({"formQuestionRels", "questionChoices", "questionAnswers", "hibernateLazyInitializer", "handler"})
    private Question question;

    @Column(name = "\"ORD_NO\"", nullable = false)
    private Integer ordNo;

    @Column(name = "\"IS_REQ\"", nullable = false, columnDefinition = "int2")
    private Short isReq = (short) 1;
}
