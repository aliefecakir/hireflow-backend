package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/** ACADEMY_APP_ST_HSTR: başvuru statü değişim geçmişi (satırları DB trigger yazar). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "\"ACADEMY_APP_ST_HSTR\"")
public class AcademyAppStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ACADEMY_APP_ST_HSTR_ID\"")
    private Long academyAppStHstrId;

    @Column(name = "\"ACADEMY_APP_ID\"", nullable = false)
    private Long academyAppId;

    @Column(name = "\"ST_ID\"", nullable = false)
    private Long stId;

    @Column(name = "\"PREV_ST_ID\"")
    private Long prevStId;

    @Column(name = "\"CHNG_RSN\"")
    private String chngRsn;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;
}
