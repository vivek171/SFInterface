package com.htc.remedy.domain;

import com.htc.remedy.base.BaseModel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by kvivek on 10/3/2017.
 */
@Entity
@Table(name = "LUC_Indexer")
public class IndexerDomain implements BaseModel {

    @Id
    @Column(name = "Indexer_sk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long indexersk;
    @Column(name = "Indexername")
    String name;
    @Column(name = "Indexerquery")
    String query;
    @Column(name = "Indexerkeyfield")
    String indexerkey;
    @Column(name = "Indexerkeyfieldaliasname")
    String indexerkeyaliasname;
    @Column(name = "Indexerlastrunon")
    java.sql.Timestamp indexerlastrun;
    @Column(name = "Status")
    int statussk;

    @Transient
    String[] ticketID;

    @Transient
    String clientInstance;

    public String[] getTicketID() {
        return ticketID;
    }

    public void setTicketID(String[] ticketID) {
        this.ticketID = ticketID;
    }

    public Long getIndexersk() {
        return indexersk;
    }

    public void setIndexersk(Long indexersk) {
        this.indexersk = indexersk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getStatussk() {
        return statussk;
    }

    public void setStatussk(int statussk) {
        this.statussk = statussk;
    }

    public String getIndexerkey() {
        return indexerkey;
    }

    public void setIndexerkey(String indexerkey) {
        this.indexerkey = indexerkey;
    }

    public Timestamp getIndexerlastrun() {
        return indexerlastrun;
    }

    public void setIndexerlastrun(Timestamp indexerlastrun) {
        this.indexerlastrun = indexerlastrun;
    }

    public IndexerDomain() {
    }

    public IndexerDomain(String name, String ticketID, String clientInstance) {
        this.name = name;
        this.ticketID = new String[]{ticketID};
        this.clientInstance = clientInstance;
    }

    public IndexerDomain(String name, String[] ticketID, String clientInstance) {
        this.name = name;
        this.ticketID = ticketID;
        this.clientInstance = clientInstance;
    }

    public IndexerDomain(Long indexersk, String name, String query, String indexerkey, String indexerkeyaliasname, Timestamp indexerlastrun, int statussk) {
        this.indexersk = indexersk;
        this.name = name;
        this.query = query;
        this.indexerkey = indexerkey;
        this.indexerkeyaliasname = indexerkeyaliasname;
        this.indexerlastrun = indexerlastrun;
        this.statussk = statussk;
    }

    public String getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(String clientInstance) {
        this.clientInstance = clientInstance;
    }

    public String getIndexerkeyaliasname() {
        return indexerkeyaliasname;
    }

    public void setIndexerkeyaliasname(String indexerkeyaliasname) {
        this.indexerkeyaliasname = indexerkeyaliasname;
    }
}
