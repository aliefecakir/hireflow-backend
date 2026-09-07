package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "\"CDATE\"", nullable = false)
    private LocalDateTime cdate = LocalDateTime.now();

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", nullable = false, columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    @PrePersist
    protected void onCreate() {
        if (cdate == null) {
            cdate = LocalDateTime.now();
        }
    }
}
