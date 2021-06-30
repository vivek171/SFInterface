package com.htc.remedy.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Db {
    String connectionname;
    String host;
    int port;
    String driverclassname;
    String databasename;
    String username;
    String password;
}
