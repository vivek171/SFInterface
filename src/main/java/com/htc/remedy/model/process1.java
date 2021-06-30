package com.htc.remedy.model;

/**
 * Created by kvivek on 5/12/2018.
 */

public class process1 {

    String processcode;
    String processname;
    String Accountid;
    String processid;

    public String getProcesscode() {
        return processcode;
    }

    public void setProcesscode(String processcode) {
        this.processcode = processcode;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname;
    }

    public String getAccountid() {
        return Accountid;
    }

    public void setAccountid(String accountid) {
        Accountid = accountid;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public process1(String processcode, String processname) {
        this.processcode = processcode;
        this.processname = processname;
    }

    public process1(String processcode, String processname, String accountid, String processid) {
        this.processcode = processcode;
        this.processname = processname;
        Accountid = accountid;

        this.processid = processid;
    }
}
