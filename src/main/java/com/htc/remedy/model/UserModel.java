package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by kvivek on 9/28/2017.
 */

@Entity
@Table(name = "auth_user_model")
public class UserModel implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column
    String userName;

    @Column
    boolean status;

    @Column
    String type;

    @Column
    String application;

    @Column
    LocalDateTime recentlogindatetime = LocalDateTime.now();

    @Column
    String client;


    @Column
    String masterClient;

    public String getMasterClient() {
        return masterClient;
    }

    public void setMasterClient(String masterClient) {
        this.masterClient = masterClient;
    }

    public LocalDateTime getRecentlogindatetime() {
        return recentlogindatetime;
    }

    public void setRecentlogindatetime(LocalDateTime recentlogindatetime) {
        this.recentlogindatetime = recentlogindatetime;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public UserModel() {
    }

    public UserModel(String userName, boolean status, String type, String application, LocalDateTime recentlogindatetime, String client) {
        this.userName = userName;
        this.status = status;
        this.type = type;
        this.application = application;
        this.recentlogindatetime = recentlogindatetime;
        this.client = client;
    }
}
