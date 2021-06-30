package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

/**
 * Created by kvivek on 10/4/2017.
 */
public class CatalogModel implements BaseModel {
    String serveyID;
    String catalog;
    String catalogDescription;


    public CatalogModel(String serveyID, String catalog, String catalogDescription) {
        this.serveyID = serveyID;
        this.catalog = catalog;
        this.catalogDescription = catalogDescription;
    }

    public String getServeyID() {
        return serveyID;
    }

    public void setServeyID(String serveyID) {
        this.serveyID = serveyID;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getCatalogDescription() {
        return catalogDescription;
    }

    public void setCatalogDescription(String catalogDescription) {
        this.catalogDescription = catalogDescription;
    }
}
