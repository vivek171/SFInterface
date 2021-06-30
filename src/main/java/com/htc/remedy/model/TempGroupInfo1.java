package com.htc.remedy.model;

import java.util.List;

public class TempGroupInfo1 {
    String name;
    String id;
    String groupType;
    List<String> groupnames;
    String category;
    String groupParent;
    String groupOverLay;
    String groupcategory;

    public TempGroupInfo1(String name, String id, String groupType, List<String> groupnames, String category, String groupParent, String groupOverLay, String groupcategory) {
        this.name = name;
        this.id = id;
        this.groupType = groupType;
        this.groupnames = groupnames;
        this.category = category;
        this.groupParent = groupParent;
        this.groupOverLay = groupOverLay;
        this.groupcategory = groupcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<String> getGroupnames() {
        return groupnames;
    }

    public void setGroupnames(List<String> groupnames) {
        this.groupnames = groupnames;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroupParent() {
        return groupParent;
    }

    public void setGroupParent(String groupParent) {
        this.groupParent = groupParent;
    }

    public String getGroupOverLay() {
        return groupOverLay;
    }

    public void setGroupOverLay(String groupOverLay) {
        this.groupOverLay = groupOverLay;
    }

    public String getGroupcategory() {
        return groupcategory;
    }

    public void setGroupcategory(String groupcategory) {
        this.groupcategory = groupcategory;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}