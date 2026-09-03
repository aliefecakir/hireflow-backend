package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"LANG\"")
public class Lang {

    @Id
    @Column(name = "\"LANG_ID\"", columnDefinition = "uuid")
    private UUID langId;

    @Column(name = "\"NAME\"")
    private String name;

    @Column(name = "\"SHRT_CODE\"")
    private String shrtCode;

    @Column(name = "\"IS_ACTV\"", columnDefinition = "int2")
    private Short isActv;

    @Column(name = "\"CDATE\"")
    private LocalDateTime cdate;

    @Column(name = "\"UDATE\"")
    private LocalDateTime udate;

    @Column(name = "\"CUSER\"", columnDefinition = "uuid")
    private UUID cuser;

    @Column(name = "\"UUSER\"", columnDefinition = "uuid")
    private UUID uuser;

    public Lang() {
    }

    public UUID getLangId() {
        return langId;
    }

    public void setLangId(UUID langId) {
        this.langId = langId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShrtCode() {
        return shrtCode;
    }

    public void setShrtCode(String shrtCode) {
        this.shrtCode = shrtCode;
    }

    public Short getIsActv() {
        return isActv;
    }

    public void setIsActv(Short isActv) {
        this.isActv = isActv;
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
