package com.htc.remedy.config;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by kvivek on 3/1/2018.
 */

@Configuration
public class RemedyConfiguration {
    @Autowired
    Environment environment;

    @Bean
    public ARServerUser getarserveruser() {
        ARServerUser arServerUser = new ARServerUser();
        arServerUser.setServer(environment.getProperty("remedy.host"));
        arServerUser.setUser(environment.getProperty("ctsspi.username"));
        arServerUser.setPassword(environment.getProperty("ctsspi.password"));
        arServerUser.setPort(Integer.parseInt(environment.getProperty("remedy.port")));
        try {
            arServerUser.login();
            arServerUser.getAuthentication();
        } catch (ARException e) {
            return new ARServerUser();
        }
        return arServerUser;
    }

}


