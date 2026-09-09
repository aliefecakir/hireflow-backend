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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** ACADEMY_APP: forma yapılan başvurunun kimlik, okul ve puan satırı. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"ACADEMY_APP\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AcademyApp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ACADEMY_APP_ID\"")
    private Long academyAppId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"FORM_ID\"", referencedColumnName = "\"FORM_ID\"", nullable = false)
    @JsonIgnoreProperties({"formQuestionRels", "academyApps", "hibernateLazyInitializer", "handler"})
    private Form form;

    @Column(name = "\"NAME\"", nullable = false)
    private String name;

    @Column(name = "\"SURNAME\"", nullable = false)
    private String surname;

    @Column(name = "\"EMAIL\"", nullable = false)
    private String email;

    @Column(name = "\"PHONE\"", nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"UNIVERSITY_ID\"", referencedColumnName = "\"UNIVERSITY_ID\"", nullable = false)
    @JsonIgnoreProperties({"academyApps", "hibernateLazyInitializer", "handler"})
    private University university;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"DEPARTMENT_ID\"", referencedColumnName = "\"DEPARTMENT_ID\"", nullable = false)
    @JsonIgnoreProperties({"academyApps", "hibernateLazyInitializer", "handler"})
    private Department department;

    @Column(name = "\"GRAD_DATE\"")
    private LocalDate gradDate;

    @Column(name = "\"UNI_SCORE\"", nullable = false)
    private Integer uniScore = 0; // UNIVERSITY.SCORE kopyası

    @Column(name = "\"DEP_SCORE\"", nullable = false)
    private Integer depScore = 0;

    @Column(name = "\"TOTAL_SCORE\"", nullable = false)
    private Integer totalScore = 0; // uni + bölüm + cevap puanları

    @Column(name = "\"INTERVIEW_SCORE\"", nullable = false)
    private Integer interviewScore = 0;

    @Column(name = "\"ST_ID\"", nullable = false)
    private Long stId;

    @Column(name = "\"STATUS_DESCR\"")
    private String statusDescr;

    @Column(name = "\"EVALUATED_BY\"", columnDefinition = "uuid")
    private UUID evaluatedBy;

    @OneToMany(mappedBy = "academyApp", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();
}
