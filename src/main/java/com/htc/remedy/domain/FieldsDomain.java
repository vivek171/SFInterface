package com.htc.remedy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.htc.remedy.base.BaseModel;

import javax.persistence.*;

/**
 * Created by kvivek on 10/3/2017.
 */
@Entity
@Table(name = "CTSSPI_fields_domain")
public class FieldsDomain implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String fieldName;

    @Column
    Long fieldId;


    @Column
    String representation;


    @ManyToOne
    @JoinColumn(name = "endpoint_field_id")
    @JsonIgnore
    EndPointDomain fieldsEndpoint;

    public FieldsDomain(Long fieldId, String fieldName, EndPointDomain endPointDomain) {
        this.fieldName = fieldName;
        this.fieldId = fieldId;
        this.fieldsEndpoint = endPointDomain;
    }

    public Long getId() {
        return id;
    }


    public String getFieldName() {
        return fieldName;
    }

    public FieldsDomain() {
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public EndPointDomain getFieldsEndpoint() {
        return fieldsEndpoint;
    }

    public void setFieldsEndpoint(EndPointDomain fieldsEndpoint) {
        this.fieldsEndpoint = fieldsEndpoint;
    }

    @Override
    public String toString() {
        return "FieldsDomain{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", fieldId=" + fieldId +
                ", representation='" + representation + '\'' +
                ", fieldsEndpoint=" + fieldsEndpoint +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }
}
