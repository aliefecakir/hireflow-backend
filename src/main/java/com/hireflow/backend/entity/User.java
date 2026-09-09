package com.hireflow.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** USER tablosu: oturum, profil ve ilan başvurularının sahibi. */
@Entity
@Table(name = "\"USER\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "USER_ID", columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "SURNAME", nullable = false)
    private String surname;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "ST_ID")
    private Long stId; // GNL_ST kullanıcı durumu

    @Column(name = "CDATE")
    private LocalDateTime cdate;

    @Column(name = "UDATE")
    private LocalDateTime udate;

    @Column(name = "CUSER", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "UUSER", columnDefinition = "uuid")
    private UUID uuser;
}
