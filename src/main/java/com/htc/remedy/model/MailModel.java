package com.htc.remedy.model;

public class MailModel {
    MailObject mailObject;
    String ticketID;
    String ticketType;
    String client;

    public MailObject getMailObject() {
        return mailObject;
    }

    public void setMailObject(MailObject mailObject) {
        this.mailObject = mailObject;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "MailModel{" +
                "mailObject=" + mailObject +
                ", ticketID='" + ticketID + '\'' +
                ", ticketType='" + ticketType + '\'' +
                ", client='" + client + '\'' +
                '}';
    }
}
