package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAccountNGroup implements BaseModel {

    Boolean isAdministrator = Boolean.FALSE;
    Boolean isCSS = Boolean.FALSE;
    String primaryAccount;
    String primaryMasterAccount;
    String user_sk;
    String full_name;
    List<Map<String, Object>> userAccounts = new ArrayList<>();
    List<Map<String, Object>> userGroups = new ArrayList<>();
    List<Map<String, Object>> userPermission = new ArrayList<>();
    List<String> accountGroup_sk = new ArrayList<>();

    public String getPrimaryMasterAccount() {
        return primaryMasterAccount;
    }

    public void setPrimaryMasterAccount(String primaryMasterAccount) {
        this.primaryMasterAccount = primaryMasterAccount;
    }

    public Boolean getAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(Boolean administrator) {
        isAdministrator = administrator;
    }

    public Boolean getCSS() {
        return isCSS;
    }

    public void setCSS(Boolean CSS) {
        isCSS = CSS;
    }

    public List<Map<String, Object>> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<Map<String, Object>> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public List<Map<String, Object>> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<Map<String, Object>> userGroups) {
        this.userGroups = userGroups;
    }

    public List<Map<String, Object>> getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(List<Map<String, Object>> userPermission) {
        this.userPermission = userPermission;
    }

    public UserAccountNGroup() {
        this.isAdministrator = Boolean.FALSE;
        this.isCSS = Boolean.FALSE;
        this.setUserAccounts(new ArrayList<>());
        this.setUserGroups(new ArrayList<>());
        this.setUserPermission(new ArrayList<>());
    }

    public List<String> getAccountGroup_sk() {
        return accountGroup_sk;
    }

    public void setAccountGroup_sk(List<String> accountGroup_sk) {
        this.accountGroup_sk = accountGroup_sk;
    }

    public UserAccountNGroup(Boolean isAdministrator, Boolean isCSS, String primaryAccount, String primaryMasterAccount, List<Map<String, Object>> userAccounts, List<Map<String, Object>> userGroups, List<Map<String, Object>> userPermission) {
        this.isAdministrator = isAdministrator;
        this.isCSS = isCSS;
        this.primaryAccount = primaryAccount;
        this.primaryMasterAccount = primaryMasterAccount;
        this.userAccounts = userAccounts;
        this.userGroups = userGroups;
        this.userPermission = userPermission;
    }

    public String getUser_sk() {
        return user_sk;
    }

    public void setUser_sk(String user_sk) {
        this.user_sk = user_sk;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(String primaryAccount) {
        this.primaryAccount = primaryAccount;
    }
}
