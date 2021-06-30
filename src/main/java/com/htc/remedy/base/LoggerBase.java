package com.htc.remedy.base;

import com.bmc.arsys.api.ARServerUser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.domain.LogDomain;
import com.htc.remedy.repo.LogRepo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@Component
public class LoggerBase implements BaseModel {

    private static
    LogRepo logger;

    private static
    ResourceLoader resourceLoader;


    @Autowired
    public LoggerBase(LogRepo logger, ResourceLoader resourceLoader) {
        LoggerBase.logger = logger;
        this.resourceLoader = resourceLoader;
    }

    private static void info(LogDomain logDomain) {
        logger.save(logDomain);
    }

    private static void error(LogDomain logDomain) {
        logger.save(logDomain);
    }

    private static void warning(LogDomain logDomain) {
        logger.save(logDomain);
    }

    public static void loguserrecords(
            ARServerUser user,
            String formname,
            String methodtype, HttpServletRequest request) {
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setMethodtype(methodtype);
        log.setUsername(ITSMBase.getloggedinuser(user));
        log.setSupportfunction(mapsupportfunction(formname));
        info(log);
    }

    public static void loguserrecords(
            String supportfunction,
            ARServerUser user,
            String formname,
            String methodtype, HttpServletRequest request) {
        String tempsupportfunction = mapsupportfunction(formname);
        if (tempsupportfunction != null && !tempsupportfunction.isEmpty()) {
            supportfunction = tempsupportfunction;
        }
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setSupportfunction(supportfunction);
        log.setMethodtype(methodtype);
        log.setUsername(ITSMBase.getloggedinuser(user));
        info(log);
    }

    public static void loguserrecords(
            String supportfunction,
            ARServerUser user,
            String ticketnumber,
            String formname,
            String methodtype, HttpServletRequest request) {
        String tempsupportfunction = mapsupportfunction(formname);
        if (tempsupportfunction != null && !tempsupportfunction.isEmpty()) {
            supportfunction = tempsupportfunction;
        }
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setTicketnumber(ticketnumber);
        log.setSupportfunction(supportfunction);
        log.setMethodtype(methodtype);
        log.setUsername(ITSMBase.getloggedinuser(user));
        info(log);
    }

    public static void loguserrecords(
            ARServerUser user,
            String ticketnumber,
            String formname,
            String methodtype, HttpServletRequest request) {
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setMethodtype(methodtype);
        log.setSupportfunction(mapsupportfunction(formname));
        log.setUsername(ITSMBase.getloggedinuser(user));
        log.setTicketnumber(ticketnumber);
        info(log);
    }

    public static void loguserrecords(
            ARServerUser user,
            String ticketnumber,
            String jsonfields,
            String formname,
            String methodtype, HttpServletRequest request) {
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setMethodtype(methodtype);
        log.setSupportfunction(mapsupportfunction(formname));
        log.setUsername(ITSMBase.getloggedinuser(user));
        log.setTicketnumber(ticketnumber);
        if (ITSMBase.convertregexseperatedstringtoList(Constants.getLogjson(), ",").contains(methodtype.toUpperCase())) {
            log.setUpdatedfields(jsonfields);
        }
        info(log);
    }

    public static void loguserrecords(
            String supportfunction,
            ARServerUser user,
            String ticketnumber,
            String jsonfields,
            String formname,
            String methodtype, HttpServletRequest request) {
        LogDomain log = getapplication(request);
        log.setFormname(formname);
        log.setMethodtype(methodtype);
        log.setSupportfunction(supportfunction);
        log.setUsername(ITSMBase.getloggedinuser(user));
        log.setTicketnumber(ticketnumber);
        if (ITSMBase.convertregexseperatedstringtoList(Constants.getLogjson(), ",").contains(methodtype.toUpperCase())) {
            log.setUpdatedfields(jsonfields);
        }
        info(log);
    }

    public static String getremoteaddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static LogDomain getapplication(HttpServletRequest request) {
        LogDomain logDomain = new LogDomain();
        if (request.getHeader("app") == null) {
            logDomain.setSourceip(getremoteaddress(request));
            if (ITSMBase.convertregexseperatedstringtoList(Constants.getSsphosts(), ",").contains(logDomain.getSourceip())) {
                logDomain.setApplicationname("SSP");
            } else if (ITSMBase.convertregexseperatedstringtoList(Constants.getSfhosts(), ",").contains(logDomain.getSourceip())) {
                logDomain.setApplicationname("ServiceFocus");
            }
        } else {
            logDomain.setSourceip(getremoteaddress(request));
            logDomain.setApplicationname(request.getHeader("app"));
        }
        return logDomain;
    }

    public static String mapsupportfunction(String formname) {
        try {
            Resource resource = resourceLoader.getResource("classpath:config/supportfunctionmapping.json");
            String jsonstring = IOUtils.toString(resource.getInputStream(), "UTF-8");
            JsonObject jsonObject = new Gson().fromJson(jsonstring, JsonObject.class);
            JsonElement supportfunction = (JsonElement) jsonObject.get(formname);
            return supportfunction != null ? supportfunction.getAsString() : "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}
