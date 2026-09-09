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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** FORM: akademi başvuru formu; SDATE/EDATE penceresi, ORGANIZATION bağlanır. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"FORM\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"FORM_ID\"")
    private Long formId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"ORGANIZATION_ID\"", referencedColumnName = "\"ORGANIZATION_ID\"", nullable = false)
    @JsonIgnoreProperties({"forms", "hibernateLazyInitializer", "handler"})
    private Organization organization;

    @Column(name = "\"TITLE\"", nullable = false)
    private String title;

    @Column(name = "\"DESCR\"")
    private String descr;

    @Column(name = "\"IS_ACTV\"", nullable = false, columnDefinition = "int2")
    private Short isActv = (short) 1; // 1 aktif, 0 süresi dolmuş / kapatılmış

    @Column(name = "\"SDATE\"", nullable = false)
    private LocalDateTime sdate;

    @Column(name = "\"EDATE\"", nullable = false)
    private LocalDateTime edate;

    @OneToMany(mappedBy = "form", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FormQuestionRel> formQuestionRels = new ArrayList<>();

    @OneToMany(mappedBy = "form", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AcademyApp> academyApps = new ArrayList<>();
}
