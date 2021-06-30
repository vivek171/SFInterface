package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

/**
 * Created by kvivek on 10/10/2017.
 */
public class FloorModel implements BaseModel {

    String FloorId;
    String Floor;
    String BuildingId;


    public FloorModel(String floorId, String floor, String buildingId) {
        FloorId = floorId;
        Floor = floor;
        BuildingId = buildingId;
    }

    public String getFloorId() {
        return FloorId;
    }

    public void setFloorId(String floorId) {
        FloorId = floorId;
    }

    public String getFloor() {
        return Floor;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public String getBuildingId() {
        return BuildingId;
    }

    public void setBuildingId(String buildingId) {
        BuildingId = buildingId;
    }
}
