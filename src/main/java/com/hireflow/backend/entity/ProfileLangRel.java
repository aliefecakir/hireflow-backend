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
@Table(name = "\"PRFL_LANG_REL\"")
public class ProfileLangRel {

    @Id
    @Column(name = "\"PRFL_LANG_REL_ID\"", columnDefinition = "uuid")
    private UUID prflLangRelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"PRFL_ID\"", referencedColumnName = "\"PROFILE_ID\"")
    private Profile profile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "\"LANG_ID\"", referencedColumnName = "\"LANG_ID\"")
    private Lang lang;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public ProfileLangRel() {
    }

    @PrePersist
    protected void onCreate() {
        if (prflLangRelId == null) {
            prflLangRelId = UUID.randomUUID();
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

    public UUID getPrflLangRelId() {
        return prflLangRelId;
    }

    public void setPrflLangRelId(UUID prflLangRelId) {
        this.prflLangRelId = prflLangRelId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
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
