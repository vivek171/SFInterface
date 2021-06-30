package com.htc.remedy.model;

import com.htc.remedy.base.BaseModel;

public class SFInterfaceIndexerSyncModel implements BaseModel {
    String action;
    String[] indexernames;
    String[] ticketids;
    String clientInstance;

    public String getAction() {
        return action;
    }

    public String getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(String clientInstance) {
        this.clientInstance = clientInstance;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getIndexernames() {
        return indexernames;
    }

    public void setIndexernames(String[] indexernames) {
        this.indexernames = indexernames;
    }

    public String[] getTicketids() {
        return ticketids;
    }

    public void setTicketids(String[] ticketids) {
        this.ticketids = ticketids;
    }


    public SFInterfaceIndexerSyncModel(String action, String[] indexernames, String[] ticketids, String clientInstance) {
        this.action = action;
        this.indexernames = indexernames;
        this.ticketids = ticketids;
        this.clientInstance = clientInstance;
    }
}
