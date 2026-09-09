package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/** PROFILE: aday CV özeti; USER_ID tekil. */
@Entity
@Table(name = "\"PROFILE\"")
public class Profile {

    @Id
    @Column(name = "\"PROFILE_ID\"", columnDefinition = "uuid")
    private UUID profileId;

    @Column(name = "\"USER_ID\"", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "\"PHONE\"")
    private String phone;

    @Column(name = "\"DEPT\"")
    private String dept;

    @Column(name = "\"EDUCATION\"")
    private String education;

    @Column(name = "\"PRFL_PHT_URL\"")
    private String prflPhtUrl;

    @Column(name = "\"CV_URL\"")
    private String cvUrl;

    @Column(name = "\"IS_CMPLTD\"", columnDefinition = "int2")
    private Short isCmpltd; // 8 alan doluysa 1

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public Profile() {
    }

    @PrePersist
    protected void onCreate() {
        if (profileId == null) {
            profileId = UUID.randomUUID(); // uygulama üretir
        }
        LocalDateTime now = LocalDateTime.now();
        if (cdate == null) {
            cdate = now;
        }
        udate = now;
        if (isCmpltd == null) {
            isCmpltd = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udate = LocalDateTime.now();
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getPrflPhtUrl() {
        return prflPhtUrl;
    }

    public void setPrflPhtUrl(String prflPhtUrl) {
        this.prflPhtUrl = prflPhtUrl;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public Short getIsCmpltd() {
        return isCmpltd;
    }

    public void setIsCmpltd(Short isCmpltd) {
        this.isCmpltd = isCmpltd;
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
