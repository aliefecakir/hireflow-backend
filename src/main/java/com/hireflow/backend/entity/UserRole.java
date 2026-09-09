package com.hireflow.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** USER_ROLE: kullanıcı ↔ GNL_TP rol (CAND, HR, ACADEMY_MNGR). */
@Entity
@Table(name = "\"USER_ROLE\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @Column(name = "USER_ROLE_ID", columnDefinition = "uuid")
    private UUID userRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_TP_ID", referencedColumnName = "GNL_TP_ID")
    private GeneralType roleType; // SHRT_CODE -> ROLE_*

    @Column(name = "IS_ADMIN", columnDefinition = "int2")
    private Short isAdmin;

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
