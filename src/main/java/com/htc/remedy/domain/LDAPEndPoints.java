package com.htc.remedy.domain;

import javax.persistence.*;

@Entity
@Table(name="SEC_LDAP_Endpoints")
public class LDAPEndPoints {

    @Id
    @Column(name="Ldap_endpoints_sk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "LDAP_Details_sk")
    private LDAPAccounts ldapAccounts;

    @Column(name="end_point_name")
    private String endPointName;

    @Column
    private String base;

    @Column
    private String query;


    @Column
    private String requiredFields = "*";

    @Column
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LDAPEndPoints() {
    }

    public Long getId() {
        return id;
    }

    public LDAPAccounts getLdapAccounts() {
        return ldapAccounts;
    }

    public void setLdapAccounts(LDAPAccounts ldapAccounts) {
        this.ldapAccounts = ldapAccounts;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(String requiredFields) {
        this.requiredFields = requiredFields;
    }
}
