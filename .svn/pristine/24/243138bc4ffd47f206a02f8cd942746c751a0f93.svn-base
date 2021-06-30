package com.htc.remedy;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJms
@SpringBootApplication
public class RemedyWebServicesApplication  extends SpringBootServletInitializer{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RemedyWebServicesApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(RemedyWebServicesApplication.class, args);
    }
}
