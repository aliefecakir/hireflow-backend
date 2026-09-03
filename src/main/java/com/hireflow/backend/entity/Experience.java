package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"EXPERIENCE\"")
public class Experience {

    @Id
    @Column(name = "\"EXPERIENCE_ID\"", columnDefinition = "uuid")
    private UUID experienceId;

    @Column(name = "\"PROFILE_ID\"", columnDefinition = "uuid")
    private UUID profileId;

    @Column(name = "\"CORP_NAME\"")
    private String corpName;

    @Column(name = "\"POSITION\"")
    private String position;

    @Column(name = "\"DESCR\"", columnDefinition = "text")
    private String descr;

    @Column(name = "\"STLL_WRKG\"", columnDefinition = "int2")
    private Short stllWrkg;

    @Column(name = "\"SDATE\"")
    private String sdate;

    @Column(name = "\"EDATE\"")
    private String edate;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public Experience() {
    }

    @PrePersist
    protected void onCreate() {
        if (experienceId == null) {
            experienceId = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        if (cdate == null) {
            cdate = now;
        }
        udate = now;
        if (stllWrkg == null) {
            stllWrkg = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udate = LocalDateTime.now();
    }

    public UUID getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(UUID experienceId) {
        this.experienceId = experienceId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Short getStllWrkg() {
        return stllWrkg;
    }

    public void setStllWrkg(Short stllWrkg) {
        this.stllWrkg = stllWrkg;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
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
