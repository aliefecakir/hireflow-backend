package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"PRFL_EXP_REL\"")
public class ProfileExpRel {

    @Id
    @Column(name = "\"PRFL_EXP_REL_ID\"", columnDefinition = "uuid")
    private UUID prflExpRelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"PRFL_ID\"", referencedColumnName = "\"PROFILE_ID\"")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"EXP_ID\"", referencedColumnName = "\"EXPERIENCE_ID\"")
    private Experience experience;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public ProfileExpRel() {
    }

    @PrePersist
    protected void onCreate() {
        if (prflExpRelId == null) {
            prflExpRelId = UUID.randomUUID();
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

    public UUID getPrflExpRelId() {
        return prflExpRelId;
    }

    public void setPrflExpRelId(UUID prflExpRelId) {
        this.prflExpRelId = prflExpRelId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
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
