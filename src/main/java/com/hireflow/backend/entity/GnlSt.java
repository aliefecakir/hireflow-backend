package com.hireflow.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "\"GNL_ST\"")
public class GnlSt {

    @Id
    @Column(name = "\"GNL_ST_ID\"", columnDefinition = "uuid")
    private UUID gnlStId;

    @Column(name = "\"NAME\"")
    private String name;

    @Column(name = "\"DESCR\"")
    private String descr;

    @Column(name = "\"SHRT_CODE\"")
    private String shrtCode;

    @Column(name = "\"ENT_CODE_NAME\"")
    private String entCodeName;

    public GnlSt() {
    }

    public UUID getGnlStId() {
        return gnlStId;
    }

    public void setGnlStId(UUID gnlStId) {
        this.gnlStId = gnlStId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getShrtCode() {
        return shrtCode;
    }

    public void setShrtCode(String shrtCode) {
        this.shrtCode = shrtCode;
    }

    public String getEntCodeName() {
        return entCodeName;
    }

    public void setEntCodeName(String entCodeName) {
        this.entCodeName = entCodeName;
    }
}
