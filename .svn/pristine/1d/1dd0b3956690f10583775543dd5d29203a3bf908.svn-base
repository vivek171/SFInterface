package com.htc.remedy.base;

import com.htc.remedy.model.User;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Component
public class SFInterfaceLoggerBase implements BaseModel {

    public static void log(Logger logger, String methodType, String endpointName, String qualification, String version, String ticketNumber, String userID, String logindID, String data) {
        logger.info("Endpointname: " + printlogValue(endpointName)
                + "\tVersion:" + printlogValue(version)
                + "\tUser: " + printlogValue(logindID != null ? logindID : userID)
                + "\tTicketNumber:" + printlogValue(ticketNumber)
                + "\tQualification:" + printlogValue(qualification)
                + "\tMethodType:" + printlogValue(methodType)
                + "\tData:" + printlogValue(data));
    }

    public static void log(Logger logger, String methodType, String endpointName, String qualification, String version, String ticketNumber, String data, User user) {
        logger.info("Endpointname: " + printlogValue(endpointName)
                + "\tVersion:" + printlogValue(version)
                + "\tUser: " + printlogValue(user.toString())
                + "\tTicketNumber:" + printlogValue(ticketNumber)
                + "\tQualification:" + printlogValue(qualification)
                + "\tMethodType:" + printlogValue(methodType)
                + "\tData:" + printlogValue(data));
    }

    public static void log(Logger logger, String endpointName, String ticketNumber, String userID, String data, String methodType, String version) {
        log(logger, methodType, endpointName, null, version, ticketNumber, userID, null, data);
    }

    public static void log(Logger logger, String endpointName, String ticketNumber, String userID, String data, String methodType) {
        log(logger, endpointName, ticketNumber, userID, data, methodType, null);
    }

    public static void log(Logger logger, String endpointName, String ticketNumber, String userID, String data) {
        log(logger, endpointName, ticketNumber, userID, data, null);
    }

    public static void response(Logger logger, String endpointName, String ticketNumber, String userID, String data) {
        logger.info("Endpointname: " + printlogValue(endpointName)
                + "\tUser: " + printlogValue(userID != null ? userID : NA)
                + "\tTicketNumber:" + printlogValue(ticketNumber)
                + "\tData:" + printlogValue(RESPONSE + COLON + data));
    }

    public static void log(Logger logger, String endpointName, String userID, String data) {
        log(logger, endpointName, null, userID, data);
    }

    public static void log(Logger logger, String endpointName, String data) {
        log(logger, endpointName, endpointName, data);
    }

    public static void exception(Logger logger, String endpointName, Exception e) {
        logger.error("Endpointname: " + printlogValue(endpointName) +
                "\tException: " + e.getMessage(), e);
    }

    public static void log(Logger logger, String msg) {
        logger.info(printlogValue(msg));
    }

    public static String printlogValue(String input) {
        return ((input != null) && !input.isEmpty()) ? input : "NA";
    }


}
