package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;


public class EndpointModel implements BaseModel {
    String endpointname;
    long requesttime;
    long responsetime;
    String username;
    String user_sk;

    public String getEndpointname() {
        return endpointname;
    }

    public void setEndpointname(String endpointname) {
        this.endpointname = endpointname;
    }

    public long getRequesttime() {
        return requesttime;
    }

    public EndpointModel() {
    }

    public EndpointModel(String endpointname, long requesttime, long responsetime, String username, String user_sk) {
        this.endpointname = endpointname;
        this.requesttime = requesttime;
        this.responsetime = responsetime;
        this.username = username;
        this.user_sk = user_sk;
    }

    public void setRequesttime(long requesttime) {
        this.requesttime = requesttime;
    }

    public long getResponsetime() {
        return responsetime;
    }

    public void setResponsetime(long responsetime) {
        this.responsetime = responsetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_sk() {
        return user_sk;
    }

    public void setUser_sk(String user_sk) {
        this.user_sk = user_sk;
    }
}
