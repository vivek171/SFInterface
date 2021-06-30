package com.htc.remedy.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.htc.remedy.base.BaseModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Created by kvivek on 10/3/2017.
 */
@Entity
@Table(name = "CTSSPI_end_point_domain")
public class EndPointDomain implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @Column
    String endPointName;

    @Column
    String endPointDescription;

    @Column
    String endPointKey;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime date = LocalDateTime.now();


    @JsonIgnore
    @OneToMany(mappedBy = "fieldsEndpoint", fetch = FetchType.LAZY)
    Set<FieldsDomain> selectedFields;

    @JsonIgnore
    @OneToMany(mappedBy = "qualificationEndPoint", fetch = FetchType.LAZY)
    Set<QualificationDomain> filter;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Column

    @JsonIgnore
    String qualificationString;

    @Column
    String formName;


    @Column
    Boolean active = Boolean.TRUE;

    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getQualificationString() {
        return qualificationString;
    }

    public void setQualificationString(String qualificationString) {
        this.qualificationString = qualificationString;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public EndPointDomain() {
    }

    public EndPointDomain(String endPointName, String endPointDescription, String endPointKey, String formName, Set<FieldsDomain> selectedFields, Set<QualificationDomain> filter) {
        this.endPointName = endPointName;
        this.endPointDescription = endPointDescription;
        this.endPointKey = endPointKey;
        this.formName = formName;
        this.selectedFields = selectedFields;
        this.filter = filter;
    }


    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public String getEndPointDescription() {
        return endPointDescription;
    }

    public void setEndPointDescription(String endPointDescription) {
        this.endPointDescription = endPointDescription;
    }

    public String getEndPointKey() {
        return endPointKey;
    }

    public void setEndPointKey(String endPointKey) {
        this.endPointKey = endPointKey;
    }

    public Set<FieldsDomain> getSelectedFields() {
        return selectedFields;
    }

    public void setSelectedFields(Set<FieldsDomain> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public Set<QualificationDomain> getFilter() {
        return filter;
    }

    public void setFilter(Set<QualificationDomain> filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndPointDomain that = (EndPointDomain) o;
        return Objects.equals(endPointName, that.endPointName);
    }


    @Override
    public int hashCode() {

        return Objects.hash(endPointName);
    }
}
