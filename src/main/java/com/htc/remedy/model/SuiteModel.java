package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

/**
 * Created by kvivek on 10/10/2017.
 */
public class SuiteModel implements BaseModel {


    String SuiteId;
    String BuildingId;
    String suite;
    String FloorId;


    public SuiteModel(String suiteId, String buildingId, String suite, String floorId) {
        SuiteId = suiteId;
        BuildingId = buildingId;
        this.suite = suite;
        FloorId = floorId;
    }

    public String getSuiteId() {
        return SuiteId;
    }

    public void setSuiteId(String suiteId) {
        SuiteId = suiteId;
    }

    public String getFloorId() {
        return FloorId;
    }

    public void setFloorId(String floorId) {
        FloorId = floorId;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getBuildingId() {
        return BuildingId;
    }

    public void setBuildingId(String buildingId) {
        BuildingId = buildingId;
    }
}
