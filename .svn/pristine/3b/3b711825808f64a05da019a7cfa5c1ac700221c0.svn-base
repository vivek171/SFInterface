package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

/**
 * Created by kvivek on 10/11/2017.
 */
public class FormModel implements BaseModel,Comparable<FormModel> {
    String formId;
    String formName;

    public FormModel(String formId, String formName) {
        this.formId = formId;
        this.formName = formName;
    }

    public String getFormId() {
        return formId;
    }

    @Override
    public String toString() {
        return "FormModel{" +
                "formId='" + formId + '\'' +
                ", formName='" + formName + '\'' +
                '}';
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormModel formModel = (FormModel) o;

        if (!formId.equals(formModel.formId)) return false;
        return formName.equals(formModel.formName);

    }

    @Override
    public int hashCode() {
        int result = formId.hashCode();
        result = 31 * result + formName.hashCode();
        return result;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public int compareTo(FormModel o) {
            return this.getFormName().compareTo(o.getFormName());
    }
}
