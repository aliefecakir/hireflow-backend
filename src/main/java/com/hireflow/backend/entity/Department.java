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

/** DEPARTMENT: akademi puanı için bölüm katalogu. */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"DEPARTMENT\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"DEPARTMENT_ID\"")
    private Long departmentId;

    @Column(name = "\"NAME\"", nullable = false)
    private String name;

    @Column(name = "\"SCORE\"", nullable = false)
    private Integer score = 0;

    @Column(name = "\"IS_ACTV\"", nullable = false, columnDefinition = "int2")
    private Short isActv = (short) 1;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AcademyApp> academyApps = new ArrayList<>();
}
