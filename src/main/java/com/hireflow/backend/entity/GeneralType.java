package com.hireflow.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"GNL_TP\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralType {

    @Id
    @Column(name = "GNL_TP_ID")
    private Long gnlTpId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCR")
    private String descr;

    @Column(name = "SHRT_CODE")
    private String shrtCode;

    @Column(name = "ENT_CODE_NAME")
    private String entCodeName;

    @Column(name = "IS_ACTV", columnDefinition = "int2")
    private Short isActv;

    @Column(name = "CDATE")
    private LocalDateTime cdate;

    @Column(name = "UDATE")
    private LocalDateTime udate;

    @Column(name = "CUSER", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "UUSER", columnDefinition = "uuid")
    private UUID uuser;
}
