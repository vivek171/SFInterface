package com.htc.remedy.domain;

import com.htc.remedy.base.BaseModel;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "interface_access_log")
public class LogDomain implements BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "application_name")
    String applicationname;

    @Column(name = "source_ip")
    String sourceip;

    @Column(name = "user_name")
    String username;

    @Column(name = "created_date")
    long createddate = (new Date().getTime()) / 1000;

    @Column(name = "method_type")
    String methodtype;

    @Column(name = "form_name")
    String formname;

    @Column(name = "support_function")
    String supportfunction;

    @Column(name = "ticket_id")
    String ticketnumber;

    @Column(name = "data", columnDefinition = "nvarchar(max)")
    String updatedfields;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicketnumber() {
        return ticketnumber;
    }

    public void setTicketnumber(String ticketnumber) {
        this.ticketnumber = ticketnumber;
    }

    public String getUpdatedfields() {
        return updatedfields;
    }

    public void setUpdatedfields(String updatedfields) {
        this.updatedfields = updatedfields;
    }

    public String getSourceip() {
        return sourceip;
    }

    public void setSourceip(String sourceip) {
        this.sourceip = sourceip;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public String getSupportfunction() {
        return supportfunction;
    }

    public void setSupportfunction(String supportfunction) {
        this.supportfunction = supportfunction;
    }

    public String getMethodtype() {
        return methodtype;
    }

    public void setMethodtype(String methodtype) {
        this.methodtype = methodtype;
    }

    public LogDomain(String applicationname, String username, String sourceip, String formname, String methodtype) {
        this.applicationname = applicationname;
        this.username = username;
        this.sourceip = sourceip;
        this.formname = formname;
        this.methodtype = methodtype;
    }

    public LogDomain(String applicationname, String username, String ticketnumber, String sourceip, String formname, String methodtype) {
        this.applicationname = applicationname;
        this.username = username;
        this.ticketnumber = ticketnumber;
        this.sourceip = sourceip;
        this.formname = formname;
        this.methodtype = methodtype;
    }

    public LogDomain(String applicationname, String username, String ticketnumber, String updatedfields, String sourceip, String formname, String methodtype) {
        this.applicationname = applicationname;
        this.username = username;
        this.ticketnumber = ticketnumber;
        this.updatedfields = updatedfields;
        this.sourceip = sourceip;
        this.formname = formname;
        this.methodtype = methodtype;
    }

    public LogDomain() {
    }
}
