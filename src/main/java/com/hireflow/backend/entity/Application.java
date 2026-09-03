package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"APP\"")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"APP_ID\"", columnDefinition = "uuid")
    private UUID appId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"POST_ID\"", referencedColumnName = "\"POST_ID\"", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "\"CNDT_ID\"", referencedColumnName = "USER_ID", nullable = false)
    private User candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ST_ID\"", referencedColumnName = "\"GNL_ST_ID\"")
    private GnlSt status;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public Application() {
    }

    @PrePersist
    protected void onCreate() {
        if (appId == null) {
            appId = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        if (cdate == null) {
            cdate = now;
        }
        udate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        udate = LocalDateTime.now();
    }

    public UUID getAppId() {
        return appId;
    }

    public void setAppId(UUID appId) {
        this.appId = appId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getCandidate() {
        return candidate;
    }

    public void setCandidate(User candidate) {
        this.candidate = candidate;
    }

    public GnlSt getStatus() {
        return status;
    }

    public void setStatus(GnlSt status) {
        this.status = status;
    }

    public LocalDateTime getCdate() {
        return cdate;
    }

    public void setCdate(LocalDateTime cdate) {
        this.cdate = cdate;
    }

    public LocalDateTime getUdate() {
        return udate;
    }

    public void setUdate(LocalDateTime udate) {
        this.udate = udate;
    }

    public UUID getCuser() {
        return cuser;
    }

    public void setCuser(UUID cuser) {
        this.cuser = cuser;
    }

    public UUID getUuser() {
        return uuser;
    }

    public void setUuser(UUID uuser) {
        this.uuser = uuser;
    }
}
