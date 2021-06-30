package com.htc.remedy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.htc.remedy.base.BaseModel;

import javax.persistence.*;

/**
 * Created by kvivek on 10/3/2017.
 */
@Entity
@Table(name="CTSSPI_qualification_domain")
public class QualificationDomain implements BaseModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "endpoint_qualification_id")
    @JsonIgnore
    EndPointDomain qualificationEndPoint;

    public QualificationDomain(String columnName, String columnValue, String condition,String append,EndPointDomain endPointDomain) {

        this.columnName = columnName;
        this.columnValue = columnValue;
        this.condition = condition;
        this.appendCondition=append;
        this.qualificationEndPoint = endPointDomain;
    }

    @Column
    String columnName;

    @Column
    String columnValue;

    @Column
    String condition;

    @Column
    String appendCondition;

    public String getAppendCondition() {
        return appendCondition;
    }

    public void setAppendCondition(String appendCondition) {
        this.appendCondition = appendCondition;
    }

    public QualificationDomain() {
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Long getId() {
        return id;
    }


    public EndPointDomain getQualificationEndPoint() {
        return qualificationEndPoint;
    }

    public void setQualificationEndPoint(EndPointDomain qualificationEndPoint) {
        this.qualificationEndPoint = qualificationEndPoint;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QualificationDomain that = (QualificationDomain) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (qualificationEndPoint != null ? !qualificationEndPoint.equals(that.qualificationEndPoint) : that.qualificationEndPoint != null)
            return false;
        if (columnName != null ? !columnName.equals(that.columnName) : that.columnName != null) return false;
        if (columnValue != null ? !columnValue.equals(that.columnValue) : that.columnValue != null) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        return !(appendCondition != null ? !appendCondition.equals(that.appendCondition) : that.appendCondition != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (qualificationEndPoint != null ? qualificationEndPoint.hashCode() : 0);
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        result = 31 * result + (columnValue != null ? columnValue.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (appendCondition != null ? appendCondition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QualificationDomain{" +
                "id=" + id +
                ", qualificationEndPoint=" + qualificationEndPoint +
                ", columnName='" + columnName + '\'' +
                ", columnValue='" + columnValue + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }

    public void setId(Long id) {
        this.id = id;
    }
}
