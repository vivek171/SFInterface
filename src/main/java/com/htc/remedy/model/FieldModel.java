package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

/**
 * Created by kvivek on 10/11/2017.
 */
public class FieldModel implements BaseModel,Comparable<FieldModel> {
    String fieldId;
    String fieldName;

    public FieldModel(String fieldId, String fieldName) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
    }

    public String getFieldId() {
        return fieldId;
    }

    @Override
    public String toString() {
        return "FieldModel{" +
                "fieldId='" + fieldId + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldModel that = (FieldModel) o;

        if (!fieldId.equals(that.fieldId)) return false;
        return fieldName.equals(that.fieldName);

    }

    @Override
    public int hashCode() {
        int result = fieldId.hashCode();
        result = 31 * result + fieldName.hashCode();
        return result;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public int compareTo(FieldModel o) {
        return this.fieldName.compareTo(o.fieldName);
    }
}
