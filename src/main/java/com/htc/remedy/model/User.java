package com.htc.remedy.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class User implements UserDetails {

    String authUserName;
    String clientInstance;
    String client;
    String masterclient;
    String networkLogin;
    String userID;
    String authTokenType;
    String localusername;
    String requestID;

    public String getLocalusername() {
        return localusername;
    }

    public void setLocalusername(String localusername) {
        this.localusername = localusername;
    }

    public String getAuthTokenType() {
        return authTokenType;
    }

    public void setAuthTokenType(String authTokenType) {
        this.authTokenType = authTokenType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getNetworkLogin() {
        return networkLogin;
    }

    public void setNetworkLogin(String networkLogin) {
        this.networkLogin = networkLogin;
    }

    public String getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.getAuthUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getClientInstance() {
        return clientInstance;
    }

    public String getMasterclient() {
        return masterclient;
    }

    public void setMasterclient(String masterclient) {
        this.masterclient = masterclient;
    }

    public void setClientInstance(String clientInstance) {
        this.clientInstance = clientInstance;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }


    @Override
    public String toString() {
        return "User{" +
                " requestID='" + requestID + '\'' +
                ", authUserName='" + authUserName + '\'' +
                ", clientInstance='" + clientInstance + '\'' +
                ", client='" + client + '\'' +
                ", masterclient='" + masterclient + '\'' +
                ", networkLogin='" + networkLogin + '\'' +
                ", userID='" + userID + '\'' +
                ", authTokenType='" + authTokenType + '\'' +
                ", localusername='" + localusername + '\'' +
                '}';
    }
}