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

@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"ORGANIZATION\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ORGANIZATION_ID\"")
    private Long organizationId;

    @Column(name = "\"NAME\"", nullable = false)
    private String name;

    @Column(name = "\"DESCR\"")
    private String descr;

    @Column(name = "\"IS_ACTV\"", nullable = false, columnDefinition = "int2")
    private Short isActv = (short) 1;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Form> forms = new ArrayList<>();
}
