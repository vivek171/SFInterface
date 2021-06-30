package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

import java.util.List;

/**
 * Created by kvivek on 10/10/2017.
 */
public class ClientBuildingDetailsModel implements BaseModel {

    List<BaseModel> organisationModelList;
    List<BaseModel> buildingModels;
    List<BaseModel> departmentModels;
    List<BaseModel> floorModels;
    List<BaseModel> suiteModels;

    public ClientBuildingDetailsModel() {
    }

    public ClientBuildingDetailsModel(List<BaseModel> organisationModelList, List<BaseModel> buildingModels, List<BaseModel> departmentModels, List<BaseModel> floorModels, List<BaseModel> suiteModels) {
        this.organisationModelList = organisationModelList;
        this.buildingModels = buildingModels;
        this.departmentModels = departmentModels;
        this.floorModels = floorModels;
        this.suiteModels = suiteModels;
    }

    public List<BaseModel> getOrganisationModelList() {
        return organisationModelList;
    }

    public void setOrganisationModelList(List<BaseModel> organisationModelList) {
        this.organisationModelList = organisationModelList;
    }

    public List<BaseModel> getBuildingModels() {
        return buildingModels;
    }

    public void setBuildingModels(List<BaseModel> buildingModels) {
        this.buildingModels = buildingModels;
    }

    public List<BaseModel> getDepartmentModels() {
        return departmentModels;
    }

    public void setDepartmentModels(List<BaseModel> departmentModels) {
        this.departmentModels = departmentModels;
    }

    public List<BaseModel> getFloorModels() {
        return floorModels;
    }

    public void setFloorModels(List<BaseModel> floorModels) {
        this.floorModels = floorModels;
    }

    public List<BaseModel> getSuiteModels() {
        return suiteModels;
    }

    public void setSuiteModels(List<BaseModel> suiteModels) {
        this.suiteModels = suiteModels;
    }
}
