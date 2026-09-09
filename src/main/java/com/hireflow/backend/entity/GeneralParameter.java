package com.hireflow.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/** GNL_PARM: genel uygulama parametreleri (SHRT_CODE + VAL). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "\"GNL_PARM\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GeneralParameter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"GNL_PARM_ID\"")
    private Long gnlParmId;

    @Column(name = "\"NAME\"", nullable = false, length = 100)
    private String name;

    @Column(name = "\"SHRT_CODE\"", nullable = false, length = 100)
    private String shrtCode;

    @Column(name = "\"VAL\"", nullable = false)
    private Long val;

    @Column(name = "\"IS_ACTV\"", nullable = false, columnDefinition = "int2")
    private Short isActv = (short) 1;

    @Column(name = "\"CUSER_NAME\"")
    private String cuserName;

    @Column(name = "\"UUSER_NAME\"")
    private String uuserName;
}
