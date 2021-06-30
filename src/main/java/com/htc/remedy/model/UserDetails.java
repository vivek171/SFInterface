package com.htc.remedy.model;


import com.google.gson.annotations.SerializedName;

public class UserDetails {

    Boolean authenticated;

    @SerializedName("Login ID")
    String loginid;

    String loginName;

    @SerializedName("Full Name")
    String FullName;
    String Full_Name;

    @SerializedName("First Name")
    String FirstName;
    String First_Name;

    @SerializedName("Last Name")
    String LastName;
    String Last_Name;
    String BusinessEmailAddress;

    String Client;
    String acc_code;
    String token;
    String refreshtoken;
    String navigatorapps;
    String issso;

    @SerializedName("Network Login")
    String NetworkLogin;

    String Network_Login;
    String People_Info_SK;

    String ITSM_Login;

    public String getITSM_Login() {
        return ITSM_Login;
    }

    public void setITSM_Login(String ITSM_Login) {
        this.ITSM_Login = ITSM_Login;
    }

    public String getAcc_code() {
        return acc_code;
    }

    public void setAcc_code(String acc_code) {
        this.acc_code = acc_code;
    }

    public String getPeople_Info_SK() {
        return People_Info_SK;
    }

    public void setPeople_Info_SK(String people_Info_SK) {
        People_Info_SK = people_Info_SK;
    }

    public String getNetwork_Login() {
        return Network_Login;
    }

    public void setNetwork_Login(String network_Login) {
        Network_Login = network_Login;
    }

    public String getNavigatorapps() {
        return navigatorapps;
    }

    public void setNavigatorapps(String navigatorapps) {
        this.navigatorapps = navigatorapps;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getClient() {
        return Client;
    }

    public String getIssso() {
        return issso;
    }

    public void setIssso(String issso) {
        this.issso = issso;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }

    public void setRefreshtoken(String refreshtoken) {
        this.refreshtoken = refreshtoken;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public void setLast_Name(String last_Name) {
        Last_Name = last_Name;
    }

    public String getBusinessEmailAddress() {
        return BusinessEmailAddress;
    }

    public void setBusinessEmailAddress(String businessEmailAddress) {
        BusinessEmailAddress = businessEmailAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;

    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;

    }

    public String getNetworkLogin() {
        return NetworkLogin;
    }

    public void setNetworkLogin(String networkLogin) {
        NetworkLogin = networkLogin;
    }





}
