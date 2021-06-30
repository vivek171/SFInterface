package com.htc.remedy.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CTSSPI_jdbcendpoints")
public class JDBCEndpoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String endpointName;

    @ManyToOne
    @JoinColumn(name = "jdbc_account_id")
    private JDBCAccounts jdbcAccounts;

    @Column
    String query;

    @Column
    String inup;

    @Column
    private boolean active = true;


    String whooshIndexerName;
    String type;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime date = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public JDBCAccounts getJdbcAccounts() {
        return jdbcAccounts;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setJdbcAccounts(JDBCAccounts jdbcAccounts) {
        this.jdbcAccounts = jdbcAccounts;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getInup() {
        return inup;
    }

    public void setInup(String inup) {
        this.inup = inup;
    }

    public JDBCEndpoints(String endpointName, String query) {
        this.endpointName = endpointName;
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWhooshIndexerName() {
        return whooshIndexerName;
    }

    public void setWhooshIndexerName(String whooshIndexerName) {
        this.whooshIndexerName = whooshIndexerName;
    }

    public JDBCEndpoints(String endpointName, String query, String whooshIndexerName, String type){
        this.endpointName = endpointName;
        this.query = query;
        this.whooshIndexerName = whooshIndexerName;
        this.type = type;
    }

    public JDBCEndpoints(String endpointName, String query, String inup) {
        this.endpointName = endpointName;
        this.query = query;
        this.inup = inup;
    }

    public JDBCEndpoints() {
    }
}
