package com.htc.remedy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.htc.remedy.base.*;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.model.*;
import com.htc.remedy.services.SFInterfaceServices;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.htc.remedy.base.SFInterfaceBase.*;
import static com.htc.remedy.constants.SFInterfaceConstants.*;
import static com.htc.remedy.services.SFInterfaceServices.*;

/**
 * SFInterface API's Controller - Reference DB
 *
 * @Author : Gayathri Ashok & Vivek
 * ModifiedBy : Vani & Vinodhini
 */

@RestController
@RequestMapping(path = "")
@Order(1)
public class SFInterfaceController {

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    SFInterfaceMessages sfInterfaceMessages;

    @Autowired
    Gson gson;

    @Value("${cr.filepath}")
    String crfilepath;

    @Value("${mail.username}")
    String mailusername;


    private static final Logger LOGGER = LogManager.getLogger(SFInterfaceController.class);

    /**
     * Author - Gayathri Ashok
     * ModifiedBy - Vani
     * The Function supports GET method, Read lucene segment file and returns an json response
     *
     * @return - Returns Json response
     * @param_mandatory - LoginID/Account/MasterAccount/Accountsk/MasterAccountsk/User_sk any one the param is mandatory
     * @param_optional - Other params are dynamic and optional based on the endpoints
     * @header- Requires an valid accesstoken or aToken
     */
    @GetMapping(path = GET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processEndPointGet(Model model,
                                                     @PathVariable(ENDPOINT_NAME) String endpointName,
                                                     @PathVariable(VERSION) String version,
                                                     @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                     @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                     @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                     HttpServletRequest request) throws IOException {
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.processRequest(GET_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), null);

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Gayathri Ashok
     * ModifiedBy - Vani
     * The Function supports POST method, Read lucene segment file and returns an json response
     *
     * @return - Returns Json response
     * @param_mandatory - LoginID/Account/MasterAccount/Accountsk/MasterAccountsk/User_sk any one the param is mandatory
     * @param_optional - Other params are dynamic and optional based on the endpoints
     * @header- Requires an valid accesstoken or aToken
     */
    @PostMapping(path = POST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity processEndPointPost(Model model,
                                              @PathVariable(ENDPOINT_NAME) String endpointName,
                                              @PathVariable(VERSION) String version,
                                              @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                              @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                              @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                              @RequestBody(required = FALSE) Map<String, Object> ticketFields,
                                              HttpServletRequest request) throws IOException {
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.processRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), ticketFields);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function supports GET method, Read lucene segment file and returns an json response for Admin Screen APIs
     *
     * @return - Returns Json response
     * @param_mandatory - LoginID/Account/MasterAccount/Accountsk/MasterAccountsk/User_sk any one the param is mandatory
     * @param_optional - Other params are dynamic and optional based on the endpoints
     * @header- Requires an valid accesstoken or aToken
     */
    @GetMapping(path = ADMIN_GET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processAdminEndPointGet(Model model,
                                                          @PathVariable(ENDPOINT_NAME) String endpointName,
                                                          @PathVariable(VERSION) String version,
                                                          @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                          @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                          @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                          HttpServletRequest request) throws IOException {
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endpointName = ADMIN_PATH + endpointName;
            return SFInterfaceServices.processRequest(GET_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), null);

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function supports GET method, Read lucene segment file and returns an json response for Admin Screen APIs
     *
     * @return - Returns Json response
     * @param_mandatory - LoginID/Account/MasterAccount/Accountsk/MasterAccountsk/User_sk any one the param is mandatory
     * @param_optional - Other params are dynamic and optional based on the endpoints
     * @header- Requires an valid accesstoken or aToken
     */
    @GetMapping(path = "/{version}/cmdb/{endpointname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> processCMDBEndPointGet(Model model,
                                                         @PathVariable(ENDPOINT_NAME) String endpointName,
                                                         @PathVariable(VERSION) String version,
                                                         @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                         @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                         @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                         HttpServletRequest request) throws IOException {
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endpointName = ADMIN_PATH + endpointName;
            return SFInterfaceServices.processRequest(GET_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), null);

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Gayathri Ashok
     * ModifiedBy - Vani
     * The Function supports POST method, Read lucene segment file and returns an json response
     *
     * @return - Returns Json response
     * @param_mandatory - LoginID/Account/MasterAccount/Accountsk/MasterAccountsk/User_sk any one the param is mandatory
     * @param_optional - Other params are dynamic and optional based on the endpoints
     * @header- Requires an valid accesstoken or aToken
     */
    @PostMapping(path = ADMIN_GET_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity processAdminEndPointPost(Model model,
                                                   @PathVariable(ENDPOINT_NAME) String endpointName,
                                                   @PathVariable(VERSION) String version,
                                                   @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                   @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                   @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                   @RequestBody(required = FALSE) Map<String, Object> ticketFields,
                                                   HttpServletRequest request) throws IOException {
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endpointName = ADMIN_PATH + endpointName;
            return SFInterfaceServices.processRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), null);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author : Vani
     * The Function is used for creating an incident ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = GENERATE_TICKET_NUMBER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> generateTicketNumber(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>(), ticketNumberMap = new LinkedHashMap<>();
        String loginID = "";
        String endpointName = GENERATE_TICKET_NUMBER;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(trimToken(aToken), trimToken(refreshToken), trimToken(accessToken))) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
            }
            if (StringUtils.isNotBlank(loginID)) {

                if (ticketFields.containsKey(ACCOUNT) && ticketFields.containsKey(MASTER_ACCOUNT) && ticketFields.containsKey(MASTER_ACCOUNT_CODE)
                        && ticketFields.containsKey(BUSINESS_FUNCTION)) {
                    ticketNumberMap = SFInterfaceBase.generateTicketNumber(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), Constants.TicketNumberGeneration, version, ticketFields, request, user);
                    if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                    } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                        resultMap.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                    }
                } else {
                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORGENERATETICKETNUMBER", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * Author : Vinodhini
     * ModifiedBy - Vani
     * The Function is used for creating an incident ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_INCIDENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createIncident(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {

        String endpointName = CREATE_INCIDENT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicket(INCIDENTS, INDEXERNAME_FETCH_INCIDENT, POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), jmsTemplate);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for creating a sr ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_SR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createServiceRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_SERVICE_REQUEST;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicket(SR, INDEXERNAME_FETCH_SERVICE_REQUEST, POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), jmsTemplate);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for creating an srt ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_SRT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createServiceRequestTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_SRTASK;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicket(SRT, INDEXERNAME_FETCH_SERVICE_REQUEST_TASK, POST_METHOD, trimToken(aToken)
                    , trimToken(refreshToken), trimToken(accessToken), ticketFields, CREATE_SRTASK, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), jmsTemplate);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_SRTASK), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting Incident details (Alt_Contact,SecureInfo,attacments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_INCIDENT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createIncidentDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_INCIDENT_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicketDetails(INCIDENTS, POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, CREATE_INCIDENT_DETAILS, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting SR details (Alt_Contact,SecureInfo,attacments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_SR_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createServiceRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_SR_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicketDetails(SR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting SRTask details (Alt_Contact,SecureInfo,attacments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_SRT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createSRTaskDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_SRTASK_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createTicketDetails(SRT, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating an incident ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accessToken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_INCIDENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateIncident(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_INCIDENT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicket(INCIDENTS, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), INDEXERNAME_FETCH_INCIDENT, request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating an sr ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateServiceRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SERVICE_REQUEST;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicket(SR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), INDEXERNAME_FETCH_SERVICE_REQUEST, request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating an srt ticket with worknotes alone
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SRT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSRTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SRTASK;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicket(SRT, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), INDEXERNAME_FETCH_SERVICE_REQUEST_TASK, request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating Incident details (Alt_Contact,SecureInfo,attacments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_INCIDENT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateIncidentDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_INCIDENT_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketDetails(INCIDENTS, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating SR details (Alt_Contact,SecureInfo,attachments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SR_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateServiceRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SR_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketDetails(SR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating SRTask details (Alt_Contact,SecureInfo,attachments)
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SRT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSRTaskDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SRTASK_DETAILS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketDetails(SRT, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the vacation details
     *
     * @return - Returns VacationSk as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_VACATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createVacation(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_VACATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the vacation details
     *
     * @return - Returns Success or Failure message as JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_VACATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateVacation(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_VACATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for creating the document details
     *
     * @return - Returns Success or Failure message as JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DOCUMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createDocument(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_DOCUMENT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithAttachments(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the document details
     *
     * @return - Returns Success or Failure message as JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DOCUMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateDocument(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DOCUMENT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithAttachments(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for fetching the oncall details
     *
     * @return - Returns Success or Failure message as JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/" + FETCH_SUPPORT_DETAILS, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity FETCH_ONCALL_DETAILS(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {


        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        List<Map<String, Object>> results = new ArrayList<>();
        String endpointName = FETCH_SUPPORT_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (StringUtils.isNotBlank(user.getAuthUserName())) {

                Map<String, Object> endPointDomain1 = fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                if (endPointDomain1.size() > NUM_ZERO && !(endPointDomain1.containsKey(ERRORCODE))) {
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    String refdbquery = getDBQuery(endPointDomain1.get(QUERY).toString(), customWhooshModel.getParams());
                    SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version, null, null, user.getAuthUserName(), null);
                    results = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, null);

                    if (params.containsKey(SFInterfaceConstants.ONCALLSK) && results.size() > NUM_ZERO && !results.contains(ERRORCODE)) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(FETCH_SUPPORT_ATTACHMENT, version, GET_METHOD, user);
                        Map<String, String[]> requestparametermap = new HashMap<>();
                        requestparametermap.put(SFInterfaceConstants.ONCALLSK, new String[]{request.getParameter(ONCALLSK)});

                        //  List<Map<String, Object>> tempResults = (List<Map<String, Object>>) results.getBody();
                        results.get(0).put(ATTACHMENTS, SFInterfaceBase.runjdbcQueryWithSelectedColumns
                                (SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), getDBQuery(endPointDomain.get(QUERY).toString(), requestparametermap), null));
                        return new ResponseEntity<>(results, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(results, HttpStatus.OK);
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the oncalldetails / Primary support
     *
     * @return - Returns Oncallsk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_SUPPORT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createOnCall(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_SUPPORT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithAttachments(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the oncall / Primary Support details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SUPPORT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateOnCall(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SUPPORT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithAttachments(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the Profile details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_PROFILE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_PROFILE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_PROFILE, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the Problem Manager
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_INCIDENT_MANAGER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createProblemManager(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_INCIDENT_MANAGER;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for updating the Problem Manager
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_INCIDENT_MANAGER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateProblemManager(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_INCIDENT_MANAGER;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for Creating the Profile details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_PROFILE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createProfile(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_PROFILE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createProfile(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the Watchlist details
     *
     * @return - Returns Watchlist_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_WATCHLIST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateWatchlist(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_WATCHLIST;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            ResponseEntity<Map<String, Object>> watchListResponse = SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_WATCHLIST, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
            Map<String, Object> watchList = watchListResponse.getBody();
            result.put(WATCHLIST, watchList);
            if (watchList.containsKey(WATCHLIST_SK) && (watchList.get(STATUS).equals("1"))) {
                params.put(WATCHLIST_SK, new String[]{watchList.get(WATCHLIST_SK).toString()});
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(WATCHLIST_SP, version, GET_METHOD, user);
                if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                    result2 = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                    if (result2.size() > NUM_ZERO) {
                        ResponseEntity<Map<String, Object>> watchList2 = SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                                , result2.get(NUM_ZERO), CREATE_WATCHLIST_REPORT, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
                    }
                }
            }
            return watchListResponse;
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the UserFavourite details
     *
     * @return - Returns UserFavourite_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_USER_FAVORITIES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateCRUserFavorities(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_CR_USER_FAVORITIES;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (!ticketFields.containsKey(USER_SK)) {
                ticketFields.put(USER_SK, user.getUserID());
            }
            if (!ticketFields.containsKey(MODIFIED_BY)) {
                ticketFields.put(MODIFIED_BY, user.getUserID());
            }
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_CR_USER_FAVORITIES, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the ActionItem details
     *
     * @return - Returns ActionItem_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACTION_ITEMS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateActionItems(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_ACTION_ITEMS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateCRMultipleInputRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_ACTION_ITEMS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Impacted details
     *
     * @return - Returns Impact_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_IMPACTED_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateImpact(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_IMPACTED;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateCRMultipleInputRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_IMPACTED, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Risks details
     *
     * @return - Returns Risk_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_RISKS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateCRRisks(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_CR_RISKS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateCRMultipleInputRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_CR_RISKS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the CR Approvals details
     *
     * @return - Returns Approval_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CR_APPROVALS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createCRApprovals(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = CREATE_CR_APPROVALS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRApprovalsRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, CREATE_CR_APPROVALS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the CR Approvals details
     *
     * @return - Returns Approval_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_APPROVALS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateCRApprovals(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> result2 = null;
        String endpointName = UPDATE_CR_APPROVALS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRApprovalsRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_CR_APPROVALS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the profile prefrence details
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_PROFILE_PREFRENCE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfilePreference(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_PROFILE_PREFRENCE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the profile prefrence details
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_PREFRENCE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateUserPreference(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_USER_PREFRENCE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the user associated group details
     *
     * @return - Returns sk and success message as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACCOUNTGROUPUSER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateAccountGroupUsers(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACCOUNTGROUPUSER;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for updating the UserNotes
     *
     * @return - Returns UserNotes sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USERNOTES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createUserNotes(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_USERNOTES;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the secure info viewed history in audit table
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SECURE_INFO_VIEW_HISTORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSecureInfoHistory(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SECURE_INFO_HISTORY;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the secure info downloaded attchment's history in audit table
     *
     * @return - Returns TicketNumber as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SECUREINFO_DOWNLADED_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSecureInfoDownloadedAttachments(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SECUREINFO_DOWNLADED_HISTORY;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the INC email details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_INC_EMAIL_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createINCEmail(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_INC_EMAIL;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateEmailRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the SR email details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SR_EMAIL_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createSREmail(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SR_EMAIL;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateEmailRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the SR email details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SRTASK_EMAIL_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createSRTEmail(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SRTASK_EMAIL;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateEmailRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for inserting the inserting FEM details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_FRONTENDMESSAGES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createFrontEndMessages(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_FRONTENDMESSAGES;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateFrontEndMessages(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for inserting the SR email details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_FRONTENDMESSAGES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> UpdateFrontEndMessages(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_FRONTENDMESSAGES;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateFrontEndMessages(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for inserting the FrontEndMessage details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_NOTIFICATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> CreateNotification(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_NOTIFICATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createNotification(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author : Vani
     * The Function is used for creating an incident ticket for reporting
     *
     * @return - Returns Incident_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_INC_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> reportingCreateIncident(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {
        String endpointName = CREATE_DMART_INC;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.reportingCreateTicket(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), jmsTemplate, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author : Vani
     * The Function is used for creating an SR ticket for reporting
     *
     * @return - Returns SR_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_SR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> reportingCreateServiceRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {
        String endpointName = CREATE_DMART_SR;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.reportingCreateTicket(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), jmsTemplate, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author : Vani
     * The Function is used for creating an SRT ticket for reporting
     *
     * @return - Returns SRT_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_SRTASK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> reportingCreateServiceRequestTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {
        String endpointName = CREATE_DMART_SRTASK;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.reportingCreateTicket(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), jmsTemplate, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author : Vani
     * The Function is used for creating an SRT ticket for reporting
     *
     * @return - Returns SRT_sk as Json response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_CONSOLIDATED_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> reportingInsertConsolidatedList(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {
        String endpointName = CREATE_DMART_CONSOLIDATED;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.reportingCreateTicket(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), jmsTemplate, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the INC Report details
     *
     * @return - Returns  as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_INC_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateINCReport(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DMART_INC;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketReportsDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the SR Report details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_SR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSRReport(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DMART_SR;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketReportsDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the SRT Report details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_SRTASK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSRTReport(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DMART_SRTASK;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketReportsDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the SR Report details
     *
     * @return - Returns email_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_CONSOLIDATED_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateConsolidatedList(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DMART_CONSOLIDATED;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateTicketReportsDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for insert/update Secure Answer details
     *
     * @return - Returns Question_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SECURE_ANSWERS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateSecureAnswers(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SECURE_ANSWERS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for insert com_response details
     *
     * @return - Returns Response_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_COM_RESPONSES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createResponses(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_COM_RESPONSES;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for update com_response details
     *
     * @return - Returns Response_sk and Status as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_COM_RESPONSES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateResponses(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_COM_RESPONSES;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - VIvek
     * The Function is used for update com_response details
     *
     * @return - Returns Response_sk and Status as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_APPLICATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateApplication(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_APPLICATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - VIvek
     * The Function is used for update com_response details
     *
     * @return - Returns Response_sk and Status as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACC_APPLICATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateAccApplication(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACC_APPLICATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - VIvek
     * The Function is used for update com_response details
     *
     * @return - Returns Response_sk and Status as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACC_GROUP_APP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateAccGroupApp(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACC_GROUP_APP;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for insert KBArticle Log details
     *
     * @return - Returns ArticleLog_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_KBARTICLET_LOG_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createKBArticleLog(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_KBARTICLET_LOG;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            Map<String, Object> articleBody = new HashMap<>();
            String articleLog = "";
            String finalQuery = "";
            List<Map<String, Object>> retrunWithPagin;
            Map<String, Object> resultMap = new LinkedHashMap<>();
            Map<String, String[]> resultMapAR = new LinkedHashMap<>();


            ResponseEntity<Map<String, Object>> articleresponse = SFInterfaceServices.createOrUpdateKBAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
            articleBody = articleresponse.getBody();
            //To insert int AR_SystemReporting
            articleLog = articleBody.get(ARTICLELOG_SK).toString();
            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(KBARTICLE_LOG, version, GET_METHOD, user);
            if (!endPointDomain.containsKey(ERRORCODE)) {
                String spName1 = endPointDomain.get(QUERY).toString();

                resultMapAR.put(ARTICLELOG_SK, new String[]{articleLog});
                finalQuery = SFInterfaceBase.getDBQuery(spName1, resultMapAR);

                retrunWithPagin = SFInterfaceBase.runjdbcQueryWithPagination(
                        SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), finalQuery
                        , null, null, null, null, null, 0, 0, null);
                Map<String, Object> endPointDomain2 = SFInterfaceBase.fetchEndpointDetails(CREATE_DMART_KBARTICLE_LOG, version, POST_METHOD, user);
                if (!endPointDomain.containsKey(ERRORCODE)) {
                    String spName2 = endPointDomain2.get(QUERY).toString();
                    SFInterfaceLoggerBase.log(LOGGER, CREATE_DMART_KBARTICLE_LOG, " Request:" + retrunWithPagin.get(0));
                    resultMap = SFInterfaceBase.createKBArticleLogDetailsARSystem(
                            SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName())
                            , spName2, retrunWithPagin.get(0), endpointName);
                    SFInterfaceLoggerBase.log(LOGGER, CREATE_DMART_KBARTICLE_LOG, " Response:" + resultMap);
                }
            }
            return articleresponse;
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for update KBArticle Log details
     *
     * @return - Returns TicketMapping_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KBARTICLET_LOG_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateKBArticleLog(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_KBARTICLET_LOG;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateKBAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for insert/update KB Ticket Mapping details
     *
     * @return - Returns ArticleLog_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_TICKET_MAPPING_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateKBTicketmapping(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_KB_TICKET_MAPPING;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateKBAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * CreatedBy - Vinodhini
     * ModifiedBy - Vani
     * The Function is used for insert/update HangUp details
     *
     * @return - Returns HangUp_sk as Json response
     * @header- Requires an valid accesstoken or atoken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_HANGUPS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateHangup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = false) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = false) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> result = new HashMap<>();
        String endpointName = UPDATE_HANGUPS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            ResponseEntity<Map<String, Object>> hangUps = SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
            Map<String, Object> hangupsResponse = hangUps.getBody();

            if (hangupsResponse != NULL && hangupsResponse.containsKey(STATUS) && hangupsResponse.get(STATUS).equals(ONE)) {
                hangupsResponse = convertTimestamptoStringFromMap(hangupsResponse);
                ticketFields.put(CREATE_DATE, hangupsResponse.get(CREATED_DATE));
                ResponseEntity<Map<String, Object>> hangUpsReporting = SFInterfaceServices.createOrUpdateAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                        , trimToken(accessToken), ticketFields, CREATE_HANGUPS_REPORTING, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
                return new ResponseEntity<>(hangupsResponse, HttpStatus.OK);
            }

            return new ResponseEntity<>(hangupsResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting / updating multiple secure answers
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SECURE_ANSWER1_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateSecureAnswer1(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SECURE_ANSWERS1;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Building
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BUILDING_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateBuilding(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_BUILDING;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Department
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DEPARTMENT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateDepartment(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DEPARTMENT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Floor
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_FLOOR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateFloor(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_FLOOR;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Suite
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SUITE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateSuite(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_SUITE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Account
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACCOUNT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateAccount(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACCOUNT;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Group
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_GROUP;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating AccountGroup details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACCOUNT_GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateAccountGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACCOUNT_GROUP;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Category details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CATEGORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateCategory(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CATEGORY;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Type details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TYPE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_TYPE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Item details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ITEM_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateItem(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ITEM;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating CTI details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CTI_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updatecti(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CTI;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Pending Reasons details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_PENDING_REASONS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updatePendingReasons(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_PENDING_REASON;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Designation  details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DESIGNATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateDesignation(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_DESIGNATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateLocationDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Author - Vinodhini
     * The Function is used for inserting ChangeRequest
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CHANGE_REQUEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createChangeRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_CHANGE_REQUEST;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createChangeRequest(CR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating ChangeRequest
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CHANGE_REQUEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateChangeRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CHANGE_REQUEST;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateChangeRequest(CR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting CRTask
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CHANGE_REQUEST_TASKS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createChangeRequestTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_CHANGE_REQUEST_TASKS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createChangeRequestTask(CRT, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for updating CRTask
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CHANGE_REQUEST_TASKS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateChangeRequestTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CHANGE_REQUEST_TASKS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateChangeRequestTask(CRT, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, UPDATE_CHANGE_REQUEST_TASKS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting CR Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    /*@PostMapping(path = CREATE_CHANGE_REQUEST_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createChangeRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = CREATE_CHANGE_REQUEST_DETAILS;
        try {
  User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createChangeRequestDetails(CR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user,SFInterfaceConstants.getDbRefConnectionName())), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
*/
    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CR Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    /*@PostMapping(path = UPDATE_CHANGE_REQUEST_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateChangeRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CHANGE_REQUEST_DETAILS;
        try {
         User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateChangeRequestDetails(CR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user,SFInterfaceConstants.getDbRefConnectionName())), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the ChangeType details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CHANGE_TYPE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateChangeType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CHANGE_TYPE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Platforms details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_PLATFORMS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updatePlatforms(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_PLATFORMS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the CRCategory details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_CATEGORY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCRCategory(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CR_CATEGORY;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the CRSystems details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_SYSTEMS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateCRSystems(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_CR_SYSTEMS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the LOV details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_LOV_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLOV(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_LOV;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the RiskLevel details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RISKLEVEL_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRiskLevel(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_RISKLEVEL;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Questions details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_QUESTIONS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateQuestions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_QUESTIONS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the TaskType details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TASKTYPE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateTaskType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_TASKTYPE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the BusinessOrganization details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BUSINESS_ORGANIZATION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateBusinessOrganization(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_BUSINESS_ORGANIZATION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Priority details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_PRIORITY_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updatePriority(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_PRIORITY;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Role details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACCOUNT_ROLE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAccountRole(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ACCOUNT_ROLE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the ApprovalType details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_APPROVAL_TYPE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateApprovalType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_APPROVAL_TYPE;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vani
     * The Function is used for updating the Vendor details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_VENDOR_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateVendor(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_VENDOR;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - vivek
     * The Function is used for updating the Vendor details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = "/{version}/UpdateAccGroupUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity UpdateAccGroupUser(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UpdateAccGroupUser;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the LOVName details
     *
     * @return - Returns Success or Failure Message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_LOV_NAME_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateLOVNAme(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_LOV_NAME;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateMasterDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * author - Vivek
     * The Function is used for Procurement Details
     *
     * @return - Returns an HTML content as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Client as input
     */
    @PostMapping(value = PROCUREMENT_PATH, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity procurement(@PathVariable(VERSION) String version,
                                      @RequestParam(ACCOUNT) String currentClient,
                                      @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                      @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                      @RequestHeader(value = A_TOKEN, required = FALSE) String aToken, HttpServletRequest request) throws IOException {


        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "", output = "";
        String endpointName = PROCUREMENT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (isRequestContainToken(trimToken(aToken), trimToken(refreshToken), trimToken(accessToken))) {

                loginID = user.getAuthUserName();
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }
            if (loginID != null) {
                List<Map<String, Object>> procurementitems = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROCUREMENT, ACCOUNT_WITH_COLON + currentClient + ESCAPE_CHARACTER, null, null, null, null, null, null, NUM_ZERO, Boolean.FALSE, user);
                List<Map<String, Object>> procurementitemrelations = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROCUREMENT_DETAILS, PROC_QUERY, null, null, null, null, null, null, NUM_ZERO, Boolean.FALSE, user);

                String accessoriesList = "";
                String FileAtt = "";
                int procurementcount = NUM_ZERO;

                for (Map<String, Object> procurementitem : procurementitems) {
                    if (procurementcount == NUM_ZERO) {
                        output = PROC_OUTPUT;
                    }
                    accessoriesList = "";
                    String[] accessoryType;
                    accessoryType = new String[NUM_TWO];
                    accessoryType[NUM_ZERO] = PROCUREMENT_REQ_ACCESSORIES;
                    accessoryType[NUM_ONE] = PROCUREMENT_OPT_ACCESSORIES;
                    int[] status;
                    status = new int[NUM_TWO];
                    status[NUM_ZERO] = NUM_ONE; // Required
                    status[NUM_ONE] = NUM_ZERO; // Optional

                    String unique_identifier = procurementitem.get(PROCUREMENT_SK).toString();

                    for (int i = NUM_ZERO; i < accessoryType.length; i++) {

                        String currentTypeList = "";

                        int count = NUM_ZERO;

                        List<Map<String, Object>> procurementitemrelation = new ArrayList<>();
                        for (Map<String, Object> entry : procurementitemrelations) {
                            if (entry.get(PROCUREMENT_SK).toString().equalsIgnoreCase(unique_identifier) && Integer.parseInt(entry.get(IS_REQUIRED).toString()) == status[i]) {
                                procurementitemrelation.add(entry);
                            }
                        }

                        for (Map<String, Object> entry : procurementitemrelation) {
                            String currentAccessory = "";
                            // If this is the first time through the resultset then start the JSON string properly
                            if (count == NUM_ZERO) {
                                currentAccessory = COMMA_WITH_DOUBLE_QUOTES + accessoryType[i] + OPEN_SQUARE_BRACKET_WITH_NEWLINE;
                            }
                            count++;

                            String accessoryUID = (String) entry.get(PROCUREMENT_SK);//child_unique_id  requires clarification

                            List<Map<String, Object>> procuremenitems = new ArrayList<>();
                            procurementitems.forEach(entry1 -> {
                                if (entry1.get(PROCUREMENT_SK).toString().equalsIgnoreCase(accessoryUID)) {
                                    procuremenitems.add(entry1);
                                }
                            });

                            Map<String, Object> procuremenitem = procuremenitems.get(NUM_ZERO);

                            String entryid = (String) entry.get(PROCUREMENT_DETAILS_SK);

                            String accessoryCategory = (String) entry.get(CATEGORY);
                            String accessoryEquipment = (String) entry.get(CHILDEQUIPMENTNAME);

                            // Accessory question value pair
                            String accessoryWorkflow = "";

                            // Set accessory description
                            String accessoryDescription = "";
                            if ((String) entry.get(DESCRIPTION) != null && !entry.get(DESCRIPTION).toString().equalsIgnoreCase(ZERO)) {//description
                                accessoryDescription = (String) entry.get(DESCRIPTION) + BREAKTAG;
                            }

                            // Set accessory support user text
                            String accessorySupportUserText = "";

                            String accessoryImageFile;
                            String accessoryPrice = "";

                            if (FileAtt.isEmpty()) {
                                accessoryImageFile = "";
                            } else {
                                accessoryImageFile = ACCESSORYIMGFILE + FileAtt + CLOSETAG;
                            }

                            if ((String) entry.get(PRICE) != null) {//price
                                accessoryPrice = (String) entry.get(PRICE);
                            }
                            accessoryPrice = accessoryPrice.equalsIgnoreCase(ZERO) ? "" : accessoryPrice;

                            currentAccessory += "\n{\"item\": \"" + accessoryImageFile + "<div class='itemDescriptionText'><strong>"
                                    + accessoryEquipment + "</strong><br />" + RemedyBase.escapeQuotes(accessoryDescription) + accessoryPrice
                                    + "</div>\",\"equipmentUID\":\"" + entryid + "\",\"equipment\":\"" + accessoryEquipment + "\",\"price\":\""
                                    + accessoryPrice + "\",\"category\":\"" + accessoryCategory + "\",\"supporttext\":\"" + accessorySupportUserText
                                    + "\",\"workflow\":\"" + accessoryWorkflow + "\"\n}";

                            if (procurementitemrelation.size() == count) {
                                currentAccessory += CLOSE_SQUARE_BRACKET_WITH_NEWLINE;
                            } else {
                                currentAccessory += COMMA;
                            }

                            // Add this current accessory on to the current type list
                            currentTypeList += currentAccessory;
                        }

                        // Add the current type list onto the overall accessory list for the main item we are working on
                        accessoriesList += currentTypeList;

                        // Close the resultset for the accessories
                    }

                    String entryid = (String) procurementitem.get(PROCUREMENT_SK);

                    // Prepare strings to build JSON
                    String category = (String) procurementitem.get(CATEGORY);//category
                    String equipment = (String) procurementitem.get(EQUIPMENT);//equipment

                    // Question value pair
                    String workflow = "";

                    // Set item description
                    String description = "";
                    if (procurementitem.get(DESCRIPTION) != null && !procurementitem.get(DESCRIPTION).toString().equalsIgnoreCase(ZERO)) {
                        description = procurementitem.get(DESCRIPTION).toString() + BREAKTAG;
                    }
                    //String description = "";

                    // Set item support user text
                    String supportUserText = "";

                    String itemImageFile = "";

                    // Set price if one exists
                    String price = "";
                    if ((String) procurementitem.get(PRICE) != null) {
                        price = (String) procurementitem.get(PRICE);
                    }
                    price = price.equalsIgnoreCase(ZERO) ? "" : price;
                    description = description.equalsIgnoreCase(ZERO) ? "" : description;

                    // Check the Set Price from Details Checkbox
                    String priceCheckbox = "";

                    output += "\n{\"item\": \"" + itemImageFile + "<div class='itemDescriptionText'><strong>" + equipment + "</strong><br />"
                            + RemedyBase.escapeQuotes(description) + price + "</div>\",\"equipmentUID\":\"" + unique_identifier
                            + "\",\"equipment\":\"" + equipment + "\",\"price\":\"" + price + "\",\"pricecheckbox\":\"" + priceCheckbox
                            + "\",\"category\":\"" + category + "\",\"supporttext\":\"" + supportUserText + "\",\"workflow\":\""
                            + workflow + "\"" + accessoriesList + "\n}";
                    if (procurementcount + NUM_ONE == procurementitems.size()) {
                        output += CLOSE_BRACKETS_AND_BRACES;
                    } else {
                        output += COMMA;
                    }
                    procurementcount++;
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(output, HttpStatus.OK);
    }

    /**
     * author - Vani
     * To get the count of support groups for the loggedinuser
     *
     * @return - Returns the supportcount as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  LoginID as input
     */
    @GetMapping(path = SUPPORTGROUPS_COUNT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getCountOfSupportGroups(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        Boolean isAccountValid = Boolean.FALSE;
        String loginID = "";
        Map<String, String[]> params = request.getParameterMap();
        String endpointName = SUPPORTGROUPS_COUNT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(LOGIN_ID) || request.getParameterMap().containsKey(USER_SK)) {
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    if (customWhooshModel.getQuery() != null && !customWhooshModel.getQuery().isEmpty()) {
                        customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + FUNCTIONAL_TYPE + COLON + ESCAPE_CHARACTER + SUPPORT + ESCAPE_CHARACTER);
                    } else {
                        customWhooshModel.setQuery(FUNCTIONAL_TYPE + COLON + ESCAPE_CHARACTER + SUPPORT + ESCAPE_CHARACTER);
                    }
                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS, customWhooshModel.getQuery(), customWhooshModel.getSort()
                            , new String[]{GROUP_NAME}, customWhooshModel.getSortfields(), customWhooshModel.getSortorder()
                            , customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), NUM_ZERO, Boolean.FALSE, customWhooshModel.getDistinctfield(), user);
                    if (result.size() > NUM_ZERO) {
                        resultMap = SFInterfaceBase.getCountofSupportGroups(result);
                    } else {
                        resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.OK);
                    }
                } else {
                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYLOGINIDORUSERSK", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * ProfileSearch with loggedinuser's visibility group
     *
     * @return - Returns the profiles as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  gets partialname networklogin emailaddress as input
     */
    @RequestMapping(value = PROFILE_SEARCH4_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity userBasedProfileSearch(@PathVariable(VERSION) String version,
                                                 @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                 @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                 @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                 @RequestBody(required = false) Map<String, Object> requestFields,
                                                 HttpServletRequest request) throws IOException {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        List<Map<String, Object>> userprofiles = new ArrayList<>();
        List<Map<String, Object>> accounts = new ArrayList<>();
        String loginID = "";
        String endpointName = PROFILE_SEARCH4;

        try {

            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            Map<String, String[]> requestparametermap = new HashMap<>();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(LOGIN_ID)) {
                    user.setAuthUserName(request.getParameter(LOGIN_ID));
                    user.setUserID(null);
                } else if (request.getParameterMap().containsKey(USER_SK)) {
                    user.setUserID(request.getParameter(USER_SK));
                    user.setAuthUserName(null);
                }
                if (requestFields != null)
                    requestparametermap = convertmapObject(requestFields);
                Map<String, String[]> params1 = new HashMap<>();
                params1.putAll(request.getParameterMap());
                params1.putAll(requestparametermap);

                String accountquery = "";
                if (params1.containsKey(ACCOUNT) || params1.containsKey(ACCOUNT_SK)) {//IF Account exist in  Params
                    if (params1.containsKey(ACCOUNT)) {
                        accountquery = OPEN_BRACKET + MASTERACCOUNT_WITH_COLON + params1.get(ACCOUNT)[NUM_ZERO].toString() + ESCAPE_CHARACTER + ACCOUNT_WITH_COLON_AND_OR + params1.get(ACCOUNT)[NUM_ZERO].toString() + CLOSE_BRACKET_WITH_DOUBLEQUOTES;
                        params1.remove(ACCOUNT);
                    } else {
                        accountquery = OPEN_BRACKET + MASTERACCOUNTSK_WITH_COLON + params1.get(ACCOUNT_SK)[NUM_ZERO].toString() + ACCOUNTSK_WITH_COLON_AND_OR + params1.get(ACCOUNT_SK)[NUM_ZERO].toString() + CLOSE_BRACKET_WITH_DOUBLEQUOTES;
                        params1.remove(ACCOUNT_SK);
                    }
                } else {
                    UserAccountNGroup userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                    if (!userAccountNGroup.getUserAccounts().isEmpty()) {
                        accountquery += OPEN_BRACKET + userAccountNGroup.getUserAccounts()
                                .stream()
                                .map(useraccount -> ACCOUNT_WITH_COLON + useraccount.get(ACCOUNT) + MASTERACCOUNT_WITH_COLON_AND_OR + useraccount.get(ACCOUNT) + ESCAPE_CHARACTER)
                                .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    } else {
                        resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                        return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
                    }
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelWithLike(params1, user);

                String query = customWhooshModel.getQuery();
                if (query.isEmpty()) {
                    query += accountquery;
                } else {
                    query += AND + accountquery;
                }
                SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version, null, user.getUserID(), user.getAuthUserName(), null);

                userprofiles = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, query, customWhooshModel.getSort(), customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.FALSE, user);

            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGUSERTOKEN", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(userprofiles, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * Consolidated Tickets with Status(Assigned,Acknowledged,Pending) of the loggedinuser's visibility group
     *
     * @return - Returns the ticketdetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  loggedinuser Account Group SearchQuery
     */
    @PostMapping(value = CONSOLIDATED_TICKETS_PATH, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity consolidatedTickets(@PathVariable(VERSION) String version,
                                              @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                              @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                              @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                              @RequestBody SearchRequest searchRequest, HttpServletRequest request) throws IOException {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> watchList = new ArrayList<>();
        String loginID = "", assignedgroupquery = "", tickettypequery = "", clientquery = "", groupquery = "", useraccounts = "", statusquery = "";
        StringBuilder consolidatedquery = new StringBuilder("");
        String /*accountquery = "",*/ tempaccountquery = "";
        String endpointName = CONSOLIDATED_TICKETS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, searchRequest != null ? searchRequest.toString() : NA);
            if (trimToken(aToken) != NULL || (trimToken(refreshToken) != NULL && trimToken(accessToken) != NULL)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }
            loginID = searchRequest.getLoginID();
            String user_sk = searchRequest.getUsersk();
            UserAccountNGroup userAccountNGroup = new UserAccountNGroup();

            //problemManager changes starts
            if (searchRequest.getProblemmanager_sk() == null || searchRequest.getProblemmanager_sk().length == 0) {
                userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(loginID, user_sk, user);
                if (searchRequest.getQuery() == null || searchRequest.getQuery().length == 0) {
                    if ((searchRequest.getGroups() == null || searchRequest.getGroups().isEmpty()) && ((searchRequest.getAccount() == null || searchRequest.getAccount().isEmpty())
                            && (searchRequest.getAccount_sk() == null || searchRequest.getAccount_sk().isEmpty()))) {
                        if (!userAccountNGroup.getUserGroups().isEmpty()) {
                            assignedgroupquery = OPEN_BRACKET + userAccountNGroup.getUserGroups().parallelStream().map(assignedgroup ->
                                    ASSIGNED_GROUPNAME_WITH_COLON + assignedgroup.get(GROUP_NAME) + ESCAPE_CHARACTER)
                                    .collect(Collectors.joining(OR)) + OR + RAISED_BY_User_sk + COLON + ESCAPE_CHARACTER + user_sk + ESCAPE_CHARACTER
                                    + OR + ALTERNATE_User_sk + COLON + searchRequest.getUsersk()
                                    + CLOSE_BRACKET;
                        } else {
                            assignedgroupquery += OPEN_BRACKET + RAISED_BY_User_sk + COLON + ESCAPE_CHARACTER + user_sk + ESCAPE_CHARACTER
                                    + OR + ALTERNATE_User_sk + COLON + searchRequest.getUsersk()
                                    + CLOSE_BRACKET;
                        }
                    }
                }
            } else {
                //        userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroupforProblemManager(searchRequest.getProblemmanager_sk());
                String[] problemManagersk = searchRequest.getProblemmanager_sk();
                String accountQuery1 = "";
                for (String pm_sk : problemManagersk) {
                    if (StringUtils.isBlank(accountQuery1)) {
                        accountQuery1 += OPEN_BRACKET + USER_SK + COLON + ESCAPE_CHARACTER + pm_sk + ESCAPE_CHARACTER;
                    } else {
                        accountQuery1 += OR + USER_SK + COLON + ESCAPE_CHARACTER + pm_sk + ESCAPE_CHARACTER;
                    }
                }
                if (StringUtils.isNotBlank(accountQuery1)) {
                    accountQuery1 += CLOSE_BRACKET;
                }
                List<Map<String, Object>> results = new ArrayList<>();
                results = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROBLEM_MANAGER, accountQuery1, null
                        , new String[]{USER_SK, MASTER_ACCOUNT, ACCOUNT}, ACCOUNT, ASC, TRUE, null, NUM_ZERO, Boolean.FALSE, user);
                userAccountNGroup.setUserAccounts(results);
            }
            //problem Manager changes ends

            if (!userAccountNGroup.getUserAccounts().isEmpty()) {
                useraccounts += OPEN_BRACKET + userAccountNGroup.getUserAccounts().parallelStream().map(account ->
                        ACCOUNT_CODE_WITH_COLON + account.get(ACCOUNT) + ESCAPE_CHARACTER)//ACCOUNT_CODE_WITH_COLON_AND_OR + account.get(MASTER_ACCOUNT) +
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            } else {
                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                return new ResponseEntity(resultMap, HttpStatus.UNAUTHORIZED);
            }

            //tickettype
            if (searchRequest.getBusinessFunctionCode() != null && !searchRequest.getBusinessFunctionCode().isEmpty()) {
                tickettypequery += OPEN_BRACKET + Arrays.asList(searchRequest.getBusinessFunctionCode().split(",")).parallelStream().map(businessfn ->
                        BUSINESSFUNCTIONCODE_WITH_COLON + businessfn + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            } else {
                tickettypequery += OPEN_BRACKET + SFInterfaceBase.fetchRequiredFieldsForBusinessFunction("CONSOLIDATED_TICKET_DEFAULT_BUSINESSFUNCTION").parallelStream()
                        .map(businessfn -> BUSINESSFUNCTIONCODE_WITH_COLON + businessfn + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            }

            if (searchRequest.getStatus() != null && searchRequest.getStatus().length > NUM_ZERO) {
                statusquery += OPEN_BRACKET + Arrays.asList(searchRequest.getStatus()).parallelStream()
                        .map(status -> STATUS_CODE_WITH_COLON + status + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            } else {
                statusquery += OPEN_BRACKET + SFInterfaceBase.fetchRequiredFieldsForBusinessFunction("CONSOLIDATED_TICKET_DEFAULT_STATUS").parallelStream()
                        .map(status -> STATUS_CODE_WITH_COLON + status + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            }

            //with client either groups
            if (searchRequest.getAccount() != null && !searchRequest.getAccount().isEmpty()) {
               /* accountquery = searchRequest.getAccount().parallelStream()
                        .map(s -> ACCOUNT + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR));*/
                tempaccountquery = searchRequest.getAccount().parallelStream()
                        .map(s -> ACCOUNT_CODE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR));
            }
            if (searchRequest.getAccount_sk() != null && !searchRequest.getAccount_sk().isEmpty()) {
                String tempAccount_skQuery = "";
                /*accountquery = accountquery.isEmpty() ? EMPTY : accountquery + OR;*/
                tempaccountquery = tempaccountquery.isEmpty() ? EMPTY : tempaccountquery + OR;
                tempAccount_skQuery += searchRequest.getAccount_sk().parallelStream()
                        .map(s -> ACCOUNT_SK + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR));
                tempaccountquery += tempAccount_skQuery;
                /*accountquery += tempAccount_skQuery;*/
            }
            if (!tempaccountquery.isEmpty()) {
                /* accountquery = OPEN_BRACKET + accountquery + CLOSE_BRACKET;*/
                tempaccountquery = OPEN_BRACKET + tempaccountquery + CLOSE_BRACKET;
            }

            /*if (!accountquery.isEmpty()) {
                String tempAccountQuery = "";
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, accountquery, null,
                        new String[]{MASTER_ACCOUNT}, null, null, "true", null, 0);
                tempAccountQuery = OPEN_BRACKET + result.parallelStream()
                        .map(s -> MASTER_ACCOUNT + COLON + s.get(MASTER_ACCOUNT).toString())
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS, tempAccountQuery +
                                AND + OPEN_BRACKET + FUNCTIONAL_TYPE + COLON + SUPPORT + OR + FUNCTIONAL_TYPE + COLON + ESCAPE_CHARACTER + NOTIFICATION + ESCAPE_CHARACTER + CLOSE_BRACKET,
                        null, new String[]{ACCOUNT, MASTER_ACCOUNT, GROUP_NAME, FUNCTIONAL_TYPE}, GROUP_NAME, null, TRUE
                        , null, 0, Boolean.FALSE, GROUP_NAME);

                if (result != null && !result.isEmpty()) {
                    clientquery += OPEN_BRACKET + result.parallelStream()
                            .map(account -> ASSIGNED_GROUPNAME_WITH_COLON + account.get(GROUP_NAME) + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }
            }*/

            if (searchRequest.getGroups() != null && !searchRequest.getGroups().isEmpty()) {
                groupquery += OPEN_BRACKET + searchRequest.getGroups().parallelStream().map(group -> ASSIGNED_GROUPNAME_WITH_COLON + group + ESCAPE_CHARACTER)
                        .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
            }

            if (searchRequest.getQuery() != null && searchRequest.getQuery().length > NUM_ZERO) {
               /* Directory allTicketIndex = indexerUtil.getIndexDirectory(INDEXERNAME_FULL_TEXT_SEARCH);
                IndexerIntf allTicketIndexF = new LucenceImpl(allTicketIndex);
                consolidatedquery.append(
                        OPEN_BRACKET + Arrays.stream(searchRequest.getQuery()).map(s ->
                        {
                            try {
                                return allTicketIndexF.formQuery(s);
                            } catch (IOException e) {

                                return "";
                            }
                        }).collect(Collectors.joining(OR)) + CLOSE_BRACKET
                );*/

                consolidatedquery.append(
                        OPEN_BRACKET + Arrays.stream(searchRequest.getQuery())
                                .map(s ->
                                        SFInterfaceBase.consolidatedTicketsFullTextQuery(s))
                                .collect(Collectors.joining(OR)) + CLOSE_BRACKET);
            }

            //consolidated all query
            if (!assignedgroupquery.isEmpty()) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + assignedgroupquery);
                } else {
                    consolidatedquery.append(" " + assignedgroupquery);
                }
            }

            if (!groupquery.isEmpty()) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + groupquery);
                } else {
                    consolidatedquery.append(" " + groupquery);
                }
            }
            //removing client query
            /*if (!clientquery.isEmpty()) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + clientquery);
                } else {
                    consolidatedquery.append(" " + clientquery);
                }
            }*/
            //
            if (!tickettypequery.isEmpty()) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + tickettypequery);
                } else {
                    consolidatedquery.append(" " + tickettypequery);
                }
            }
            //Adding status if exist are default status values
            if (!statusquery.isEmpty()) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + statusquery);
                } else {
                    consolidatedquery.append(" " + statusquery);
                }
            }


            if (StringUtils.isNotBlank(searchRequest.getRaisedby_sk())) {
                //RaisedByUser_sk which includes alternate user too in the filter
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + OPEN_BRACKET + RAISED_BY_User_sk + COLON + searchRequest.getRaisedby_sk()
                            + OR + ALTERNATE_User_sk + COLON + searchRequest.getRaisedby_sk() + CLOSE_BRACKET);
                } else {
                    consolidatedquery.append(" " + OPEN_BRACKET + RAISED_BY_User_sk + COLON + searchRequest.getRaisedby_sk()
                            + OR + ALTERNATE_User_sk + COLON + searchRequest.getRaisedby_sk() + CLOSE_BRACKET);
                }
            }
            //AssignedUser too in the filter
            if (searchRequest.getAssigned_sk() != null && searchRequest.getAssigned_sk().length > 0) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + OPEN_BRACKET + Arrays.stream(searchRequest.getAssigned_sk())
                            .map(s -> ASSIGNED_INDIVIDUALSK + COLON + s)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET);
                } else {
                    consolidatedquery.append(OPEN_BRACKET + Arrays.stream(searchRequest.getAssigned_sk())
                            .map(s -> ASSIGNED_INDIVIDUALSK + COLON + s)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET);
                }
            }
            //Submitter too in the filter
            if (searchRequest.getSubmitter_sk() != null && searchRequest.getSubmitter_sk().length > 0) {
                if (consolidatedquery.length() != NUM_ZERO) {
                    consolidatedquery.append(AND + OPEN_BRACKET + Arrays.stream(searchRequest.getSubmitter_sk())
                            .map(s -> SUBMITTER_SK + COLON + s)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET);
                } else {
                    consolidatedquery.append(OPEN_BRACKET + Arrays.stream(searchRequest.getSubmitter_sk())
                            .map(s -> SUBMITTER_SK + COLON + s)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET);
                }
            }

            //filter for restricting sub-account to not to view other groups ticket if group not selected
            if ((searchRequest.getGroups() == null || searchRequest.getGroups().isEmpty())
                    && ((searchRequest.getAccount_sk() != null && !searchRequest.getAccount_sk().isEmpty()) ||
                    (searchRequest.getAccount() != null && !searchRequest.getAccount().isEmpty()))
                    && consolidatedquery.length() > 0
                    && !tempaccountquery.isEmpty()) {
                consolidatedquery.append(AND + tempaccountquery);
            }
            //By default user level restriction is applied

            if (consolidatedquery.length() != NUM_ZERO) {
                consolidatedquery.append(AND + useraccounts);
            } else {
                consolidatedquery.append(" " + useraccounts);
            }


            /* explicitly for change request*/
            if (searchRequest.getIsraised())
                consolidatedquery = new StringBuilder(OPEN_BRACKET + RAISED_BY_User_sk + COLON + searchRequest.getUsersk()
                        + OR + ALTERNATE_User_sk + COLON + searchRequest.getUsersk() + CLOSE_BRACKET
                )
                        .append(AND)
                        .append(useraccounts)
                        .append(AND)
                        .append(tickettypequery)
                        .append(AND)
                        .append(statusquery);
            /*End explicitly for change request*/

            if (searchRequest.getProblemmanager_sk() == null || searchRequest.getProblemmanager_sk().length == 0) {
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FULL_TEXT_SEARCH, consolidatedquery.toString(),
                        null, searchRequest.getSelectedFields(), searchRequest.getSortfield(), searchRequest.getSortorder(), null, null,
                        searchRequest.getCount() != null ? searchRequest.getCount() : NUM_ZERO, Boolean.FALSE, user);
            } else {
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FULL_TEXT_SEARCH, consolidatedquery.toString() + AND +
                                OPEN_BRACKET + ASSIGNED_GROUPNAME_WITH_COLON + CTS_1ST_LEVEL + ESCAPE_CHARACTER + CLOSE_BRACKET,
                        null, searchRequest.getSelectedFields(), searchRequest.getSortfield(), searchRequest.getSortorder(), null, null,
                        searchRequest.getCount() != null ? searchRequest.getCount() : NUM_ZERO, Boolean.FALSE, user);
            }

            try {
                watchList = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_WATCHLIST, OPEN_BRACKET + USER_SK + COLON + user_sk + OR + LOGINID_WITH_COLON + loginID + CLOSE_BRACKET_WITH_DOUBLEQUOTES + AND + STATUS + COLON + WATCHLIST_STATUS + AND + NOT + TICKET_NUMBER + COLON + NULL_VALUE, null, new String[]{TICKET_NUMBER}, null, null, null, null, NUM_ZERO, user);
            } catch (Exception e) {
                SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
                watchList = new ArrayList<>();
            }
            //watchlist starts
            Set<String> loggedUserGroupName = userAccountNGroup.getUserGroups()
                    .parallelStream()
                    .filter(group -> group.get(GROUP_NAME) != null
                            && !group.get(GROUP_NAME).toString().isEmpty())
                    .map(group -> group.get(GROUP_NAME).toString())
                    .collect(Collectors.toSet());
            Set<String> watchListTickets = watchList
                    .parallelStream()
                    .filter(watchlist -> watchlist.get(TICKET_NUMBER) != null
                            && !watchlist.get(TICKET_NUMBER).toString().isEmpty())
                    .map(watchlist -> watchlist.get(TICKET_NUMBER).toString())
                    .collect(Collectors.toSet());
            result = result.stream()
                    .map(stringObjectMap -> {
                        Boolean finalSelfFlag = Boolean.FALSE;
                        stringObjectMap.put(WATCHLIST, watchListTickets.contains(stringObjectMap.get(TICKET)));
                        if ((stringObjectMap.get(ASSIGNED_GROUP_NAME) != null)
                                && (stringObjectMap.get(RAISED_BY_User_sk) != null)
                                && (stringObjectMap.get(RAISED_BY_User_sk).toString().equalsIgnoreCase(user_sk))
                                && (!loggedUserGroupName.contains((String) stringObjectMap.get(ASSIGNED_GROUP_NAME)))) {
                            finalSelfFlag = Boolean.TRUE;
                        }
                        stringObjectMap.put(SELFFLAG, finalSelfFlag);
                        return stringObjectMap;
                    })
                    .collect(Collectors.toList());
            //watchlist ends


        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * Modifiedby - Vani
     * Fetch Incident Ticket details
     *
     * @return - Returns the ticketdetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account or user_sk
     */
    @GetMapping(path = INCIDENT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchIncidentDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = INCIDENT_DETAILS;
        JdbcTemplate jdbcTemplate;
        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {

                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)
                        || request.getParameterMap().containsKey(USER_SK)) {

                    jdbcTemplate = fetchConnection(request, user);
                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    params.put(BUSINESS_FUNCTION, new String[]{INCIDENTS});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_INCIDENT_SP);
                    endpoints.add(INDEXERNAME_FETCH_INC_HISTORY);
                    endpoints.add(INDEXERNAME_FETCH_INC_WORKNOTES);
                    endpoints.add(INDEXERNAME_FETCH_INC_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP);
                    endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);
                    endpoints.add(INDEXERNAME_FETCH_INC_ROOT_CAUSE);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> articlelog = new HashMap<>();
                                    articlelog.put(TICKET_SK, new String[]{ticket_sk});
                                    articlelog.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), articlelog);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                }

                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_INC_ROOT_CAUSE)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameters = new HashMap<>();
                                    parameters.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameters);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                }

                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_INC_ATTACHMENTS)) {
                                params.put(TYPE, new String[]{ATTACHMENT_TYPE_U});
                                CustomWhooshModel attachmentsWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), attachmentsWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , attachmentsWhooshModel.getRequiredColumns());
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_INCIDENT_SP)) {
                                resultMap.put(INCIDENT, result);
                                if (result.size() > NUM_ZERO) {
                                    businessfnsk = result.get(NUM_ZERO).get(BUSINESS_FUNCTION_SK).toString();
                                    ticket_sk = result.get(NUM_ZERO).get(TICKET_SK).toString();
                                }

                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)) {
                                resultMap.put(MASTERTYPE_MAPPING, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                                resultMap.put(KB_TICKET_MAPPING, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP)) {
                                resultMap.put(KB_ARTICLE_LOG, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_INC_ROOT_CAUSE)) {
                                resultMap.put(ROOT_CAUSE, result);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                        // }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * Modifiedby - Vani
     * Fetch ServiceRequest Ticket details
     *
     * @return - Returns the ticketdetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account or user_sk
     */
    @GetMapping(path = SERVICE_REQUEST_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchServiceRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>(), secureInfoMap = new HashMap<>();
        List<Map<String, Object>> result, endpointResultSet = new ArrayList<>();
        String loginID = "", businessfnsk = "", ticket_sk = "";
        List<Map<String, Object>> normalAttachments = new ArrayList<>();
        String endpointName = SERVICE_REQUEST_DETAILS;
        JdbcTemplate jdbcTemplate;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)
                        || request.getParameterMap().containsKey(USER_SK)) {

                    jdbcTemplate = fetchConnection(request, user);

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    if (request.getParameterMap().containsKey(TICKET)) {
                        String ticket = request.getParameter(TICKET);
                        tempParams1.put(SR_NUMBER, new String[]{ticket});
                    }
                    if (request.getParameterMap().containsKey(ACCOUNT)) {
                        String account = request.getParameter(ACCOUNT);
                        tempParams1.put(ACCOUNT, new String[]{account});
                    }
                    if (request.getParameterMap().containsKey(USER_SK)) {
                        String user_sk = request.getParameter(USER_SK);
                        tempParams1.put(USER_SK, new String[]{user_sk});
                    }
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(tempParams1, user);

                    params.put(BUSINESS_FUNCTION, new String[]{SR});
                    CustomWhooshModel customWhooshModel2 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_SR_SP);
                    endpoints.add(INDEXERNAME_FETCH_SR_HISTORY);
                    endpoints.add(INDEXERNAME_FETCH_SR_WORKNOTES);
                    endpoints.add(INDEXERNAME_FETCH_SR_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_SRT_SP);
                    endpoints.add(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP);
                    endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);
                    endpoints.add(INDEXERNAME_FETCH_SR_ROOT_CAUSE);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SRT_SP)) {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                                result = SFInterfaceBase.sortNDistinct(result, TASK_SEQUENCE, null, null, null);
                                if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SRT_SP)) {
                                    resultMap.put(SRTASK, result);
                                } else {
                                    resultMap.put(endpoint, result);
                                }
                            } else {
                                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                String message = (String) errorMap.get(MESSAGE);
                                errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                        } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)
                                || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel2.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                                if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)) {
                                    resultMap.put(MASTERTYPE_MAPPING, result);
                                } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                                    resultMap.put(KB_TICKET_MAPPING, result);
                                }
                            } else {
                                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                String message = (String) errorMap.get(MESSAGE);
                                errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                        } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                            String refdbquery = "";
                            if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                                Map<String, String[]> articlelog = new HashMap<>();
                                articlelog.put(TICKET_SK, new String[]{ticket_sk});
                                articlelog.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), articlelog);
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KB_ARTICLE_LOG_SP)) {
                                resultMap.put(KB_ARTICLE_LOG, result);
                            }
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                resultMap.put(INDEXERNAME_FETCH_COM_RESPONSES, result);
                            }

                        } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SR_ROOT_CAUSE)) {
                            if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                                Map<String, String[]> parameters = new HashMap<>();
                                parameters.put(TICKET, new String[]{ticket_sk});
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameters);
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                                resultMap.put(ROOT_CAUSE, result);
                            }

                        } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SR_ATTACHMENTS)) {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                params.put(TYPE, new String[]{ATTACHMENT_TYPE_U});
                                CustomWhooshModel attachmentsWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), attachmentsWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , attachmentsWhooshModel.getRequiredColumns());
                                resultMap.put(endpoint, result);

                            } else {
                                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                String message = (String) errorMap.get(MESSAGE);
                                errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                                if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SR_SP)) {
                                    resultMap.put(SERVICEREQUEST, result);
                                    if (result.size() > NUM_ZERO) {
                                        businessfnsk = result.get(NUM_ZERO).get(BUSINESS_FUNCTION_SK).toString();
                                        ticket_sk = result.get(NUM_ZERO).get(TICKET_SK).toString();
                                    }
                                } else {
                                    resultMap.put(endpoint, result);
                                }
                            } else {
                                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                String message = (String) errorMap.get(MESSAGE);
                                errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * Modifiedby - Vani
     * Fetch ServiceRequestTask Ticket details
     *
     * @return - Returns the ticketdetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account or user_sk
     */
    @GetMapping(path = SRTASK_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchSRTaskDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = SRTASK_DETAILS;
        JdbcTemplate jdbcTemplate;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)
                        || request.getParameterMap().containsKey(USER_SK)) {
                    jdbcTemplate = SFInterfaceBase.fetchConnection(request, user);

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    params.put(BUSINESS_FUNCTION, new String[]{SRT});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_SRT_SP);
                    endpoints.add(INDEXERNAME_FETCH_SRT_HISTORY);
                    endpoints.add(INDEXERNAME_FETCH_SRT_WORKNOTES);
                    endpoints.add(INDEXERNAME_FETCH_SRT_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP);
                    endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> articlelog = new HashMap<>();
                                    articlelog.put(TICKET_SK, new String[]{ticket_sk});
                                    articlelog.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), articlelog);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                }

                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SRT_ATTACHMENTS)) {
                                params.put(TYPE, new String[]{ATTACHMENT_TYPE_U});
                                CustomWhooshModel attachmentsWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), attachmentsWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , attachmentsWhooshModel.getRequiredColumns());
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(jdbcTemplate, refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SRT_SP)) {
                                resultMap.put(SRTASK, result);
                                if (result.size() > NUM_ZERO) {
                                    businessfnsk = result.get(0).get(BUSINESS_FUNCTION_SK).toString();
                                    ticket_sk = result.get(0).get(TICKET_SK).toString();
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_MASTERTYPE_MAPPING_SP)) {
                                resultMap.put(MASTERTYPE_MAPPING, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_KBTICKET_MAPPING_SP)) {
                                resultMap.put(KB_TICKET_MAPPING, result);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author -  Vinodhini
     * ModifiedBy - Vani
     * Fetch UserAccessible Accounts
     *
     * @return - Returns the Accounts as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    @GetMapping(path = ACCOUNTS_BY_USER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userBasedAccounts(Model model,
                                            @PathVariable(VERSION) String version,
                                            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String> fieldaliass = new HashMap<>();
        String userLoginID = "";

        String endpointName = ACCOUNTS_BY_USER;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                String accountquery = "";
                if (request.getParameterMap().containsKey(USER_SK)) {
                    user.setUserID(request.getParameter(USER_SK).toString());
                    user.setAuthUserName(null);
                }
                if (request.getParameterMap().containsKey(LOGIN_ID)) {
                    user.setAuthUserName(request.getParameter(LOGIN_ID).toString());
                    user.setUserID(null);
                }
                UserAccountNGroup userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);

                if (!userAccountNGroup.getUserAccounts().isEmpty()) {
                    accountquery = OPEN_BRACKET + userAccountNGroup.getUserAccounts().stream().map(
                            account -> ACCOUNT_WITH_COLON + account.get(ACCOUNT) + ESCAPE_CHARACTER
                    ).collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(request.getParameterMap(), user);
                    if (customWhooshModel.getParams().containsKey(LOGIN_ID)) {
                        customWhooshModel.getParams().remove(LOGIN_ID);
                    }
                    if (customWhooshModel.getParams().containsKey(USER_SK)) {
                        customWhooshModel.getParams().remove(USER_SK);
                    }

                    String query = SFInterfaceBase.generateWhooshQuery(customWhooshModel.getParams());
                    if (StringUtils.isNotBlank(query)) {
                        accountquery = query + AND + accountquery;
                    }

                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, accountquery, customWhooshModel.getSort()
                            , customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder()
                            , customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.FALSE, user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * Api to fetch user permission based accessible account includes(ALL,child-masteraccount)
     *
     * @return - Returns the List of Accounts as Json response
     * @header - Requires an valid accesstoken or aToken
     */
    @GetMapping(path = USER_ACCOUNTS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userBasedAccountsWithCTS(Model model,
                                                   @PathVariable(VERSION) String version,
                                                   HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String> fieldaliass = new HashMap<>();
        Boolean isAccountValid = false;
        String[] requiredColumns = null;
        String userLoginID = "";
        String endpointName = USER_ACCOUNTS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                String accountquery = "";
                if (request.getParameterMap().containsKey(USER_SK)) {
                    user.setUserID(request.getParameter(USER_SK).toString());
                    user.setAuthUserName(null);
                }
                if (request.getParameterMap().containsKey(LOGIN_ID)) {
                    user.setAuthUserName(request.getParameter(LOGIN_ID).toString());
                    user.setUserID(null);
                }
                UserAccountNGroup userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);

                if (!userAccountNGroup.getUserAccounts().isEmpty()) {
                    accountquery = OPEN_BRACKET + userAccountNGroup.getUserAccounts().stream().map(
                            account -> ACCOUNT_WITH_COLON + account.get(ACCOUNT) + ESCAPE_CHARACTER
                    ).collect(Collectors.joining(OR)) + OR + ACCOUNT_WITH_COLON + "CTS" + ESCAPE_CHARACTER + CLOSE_BRACKET;
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(request.getParameterMap(), user);
                    if (customWhooshModel.getParams().containsKey(LOGIN_ID)) {
                        customWhooshModel.getParams().remove(LOGIN_ID);
                    }
                    if (customWhooshModel.getParams().containsKey(USER_SK)) {
                        customWhooshModel.getParams().remove(USER_SK);
                    }
                    String query = SFInterfaceBase.generateWhooshQuery(customWhooshModel.getParams());
                    if (StringUtils.isNotBlank(query)) {
                        accountquery = query + AND + accountquery;
                    }
                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, accountquery, customWhooshModel.getSort(),
                            customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(),
                            customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(),
                            Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.FALSE, user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * Api to fetch user permission based accessible account includes(ALL,child-masteraccount)
     *
     * @return - Returns the List of Accounts as Json response
     * @header - Requires an valid accesstoken or aToken
     */
    @GetMapping(path = USER_ACCOUNTS_PATH1, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userBasedAccounts1(Model model,
                                             @PathVariable(VERSION) String version,
                                             HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String> fieldaliass = new HashMap<>();
        Boolean isAccountValid = false;
        String[] requiredColumns = null;
        String userLoginID = "";
        String endpointName = USER_ACCOUNTS1;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                String accountquery = "";
                if (request.getParameterMap().containsKey(USER_SK)) {
                    user.setUserID(request.getParameter(USER_SK).toString());
                    user.setAuthUserName(null);
                }
                if (request.getParameterMap().containsKey(LOGIN_ID)) {
                    user.setAuthUserName(request.getParameter(LOGIN_ID).toString());
                    user.setUserID(null);
                }
                UserAccountNGroup userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                if (userAccountNGroup.getPrimaryAccount().equalsIgnoreCase("CTS")) {
                    userAccountNGroup.setUserAccounts(SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, STATUS + COLON + STATUS_VALUE_ACTIVE
                            , null, new String[]{MASTER_ACCOUNT, ACCOUNT, ACCOUNT_CODE_NAME, MASTER_ACCOUNT_CODE_NAME}, ACCOUNT, ASC, TRUE, null
                            , NUM_ZERO, Boolean.FALSE, user));
                }
                if (!(userAccountNGroup.getPrimaryAccount().equalsIgnoreCase("CTS")) && userAccountNGroup.getUserPermission()
                        .parallelStream()
                        .anyMatch(stringObjectMap -> stringObjectMap.get(GROUP_NAME).toString().equalsIgnoreCase("ALL"))) {
                    userAccountNGroup.getUserAccounts().addAll(SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, STATUS + COLON + STATUS_VALUE_ACTIVE
                                    + AND + QUEUE + COLON + IDOC
                            , null, new String[]{MASTER_ACCOUNT, ACCOUNT, ACCOUNT_CODE_NAME, MASTER_ACCOUNT_CODE_NAME}, ACCOUNT, ASC, TRUE, null
                            , NUM_ZERO, Boolean.FALSE, user));
                }

                accountquery = OPEN_BRACKET + userAccountNGroup.getUserAccounts().stream().map(
                        account -> ACCOUNT_WITH_COLON + account.get(ACCOUNT) + ESCAPE_CHARACTER
                ).collect(Collectors.joining(OR)) + OR + ACCOUNT_WITH_COLON + "CTS" + ESCAPE_CHARACTER + CLOSE_BRACKET;
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(request.getParameterMap(), user);
                if (customWhooshModel.getParams().containsKey(LOGIN_ID)) {
                    customWhooshModel.getParams().remove(LOGIN_ID);
                }
                if (customWhooshModel.getParams().containsKey(USER_SK)) {
                    customWhooshModel.getParams().remove(USER_SK);
                }
                String query = SFInterfaceBase.generateWhooshQuery(customWhooshModel.getParams());
                if (StringUtils.isNotBlank(query)) {
                    accountquery = query + AND + accountquery;
                }
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, accountquery, customWhooshModel.getSort(),
                        customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(),
                        customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE,
                        customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.FALSE, user);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

   /* @GetMapping(path = ACCOUNT_GROUP1_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity AccountGroupsBasedONUser(Model model,
                                                   @PathVariable(VERSION) String version,
                                                   @RequestParam(name = ACCOUNT) String account,
                                                   HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String> fieldaliass = new HashMap<>();
        Boolean isAccountValid = false;
        String[] requiredColumns = null;
        String userLoginID = "";

        try {
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                String accountquery = "";
                if (request.getParameterMap().containsKey(USER_SK)) {
                    user.setUserID(request.getParameter(USER_SK).toString());
                    user.setAuthUserName(null);
                }
                if (request.getParameterMap().containsKey(LOGIN_ID)) {
                    user.setAuthUserName(request.getParameter(LOGIN_ID).toString());
                    user.setUserID(null);
                }
                CustomWhooshModel customWhooshModel = generateWhooshModel(request.getParameterMap());

                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, USER_SK + COLON + user.getUserID() + OR + LOGINID_WITH_COLON + user.getAuthUserName() + ESCAPE_CHARACTER, null,
                        new String[]{ACCOUNT, MASTER_ACCOUNT}, null, null, null, null, NUM_ZERO, Boolean.FALSE, null);
                if (!result.isEmpty()) {
                    if ((!result.get(NUM_ZERO).get(MASTER_ACCOUNT).toString().equalsIgnoreCase("CTS")) && account.equalsIgnoreCase("CTS"))
                        result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ASSINGED_GROUPS, MAPPING_TYPE + COLON + ESCAPE_CHARACTER + MAPPING_TYPE_VALUE_GS + ESCAPE_CHARACTER + AND + MASTER_ACCOUNT + COLON + ESCAPE_CHARACTER + result.get(0).get(MASTER_ACCOUNT).toString() + ESCAPE_CHARACTER + AND + GROUP_MASTER_ACCOUNT + COLON + ESCAPE_CHARACTER + account + ESCAPE_CHARACTER, null,
                                customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(),null);
                    else
                        result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS, MASTER_ACCOUNT + COLON + account + AND + FUNCTIONAL_TYPE + COLON + SUPPORT, null,
                                customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(),null);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, "AccountGroup1");
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, "AccountGroup1");
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, "AccountGroup1"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }*/

    /**
     * author - Vivek
     * Fetch groups based on visibility permission of the user in the given account
     *
     * @return - Returns the List of Groups as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    @PostMapping(path = ACCOUNT_GROUP1_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity accountGroup1(Model model,
                                        @PathVariable(VERSION) String version,
                                        @RequestBody Map<String, Object> requestFields,
                                        HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result1 = new ArrayList<>(), resultMap1 = new ArrayList<>();
        List<List<Map<String, Object>>> resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Set<String> accounts = new HashSet<>();
        String userLoginID = "";
        String endpointName = ACCOUNT_GROUP1;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, requestFields.toString());
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                String accountquery = "";
                if (requestFields.containsKey(USER_SK)) {
                    user.setUserID(requestFields.get(USER_SK).toString());
                    user.setAuthUserName(null);
                }
                if (requestFields.containsKey(LOGIN_ID)) {
                    user.setAuthUserName(requestFields.get(LOGIN_ID).toString());
                    user.setUserID(null);
                }
                //fetch visibility groups of user
                UserAccountNGroup userAccountNGroup = fetchUserAccountNGroup(null, user.getUserID(), user);
                //end fetch visibility groups of user

                CustomWhooshModel customWhooshModel = SFInterfaceServices.createCustomWhooshModel(requestFields);

                if (requestFields.get(ACCOUNT) != null) {
                    accountquery += OPEN_BRACKET + ((List) requestFields.get(ACCOUNT))
                            .parallelStream()
                            .map(
                                    s -> ACCOUNT + COLON + s
                            )
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;

                }
                if (requestFields.get(ACCOUNT_SK) != null) {
                    //  accountquery+=ACCOUNT_SK + COLON + requestFields.get(ACCOUNT);
                    String tempaccountquery = "";
                    tempaccountquery += OPEN_BRACKET + ((List) requestFields.get(ACCOUNT_SK))
                            .parallelStream()
                            .map(s -> ACCOUNT_SK + COLON + s)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    accountquery = accountquery.isEmpty() ? tempaccountquery : accountquery + OR + tempaccountquery;
                }

                result1 = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, accountquery, null, new String[]{ACCOUNT, MASTER_ACCOUNT}
                        , null, null, null, null, NUM_ZERO, Boolean.FALSE, null, user);

                accounts = result1.stream()
                        .map(tempaccount -> tempaccount.get(MASTER_ACCOUNT).toString())
                        .collect(Collectors.toSet());
                //accounts = (List<String>) requestFields.get(ACCOUNT);
                for (String account : accounts) {
                    if ((!userAccountNGroup.getPrimaryAccount().equalsIgnoreCase("CTS"))
                            && account.equalsIgnoreCase("CTS")) {
                        resultMap1 = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ASSINGED_GROUPS, MAPPING_TYPE + COLON
                                        + ESCAPE_CHARACTER + MAPPING_TYPE_VALUE_GS + ESCAPE_CHARACTER
                                        + AND + MASTER_ACCOUNT + COLON + ESCAPE_CHARACTER + userAccountNGroup.getPrimaryMasterAccount() + ESCAPE_CHARACTER
                                        + AND + GROUP_MASTER_ACCOUNT + COLON + ESCAPE_CHARACTER + account + ESCAPE_CHARACTER
                                , null
                                , customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder()
                                , customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE
                                , customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), null, user);
                        resultMap.add(resultMap1);
                    } else {
                        resultMap1 = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS, MASTER_ACCOUNT + COLON + account
                                        + AND + FUNCTIONAL_TYPE + COLON + SUPPORT, null, customWhooshModel.getRequiredColumns()
                                , customWhooshModel.getSortfields(), customWhooshModel.getSortorder(), customWhooshModel.getDistinct()
                                , customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE
                                , customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), null, user);
                        resultMap.add(resultMap1);
                    }
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * ModifiedBy - Vani
     * Fetch UserAccessible Accounts with master categoriation
     *
     * @return - Returns the Queue as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    /*@GetMapping(path = ACCOUNT_QUEUES_BY_USER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userBasedAccountQueue(Model model,
                                                @PathVariable(VERSION) String version,
                                                @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String> fieldaliass = null;
        String[] requiredColumns = null;
        String loginID = "";
        User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
        String endpointName = ACCOUNT_QUEUES_BY_USER;

        try {
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                String accountQuery = "";
                UserAccountNGroup userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(loginID, user.getUserID());
                if (userAccountNGroup.getAdministrator() || userAccountNGroup.getCSS()) {
                    accountQuery = DOUBLE_ASTERISK;
                } else {
                    for (Map<String, Object> useraccount : userAccountNGroup.getUserAccounts()) {
                        if (accountQuery.isEmpty()) {
                            accountQuery += OPEN_BRACKET + ACCOUNT_WITH_COLON + useraccount.get(ACCOUNT) + ESCAPE_CHARACTER;
                        } else {
                            accountQuery += OR + ACCOUNT_WITH_COLON + useraccount.get(ACCOUNT) + ESCAPE_CHARACTER;
                        }
                    }
                    if (!accountQuery.isEmpty()) {
                        accountQuery += CLOSE_BRACKET;
                    }
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(request.getParameterMap());

                if (StringUtils.isNotBlank(accountQuery) && !customWhooshModel.getQuery().isEmpty()) {
                    customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + accountQuery);
                } else if (!accountQuery.isEmpty()) {
                    customWhooshModel.setQuery(accountQuery);
                }
                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, customWhooshModel.getQuery(), customWhooshModel.getSort(),
                        customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), customWhooshModel.getSortorder(),
                        customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass(), customWhooshModel.getCount(), Boolean.FALSE,
                        customWhooshModel.getDistinctfield(), customWhooshModel.getPageno());
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }*/

    /**
     * author - Vani
     * Fetch User associated oncall groups
     *
     * @return - Returns the OncallDetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    @PostMapping(path = SUPPORT_GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity onCall2(Model model,
                                  @PathVariable(VERSION) String version,
                                  @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                  @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                  @RequestBody Map<String, Object> oncallFields,
                                  HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>(), params1 = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        String loginID = "", usersk = "";

        String endpointName = SUPPORT2;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, oncallFields != null ? oncallFields.toString() : NA);
            loginID = user.getAuthUserName();

            if (StringUtils.isNotBlank(loginID)) {
                usersk = user.getUserID();
                if (user.getAuthTokenType().equalsIgnoreCase(ATOKEN)
                        && (!(oncallFields.containsKey(USER_SK)) || (StringUtils.isBlank(oncallFields.get(USER_SK).toString())))) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.ONCALLREQUIREDPARAMETER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                }

                for (Map.Entry<String, Object> oncallField : oncallFields.entrySet()) {
                    if (!oncallField.getKey().equalsIgnoreCase(USER_SK) && !oncallField.getKey().equalsIgnoreCase(DISTINCT)
                            && !oncallField.getKey().equalsIgnoreCase(SUPPORT_TYPE) && !oncallField.getKey().equalsIgnoreCase(RELATED_TYPE)
                            && !oncallField.getKey().equalsIgnoreCase(SELECTED_FIELDS) && !oncallField.getKey().equalsIgnoreCase(SORTFIELD)
                            && !oncallField.getKey().equalsIgnoreCase(DISTINCTFIELD) && !oncallField.getKey().equalsIgnoreCase(PAGE_NUMBER)
                            && !oncallField.getKey().equalsIgnoreCase(COUNT)) {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.PARAMETERNOTNEEDEDFORFETCH", oncallField.getKey(), endpointName);
                        return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                    }
                }

                String groupQuery = "";
                UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
                if (oncallFields.containsKey(USER_SK)) {
                    usersk = oncallFields.get(USER_SK).toString();
                }
                if (StringUtils.isNotBlank(usersk)) {
                    userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(null, usersk, user);
                } else {
                    userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(loginID, user.getUserID(), user);
                }
                if (!userAccountNGroup.getUserGroups().isEmpty()) {
                    for (Map<String, Object> userGroup : userAccountNGroup.getUserGroups()) {
                        if (groupQuery.isEmpty()) {
                            groupQuery += OPEN_BRACKET + GROUPNAME_WITH_COLON + userGroup.get(GROUP_NAME) + ESCAPE_CHARACTER;
                        } else {
                            groupQuery += OR + GROUPNAME_WITH_COLON + userGroup.get(GROUP_NAME) + ESCAPE_CHARACTER;
                        }
                    }
                    if (!groupQuery.isEmpty()) {
                        groupQuery += CLOSE_BRACKET;
                    }

                    String relatedTypeQuery = "";
                    if (oncallFields.containsKey(RELATED_TYPE)) {
                        for (String relatedType : oncallFields.get(RELATED_TYPE).toString().split(COMMA)) {
                            if (StringUtils.isBlank(relatedTypeQuery)) {
                                relatedTypeQuery += OPEN_BRACKET + RELATEDTYPE_WITH_COLON + relatedType + ESCAPE_CHARACTER;
                            } else {
                                relatedTypeQuery += OR + RELATEDTYPE_WITH_COLON + relatedType + ESCAPE_CHARACTER;
                            }
                        }
                        if (!relatedTypeQuery.isEmpty()) {
                            relatedTypeQuery += CLOSE_BRACKET;
                        }

                        if (StringUtils.isNotBlank(groupQuery)) {
                            groupQuery += AND + relatedTypeQuery;
                        }
                    }

                    if (oncallFields.containsKey(SUPPORT_TYPE)) {
                        if (StringUtils.isNotBlank(groupQuery)) {
                            groupQuery += AND + SUPPORTTYPE_WITH_COLON + oncallFields.get(SUPPORT_TYPE) + ESCAPE_CHARACTER;
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceServices.createCustomWhooshModel(oncallFields);

                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_SUPPORT_DETAILS, groupQuery, null
                            , customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), null, customWhooshModel.getDistinct()
                            , null, customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.TRUE, user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.OK);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return SFInterfaceServices.returnResponseWithPagination(result);
    }

    /**
     * author - Vani
     * Fetch User not associated oncall groups
     *
     * @return - Returns the OncallDetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    @PostMapping(path = SUPPORT_OTHER_GROUP_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity onCall3(Model model,
                                  @PathVariable(VERSION) String version,
                                  @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                  @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                  @RequestBody Map<String, Object> oncallFields,
                                  HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>(), params1 = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        String loginID = "", usersk = "";

        String endpointName = SUPPORT3;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, oncallFields != null ? oncallFields.toString() : NA);
            loginID = user.getAuthUserName();

            if (StringUtils.isNotBlank(loginID)) {
                usersk = user.getUserID();
                if (user.getAuthTokenType().equalsIgnoreCase(ATOKEN)
                        && (!(oncallFields.containsKey(USER_SK)) || (StringUtils.isBlank(oncallFields.get(USER_SK).toString())))) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.ONCALLREQUIREDPARAMETER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                }

                if (oncallFields.containsKey(SUPPORT_TYPE) || oncallFields.containsKey(RELATED_TYPE)) {
                    for (Map.Entry<String, Object> oncallField : oncallFields.entrySet()) {
                        if (!oncallField.getKey().equalsIgnoreCase(USER_SK) && !oncallField.getKey().equalsIgnoreCase(SUPPORT_TYPE)
                                && !oncallField.getKey().equalsIgnoreCase(RELATED_TYPE) && !oncallField.getKey().equalsIgnoreCase(SELECTED_FIELDS)
                                && !oncallField.getKey().equalsIgnoreCase(SORTFIELD) && !oncallField.getKey().equalsIgnoreCase(DISTINCTFIELD)
                                && !oncallField.getKey().equalsIgnoreCase(PAGE_NUMBER) && !oncallField.getKey().equalsIgnoreCase(COUNT)) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.PARAMETERNOTNEEDEDFORFETCH", oncallField.getKey(), endpointName);
                            return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }

                    String groupQuery = "";
                    UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
                    if (oncallFields.containsKey(USER_SK)) {
                        usersk = oncallFields.get(USER_SK).toString();
                    }
                    if (StringUtils.isNotBlank(usersk)) {
                        userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(null, usersk, user);
                    } else {
                        userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(loginID, user.getUserID(), user);
                    }

                    if (!userAccountNGroup.getUserGroups().isEmpty()) {
                        String supportTypeQuery = "";
                        if (oncallFields.containsKey(SUPPORT_TYPE)) {
                            if (StringUtils.isBlank(supportTypeQuery)) {
                                supportTypeQuery += SUPPORTTYPE_WITH_COLON + oncallFields.get(SUPPORT_TYPE) + ESCAPE_CHARACTER;
                            }
                        }

                        for (Map<String, Object> userGroup : userAccountNGroup.getUserGroups()) {
                            if (groupQuery.isEmpty()) {
                                groupQuery += NOT_GROUPNAME_WITH_COLON + userGroup.get(GROUP_NAME) + ESCAPE_CHARACTER;
                            } else {
                                groupQuery += AND + NOT_GROUPNAME_WITH_COLON + userGroup.get(GROUP_NAME) + ESCAPE_CHARACTER;
                            }
                        }

                        if (StringUtils.isNotBlank(groupQuery) && StringUtils.isNotBlank(supportTypeQuery)) {
                            groupQuery = supportTypeQuery + AND + groupQuery;
                        }

                        String relatedTypeQuery = "";
                        if (oncallFields.containsKey(RELATED_TYPE)) {
                            for (String relatedType : oncallFields.get(RELATED_TYPE).toString().split(COMMA)) {
                                if (StringUtils.isBlank(relatedTypeQuery)) {
                                    relatedTypeQuery += OPEN_BRACKET + RELATEDTYPE_WITH_COLON + relatedType + ESCAPE_CHARACTER;
                                } else {
                                    relatedTypeQuery += OR + RELATEDTYPE_WITH_COLON + relatedType + ESCAPE_CHARACTER;
                                }
                            }
                            if (!relatedTypeQuery.isEmpty()) {
                                relatedTypeQuery += CLOSE_BRACKET;
                            }

                            if (StringUtils.isNotBlank(groupQuery)) {
                                groupQuery += AND + relatedTypeQuery;
                            }
                        }

                        String accountQuery = OPEN_BRACKET + userAccountNGroup.getUserAccounts().stream()
                                .map(useraccount -> ACCOUNT_WITH_COLON + useraccount.get(ACCOUNT) + ESCAPE_CHARACTER)
                                .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                        groupQuery += AND + accountQuery;

                        CustomWhooshModel customWhooshModel = SFInterfaceServices.createCustomWhooshModel(oncallFields);

                        result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_SUPPORT_DETAILS, groupQuery, null
                                , customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields(), null, customWhooshModel.getDistinct()
                                , null, customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield(), customWhooshModel.getPageno(), Boolean.TRUE, user);
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity(errorMap, HttpStatus.OK);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYUSERSKANDSUPPORTORRELATEDTYPE", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return SFInterfaceServices.returnResponseWithPagination(result);
    }

    /**
     * author - Vinodhini
     * Fetch SupportPersons
     *
     * @return - Returns the SupprotPersons as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User and account
     */
    @GetMapping(path = SUPPORT_PERSONS_BY_USER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity supportPersons(Model model,
                                         @PathVariable(VERSION) String version,
                                         @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                         @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                         @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
                                         HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>(), params1 = request.getParameterMap();
        String loginID = "";
        Map<String, Object> errorMap = new HashMap<>();
        String endpointName = SUPPORT_PERSONS_BY_USER;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {
                for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                    params.put(stringEntry.getKey(), stringEntry.getValue());
                }

                UserAccountNGroup userAccountAGroup = new UserAccountNGroup();
                String accountquery = "";
                if (params.containsKey(USER_SK)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(null, (String) params.get(USER_SK)[NUM_ZERO], user);
                    params.remove(USER_SK);
                } else if (params.containsKey(LOGIN_ID)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup((String) params.get(LOGIN_ID)[NUM_ZERO], null);
                    params.remove(LOGIN_ID);
                } else if (StringUtils.isBlank(atoken) && (StringUtils.isNotBlank(accessToken) || StringUtils.isNotBlank(refreshToken))) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }

                accountquery = OPEN_BRACKET + userAccountAGroup.getUserAccounts().stream().map(
                        account -> ACCOUNT_WITH_COLON + account.get(ACCOUNT) + ESCAPE_CHARACTER
                ).collect(Collectors.joining(OR)) + CLOSE_BRACKET;

                CustomWhooshModel customModel = SFInterfaceBase.generateWhooshModel(params, user);

                if (StringUtils.isNotBlank(accountquery) && !customModel.getQuery().isEmpty()) {
                    customModel.setQuery(customModel.getQuery() + AND + accountquery);
                } else if (!accountquery.isEmpty()) {
                    customModel.setQuery(accountquery);
                }

                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, customModel.getQuery(), customModel.getSort()
                        , customModel.getRequiredColumns(), customModel.getSortfields(), customModel.getSortorder(), customModel.getDistinct()
                        , customModel.getFieldaliass(), customModel.getCount(), Boolean.FALSE, customModel.getDistinctfield(), customModel.getPageno(), Boolean.FALSE, user);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * ModifiedBy - Vani
     * Fetch TicketCount of the loggedinuser in Status(Assigned,Acknowledged,Pending)
     *
     * @return - Returns the Ticektcount as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input_optional -  Account and Loggedinuser
     */
    @GetMapping(path = TICKET_COUNT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ticketSummaryCount(Model model,
                                             @PathVariable(VERSION) String version,
                                             @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                             @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                             @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                             HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> statusResult = new HashSet<>();
        Set<String> businessFunctionCode = new HashSet<>();
        List<String> status = new ArrayList<>();
        Map<String, Object> countResult = new HashMap<>(), errorMap = new HashMap<>();
        Map<String, Object> assignedIndividualResultSet = new HashMap<>(), submitterResultSet = new HashMap<>(), groupResultSet = new HashMap<>();
        String loginID = "", parameter = "", businessFunctionQuery = "", statusQuery = "", accountquery = "", businessFunction = "", userSK = "", entityquery = "", assingedgroup = "";
        String endpointName = TICKET_COUNT;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userSK = user.getUserID();
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {
                if (request.getParameterMap().containsKey(USER_SK)) {
                    userSK = request.getParameter(USER_SK);
                }
                if (request.getParameterMap().containsKey(BUSINESS_FUNCTION_CODE)) {
                    businessFunction = request.getParameter(BUSINESS_FUNCTION_CODE);
                    businessFunctionQuery = OPEN_BRACKET + Arrays.asList(businessFunction.split(","))
                            .stream()
                            .map(s -> BUSINESS_FUNCTION_CODE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    businessFunctionCode = Arrays.asList(businessFunction.split(","))
                            .stream()
                            .collect(Collectors.toSet());

                    entityquery = OPEN_BRACKET + Arrays.asList(businessFunction.split(","))
                            .stream()
                            .map(s -> ENTITYCODE + COLON + ESCAPE_CHARACTER + (s.equalsIgnoreCase(INCIDENTS) ? INC : s) + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                } else {
                    List<String> businessFuncList = SFInterfaceBase.fetchRequiredFieldsForBusinessFunction("CONSOLIDATED_TICKET_DEFAULT_BUSINESSFUNCTION");
                    businessFunctionQuery = OPEN_BRACKET + businessFuncList
                            .stream()
                            .map(s -> BUSINESS_FUNCTION_CODE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    businessFunctionCode = businessFuncList.stream().collect(Collectors.toSet());
                    entityquery += OPEN_BRACKET + businessFuncList
                            .stream()
                            .map(businessfn -> ENTITYCODE + COLON + ESCAPE_CHARACTER + (businessfn.equalsIgnoreCase(INCIDENTS) ? INC : businessfn) + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }

                if (request.getParameterMap().containsKey(STATUS_CODE)) {
                    parameter = request.getParameter(STATUS_CODE);
                    statusQuery = OPEN_BRACKET + Arrays.asList(parameter.split(","))
                            .stream()
                            .map(s -> STATUS_CODE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                    /*statusResult = Arrays.asList(parameter.split(","))
                            .stream()
                            .collect(Collectors.toSet());*/
                }
                List<Map<String, Object>> statusresults = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_STATUS, statusQuery + AND + entityquery, null
                        , new String[]{STATUS}, null, null, TRUE, null, NUM_ZERO, Boolean.FALSE, STATUS, user);
                for (Map<String, Object> statusresult : statusresults) {
                    statusResult.add(statusresult.get(STATUS).toString());
                }

                UserAccountNGroup userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(null, userSK, user);

                if (request.getParameterMap().containsKey(ACCOUNT) && request.getParameterMap().get(ACCOUNT) != null) {
                    accountquery = ACCOUNT_CODE_WITH_COLON + request.getParameter(ACCOUNT) + ESCAPE_CHARACTER;
                } else if (!userAccountAGroup.getUserAccounts().isEmpty()) {
                    accountquery = OPEN_BRACKET + userAccountAGroup.getUserAccounts()
                            .stream()
                            .map(account -> MASTER_ACCOUNT + COLON + ESCAPE_CHARACTER + account.get(MASTER_ACCOUNT) + ESCAPE_CHARACTER)//ACCOUNT + COLON + account.get(ACCOUNT) + OR +
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }

                if (!userAccountAGroup.getUserGroups().isEmpty()) {
                    assingedgroup = OPEN_BRACKET + userAccountAGroup.getUserGroups()
                            .stream()
                            .map(usergroup -> ASSIGNED_GROUPNAME_WITH_COLON + usergroup.get(GROUP_NAME).toString() + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }


                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FULL_TEXT_SEARCH, accountquery + AND
                        + businessFunctionQuery + AND + statusQuery
                        + ASSIGNED_INDIVIDUALSK_WITH_COLON + userSK + ESCAPE_CHARACTER + AND + NOT + ASSIGNED_GROUP_NAME + COLON + NULL_VALUE, null, new String[]{TICKET, STATUS_DESCRIPTION, STATUS_CODE, BUSINESS_FUNCTION_CODE}, null, null, TRUE, null, NUM_ZERO, Boolean.FALSE, TICKET, user);
                assignedIndividualResultSet = SFInterfaceServices.ticketSummaryCount(result, statusResult);

                result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FULL_TEXT_SEARCH, accountquery + AND
                                + businessFunctionQuery + AND
                                + statusQuery + AND + OPEN_BRACKET + RAISED_BY_User_sk + COLON + userSK
                                + OR + ALTERNATE_User_sk + COLON + userSK + CLOSE_BRACKET
                        , null, new String[]{TICKET, STATUS_DESCRIPTION, STATUS_CODE, BUSINESS_FUNCTION_CODE}, null, null, TRUE, null, NUM_ZERO, Boolean.FALSE, TICKET, user);
                submitterResultSet = SFInterfaceServices.ticketSummaryCount(result, statusResult);//+ AND + NOT + ASSIGNED_GROUP_NAME + COLON + NULL_VALUE
                assingedgroup = assingedgroup.isEmpty() ? "" : AND + assingedgroup;
                if (!assingedgroup.isEmpty())
                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FULL_TEXT_SEARCH, accountquery + AND
                                    + businessFunctionQuery + AND + statusQuery
                                    + AND + NOT + EMPTY + ASSIGNED_INDIVIDUALSK + COLON + userSK + assingedgroup, null,
                            new String[]{TICKET, STATUS_DESCRIPTION, STATUS_CODE, BUSINESS_FUNCTION_CODE}, null, null, TRUE, null, NUM_ZERO, Boolean.FALSE, TICKET, user);
                else
                    result = new ArrayList<>();
                groupResultSet = SFInterfaceServices.ticketSummaryCount(result, statusResult);

                countResult = SFInterfaceServices.fetchTicketCounts(businessFunctionCode, assignedIndividualResultSet, groupResultSet, submitterResultSet);

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(countResult, HttpStatus.OK);
    }

    /*  *//**
     * author - Vani
     * Fetch TicketUpdateTypes of the loggedinuser
     *
     * @return - Returns the TicketUpdateTypes based on account as Json response, if the resultset is empty return the default values
     * @header - Requires an valid accesstoken or atoken
     * @input_optional -  Account and Loggedinuser
     * removing as the same functionality available in common endpoint
     *//*
    @GetMapping(path = WORK_NOTES_UPDATE_TYPES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity ticketUpdateTypes(Model model,
                                            @PathVariable(VERSION) String version,
                                            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> resultMap = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>(), params1 = request.getParameterMap();
        CustomWhooshModel customModel = null;
        String loginID = "";
        Map<String, Object> errorMap = new HashMap<>();
        String endpointName = WORK_NOTES_UPDATE_TYPES;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                    params.put(stringEntry.getKey(), stringEntry.getValue());
                }
                customModel = SFInterfaceBase.generateWhooshModel(params,user);
                resultMap = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_WORKNOTES_UPDATE_TYPE, customModel.getQuery(), customModel.getSort(),
                        customModel.getRequiredColumns(), customModel.getSortfields(), customModel.getSortorder(), customModel.getDistinct(),
                        customModel.getFieldaliass(), customModel.getCount(), Boolean.FALSE, customModel.getDistinctfield(), customModel.getPageno(), Boolean.FALSE);
                if (!(resultMap.size() > NUM_ZERO)) {
                    params.put(ACCOUNT, new String[]{NULL1});
                    customModel = SFInterfaceBase.generateWhooshModel(params,user);
                    resultMap = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_WORKNOTES_UPDATE_TYPE, customModel.getQuery(),
                            customModel.getSort(), customModel.getRequiredColumns(), customModel.getSortfields(), customModel.getSortorder(),
                            customModel.getDistinct(), customModel.getFieldaliass(), customModel.getCount(), Boolean.FALSE,
                            customModel.getDistinctfield(), customModel.getPageno(), Boolean.FALSE);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }*/

    /**
     * author - Vani
     * The Function is used to get sk for the corresponding code
     *
     * @return - Returns sk as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input - Receives an multipart request and validates the input field.
     */
    @GetMapping(path = EQUIVALENT_SK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getEquivalentSK(Model model,
                                          @PathVariable(VERSION) String version,
                                          @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                          @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                          @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                          HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        Map<String, String[]> resultMap = new HashMap<>();
        String loginID = "", dynamicQuery = "";
        Map<String, Object> errorMap = new HashMap<>();
        String endpointName = EQUIVALENT_SK;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {
                if (request.getParameterMap().containsKey(SELECTED_FIELDS) && request.getParameterMap().containsKey(FORM_NAME) && request.getParameterMap().containsKey(FILTER_CONDITION)
                        && request.getParameterMap().containsKey(FILTER_CONDITION_VALUE)) {
                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }
                    if (!(params.get(FILTER_CONDITION)[NUM_ZERO].contains(COMMA))
                            && !(params.get(FILTER_CONDITION_VALUE)[NUM_ZERO].contains(COMMA))) {
                        dynamicQuery = SELECT
                                + params.get(SELECTED_FIELDS)[NUM_ZERO]
                                + FROM_WITH_SPACE
                                + params.get(FORM_NAME)[NUM_ZERO]
                                + WHERE
                                + params.get(FILTER_CONDITION)[NUM_ZERO]
                                + EQUAL_WITH_SINGLE_QUOTE
                                + params.get(FILTER_CONDITION_VALUE)[NUM_ZERO]
                                + SINGLE_QUOTE;
                        result = runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), dynamicQuery);
                        if (result.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                            return new ResponseEntity(errorMap, HttpStatus.OK);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.CANNOTACCEPTMULTIPLEPARAMETER", null, endpointName);
                        String message = (String) errorMap.get(MESSAGE);
                        errorMap.put(MESSAGE, FILTER_CONDITION + OR + FILTER_CONDITION_VALUE + message);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYPARAMETERFOREQUIVALENTSK", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vani
     * ModifiedBy - Vinodhini
     * The Function is used to download attachments for the corresponding filename
     *
     * @return -
     * @header - Requires an valid accesstoken or aToken
     * @input - Receives an HttpServletRequest and validates the input field.
     */
    @GetMapping(path = DOWNLOAD_ATTACHMENTS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity downloadAttachments(Model model,
                                              @PathVariable(VERSION) String version,
                                              @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                              @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                              @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.downloadAttachment(request.getParameter(REFRESH_TOKEN), request.getParameter(ACCESS_TOKEN), request
                    , DOWNLOAD_ATTACHMENTS);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, DOWNLOAD_ATTACHMENTS, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, DOWNLOAD_ATTACHMENTS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vani
     * The Function is used to store attachments in fileserver
     *
     * @return - Returns success message as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input - Receives an multipart request and validates the input field.
     */
    @PostMapping(path = FILE_UPLOAD_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveAttachmentsinFileServer(@PathVariable(VERSION) String version,
                                                      @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                                      @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                                      @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
                                                      HttpServletRequest request,
                                                      org.springframework.web.multipart.MultipartHttpServletRequest multiPartRequest) throws Exception {

        Map<String, String[]> ticketFields = request.getParameterMap();

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.saveAttachments(multiPartRequest, ticketFields, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), request, FILE_UPLOAD);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, FILE_UPLOAD, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, FILE_UPLOAD), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch INC SecureInfo Ticket details
     *
     * @return - Returns the SecureInfo as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket
     */
    @GetMapping(path = INCIDENT_SECUREINFO_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchINCSecureInfoDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), endpointResultSet = new ArrayList<>();
        String loginID = "";
        String endpointName = INCIDENT_SECUREINFO_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                params1 = request.getParameterMap();
                if (params1.containsKey(TICKET) || params1.containsKey(ACCOUNT) || params1.containsKey(USER_SK)) {
                    for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                        params.put(stringEntry.getKey(), stringEntry.getValue());
                    }
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    params.put(TYPE, new String[]{ATTACHMENT_TYPE});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);
                    List<String> endpoints = new ArrayList<>();

                    endpoints.add(INDEXERNAME_FETCH_INC_SECUREINFO);
                    endpoints.add(INDEXERNAME_FETCH_INC_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_INC_SECUREINFO_ADT);

                    for (String endpoint : endpoints) {
                        result = null;
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_INC_ATTACHMENTS)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            }
                            resultMap.put(endpoint, result);
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }


    /**
     * author - Vinodhini
     * <p>
     * Fetch SR SecureInfo Ticket details
     *
     * @return - Returns the SecureInfo as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket
     */
    @GetMapping(path = SR_SECUREINFO_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchSRSecureInfoDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), endpointResultSet = new ArrayList<>();
        String loginID = "";
        String endpointName = SR_SECUREINFO_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {

                params1 = request.getParameterMap();
                if (params1.containsKey(TICKET) || params1.containsKey(ACCOUNT) || params1.containsKey(USER_SK)) {

                    for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                        params.put(stringEntry.getKey(), stringEntry.getValue());
                    }
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    params.put(TYPE, new String[]{ATTACHMENT_TYPE});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_SR_SECUREINFO);
                    endpoints.add(INDEXERNAME_FETCH_SR_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_SR_SECUREINFO_ADT);

                    for (String endpoint : endpoints) {
                        result = null;
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SR_ATTACHMENTS)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);

                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            }
                            resultMap.put(endpoint, result);
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch SRTask SecureInfo Ticket details
     *
     * @return - Returns the SecureInfo as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket
     */
    @GetMapping(path = SRTASK_SECUREINFO_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchSRTSecureInfoDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), endpointResultSet = new ArrayList<>();
        String loginID = "";
        String endpointName = SRTASK_SECUREINFO_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));

            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {

                params1 = request.getParameterMap();
                if (params1.containsKey(TICKET) || params1.containsKey(ACCOUNT) || params1.containsKey(USER_SK)) {

                    for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                        params.put(stringEntry.getKey(), stringEntry.getValue());
                    }
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    params.put(TYPE, new String[]{ATTACHMENT_TYPE});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_SRT_SECUREINFO);
                    endpoints.add(INDEXERNAME_FETCH_SRT_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_SRT_SECUREINFO_ADT);

                    for (String endpoint : endpoints) {
                        result = null;
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_SRT_ATTACHMENTS)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);

                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            }
                            resultMap.put(endpoint, result);
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNTORUSERSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * Create Tickets from E-Mail based on pattern/Set as Triage Emails
     *
     * @return - Returns the TicketNumber
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires valid valid email sent(emailaddress) available in our system
     */
    @PostMapping(path = "/{version}/CreateEMailTickets")
    public ResponseEntity createEMailTickets(@RequestBody MailModel mailModel,
                                             @PathVariable(VERSION) String version
            , HttpServletRequest request) throws IOException {

        MailObject mailObject = mailModel.getMailObject();
        String ticketid = "", ticketnumber = "", tickettype = null;
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, String[]> endpointname = new HashMap<>();
        String endpoint_Name = "CreateEMailTickets";
        SFInterfaceLoggerBase.log(LOGGER, endpoint_Name, mailModel.toString());

        endpointname.put("INCIDENTS", new String[]{"UpdateINCEmail", "INC_Email_sk"});
        endpointname.put("SR", new String[]{"UpdateSREmail", "SR_Email_sk"});
        endpointname.put("SRT", new String[]{"UpdateSRTaskEmail", "SRT_Email_sk"});
        // configureproxy();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            List<Map<String, Object>> accounts = fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, ACCOUNT + COLON + mailModel.getClient(), null,
                    new String[]{ACCOUNT_SK}, null, null, null, null, 0, false, null, 1, null, user);
            if (accounts.size() > 0) {
                mailModel.setClient(accounts.get(0).get(ACCOUNT_SK).toString());
            }
            Map<String, Object> systemprofile = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, LOGIN_ID + COLON + ESCAPE_CHARACTER + mailusername + ESCAPE_CHARACTER, null
                    , new String[]{MASTER_ACCOUNT_SK, ACCOUNT_SK, MASTER_ACCOUNT, ACCOUNT, USER_SK}, null, null, null,
                    null, 0, false, null, 1, null, user).get(0);
            String systemusersk = systemprofile.get(USER_SK).toString();
            if (mailObject.getMailid() != null) {
                List<Map<String, Object>> messageidexists = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()).queryForList(String.format("EXEC API_isEntityIDExist '%s','%s'", mailObject.getMailid(), "Mail"));
                for (Map<String, Object> stringObjectMap : messageidexists) {
                    if (stringObjectMap.get("TicketNumber") != null || stringObjectMap.get("Ticket_sk") != null || stringObjectMap.get("TicketType") != null) {
                        ticketid = stringObjectMap.get("Ticket_sk") != null ? stringObjectMap.get("Ticket_sk").toString() : null;
                        ticketnumber = stringObjectMap.get("TicketNumber") != null ? stringObjectMap.get("TicketNumber").toString() : null;
                        tickettype = stringObjectMap.get("TicketType") != null ? stringObjectMap.get("TicketType").toString() : null;
                    }
                }
                if (tickettype != null) {//(ticketid != null || ticketnumber != null) &&
                    resultMap = SFInterfaceServices.CreateEmailTicket(request, endpointname.get(tickettype)[0],
                            SFInterfaceServices.fetchemailparams(mailObject, ticketid, ticketnumber, tickettype, systemusersk, mailModel.getClient(), user), mailObject, user);//2. insertemail insert record into db with ticketid wand tickettype
                } else {
                    //3. validate ticket against inc/sr/srt TicketID
                    List<Map<String, Object>> ticketidexist = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()).queryForList(String.format("EXEC API_isEntityIDExist '%s','%s'", mailModel.getTicketID(), "Ticket"));
                    for (Map<String, Object> stringObjectMap : ticketidexist) {
                        if (stringObjectMap.get("TicketNumber") != null && stringObjectMap.get("Ticket_sk") != null) {
                            ticketid = stringObjectMap.get("Ticket_sk") != null ? stringObjectMap.get("Ticket_sk").toString() : null;
                            ticketnumber = stringObjectMap.get("TicketNumber") != null ? stringObjectMap.get("TicketNumber").toString() : null;
                            tickettype = stringObjectMap.get("TicketType") != null ? stringObjectMap.get("TicketType").toString() : null;
                        }
                    }

                    if ((ticketid != null || ticketnumber != null) && tickettype != null) {
                        resultMap = SFInterfaceServices.CreateEmailTicket(request, endpointname.get(tickettype)[0],
                                SFInterfaceServices.fetchemailparams(mailObject, ticketid, ticketnumber, tickettype, systemusersk, mailModel.getClient(), user), mailObject, user);//atach ticketid to email and insert with ticketid with tickettype in ticketemail
                    } else {
                        //check profile for valid user and given client
                        List<Map<String, Object>> profilerecords = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, "EmailAddress:\"" + mailObject.getFrom() + "\""
                                        + AND + OPEN_BRACKET + ACCOUNT_SK + COLON + mailModel.getClient() + OR + MASTER_ACCOUNT_SK + COLON
                                        + mailModel.getClient() + CLOSE_BRACKET, null
                                , new String[]{MASTER_ACCOUNT_SK, ACCOUNT_SK, MASTER_ACCOUNT, ACCOUNT, USER_SK}, null, null, null,
                                null, 0, false, null, 1, null, user);
                        if (profilerecords.size() > 0 &&
                                (mailModel.getTicketType() != null
                                        && (mailModel.getTicketType().equalsIgnoreCase(INCIDENTS)
                                        || mailModel.getTicketType().equalsIgnoreCase(SR)))) {
                            //create ticket call workflow api with incmail object
                            Map<String, Object> ticketfields = new HashMap<>();
                            ticketfields.put(MASTER_ACCOUNT, profilerecords.get(0).get(MASTER_ACCOUNT_SK));
                            ticketfields.put(MASTER_ACCOUNT_CODE, profilerecords.get(0).get(MASTER_ACCOUNT));
                            ticketfields.put(ACCOUNT, profilerecords.get(0).get(ACCOUNT_SK));
                            ticketfields.put(SUMMARY, mailObject.getSubject());
                            ticketfields.put(DESCRIPTION, (String) mailObject.getMailbody());
                            ticketfields.put(OFFLINE_UNIQUE_ID, (String) mailObject.getOfflineUniqueID());
                            mailObject.setStatussk("L");
                            resultMap = SFInterfaceServices.CreateEmailTicket(request, endpointname.get(mailModel.getTicketType())[0],
                                    SFInterfaceServices.fetchemailparams(mailObject, null, null, mailModel.getTicketType(), profilerecords.get(0).get(USER_SK).toString(), mailModel.getClient(), user), mailObject, user);
                            ticketfields.put(endpointname.get(mailModel.getTicketType())[1], resultMap.get("EmailSK"));
                            resultMap = SFInterfaceBase.createTicketFromRflow(ticketfields, mailModel.getTicketType(), null,
                                    null, aTokenValue, profilerecords.get(0).get(USER_SK).toString(), endpoint_Name, user);
                        } else {
                            resultMap = SFInterfaceServices.CreateEmailTicket(request, endpointname.get(INCIDENTS)[0],
                                    SFInterfaceServices.fetchemailparams(mailObject, ticketid, ticketnumber, INCIDENTS, systemusersk, mailModel.getClient(), user), mailObject, user); //removed insertion based on businesstype as it is an triage mail as user not found
                        }
                    }
                }
            } else {
                resultMap = SFInterfaceServices.getInfoMap("VALIDATION.MAILID.NOTEXIST", null, endpoint_Name);
                return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpoint_Name, e);
            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INTERNALSERVERERROR", null, endpoint_Name);
            return new ResponseEntity<>(resultMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }


    @RequestMapping(path = "/{version}/" + USERPREFERENCE, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity USERPREFERENCE(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketField,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, Object> endPointDomain = new HashMap<>();
        String endpointName = USERPREFERENCE;
        Set<String> defaultjson = new HashSet<>();
        Map<String, Object> errorMap = new HashMap<>();
        EndpointValidationModel finalEndpointValidationModel = new EndpointValidationModel();
        User user = new User();
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (user.getAuthUserName() != null) {
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                finalEndpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketField, request.getParameterMap());

                //convert accountgroup_sk to indivual user_sk
                if (finalEndpointValidationModel.getRequestparametermap().containsKey(ACCOUNT_GROUP_SK)) {
                    Map<String, String[]> params = new HashMap<>();
                    params.put(ACCOUNT_GROUP_SK, finalEndpointValidationModel.getRequestparametermap().get(ACCOUNT_GROUP_SK));
                    params.put(STATUS, new String[]{ONE});
                    String query = generateWhooshQueryDynamic(params);
                    response = fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS
                            , query, null, new String[]{USER_SK},
                            null, null, null, null, 0, user);
                    String[] user_sk = new String[]{};
                    //User_sk from Groups input
                    Set<String> uservalue_sk = response.parallelStream()
                            .map(stringObjectMap -> (String) stringObjectMap.get(USER_SK))
                            .collect(Collectors.toSet());
                    //User_sk input value
                    if (finalEndpointValidationModel.getRequestparametermap().containsKey(USER_SK)) {
                        uservalue_sk.addAll(new HashSet<String>(Arrays.asList(finalEndpointValidationModel.getRequestparametermap().get(USER_SK))));
                    }
                    user_sk = uservalue_sk.toArray(new String[0]);
                    finalEndpointValidationModel.getRequestparametermap().remove(ACCOUNT_GROUP_SK);
                    finalEndpointValidationModel.getRequestparametermap().put(USER_SK, user_sk);
                }
                if (finalEndpointValidationModel.getRequestparametermap().containsKey(DEFAULT_JSON)) {
                    defaultjson = convertArrayToSet(finalEndpointValidationModel.getRequestparametermap().get(DEFAULT_JSON));
                }

                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(finalEndpointValidationModel.getRequestparametermap(), Boolean.TRUE
                        , endPointDomain.get(ENDPOINT_SK).toString(), user);
                SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version, finalEndpointValidationModel.getRequestparametermap()
                                .get(TICKET) != null ? finalEndpointValidationModel.getRequestparametermap().get(TICKET)[0].toString() : "",
                        user.getUserID(), user.getAuthUserName(), null);
                if (customWhooshModel.getParams().isEmpty() &&
                        !finalEndpointValidationModel.isEmptyParams_Whitelisted()) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYPARAMETERS", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                }
                response = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                        , customWhooshModel.getQuery()
                        , customWhooshModel.getSort()
                        , customWhooshModel.getRequiredColumns()
                        , customWhooshModel.getSortfields()
                        , customWhooshModel.getSortorder()
                        , customWhooshModel.getDistinct()
                        , customWhooshModel.getFieldaliass()
                        , customWhooshModel.getCount()
                        , Boolean.FALSE
                        , customWhooshModel.getDistinctfield()
                        , customWhooshModel.getPageno()
                        , Boolean.FALSE, user);

                Set<String> typess = new HashSet<>();
                List<Map<String, Object>> finalResponse = response;
                if (defaultjson != null &&
                        finalEndpointValidationModel.getRequestparametermap().containsKey(TYPE) &&
                        defaultjson.parallelStream().anyMatch(TRUE::equalsIgnoreCase)) {

                    typess.addAll(convertArrayToSet(finalEndpointValidationModel.getRequestparametermap().get(TYPE)));

                    Map<String, String[]> params = new HashMap<>();
                    params.put(USER_SK, finalEndpointValidationModel.getRequestparametermap().get(USER_SK));
                    String query = generateWhooshQueryDynamic(params);
                    List<Map<String, Object>> systemprofiles = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE,
                            query,
                            null, new String[]{MASTER_ACCOUNT_SK, ACCOUNT_SK, MASTER_ACCOUNT, ACCOUNT, USER_SK},
                            null, null, null,
                            null, 0, Boolean.FALSE, null, 1, Boolean.FALSE, user);

                    EndpointValidationModel finalEndpointValidationModel1 = finalEndpointValidationModel;
                    User finalUser = user;
                    systemprofiles.stream().forEach(systemprofile -> {
                        String userAccount = systemprofile.get(ACCOUNT_SK).toString();
                        String userMasterAccount = systemprofile.get(MASTER_ACCOUNT_SK).toString();
                        String user_sk = systemprofile.get(USER_SK).toString();
                        typess.stream().forEach(type -> {
                            List<Map<String, Object>> tempresponse = new ArrayList<>();
                            if (!finalResponse.parallelStream()
                                    .filter(stringObjectMap -> stringObjectMap.get(TYPE) != null
                                            && stringObjectMap.get(USER_SK) != null
                                            && stringObjectMap.get(USER_SK).toString()
                                            .equalsIgnoreCase(user_sk)
                                            && stringObjectMap.get(TYPE).toString()
                                            .equalsIgnoreCase(type)
                                    )
                                    .anyMatch(stringObjectMap -> stringObjectMap.get(TYPE).toString().equalsIgnoreCase(type))
                            ) {
                                Map<String, String[]> params1 = new HashMap<>();
                                params1.put(TYPE, new String[]{type});
                                params1.put(ACCOUNT_SK, new String[]{userAccount, userMasterAccount});
                                params1.put(USER_SK, new String[]{NULL_VALUE});
                                String tempquery = SFInterfaceBase.generateWhooshQueryDynamic(params1);
                                tempresponse = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERPREFERENCE, tempquery
                                        , null, customWhooshModel.getRequiredColumns(), null, null, null, null, 0, false, null, null, Boolean.FALSE, finalUser);
                                tempresponse = tempresponse.
                                        parallelStream()
                                        .map(stringObjectMap -> {
                                            stringObjectMap.put(USER_SK, user_sk);
                                            return stringObjectMap;
                                        })
                                        .collect(Collectors.toList());
                                finalResponse.addAll(tempresponse);
                            }
                        });
                    });
                }
                response = finalResponse;
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return SFInterfaceServices.returnResponsewithValidation(endpointName, response, finalEndpointValidationModel.getRequestparametermap()
                , Boolean.FALSE, user);
    }

    /**
     * author - Vani
     * <p>
     * Fetch Document details
     *
     * @return - Returns the Documents along with attachments as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Account or Repository_sk
     */
    @GetMapping(path = DOCUMENT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchDocumentDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params1 = new HashMap<>(), params = new HashMap<>(), tempParams1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), endpointResultSet = new ArrayList<>();
        String loginID = "";
        String endpointName = DOCUMENT_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                params1 = request.getParameterMap();
                if (params1.containsKey(ACCOUNT) || params1.containsKey(REPOSITORY_SK)) {
                    for (Map.Entry<String, String[]> stringEntry : params1.entrySet()) {
                        params.put(stringEntry.getKey(), stringEntry.getValue());
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    params.remove(ACCOUNT);
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_DOCUMENTS);
                    endpoints.add(INDEXERNAME_FETCH_DOCUMENTATTACHMENTS);

                    for (String endpoint : endpoints) {
                        result = null;
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_DOCUMENTATTACHMENTS) && params.get(REPOSITORY_SK) != NULL) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_DOCUMENTS)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery);
                            }
                            resultMap.put(endpoint, result);
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.DOCUMENTSREQUIREDPARAMETER", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vani
     * Fetch User associated groups based on admin or normal user
     *
     * @return - Returns the GroupDetails as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  User or account
     */
    @GetMapping(path = USER_ACCOUNTGROUPS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity userAccountGroups(Model model,
                                            @PathVariable(VERSION) String version,
                                            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
                                            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
                                            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>();
        String loginID = "", usersk = "";
        String account = "";

        String endpointName = USER_ACCOUNTGROUPS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            loginID = user.getAuthUserName();

            if (StringUtils.isNotBlank(loginID)) {
                usersk = user.getUserID();
                for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                    if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    } else {
                        params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                    }
                }
                if (params.containsKey(USER_SK)) {
                    String groupQuery = "";
                    UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
                    if (params.containsKey(USER_SK)) {
                        usersk = params.get(USER_SK)[NUM_ZERO];
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    if (params.containsKey(ACCOUNT) && StringUtils.isNotBlank(params.get(ACCOUNT)[NUM_ZERO])) {
                        account = params.get(ACCOUNT)[NUM_ZERO];
                    }

                    if (StringUtils.isNotBlank(usersk)) {
                        userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(null, usersk, user);
                    } else {
                        userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup(loginID, user.getUserID(), user);
                    }

                    if (userAccountNGroup.getAdministrator() || userAccountNGroup.getCSS()) {
                        if (params.containsKey(ACCOUNT) && StringUtils.isNotBlank(account)) {
                            result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_MANAGEACCOUNTGROUPS, ACCOUNT + COLON + account
                                    , customWhooshModel.getSort(), customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields()
                                    , customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass()
                                    , customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield()
                                    , customWhooshModel.getPageno(), Boolean.FALSE, user);
                        } else {
                            result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_MANAGEACCOUNTGROUPS, DOUBLE_ASTERISK
                                    , customWhooshModel.getSort(), customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields()
                                    , customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass()
                                    , customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield()
                                    , customWhooshModel.getPageno(), Boolean.FALSE, user);
                        }
                    } else {
                        result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS, customWhooshModel.getQuery()
                                , customWhooshModel.getSort(), customWhooshModel.getRequiredColumns(), customWhooshModel.getSortfields()
                                , customWhooshModel.getSortorder(), customWhooshModel.getDistinct(), customWhooshModel.getFieldaliass()
                                , customWhooshModel.getCount(), Boolean.FALSE, customWhooshModel.getDistinctfield()
                                , customWhooshModel.getPageno(), Boolean.FALSE, user);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYUSERSK", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (result == null) {
            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDCOLUMN", null, endpointName);
            return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
        } else if (result.size() > NUM_ZERO) {
            return new ResponseEntity<Object>(result, HttpStatus.OK);
        } else {
            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
            return new ResponseEntity(errorMap, HttpStatus.OK);
        }
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch ChangeRequest details
     *
     * @return - Returns the CR Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = CHANGE_REQUEST_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchChangeRequestDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), crDesignboard = new ArrayList<>();

        String loginID = "", businessfnsk = "", ticket_sk = "", platformsk = "", crcategorysk = "", crsystemsk = "", pendingreasonsk = "";
        String endpointName = CHANGE_REQUEST_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    String[] requiredColumns = customWhooshModel.getRequiredColumns();

                    List<String> endpoints = new ArrayList<>();

                    endpoints.add(INDEXERNAME_FETCH_CR_DESIGNBOARD);
                    endpoints.add(INDEXERNAME_FETCH_CHANGE_REQUEST1);
                    if (isExist(requiredColumns, QUESTIONS))
                        endpoints.add(QUESTIONS1);
                    if (isExist(requiredColumns, CR_WORKNOTES))
                        endpoints.add(INDEXERNAME_FETCH_CR_WORKNOTES);
                    if (isExist(requiredColumns, CR_ATTACHMENTS))
                        endpoints.add(INDEXERNAME_FETCH_CR_ATTACHMENTS);
                    if (isExist(requiredColumns, CR_ASSESTS))
                        endpoints.add(INDEXERNAME_FETCH_CR_ASSETS);
                    if (isExist(requiredColumns, CR_GROUPS))
                        endpoints.add(INDEXERNAME_FETCH_CR_GROUPS);
                    if (isExist(requiredColumns, CR_BUILDINGS))
                        endpoints.add(INDEXERNAME_FETCH_CR_BUILDINGS);
                    if (isExist(requiredColumns, CR_LOCATIONS))
                        endpoints.add(INDEXERNAME_FETCH_CR_LOCATIONS1);
                    if (isExist(requiredColumns, CR_RISKS))
                        endpoints.add(INDEXERNAME_FETCH_CR_RISKS1);
                    if (isExist(requiredColumns, COM_RESPONSES))
                        endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);
                    if (isExist(requiredColumns, COM_REFRENCES))
                        endpoints.add(INDEXERNAME_FETCH_COM_REFERENCES);
                    if (isExist(requiredColumns, INDEXERNAME_FETCH_CR_APPROVALS))
                        endpoints.add(INDEXERNAME_FETCH_CR_APPROVALS1);
                    if (isExist(requiredColumns, CR_SCHEDULES))
                        endpoints.add(INDEXERNAME_FETCH_CR_SCHEDULES1);
                    if (isExist(requiredColumns, CR_Tasks))
                        endpoints.add(INDEXERNAME_FETCH_CR_TASKS1);
                    if (isExist(requiredColumns, CR_IMPACTED))
                        endpoints.add(INDEXERNAME_FETCH_CR_IMPACTED1);
                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();
                        List<Map<String, Object>> assignmentResult = new ArrayList<>(),
                                taskList = new ArrayList<>(),
                                approvalStatus = new ArrayList<>(),
                                stakeholders = new ArrayList<>(),
                                crtworkNotes = new ArrayList<>(),
                                crtAttachments = new ArrayList<>(),
                                designBoard = new ArrayList<>(),
                                meetingParticipants = new ArrayList<>(),
                                actionItems = new ArrayList<>();
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET_SK, new String[]{ticket_sk});
                                    parameter.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_ASSETS) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_GROUPS)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_BUILDINGS) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_LOCATIONS1)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_IMPACTED1) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_RISKS1) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_REFERENCES)
                            ) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                    //meeting participants
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CR_MEETING_PARTICIPANTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(SCHEDULE_SK, new String[]{schedule_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CR_MEETING_PARTICIPANTS, assignmentResult);
                                        meetingParticipants.add(res);
                                    }
                                    //actionitems
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_ACTIONITEMS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(ENTITYNAME, new String[]{CR_SCHEDULES_ENTITYNAME});
                                        parameter1.put(ENTITY_sk, new String[]{schedule_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(ACTION_ITEMS, assignmentResult);
                                        actionItems.add(res);
                                    }
                                    result = actionItems;
                                    //DesingBoard schedules
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_SCHEDULES_ENTITYNAME
                                                , schedule_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                    //approval users
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CR_APPROVAL_STATUS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String Approval_sk = (String) res.get(APPROVAL_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(APPROVAL_SK, new String[]{Approval_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CR_APPROVAL_STATUS, assignmentResult);
                                        approvalStatus.add(res);
                                    }
                                    result = approvalStatus;
                                    //approval designboard
                                    for (Map<String, Object> res : result) {
                                        String Approval_sk = (String) res.get(APPROVAL_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_APPROVALS_ENTITYNAME
                                                , Approval_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(CR_SK, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);

                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ASSIGNMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_ASSIGNMENTS, assignmentResult);
                                        taskList.add(res);
                                    }
                                    result = taskList;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_STAKE_HOLDERS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_STAKE_HOLDERS, assignmentResult);
                                        stakeholders.add(res);
                                    }
                                    result = stakeholders;
                                    //actionitems
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_ACTIONITEMS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String crt_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(ENTITYNAME, new String[]{CR_TASKS_ENTITYNAME});
                                        parameter1.put(ENTITY_sk, new String[]{crt_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(ACTION_ITEMS, assignmentResult);
                                        actionItems.add(res);
                                    }
                                    result = actionItems;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ATTACHMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_ATTACHMENTS, assignmentResult);
                                        crtAttachments.add(res);
                                    }
                                    result = crtAttachments;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_WORKNOTES, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_WORKNOTES, assignmentResult);
                                        crtworkNotes.add(res);
                                    }
                                    result = crtworkNotes;
                                    //designboard records
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_TASKS_ENTITYNAME
                                                , CRT_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(QUESTIONS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    if (platformsk != null) {
                                        parameter.put(ENTITY_sk, new String[]{platformsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_PLATFORM});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (crcategorysk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{crcategorysk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_CR_CATEGORY});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (crsystemsk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{crsystemsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_CR_SYSTEM});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (pendingreasonsk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{pendingreasonsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_PENDINGREASON});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                }
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , null);
                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CHANGE_REQUEST1)) {
                                resultMap.put(CHANGE_REQUEST, result);
                                if (result.size() > NUM_ZERO) {
                                    businessfnsk = result.get(0).get(BUSINESS_FUNCTION_SK).toString();
                                    ticket_sk = result.get(0).get(TICKET_SK).toString();
                                    platformsk = result.get(0).get(PLATFORM_SK) != null ? result.get(0).get(PLATFORM_SK).toString() : null;
                                    crcategorysk = result.get(0).get(CR_CATEGORY_SK) != null ? result.get(0).get(CR_CATEGORY_SK).toString() : null;
                                    crsystemsk = result.get(0).get(CR_SYSTEM_SK) != null ? result.get(0).get(CR_SYSTEM_SK).toString() : null;
                                    pendingreasonsk = result.get(0).get(CHANGE_REASON_SK) != null ? result.get(0).get(CHANGE_REASON_SK).toString() : null;
                                }
                            } else if (endpoint.equalsIgnoreCase(QUESTIONS1)) {
                                resultMap.put(QUESTIONS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_LOCATIONS1)) {
                                resultMap.put(CR_LOCATIONS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                resultMap.put(CR_SCHEDULES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                resultMap.put(CR_Tasks, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_RISKS1)) {
                                resultMap.put(CR_RISKS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_IMPACTED1)) {
                                resultMap.put(CR_IMPACTED, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                resultMap.put(COM_RESPONSES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_REFERENCES)) {
                                resultMap.put(COM_REFRENCES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                resultMap.put(INDEXERNAME_FETCH_CR_APPROVALS, result);
                            } else if (endpoint.equalsIgnoreCase(CR_DESIGNBOARD)) {
                                crDesignboard = result;
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch ChangeRequest details
     *
     * @return - Returns the CR Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = "/{version}/ChangeRequestDetails1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchChangeRequest3(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), crDesignboard = new ArrayList<>();

        String loginID = "", businessfnsk = "", ticket_sk = "", platformsk = "", crcategorysk = "", crsystemsk = "", pendingreasonsk = "";
        String endpointName = CHANGE_REQUEST_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    String[] requiredColumns = customWhooshModel.getRequiredColumns();

                    List<String> endpoints = new ArrayList<>();

                    endpoints.add(INDEXERNAME_FETCH_CR_DESIGNBOARD);
                    endpoints.add(INDEXERNAME_FETCH_CHANGE_REQUEST1);
                  /*  if (isExist(requiredColumns, QUESTIONS))
                        endpoints.add(QUESTIONS1);
                    if (isExist(requiredColumns, CR_WORKNOTES))
                        endpoints.add(INDEXERNAME_FETCH_CR_WORKNOTES);

                    if (isExist(requiredColumns, CR_ASSESTS))
                        endpoints.add(INDEXERNAME_FETCH_CR_ASSETS);
                    if (isExist(requiredColumns, CR_GROUPS))
                        endpoints.add(INDEXERNAME_FETCH_CR_GROUPS);
                    if (isExist(requiredColumns, CR_BUILDINGS))
                        endpoints.add(INDEXERNAME_FETCH_CR_BUILDINGS);
                    if (isExist(requiredColumns, CR_LOCATIONS))
                        endpoints.add(INDEXERNAME_FETCH_CR_LOCATIONS1);
                    if (isExist(requiredColumns, CR_RISKS))
                        endpoints.add(INDEXERNAME_FETCH_CR_RISKS1);
                    if (isExist(requiredColumns, COM_RESPONSES))
                        endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);
                    if (isExist(requiredColumns, COM_REFRENCES))
                        endpoints.add(INDEXERNAME_FETCH_COM_REFERENCES);
                    if (isExist(requiredColumns, INDEXERNAME_FETCH_CR_APPROVALS))
                        endpoints.add(INDEXERNAME_FETCH_CR_APPROVALS1);
                    if (isExist(requiredColumns, CR_SCHEDULES))
                        endpoints.add(INDEXERNAME_FETCH_CR_SCHEDULES1);
                    if (isExist(requiredColumns, CR_Tasks))
                        endpoints.add(INDEXERNAME_FETCH_CR_TASKS1);*/
                    if (isExist(requiredColumns, CR_IMPACTED))
                        endpoints.add(INDEXERNAME_FETCH_CR_IMPACTED1);
                    if (isExist(requiredColumns, CR_ATTACHMENTS))
                        endpoints.add(INDEXERNAME_FETCH_CR_ATTACHMENTS);
                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();
                        List<Map<String, Object>> assignmentResult = new ArrayList<>(),
                                taskList = new ArrayList<>(),
                                approvalStatus = new ArrayList<>(),
                                stakeholders = new ArrayList<>(),
                                crtworkNotes = new ArrayList<>(),
                                crtAttachments = new ArrayList<>(),
                                designBoard = new ArrayList<>(),
                                meetingParticipants = new ArrayList<>(),
                                actionItems = new ArrayList<>();
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET_SK, new String[]{ticket_sk});
                                    parameter.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_ASSETS) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_GROUPS)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_BUILDINGS) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_LOCATIONS1)
                                    || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_IMPACTED1) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_RISKS1) || endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_REFERENCES)
                            ) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                    //meeting participants
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CR_MEETING_PARTICIPANTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(SCHEDULE_SK, new String[]{schedule_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CR_MEETING_PARTICIPANTS, assignmentResult);
                                        meetingParticipants.add(res);
                                    }
                                    //actionitems
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_ACTIONITEMS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(ENTITYNAME, new String[]{CR_SCHEDULES_ENTITYNAME});
                                        parameter1.put(ENTITY_sk, new String[]{schedule_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(ACTION_ITEMS, assignmentResult);
                                        actionItems.add(res);
                                    }
                                    result = actionItems;
                                    //DesingBoard schedules
                                    for (Map<String, Object> res : result) {
                                        String schedule_sk = (String) res.get(SCHEDULE_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_SCHEDULES_ENTITYNAME
                                                , schedule_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);
                                    //approval users
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CR_APPROVAL_STATUS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String Approval_sk = (String) res.get(APPROVAL_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(APPROVAL_SK, new String[]{Approval_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CR_APPROVAL_STATUS, assignmentResult);
                                        approvalStatus.add(res);
                                    }
                                    result = approvalStatus;
                                    //approval designboard
                                    for (Map<String, Object> res : result) {
                                        String Approval_sk = (String) res.get(APPROVAL_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_APPROVALS_ENTITYNAME
                                                , Approval_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(CR_SK, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , null);

                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ASSIGNMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_ASSIGNMENTS, assignmentResult);
                                        taskList.add(res);
                                    }
                                    result = taskList;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_STAKE_HOLDERS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_STAKE_HOLDERS, assignmentResult);
                                        stakeholders.add(res);
                                    }
                                    result = stakeholders;
                                    //actionitems
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_ACTIONITEMS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String crt_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(ENTITYNAME, new String[]{CR_TASKS_ENTITYNAME});
                                        parameter1.put(ENTITY_sk, new String[]{crt_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(ACTION_ITEMS, assignmentResult);
                                        actionItems.add(res);
                                    }
                                    result = actionItems;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ATTACHMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_ATTACHMENTS, assignmentResult);
                                        crtAttachments.add(res);
                                    }
                                    result = crtAttachments;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_WORKNOTES, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        res.put(CRT_WORKNOTES, assignmentResult);
                                        crtworkNotes.add(res);
                                    }
                                    result = crtworkNotes;
                                    //designboard records
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        assignmentResult = SFInterfaceBase.fetchDesignboardforEntity(crDesignboard, CR_TASKS_ENTITYNAME
                                                , CRT_sk);
                                        res.put(DESIGN_BOARD, assignmentResult);
                                        designBoard.add(res);
                                    }
                                    result = designBoard;
                                }
                            } else if (endpoint.equalsIgnoreCase(QUESTIONS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    if (platformsk != null) {
                                        parameter.put(ENTITY_sk, new String[]{platformsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_PLATFORM});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (crcategorysk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{crcategorysk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_CR_CATEGORY});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (crsystemsk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{crsystemsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_CR_SYSTEM});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                    if (pendingreasonsk != null) {
                                        parameter = new HashMap<>();
                                        parameter.put(ENTITY_sk, new String[]{pendingreasonsk});
                                        parameter.put(ENTITYNAME, new String[]{FDN_PENDINGREASON});
                                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        result.addAll(SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                                , null));
                                    }
                                }
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , null);
                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CHANGE_REQUEST1)) {
                                resultMap.put(CHANGE_REQUEST, result);
                                if (result.size() > NUM_ZERO) {
                                    businessfnsk = result.get(0).get(BUSINESS_FUNCTION_SK).toString();
                                    ticket_sk = result.get(0).get(TICKET_SK).toString();
                                    platformsk = result.get(0).get(PLATFORM_SK) != null ? result.get(0).get(PLATFORM_SK).toString() : null;
                                    crcategorysk = result.get(0).get(CR_CATEGORY_SK) != null ? result.get(0).get(CR_CATEGORY_SK).toString() : null;
                                    crsystemsk = result.get(0).get(CR_SYSTEM_SK) != null ? result.get(0).get(CR_SYSTEM_SK).toString() : null;
                                    pendingreasonsk = result.get(0).get(CHANGE_REASON_SK) != null ? result.get(0).get(CHANGE_REASON_SK).toString() : null;
                                }
                            } else if (endpoint.equalsIgnoreCase(QUESTIONS1)) {
                                resultMap.put(QUESTIONS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_LOCATIONS1)) {
                                resultMap.put(CR_LOCATIONS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                resultMap.put(CR_SCHEDULES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_ATTACHMENTS)) {
                                List<Map<String, Object>> finalResult = (List<Map<String, Object>>) resultMap.get(CHANGE_REQUEST);
                                final int[] i = {1};
                                while (i[0] < 6) {
                                    finalResult.get(0).put(ATTACHMENT_FIELD + i[0], null);
                                    i[0]++;
                                }

                                final int[] j = {1};
                                result.stream()
                                        .filter(impacted -> checkBlankorEmpty((String) impacted.get(FILE_PATH)))
                                        .forEach(impacted -> {
                                            finalResult.get(0).put(ATTACHMENT_FIELD + j[0], (String) impacted.get(FILE_PATH));
                                            j[0]++;
                                        });
                                resultMap.put(CHANGE_REQUEST, finalResult);

                                //  resultMap.put(INDEXERNAME_FETCH_CR_ATTACHMENTS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                resultMap.put(CR_Tasks, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_RISKS1)) {
                                resultMap.put(CR_RISKS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_IMPACTED1)) {
                                //   resultMap.put(CR_IMPACTED, result);
                                String crimpacted = result.parallelStream()
                                        .filter(impacted -> checkBlankorEmpty((String) impacted.get("LOVValuesLabel")))
                                        .map(impacted ->
                                                impacted.get("LOVValuesLabel").toString()
                                        ).collect(Collectors.joining("||"));
                                result = (List<Map<String, Object>>) resultMap.get(CHANGE_REQUEST);
                                result.get(0).put(CR_IMPACTED, crimpacted);
                                resultMap.put(CHANGE_REQUEST, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                resultMap.put(COM_RESPONSES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_REFERENCES)) {
                                resultMap.put(COM_REFRENCES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                resultMap.put(INDEXERNAME_FETCH_CR_APPROVALS, result);
                            } else if (endpoint.equalsIgnoreCase(CR_DESIGNBOARD)) {
                                crDesignboard = result;
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vani
     * <p>
     * Fetch CRTasks details
     *
     * @return - Returns the CRTasks Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = CR_TASK_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchCRTaskDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = CR_TASK_DETAILS;
        String task_sk = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_CR_TASKS1);
                    endpoints.add(INDEXERNAME_FETCH_CRT_STAKEHOLDERS);
                    endpoints.add(INDEXERNAME_FETCH_CRT_ASSIGNMENTS);
                    endpoints.add(INDEXERNAME_FETCH_CRT_WORKNOTES);
                    endpoints.add(INDEXERNAME_FETCH_CRT_ATTACHMENTS);
                    endpoints.add(INDEXERNAME_FETCH_CR_DESIGNBOARD);
                    endpoints.add(INDEXERNAME_FETCH_COM_ACTIONITEMS1);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_DESIGNBOARD)) {
                                Map<String, String[]> paramsdesignboard = new HashMap<>();
                                paramsdesignboard.put(ENTITYNAME, new String[]{CR_TASKS_ENTITYNAME});
                                paramsdesignboard.put(ENTITY_sk, new String[]{task_sk});
                                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), paramsdesignboard);
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {
                                Map<String, String[]> parameter1 = new HashMap<>();
                                parameter1.put(ENTITYNAME, new String[]{CR_TASKS_ENTITYNAME});
                                parameter1.put(ENTITY_sk, new String[]{task_sk});
                                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else {
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                resultMap.put(CR_Tasks, result);
                                task_sk = (String) result.get(NUM_ZERO).get(TICKET_SK);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {
                                resultMap.put(ACTION_ITEMS, result);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * <p>
     * Fetch CRApprovals details
     *
     * @return - Returns the CRTasks Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = CR_APPROVAL_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchCRApprovalDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = CR_APPROVAL_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            UserAccountNGroup userAccountNGroup = fetchUserAccountNGroup(null, user.getUserID(), user);
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(APPROVAL_SK)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    String endpointname = INDEXERNAME_FETCH_CR_APPROVALS1;
                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointname, version, GET_METHOD, user);
                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                            , customWhooshModel.getRequiredColumns());

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(CR_APPROVAL_STATUS1);
                    List<Map<String, Object>> tempresult = new ArrayList<>();
                    for (Map<String, Object> stringObjectMap : result) {
                        for (String endpoint : endpoints) {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                if (endpoint.equalsIgnoreCase(CR_APPROVAL_STATUS1)) {
                                    Map<String, String[]> inputParams = new HashMap<>();
                                    inputParams.put(APPROVAL_SK, new String[]{stringObjectMap.get(APPROVAL_SK).toString()});
                                    refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), inputParams);
                                    tempresult = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                                            , refdbquery
                                            , null
                                            , "Level,Sequence"
                                            , "ASC"
                                            , customWhooshModel.getDistinct()
                                            , customWhooshModel.getDistinctfield()
                                            , customWhooshModel.getPageno()
                                            , customWhooshModel.getCount()
                                            , Boolean.FALSE);
                                  /*  tempresult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user,SFInterfaceConstants.getDbRefConnectionName())), refdbquery
                                            , customWhooshModel.getRequiredColumns());*/

                                    tempresult = appendUserinCRapproval(tempresult, userAccountNGroup, user);


                                    stringObjectMap.put(APPROVAL_STATUS, tempresult);
                                } else {
                                    stringObjectMap.put(APPROVAL_STATUS, tempresult);
                                }
                            } else {
                                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                String message = (String) errorMap.get(MESSAGE);
                                errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                        }
                    }
                    resultMap.put(INDEXERNAME_FETCH_CR_APPROVALS, result);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vani
     * <p>
     * Fetch CRPrint details
     *
     * @return - Returns the CRPrint Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket
     */
    @GetMapping(path = CR_PRINT_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchCRPrintDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(),
                endPointDomain = new HashMap<>(), errorMap = new HashMap<>(), tempmap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), assignmentResult = new ArrayList<>(),
                taskList = new ArrayList<>(), approvalStatus = new ArrayList<>(), stakeholders = new ArrayList<>(), questionDataList = new ArrayList<>();
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = CR_PRINT_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_CHANGE_REQUEST1);
                    endpoints.add(INDEXERNAME_FETCH_CR_TASKS1);
                    endpoints.add(INDEXERNAME_FETCH_CR_SCHEDULES1);
                    endpoints.add(INDEXERNAME_FETCH_CR_APPROVALS1);
                    endpoints.add(INDEXERNAME_FETCH_CR_IMPACTED1);
                    endpoints.add(INDEXERNAME_FETCH_CR_STAKEHOLDER);
                    endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);
                    endpoints.add(INDEXERNAME_FETCH_COM_ACTIONITEMS1);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET_SK, new String[]{ticket_sk});
                                    parameter.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                    Set<String> entities = result.parallelStream()
                                            .filter(stringObjectMap -> stringObjectMap.get(ENTITYNAME) != null)
                                            .map(stringObjectMap -> (String) stringObjectMap.get(ENTITYNAME))
                                            .collect(Collectors.toSet());
                                    List<Map<String, Object>> finalResult1 = result;

                                    entities.stream().forEach(s -> {
                                        List<Map<String, Object>> entitylists = new ArrayList<>();
                                        finalResult1.stream().filter(stringObjectMap -> stringObjectMap.get(ENTITYNAME) != null && stringObjectMap.get(ENTITYNAME).toString().equalsIgnoreCase(s))
                                                .forEach(stringObjectMap -> {
                                                    entitylists.add(stringObjectMap);
                                                });
                                        tempmap.put(s, entitylists);
                                    });

                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(CR_SK, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , customWhooshModel.getRequiredColumns());

                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ASSIGNMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , customWhooshModel.getRequiredColumns());
                                        res.put(CRT_ASSIGNMENTS, assignmentResult);
                                        taskList.add(res);
                                    }
                                    result = taskList;
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_STAKE_HOLDERS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , customWhooshModel.getRequiredColumns());
                                        res.put(CRT_STAKE_HOLDERS, assignmentResult);
                                        stakeholders.add(res);
                                    }
                                    result = stakeholders;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(TICKET, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                    //approval users
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CR_APPROVAL_STATUS1, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String Approval_sk = (String) res.get(APPROVAL_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter.put(APPROVAL_SK, new String[]{Approval_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , customWhooshModel.getRequiredColumns());
                                        res.put(CR_APPROVAL_STATUS, assignmentResult);
                                        approvalStatus.add(res);
                                    }
                                    result = approvalStatus;
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {

                                List<Map<String, Object>> finalResult = new ArrayList<>();
                                if (resultMap.get(CR_Tasks) instanceof List) {
                                    ((List) resultMap.get(CR_Tasks))
                                            .stream()
                                            .forEach(task -> {
                                                if (task instanceof Map) {
                                                    try {
                                                        finalResult.addAll(
                                                                SFInterfaceBase.fetchComActionItems(
                                                                        CR_TASKS_ENTITYNAME,
                                                                        ((Map) task).get(TICKET_SK).toString(), endpoint
                                                                        , version, customWhooshModel.getRequiredColumns(), user));
                                                    } catch (IOException e) {
                                                        SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
                                                    }
                                                }
                                            });
                                    if (resultMap.get(CR_SCHEDULES) instanceof List) {
                                        ((List) resultMap.get(CR_SCHEDULES))
                                                .stream()
                                                .forEach(task -> {
                                                    if (task instanceof Map) {
                                                        try {
                                                            finalResult.addAll(
                                                                    SFInterfaceBase.fetchComActionItems(
                                                                            CR_SCHEDULES_ENTITYNAME,
                                                                            ((Map) task).get(SCHEDULE_SK).toString(),
                                                                            endpoint, version,
                                                                            customWhooshModel.getRequiredColumns(), user));
                                                        } catch (IOException e) {
                                                            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
                                                        }
                                                    }
                                                });
                                    }
                                    result.addAll(finalResult);
                                }
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CHANGE_REQUEST1)) {
                                resultMap.put(CHANGE_REQUEST, result);
                                if (result.size() > NUM_ZERO) {
                                    businessfnsk = result.get(NUM_ZERO).get(BUSINESS_FUNCTION_SK).toString();
                                    ticket_sk = result.get(NUM_ZERO).get(TICKET_SK).toString();
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                resultMap.put(CR_Tasks, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_APPROVALS1)) {
                                resultMap.put(INDEXERNAME_FETCH_CR_APPROVALS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                resultMap.put(CR_SCHEDULES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {
                                resultMap.put(ACTION_ITEMS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_IMPACTED1)) {
                                resultMap.put(CR_IMPACTED, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                resultMap.put(COM_RESPONSES, tempmap);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vani
     * <p>
     * Fetch Schedule details
     *
     * @return - Returns the Schedule and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = SCHEDULE_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchScheduleDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = SCHEDULE_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(SCHEDULE_SK)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    params.put(TYPE, new String[]{"CR_Schedules"});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    params.put(ENTITYNAME, new String[]{"CR_Schedules"});
                    params.put(ENTITY_sk, new String[]{request.getParameterMap().get(SCHEDULE_SK)[0].toString()});
                    CustomWhooshModel customWhooshModel2 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_CR_SCHEDULES1);
                    endpoints.add(INDEXERNAME_FETCH_CR_DISCUSSIONPOINTS);
                    endpoints.add(INDEXERNAME_FETCH_COM_ACTIONITEMS1);
                    endpoints.add(INDEXERNAME_FETCH_CR_MEETINGPARTICIPANTS);
                    endpoints.add(INDEXERNAME_FETCH_CR_DESIGNBOARD);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel2.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_DESIGNBOARD)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel2.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , null);
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                resultMap.put(CR_SCHEDULES, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_ACTIONITEMS1)) {
                                resultMap.put(INDEXERNAME_FETCH_COM_ACTIONITEMS, result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_DESIGNBOARD)) {
                                resultMap.put(DESIGN_BOARD, result);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vani
     * <p>
     * Fetch Closure details
     *
     * @return - Returns the Closure and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = CLOSURE_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchClosureDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>(), params1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = CLOSURE_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(SCHEDULE_SK)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);

                    params.put(TYPE, new String[]{"CR_Schedules"});
                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModel(params, user);

                    List<String> endpoints = new ArrayList<>();
                    endpoints.add(INDEXERNAME_FETCH_CR_SCHEDULES1);
                    endpoints.add(INDEXERNAME_FETCH_CR_MEETINGPARTICIPANTS);
                    endpoints.add(INDEXERNAME_FETCH_COM_RESPONSES);

                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();

                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_COM_RESPONSES)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    params.put(TICKET_SK, new String[]{ticket_sk});
                                    params.put(BUSINESS_FUNCTION_SK, new String[]{businessfnsk});
                                    params.put(ENTITYNAME, new String[]{FDN_STATUSENTITY});
                                    CustomWhooshModel customWhooshModel2 = SFInterfaceBase.generateWhooshModel(params, user);
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel2.getParams());
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , customWhooshModel.getRequiredColumns());
                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel1.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            } else {
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_SCHEDULES1)) {
                                resultMap.put(CR_SCHEDULES, result);
                                if (result.size() > NUM_ZERO) {
                                    ticket_sk = result.get(0).get(TICKET_SK).toString();
                                    params1.put(TICKET, new String[]{ticket_sk});

                                    result = fetchWhooshRecords(INDEXERNAME_FETCH_BUSINESSFUNCTION, BUSINESS_FUNCTION_CODE + COLON + CR, null, new String[]{BUSINESS_FUNCTION_SK}, null,
                                            null, null, null, user);

                                    businessfnsk = result.get(0).get(BUSINESS_FUNCTION_SK).toString();
                                }
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch Vacation details
     *
     * @return - Returns the vacation Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @GetMapping(path = VACATION1_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchVacation(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> parameter = new HashMap<>(), params = new HashMap<>();
        String userLoginID = "";
        String endpointName = VACATION1;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                parameter = request.getParameterMap();
                for (Map.Entry<String, String[]> stringEntry : parameter.entrySet()) {
                    params.put(stringEntry.getKey(), stringEntry.getValue());
                }
                String accounts = "";
                UserAccountNGroup userAccountAGroup = new UserAccountNGroup();

                if (params.containsKey(USER_SK)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(null, (String) params.get(USER_SK)[NUM_ZERO], user);
                    params.remove(USER_SK);
                } else if (params.containsKey(LOGIN_ID)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup((String) params.get(LOGIN_ID)[NUM_ZERO], null);
                    params.remove(LOGIN_ID);
                } else if (StringUtils.isBlank(atoken) && (StringUtils.isNotBlank(accessToken) || StringUtils.isNotBlank(refreshToken))) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }
                accounts = userAccountAGroup.getUserAccounts().stream().map(
                        account -> account.get(ACCOUNT) + COMMA
                ).collect(Collectors.joining()) + "CTS";

                params.put(ACCOUNT, new String[]{accounts});
                CustomWhooshModel customWhooshModel = generateWhooshModel(params, user);

                if (customWhooshModel.getParams().containsKey(LOGIN_ID)) {
                    customWhooshModel.getParams().remove(LOGIN_ID);
                }
                if (customWhooshModel.getParams().containsKey(USER_SK)) {
                    customWhooshModel.getParams().remove(USER_SK);
                }

                Map<String, Object> endPointDomain = fetchEndpointDetails(INDEXERNAME_FETCH_VACATION, version, GET_METHOD, user);
                if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                    result = runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, customWhooshModel.getRequiredColumns());
                    result = SFInterfaceBase.sortNDistinct(result, customWhooshModel.getSortfields()
                            , customWhooshModel.getSortorder(), customWhooshModel.getDistinct()
                            , customWhooshModel.getDistinctfield());
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch Object Permission details
     *
     * @return - Returns the Permission as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input - Requires User_sk
     * @input - ParentObjectCode is an mandatory input
     */
    @RequestMapping(value = OBJECT_PERMISSION1_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchObjectPermissions1(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        Map<String, String[]> requestparams = new HashMap<>();
//        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Set<String>> objectTypeCode = new HashMap<>();
        String loginID = "", query = "";
        String[] objType = new String[]{};
        String objTypeQuery = "";
        Set<String> objectTypeSet = new HashSet<>();

        Map<String, Object> resultMap1 = new HashMap<>();

        String endpointName = OBJECT_PERMISSION1;

        try {

            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {

                if (requestFields != null)
                    params = convertmapObject(requestFields);
                params.putAll(request.getParameterMap());
                requestparams.putAll(params);       //to validate the request params after the modification
                UserAccountNGroup userAccountAGroup = new UserAccountNGroup();

                if (params.containsKey(USER_SK)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(null, (String) params.get(USER_SK)[NUM_ZERO], user);
                    params.remove(USER_SK);
                } else if (params.containsKey(LOGIN_ID)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup((String) params.get(LOGIN_ID)[NUM_ZERO], null);
                    params.remove(LOGIN_ID);
                } else if (StringUtils.isBlank(atoken) && (StringUtils.isNotBlank(accessToken) || StringUtils.isNotBlank(refreshToken))) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }

                //ObjectTypeQuery
                if (params.containsKey(OBJECT_TYPE)) {
                    objType = params.get(OBJECT_TYPE);
                    List<String> objTypeList = new ArrayList<>();
                    objTypeList.addAll(Arrays.asList(objType));
                    objTypeList.addAll(Arrays.asList(objType[0].split(",")));
                    objTypeQuery = OPEN_BRACKET + objTypeList
                            .stream()
                            .map(s -> OBJECT_TYPE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }

                String queryPermissions = STATUS_CODE + COLON + "A";
                String ParentObjectCode = null;
                if (requestparams.containsKey(PARENT_OBJECT_CODE)) {
                    ParentObjectCode = requestparams.get(PARENT_OBJECT_CODE)[0];
                    queryPermissions += AND + PARENT_OBJECT_CODE + COLON + ParentObjectCode;
                }
                if (StringUtils.isNotBlank(objTypeQuery))
                    queryPermissions += AND + objTypeQuery;

                result = fetchWhooshRecords(INDEXERNAME_FETCH_OBJECT_PERMISSIONS, queryPermissions, null
                        , new String[]{OBJECT_TYPE, OBJECT_CODE, PARENT_OBJECT_CODE}, null, null, "true",
                        null, NUM_ZERO, Boolean.FALSE, "ObjectType,ObjectCode", null, null, user);

                result.forEach(eachresult -> {
                    Set<String> tempHashSet = new HashSet<>();
                    if (objectTypeCode.containsKey(eachresult.get(OBJECT_TYPE).toString())) {
                        tempHashSet = objectTypeCode.get(eachresult.get(OBJECT_TYPE).toString());
                        tempHashSet.add(eachresult.get(OBJECT_CODE).toString());
                        objectTypeCode.put(eachresult.get(OBJECT_TYPE).toString(), tempHashSet);
                    } else {
                        tempHashSet.add(eachresult.get(OBJECT_CODE).toString());
                        objectTypeCode.put(eachresult.get(OBJECT_TYPE).toString(), tempHashSet);
                    }
                });

                //Group Query
                // userAccountAGroup.getUserGroups().addAll(userAccountAGroup.getUserPermission());
                if (!userAccountAGroup.getAccountGroup_sk().isEmpty()) {
                    query = OPEN_BRACKET + userAccountAGroup.getAccountGroup_sk().stream().map(
                            group -> ACCOUNT_GROUP_SK + COLON + ESCAPE_CHARACTER + group + ESCAPE_CHARACTER
                    ).collect(Collectors.joining(OR)) + OR + ACCOUNT_GROUP_SK + COLON + NULL_VALUE + CLOSE_BRACKET;
                }

                //Account Query
                if (!userAccountAGroup.getUserAccounts().isEmpty()) {
                    query += AND + OPEN_BRACKET + userAccountAGroup.getUserAccounts().stream().map(
                            group -> ACCOUNT + COLON + ESCAPE_CHARACTER + group.get(ACCOUNT) + ESCAPE_CHARACTER
                    ).collect(Collectors.joining(OR)) + OR + ACCOUNT + COLON + NULL_VALUE + CLOSE_BRACKET;
                }

                //ObjectTypeQuery
                if (params.containsKey(OBJECT_TYPE)) {
                    params.remove(OBJECT_TYPE);
                    if (StringUtils.isNotBlank(query) && StringUtils.isNotBlank(objTypeQuery)) {
                        query += AND + objTypeQuery;
                    }
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                if (StringUtils.isNotBlank(query) && StringUtils.isNotBlank(customWhooshModel.getQuery())) {
                    customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + query);
                } else if (StringUtils.isNotBlank(query)) {
                    customWhooshModel.setQuery(query);
                }

                List<Map<String, Object>> finalResult = fetchWhooshRecords(INDEXERNAME_FETCH_OBJECT_PERMISSIONS, customWhooshModel.getQuery(), null
                        , null, null, null, null, null, NUM_ZERO, Boolean.FALSE, null, null, null, user);

               /* if (result.size() == NUM_ZERO) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.OK);
                }*/

                if (requestparams.containsKey(OBJECT_TYPE)) {
                    objectTypeSet = Arrays.stream(requestparams.get(OBJECT_TYPE)).collect(Collectors.toSet());
                    objectTypeSet.addAll(Arrays.stream(requestparams.get(OBJECT_TYPE)[0].split(",")).collect(Collectors.toSet()));
                } else {
                    objectTypeSet = objectTypeCode
                            .entrySet()
                            .stream()
                            .map(objectType -> objectType.getKey())
                            .collect(Collectors.toSet());
                }
                String finalParentObjectCode = ParentObjectCode;
                objectTypeSet.stream().forEach(objectType -> {
                    Set<String> objectCode = objectTypeCode.get(objectType);
                    if (objectCode != null && !objectCode.isEmpty()) {
                        List<Map<String, Object>> objCodeResult = new ArrayList<>();
                        objectCode.stream().forEach(objCode -> {
                            String[] viewPermission = {ZERO};
                            String[] addPermission = {ZERO};
                            String[] deletePermission = {ZERO};
                            String[] editPermission = {ZERO};
                            String[] isMandatoryPermission = {ZERO};
                            Map<String, Object> resultMap = new HashMap<>();
                            finalResult.stream().forEach(rs -> {
                                if (rs.get(OBJECT_TYPE).toString().equalsIgnoreCase(objectType) && rs.get(OBJECT_CODE).toString().equalsIgnoreCase(objCode)) {
                                    resultMap.put(OBJECT_CODE, (String) rs.get(OBJECT_CODE));
                                    resultMap.put(PARENT_OBJECT_CODE, (String) rs.get(PARENT_OBJECT_CODE));
                                    resultMap.put(PARENT_OBJECT_SK, (String) rs.get(PARENT_OBJECT_SK));
                                    resultMap.put(OBJECT_SK, (String) rs.get(OBJECT_SK));
                                    viewPermission[0] = (!isNull(rs.get(VIEW)) && rs.get(VIEW).toString().equals(ONE)) ? ONE : viewPermission[0];
                                    addPermission[0] = (!isNull(rs.get(ADD)) && rs.get(ADD).toString().equals(ONE)) ? ONE : addPermission[0];
                                    deletePermission[0] = (!isNull(rs.get(DELETE)) && rs.get(DELETE).toString().equals(ONE)) ? ONE : deletePermission[0];
                                    editPermission[0] = (!isNull(rs.get(EDIT)) && rs.get(EDIT).toString().equals(ONE)) ? ONE : editPermission[0];
                                    isMandatoryPermission[0] = (rs.get(IS_MANDATORY) != null) ? (rs.get(IS_MANDATORY).equals(ONE) ? ONE : isMandatoryPermission[0]) : isMandatoryPermission[0];
                                }
                            });
                            if (resultMap.isEmpty()) {
                                resultMap.put(OBJECT_CODE, objCode);
                                resultMap.put(PARENT_OBJECT_CODE, finalParentObjectCode);
                                resultMap.put(PARENT_OBJECT_SK, null);
                                resultMap.put(OBJECT_SK, null);
                            }
                            resultMap.put(VIEW, viewPermission[0]);
                            resultMap.put(ADD, addPermission[0]);
                            resultMap.put(DELETE, deletePermission[0]);
                            resultMap.put(EDIT, editPermission[0]);
                            resultMap.put(IS_MANDATORY, isMandatoryPermission[0]);
                            objCodeResult.add(resultMap);
                        });
                        resultMap1.put(objectType, objCodeResult);
                    }
                });
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultMap1, HttpStatus.OK);
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for inserting the KB Article details
     *
     * @return - Returns  success message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_KBARTICLE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createKBArticle(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            Map<String, Object> kbArticle = new HashMap<>();
            ResponseEntity<Map<String, Object>> kbArticleResponse = SFInterfaceServices.createKBArticle(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, CREATE_KBARTICLE, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                    , request, user);
            kbArticle = kbArticleResponse.getBody();
            //To insert into ARSystem_Reporting DB
            SFInterfaceServices.insertOrUpdateKBArticleInReporting(kbArticle, CREATE_DMART_KB_ARTICLE_METADATA, version, user);
            return kbArticleResponse;
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, CREATE_KBARTICLE, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_KBARTICLE), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for updating the KB Article details
     *
     * @return - Returns  success message as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KBARTICLE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateKBArticle(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            Map<String, Object> kbArticle = new HashMap<>();
            ResponseEntity<Map<String, Object>> kbArticleResponse = SFInterfaceServices.updateKBArticle(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken)
                    , ticketFields, UPDATE_KBARTICLE, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
            kbArticle = kbArticleResponse.getBody();

            //To insert into ARSystem_Reporting DB
            SFInterfaceServices.insertOrUpdateKBArticleInReporting(kbArticle, UPDATE_DMART_KB_ARTICLE_METADATA, version, user);
            return kbArticleResponse;

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, UPDATE_KBARTICLE, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, UPDATE_KBARTICLE), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for fetching active Incident Manager
     *
     * @return - Returns  IncidentManager with Account as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives Status parameter.
     */

    @GetMapping(path = INCIDENT_MANAGERS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> fetchIncidentManager(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        Set<String> problemManager = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        String endpointName = INCIDENT_MANAGERS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (user != null && StringUtils.isNotBlank(user.getAuthUserName())) {

                if (request.getParameterMap().containsKey(STATUS) || request.getParameterMap().get(STATUS) != null) {
                    CustomWhooshModel customWhooshModel = generateWhooshModel(request.getParameterMap(), user);
                    List<Map<String, Object>> problemManagerList = fetchWhooshRecords(PROBLEM_MANAGER, customWhooshModel.getQuery(), null, null,
                            null, null, null, null, NUM_ZERO, Boolean.FALSE, null, null, null, user);

                    if (problemManagerList.size() > NUM_ZERO) {
                        problemManager = problemManagerList
                                .stream()
                                .map(s -> s.get(USER_SK).toString())
                                .collect(Collectors.toSet());

                        for (String userSK : problemManager) {
                            Map<String, Object> resultMap = new HashMap<>();
                            Set<Map<String, Object>> accountList = new HashSet<>();

                            problemManagerList.forEach(pbm -> {
                                if (pbm.get(USER_SK).toString().equalsIgnoreCase(userSK)) {
                                    Map<String, Object> accountMap = new HashMap<>();
                                    resultMap.put(USER_SK, pbm.get(USER_SK));
                                    resultMap.put(INCIDENT_MANAGER_NAME, pbm.get(PROBLEM_MANAGER_FULL_NAME));
                                    resultMap.put(INCIDENT_MANAGER_REFID_1, pbm.get(PROBLEM_MANAGER_LOGIN_ID));
                                    resultMap.put(INCIDENT_MANAGER_REFID_2, pbm.get(PROBLEM_MANAGER_NETWORK_LOGIN_ID));
                                    resultMap.put(INCIDENT_MANAGER_REFID_3, pbm.get(PROBLEM_MANAGER_REFID_3));
                                    accountMap.put(ACCOUNT_SK, pbm.get(ACCOUNT_SK));
                                    accountMap.put(ACCOUNT, pbm.get(ACCOUNT));
                                    accountList.add(accountMap);
                                }
                            });

                            resultMap.put(ACCOUNTS, accountList);
                            result.add(resultMap);
                        }
                        return new ResponseEntity(result, HttpStatus.OK);
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.OK);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORINCIDENTMANAGER", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }


            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            errorMap.put(ERROR, e.getMessage());
            return new ResponseEntity(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for fetching active Incident Manager
     *
     * @return - Returns  IncidentManager with Account as Json Response
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives Status parameter.
     */

    @GetMapping(path = "/{version}/ApprovalStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchticketApprovals(
            @PathVariable(VERSION) String version,
            HttpServletRequest request) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        Set<String> problemManager = new HashSet<>();
        List<Map<String, Object>> result = new ArrayList<>();
        String endpointName = "ApprovalStatus";

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (user != null && StringUtils.isNotBlank(user.getAuthUserName())) {
                Map<String, String[]> requestparametermap = new HashMap<String, String[]>(request.getParameterMap());

                JdbcTemplate template = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbSspConnectionName());
                if (requestparametermap.containsKey(USER_SK)) {
                    List<Map<String, Object>> results = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE,
                            USER_SK + COLON + requestparametermap.get(USER_SK)[0].toString(), null
                            , new String[]{LOGIN_ID}, null, null, null,
                            null, 0, false, null, 1, null, user);
                    if (!results.isEmpty()) {
                        requestparametermap.put(USER_SK, new String[]{results.get(0).get(LOGIN_ID).toString()});
                    } else {
                        errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                    }
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(requestparametermap, user);
                Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails("SSPTicketApprovals", version, GET_METHOD, user);
                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());

                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(template, refdbquery
                        , customWhooshModel.getRequiredColumns());
                result = SFInterfaceBase.sortNDistinct(result, customWhooshModel.getSortfields()
                        , customWhooshModel.getSortorder(), customWhooshModel.getDistinct()
                        , customWhooshModel.getDistinctfield());

                return new ResponseEntity(result, HttpStatus.OK);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            errorMap.put(ERROR, e.getMessage());
            return new ResponseEntity(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for fetching the Platform details
     *
     * @return - Returns Platform Details JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Platform_sk as request .
     */
    @RequestMapping(path = PLATFORM_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchPlatformDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {


        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        List<Map<String, Object>> results = new ArrayList<>();
        String endpointName = PLATFORM_DETAILS;
        Map<String, String[]> requestparametermap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(PLATFORM_SK) && request.getParameterMap().get(PLATFORM_SK) != null) {
                    Map<String, Object> endPointDomain1 = fetchEndpointDetails(PLATFORM, version, GET_METHOD, user);
                    if (endPointDomain1.size() > NUM_ZERO && !(endPointDomain1.containsKey(ERRORCODE))) {
                        requestparametermap.put(PLATFORM_SK, new String[]{request.getParameter(PLATFORM_SK)});
                        String refdbquery = getDBQuery(endPointDomain1.get(QUERY).toString(), requestparametermap);
                        results = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, null);
                        if (results.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                        }
                        if (results.size() > NUM_ZERO && !results.contains(ERRORCODE)) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(QUESTIONS1, version, GET_METHOD, user);
                            Map<String, String[]> requestparametermap1 = new HashMap<>();
                            requestparametermap1.put(ENTITYNAME, new String[]{FDN_PLATFORM});
                            requestparametermap1.put(ENTITY_sk, new String[]{request.getParameter(PLATFORM_SK)});

                            results.get(0).put(QUESTIONS, SFInterfaceBase.runjdbcQueryWithSelectedColumns
                                    (SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), getDBQuery(endPointDomain.get(QUERY).toString(), requestparametermap1), null));
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORPLATFORMDETAILS", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for fetching the CRCategory details
     *
     * @return - Returns CRCategory Details JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Platform_sk as request .
     */
    @RequestMapping(path = CR_CATEGORY_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchCRCategoryDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        List<Map<String, Object>> results = new ArrayList<>();
        String endpointName = CR_CATEGORY_DETAILS;
        Map<String, String[]> requestparametermap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (StringUtils.isNotBlank(loginID)) {

                if (request.getParameterMap().containsKey(CR_CATEGORY_SK) && request.getParameterMap().get(CR_CATEGORY_SK) != null) {
                    Map<String, Object> endPointDomain1 = fetchEndpointDetails(CR_CATEGORY, version, GET_METHOD, user);
                    if (endPointDomain1.size() > NUM_ZERO && !(endPointDomain1.containsKey(ERRORCODE))) {
                        requestparametermap.put(CR_CATEGORY_SK, new String[]{request.getParameter(CR_CATEGORY_SK)});
                        String refdbquery = getDBQuery(endPointDomain1.get(QUERY).toString(), requestparametermap);
                        results = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, null);
                        if (results.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                        }
                        if (results.size() > NUM_ZERO && !results.contains(ERRORCODE)) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(QUESTIONS1, version, GET_METHOD, user);
                            Map<String, String[]> requestparametermap1 = new HashMap<>();
                            requestparametermap1.put(ENTITYNAME, new String[]{FDN_CR_CATEGORY});
                            requestparametermap1.put(ENTITY_sk, new String[]{request.getParameter(CR_CATEGORY_SK)});

                            results.get(0).put(QUESTIONS, SFInterfaceBase.runjdbcQueryWithSelectedColumns
                                    (SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), getDBQuery(endPointDomain.get(QUERY).toString(), requestparametermap1), null));
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORCRCATEGORYDETAILS", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vinodhini
     * The Function is used for fetching the CRSystem details
     *
     * @return - Returns CRSystem Details JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Platform_sk as request .
     */
    @RequestMapping(path = CR_SYSTEM_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchCRSystemDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {


        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        List<Map<String, Object>> results = new ArrayList<>();
        String endpointName = CR_SYSTEM_DETAILS;
        Map<String, String[]> requestparametermap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(CR_SYSTEM_SK) && request.getParameterMap().get(CR_SYSTEM_SK) != null) {
                    Map<String, Object> endPointDomain1 = fetchEndpointDetails(CATEGORY_SYSTEMS, version, GET_METHOD, user);
                    if (endPointDomain1.size() > NUM_ZERO && !(endPointDomain1.containsKey(ERRORCODE))) {
                        requestparametermap.put(CR_SYSTEM_SK, new String[]{request.getParameter(CR_SYSTEM_SK)});
                        String refdbquery = getDBQuery(endPointDomain1.get(QUERY).toString(), requestparametermap);
                        results = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, null);
                        if (results.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                        }
                        if (results.size() > NUM_ZERO && !results.contains(ERRORCODE)) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(QUESTIONS1, version, GET_METHOD, user);
                            Map<String, String[]> requestparametermap1 = new HashMap<>();
                            requestparametermap1.put(ENTITYNAME, new String[]{FDN_CR_SYSTEM});
                            requestparametermap1.put(ENTITY_sk, new String[]{request.getParameter(CR_SYSTEM_SK)});


                            results.get(0).put(QUESTIONS, SFInterfaceBase.runjdbcQueryWithSelectedColumns
                                    (SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), getDBQuery(endPointDomain.get(QUERY).toString(), requestparametermap1), null));
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>(results, HttpStatus.OK);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORCRSYSTEMDETAILS", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }


            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for fetching the Platform details
     *
     * @return - Returns Platform Details JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Platform_sk as request .
     */
    @RequestMapping(path = "/{version}/Notification1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchNotifications(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {


        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        List<Map<String, Object>> results = new ArrayList<>();
        String endpointName = "Notification";
        Map<String, String[]> requestparametermap = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (StringUtils.isNotBlank(loginID)) {
                requestparametermap = request.getParameterMap();
                Map<String, Object> endPointDomain1 = fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                if (endPointDomain1.size() > NUM_ZERO && !(endPointDomain1.containsKey(ERRORCODE))) {
                    String refdbquery = "";
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(requestparametermap);
                    refdbquery = getDBQuery(endPointDomain1.get(QUERY).toString(), customWhooshModel.getParams());
                    String selectedColumns = null;
                    results = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                            , refdbquery
                            , customWhooshModel.getRequiredColumns()
                            , customWhooshModel.getSortfields()
                            , customWhooshModel.getSortorder()
                            , customWhooshModel.getDistinct()
                            , customWhooshModel.getDistinctfield()
                            , customWhooshModel.getPageno()
                            , customWhooshModel.getCount()
                            , Boolean.TRUE);
                    if (results.size() > NUM_ZERO) {
                        return returnResponseWithPagination(results);
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                    }

                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (
                Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ModifiedBy - Vivek
     * The Function is used for Updating the UpdateCR in FilePath
     *
     * @return - Returns CR Details JsonResponse
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an CR as request .
     */
    @RequestMapping(path = "/{version}/UpdateCR", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity UpdateCR(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        Map<String, String> response = new HashMap<>();

        Map<String, Object> errorMap = new HashMap<>();
        String ticketnumber = (String) ticketFields.get("TicketNumber");
        String approvalnumber = (String) ticketFields.get("ApprovalNumber");
        String status = (String) ticketFields.get("Status");
        String keyword = ticketnumber.startsWith("CR") ? "cr_list" : "cr_draft";
        //String color = status.equalsIgnoreCase("Approved") ? crapprovalcolor : crrejectcolor;

        String color = SFInterfaceBase.fetchValuefromJsonPath(status) != null ?
                SFInterfaceBase.fetchValuefromJsonPath(status) :
                status;

        String jsonparser = "$['" + ticketnumber + "']['saveJSON'][?(@.key=='" + approvalnumber + "')]['data']['arstatus']";
        String jsonparserstart = "$['" + ticketnumber + "']['treeData']";
        String jsonparserend = "['children'].[?(@.ticketNumber=='" + approvalnumber + "')]['color']";
        String jsonrepeater = ".['children'][*]";
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                Map<String, Object> inputJson = new LinkedHashMap<>();
                ObjectMapper objectMapper = new ObjectMapper();
                //fetch json file
                inputJson = objectMapper.readValue(new File(crfilepath), Map.class);
                String crrecord = (String) inputJson.get(keyword);
                DocumentContext json = JsonPath.parse(crrecord);
                //updating status
                json = json.set(jsonparser, status);
                boolean isFound = false;
                int i = 0;
                do {
                    net.minidev.json.JSONArray tempjsonarray = json.read(jsonparserstart + jsonparserend);
                    if (tempjsonarray.size() > 0) {
                        json = json.set(jsonparserstart + jsonparserend, color);//updating color
                        isFound = true;
                    }
                    jsonparserstart = jsonparserstart + jsonrepeater;
                    i++;
                } while (!isFound && i < 50);
                inputJson.put(keyword, json.jsonString());
                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                objectMapper.writeValue(new File(crfilepath), inputJson);
                if (isFound)
                    response.put("status", "success");
                else {
                    response.put("status", "failed");
                    response.put("Message", approvalnumber + " not found");
                }
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, "UpdateCR");
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, "UpdateCR", e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, "UpdateCR"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch IncidentManager details
     *
     * @return - Returns the IncidentManager  as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @RequestMapping(path = INCIDENT_MANAGER_HISTORY_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchIncidentManagerHistory(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String userLoginID = "";
        String endpointName = INCIDENT_MANAGER_HISTORY;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                if (requestFields != null) {
                    params = convertmapObject(requestFields);
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, requestFields.toString());
                }

                params.putAll(request.getParameterMap());

                if (params.containsKey(ACCOUNT_SK)) {
                    String account_sk = null;
                    account_sk = Arrays.stream(params.get(ACCOUNT_SK)).collect(Collectors.joining(COMMA));
                    params.put(ACCOUNT_SK, new String[]{account_sk});
                }

                if (params.containsKey(ACCOUNT)) {
                    String account = null;
                    account = Arrays.stream(params.get(ACCOUNT)).collect(Collectors.joining(COMMA));
                    params.put(ACCOUNT, new String[]{account});
                }

                if (params.containsKey(USER_SK)) {
                    String usersk = null;
                    usersk = Arrays.stream(params.get(USER_SK)).collect(Collectors.joining(COMMA));
                    params.put(USER_SK, new String[]{usersk});
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(params);
                Map<String, Object> endPointDomain = fetchEndpointDetails(INCIDENT_MANAGER_HISTORY, version, GET_METHOD, user);
                if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                    result = runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, customWhooshModel.getRequiredColumns());
                    result = SFInterfaceBase.sortNDistinct(result, customWhooshModel.getSortfields()
                            , customWhooshModel.getSortorder(), customWhooshModel.getDistinct()
                            , customWhooshModel.getDistinctfield());
                    if (result.size() == NUM_ZERO) {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity<Object>(errorMap, HttpStatus.OK);

                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }


    /**
     * author - Vivek
     * <p>
     * Fetch TicketsByStatus details
     *
     * @return - Returns the TicketsByStatus  as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @RequestMapping(path = AdvancedTicketSearch_PATH, method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchTicketsByStatus(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String userLoginID = "";
        String endpointName = AdvancedTicketSearch;
        String Account = "";
        String LoginID = "";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -90);
        Map<String, String> statuscode = new TreeMap<>();
        Map<String, String> businessFunctionCode = new HashMap<>();
        businessFunctionCode.put("INCIDENTS", "PT_ProblemTicket");
        businessFunctionCode.put("SR", "SR_ServiceRequest");

        statuscode.put("N", "0");
        statuscode.put("ASD", "1");
        statuscode.put("ACK", "2");
        statuscode.put("P", "3");
        statuscode.put("R", "4");
        statuscode.put("C", "5");
        statuscode.put("V", "6");
        String StatusCodeValue = " '0','1','2','3','4','5','6' ";
        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));

            String bfcode = requestFields.get(BUSINESS_FUNCTION_CODE) != null
                    ? requestFields.get(BUSINESS_FUNCTION_CODE).toString() : "INCIDENTS";
            if (businessFunctionCode.containsKey(bfcode)) {
                String status = requestFields.get(STATUS_CODE) != null ? requestFields.get(STATUS_CODE).toString() : "";
                if (status != null && !status.isEmpty()) {
                    for (Map.Entry<String, String> stringStringEntry : statuscode.entrySet()) {
                        if (stringStringEntry.getKey().equalsIgnoreCase("C")) {
                            status = status.replaceAll(stringStringEntry.getKey(), "'" + stringStringEntry.getValue() + "'");
                        } else {
                            status = status.replaceAll(stringStringEntry.getKey(), "'" + stringStringEntry.getValue() + "'");
                        }
                    }
                }
                StatusCodeValue = requestFields.get(STATUS_CODE) != null ? status : StatusCodeValue;

                User user = new User();
                user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                userLoginID = user.getAuthUserName();
                if (StringUtils.isNotBlank(userLoginID)) {
                    if (requestFields != null)
                        params = convertmapObject(requestFields);
                    params.putAll(request.getParameterMap());
                    String StartDate = convertStringToTimestamp(params.get(START_DATE) != null
                            ? params.get(START_DATE)[0] : new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
                    String endDate = convertStringToTimestampendDate(params.get(END_DATE) != null
                            ? params.get(END_DATE)[0] : new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                    String summary = "%";
                    if (params.containsKey(SUMMARY)) {
                        summary += (Arrays.stream(params.get(SUMMARY)[0].split(" "))
                                .collect(Collectors.joining("%")) + "%");
                        params.put(SUMMARY, new String[]{summary});
                    }
                    String Query = businessFunctionCode.get(bfcode)
                            + ".Status IN ( " + StatusCodeValue + " ) AND "
                            + businessFunctionCode.get(bfcode) + ".A_Create_Date_Time BETWEEN '"
                            + StartDate + "' AND '" + endDate + "' AND  " + businessFunctionCode.get(bfcode) +
                            ".summary LIKE '" + summary + "'";

                    if (params.get(TICKET_NUMBER) != null && params.get(TICKET_NUMBER)[0] != null) {
                        String ticketnumberQuery = "";
                        ticketnumberQuery = Arrays.stream(params.get(TICKET_NUMBER)).map(s -> "'" + s + "'")
                                .collect(Collectors.joining(","));

                        if (bfcode.equalsIgnoreCase(SR))
                            Query += " AND SR_ServiceRequest.request___ IN (" + ticketnumberQuery + " )";
                        else
                            Query += " AND PT_ProblemTicket.CASE___ IN (" + ticketnumberQuery + " )";
                    }

                    params.put(QUERY, new String[]{Query});
                    result = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE
                            , USER_SK + COLON + requestFields.get(USER_SK), null, new String[]{ACCOUNT, MASTER_ACCOUNT, LOGIN_ID},
                            null, ASC, TRUE, null, 1, null, user);

                    if (result != null && !result.isEmpty()) {
                        params.put(ACCOUNT, new String[]{result.get(0).get(ACCOUNT).toString()});
                        params.put(LOGIN_ID, new String[]{result.get(0).get(LOGIN_ID).toString()});
                        params.put(BUSINESS_FUNCTION_CODE, new String[]{businessFunctionCode.get(bfcode).toString()});
                        CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(params);
                        Map<String, Object> endPointDomain = fetchEndpointDetails(endpointName, version, POST_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                            result = runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), refdbquery, customWhooshModel.getRequiredColumns());
                            result = SFInterfaceBase.sortNDistinct(result, customWhooshModel.getSortfields()
                                    , customWhooshModel.getSortorder(), customWhooshModel.getDistinct()
                                    , customWhooshModel.getDistinctfield());
                            if (result.size() == NUM_ZERO) {
                                errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                                return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                            } else {
                                result = result.parallelStream().map(stringObjectMap -> {
                                    stringObjectMap.put(WATCHLIST, false);
                                    stringObjectMap.put("SelfFlag", false);
                                    return stringObjectMap;
                                })
                                        .collect(Collectors.toList());
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }


    /**
     * author - Vivek
     * <p>
     * Fetch SupportUser with Problemmanager details
     *
     * @return - Returns the SupportUser with Problemmanager  as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @RequestMapping(path = SupportUser1_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchSupportUser1(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String userLoginID = "";
        String endpointName = "SupportUser1";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(userLoginID)) {
                if (requestFields != null) {
                    params = convertmapObject(requestFields);
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, requestFields.toString());
                }

                params.putAll(request.getParameterMap());

                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, Boolean.TRUE, null, user);

                result = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, customWhooshModel.getQuery()
                        , null, customWhooshModel.getRequiredColumns(),
                        customWhooshModel.getSortfields(), ASC, TRUE, null, customWhooshModel.getCount(), null, customWhooshModel.getDistinctfield(), user);
                List<Map<String, Object>> finalProblemManageress = fetchWhooshRecords(INDEXERNAME_FETCH_PROBLEM_MANAGER,
                        "", null, new String[]{USER_SK}, USER_SK
                        , null, TRUE, null, 0, null, USER_SK, user);

                result = result.stream()
                        .map(stringObjectMap -> {
                            if (finalProblemManageress.parallelStream().anyMatch(stringObjectMap1 -> stringObjectMap1.get(USER_SK).toString()
                                    .equalsIgnoreCase(stringObjectMap.get(USER_SK).toString()))) {
                                stringObjectMap.put(PROBLEM_MANAGER, "Yes");
                            } else {
                                stringObjectMap.put(PROBLEM_MANAGER, "No");
                            }
                            return stringObjectMap;
                        }).collect(Collectors.toList());

                if (result.size() == NUM_ZERO) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * author - Vivek
     * <p>
     * Fetch AssignedGroup based on APP-SERVICE DESK
     *
     * @return - Returns the groups based on skillset
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @RequestMapping(path = AssignedGroup3_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchAssignedGroup3(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String userLoginID = "";
        String endpointName = "AssignedGroups2", methodType = GET_METHOD;
        try {
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            Map<String, String[]> tempParams = new HashMap<>();
            tempParams.put(LEVEL, new String[]{NULL_VALUE, NUMBER_THREE});
            if (StringUtils.isNotBlank(userLoginID)) {
                if (requestFields != null) {
                    params = convertmapObject(requestFields);
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, requestFields.toString());
                }
                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, gson.toJson(request.getParameterMap()));
                params.putAll(request.getParameterMap());


                result = fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS,
                        USER_SK + COLON + user.getUserID() +
                                AND + GROUP_NAME + COLON + ESCAPE_CHARACTER + APP_SERVICE_DESK + ESCAPE_CHARACTER,
                        null, new String[]{GROUP_NAME}
                        , null, null, null, null, 0, user);
                if (!result.isEmpty()) {
                    if (params.get(LEVEL) == null || params.get(LEVEL)[0].equalsIgnoreCase(NULL_VALUE))
                        tempParams.put(LEVEL, new String[]{NULL_VALUE});
                    else
                        tempParams.put(LEVEL, new String[]{NULL_VALUE, NUMBER_THREE});
                }
                //   params.putAll(tempParams);
                String whooshQuery = generateWhooshQueryDynamic(tempParams);
                result = new ArrayList<>();
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params
                        , endPointDomain.get(ENDPOINT_SK).toString(), user);
                if (customWhooshModel.getParams().isEmpty()) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYPARAMETERS", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                }
                result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                        , SFInterfaceBase.getWhooshQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams()) + AND + whooshQuery
                        , customWhooshModel.getSort()
                        , customWhooshModel.getRequiredColumns()
                        , customWhooshModel.getSortfields()
                        , customWhooshModel.getSortorder()
                        , customWhooshModel.getDistinct()
                        , customWhooshModel.getFieldaliass()
                        , customWhooshModel.getCount()
                        , Boolean.FALSE
                        , customWhooshModel.getDistinctfield()
                        , customWhooshModel.getPageno()
                        , Boolean.TRUE, user);
                if (result.size() == NUM_ZERO) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                }
                return returnResponseWithPagination(result);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vivek
     * <p>
     * Fetch AssignedGroup based on APP-SERVICE DESK
     *
     * @return - Returns the groups based on skillset
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires User_sk
     */
    @RequestMapping(path = AssignedGroup4_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchAssignedGroup4(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String userLoginID = "";
        String endpointName = "AssignedGroups1", methodType = BOTH_METHOD;
        try {
            User user = new User();
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            userLoginID = user.getAuthUserName();
            Map<String, String[]> tempParams = new HashMap<>();
            tempParams.put(LEVEL, new String[]{NULL_VALUE, NUMBER_THREE});
            if (StringUtils.isNotBlank(userLoginID)) {
                if (requestFields != null) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, requestFields.toString());
                    params = convertmapObject(requestFields);
                }
                params.putAll(request.getParameterMap());
                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, gson.toJson(request.getParameterMap()));
                result = fetchWhooshRecords(INDEXERNAME_FETCH_USERGROUPS,
                        USER_SK + COLON + user.getUserID() +
                                AND + GROUP_NAME + COLON + ESCAPE_CHARACTER + APP_SERVICE_DESK + ESCAPE_CHARACTER,
                        null, new String[]{GROUP_NAME}
                        , null, null, null, null, 0, user);
                if (!result.isEmpty()) {
                    if (params.get(LEVEL) == null || params.get(LEVEL)[0].equalsIgnoreCase(NULL_VALUE))
                        tempParams.put(LEVEL, new String[]{NULL_VALUE});
                    else
                        tempParams.put(LEVEL, new String[]{NULL_VALUE, NUMBER_THREE});
                }
                params.putAll(tempParams);
                result = new ArrayList<>();
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, Boolean.TRUE
                        , endPointDomain.get(ENDPOINT_SK).toString(), user);
                result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                        , customWhooshModel.getQuery()
                        , customWhooshModel.getSort()
                        , customWhooshModel.getRequiredColumns()
                        , customWhooshModel.getSortfields()
                        , customWhooshModel.getSortorder()
                        , customWhooshModel.getDistinct()
                        , customWhooshModel.getFieldaliass()
                        , customWhooshModel.getCount()
                        , Boolean.FALSE
                        , customWhooshModel.getDistinctfield()
                        , customWhooshModel.getPageno()
                        , Boolean.TRUE
                        , user);
                if (result.size() == NUM_ZERO) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
                }
                return returnResponseWithPagination(result);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vivek
     * Fetch ServiceNow TicketDetails
     *
     * @return - Returns the TicketDetails
     * @header - Requires an valid accesstoken or aToken
     * @input -  Requires Ticket
     */
    @GetMapping(path = SERVICENOW_FETCH_Ticket, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchServiceNowIncident(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestParam(value = BUSINESS_FUNCTION_CODE, required = FALSE, defaultValue = "incident") String
                    businessFunctionCode,
            @RequestParam(value = TICKET, required = true) String ticket,
            HttpServletRequest request) throws IOException {
        Map<String, Object> response = new HashMap<>();
        String endpointName = "SNOWTicket";
        try {
            response = SFInterfaceBase.fetchSNOWTicket(businessFunctionCode, ticket);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting CR Meeting Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CR_SCHEDULES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createCRSchedules(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRSchedules(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, CREATE_CR_SCHEDULES, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, CREATE_CR_SCHEDULES, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST_DETAILS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CR Meeting Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_SCHEDULES_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateCRSchedules(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRSchedules(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, UPDATE_CR_SCHEDULES, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, UPDATE_CR_SCHEDULES, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST_DETAILS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CR Closure Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CR_CLOSURE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createCRClosure(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRClosure(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, CREATE_CR_CLOSURE, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, CREATE_CR_CLOSURE, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST_DETAILS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CR Closure Details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_CLOSURE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateCRClosure(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRClosure(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, UPDATE_CR_CLOSURE, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, UPDATE_CR_CLOSURE, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST_DETAILS), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating Approver details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_APPROVER_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateApprover(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateRequestWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, UPDATE_APPROVERS, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, UPDATE_APPROVERS, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, UPDATE_CATEGORY), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting / updating CR Discussion Board details
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_DISCUSSION_BOARD_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ? extends Object>> updateCRDiscussionBoard(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken)
                    , trimToken(accessToken), ticketFields, UPDATE_CR_DISCUSSION_BOARD, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, UPDATE_CR_DISCUSSION_BOARD, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, UPDATE_CATEGORY), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for inserting ticket details when the system is down
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = "/{version}/CreateOfflineTickets")
    public ResponseEntity createTicketREmailTicket(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody TicketModel ticket
            , HttpServletRequest request) throws IOException {
        Map<String, Object> resultMap = new HashMap<>(), systemprofile = new HashMap<>(), ticketFailureinRflow = new HashMap<>(), mailFields = new HashMap<>();
        String endpoint_Name = "CreateOfflineTickets";
        List<Map<String, Object>> status = new ArrayList<>();
        User user = new User();
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceLoggerBase.log(LOGGER, endpoint_Name, ticket.toString());
            // account mapping
            String account = ticket.getClient().getText();
            //To validate the emailid for the given account
            String profilequery = OPEN_BRACKET + EMAIL_ADDRESS + COLON + ESCAPE_CHARACTER
                    + ticket.getEmail() + ESCAPE_CHARACTER
                    + ((ticket.getLoginName() != null && !ticket.getLoginName().isEmpty()) ? (
                    OR + NETWORK_LOGIN + COLON + ESCAPE_CHARACTER + ticket.getLoginName() + ESCAPE_CHARACTER + CLOSE_BRACKET
            ) : (CLOSE_BRACKET));

            List<Map<String, Object>> systemprofile1 = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, profilequery
                            + AND + OPEN_BRACKET + ACCOUNT + COLON + account
                            + CLOSE_BRACKET, null, null
                    , null, null, null, null, NUM_ZERO, false, null, 1, null, user);

            //To get Account_sk from the given account
            List<Map<String, Object>> accounts = fetchWhooshRecords(INDEXERNAME_FETCH_ACCOUNT, ACCOUNT + COLON + account, null,
                    new String[]{ACCOUNT_SK, MASTER_ACCOUNT, MASTER_ACCOUNT_SK}, null, null, null, null
                    , 0, false, null, 1, null, user);

            if (ticket.getTicketnumber() == null || ticket.getTicketnumber().isEmpty()) {
                if (systemprofile1.size() > NUM_ZERO) {
                    systemprofile = systemprofile1.get(NUM_ZERO);
                    if (systemprofile.containsKey(USER_SK) && StringUtils.isNotBlank((String) systemprofile.get(USER_SK))) {

                        //If the user is valid
                        String systemusersk = systemprofile.get(USER_SK).toString();
                        Map<String, Object> ticketFields = new HashMap<>();

                        // Get the equivalent_sk for the given building,department...
                        ticketFields.put(ACCOUNT, accounts.get(NUM_ZERO).get(ACCOUNT_SK));
                        ticketFields.put(MASTER_ACCOUNT, accounts.get(NUM_ZERO).get(MASTER_ACCOUNT_SK));
                        ticketFields.put(MASTER_ACCOUNT_CODE, accounts.get(NUM_ZERO).get(MASTER_ACCOUNT));
                        ticketFields.put(SUMMARY, ticket.getSummary());
                        resultMap = SFInterfaceBase.createTicketFromRflowOfflineTickets(ticketFields, ticket.getTickettype(), null,
                                null, aTokenValue, systemusersk, endpoint_Name, ticket, user);
                        if (resultMap.size() > NUM_ZERO && (resultMap.containsKey(ERROR) && StringUtils.isBlank((String) resultMap.get(ERROR)))) {
                            String tempticketnumber = resultMap.get(REFERENCE_NUMBER).toString();
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.VALIDUSERREQUESTSENTTORFLOW", " " + TICKET_NUMBER + " = " + tempticketnumber, endpoint_Name);
                            resultMap.put(TICKET_NUMBER, tempticketnumber);
                            return new ResponseEntity<>(resultMap, HttpStatus.OK);
                        } else {
                            return SFInterfaceBase.createTriageMail(ticket, version, endpoint_Name, user);
                        }
                    }
                } else {
                    return SFInterfaceBase.createTriageMail(ticket, version, endpoint_Name, user);
                }
            } else {
                return SFInterfaceBase.updateOfflineTicket(ticket, version, endpoint_Name, accessToken, refreshToken, aToken, user);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpoint_Name + ticket.toString(), e);
            return SFInterfaceBase.createTriageMail(ticket, version, endpoint_Name, user);
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for Cloning ticket details for the given CRNumber
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CLONE_CR_PATH)
    public ResponseEntity cloneCR(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CLONE_CR;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.cloneCR(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating BusinessFunction
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BUSINESS_FUNCTION_PATH)
    public ResponseEntity updateBusinessFunction(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BUSINESS_FUNCTION;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Source
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SOURCE_PATH)
    public ResponseEntity updateSource(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SOURCE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating BusinessTimeDetails
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BUSINESS_TIME_DETAILS_PATH)
    public ResponseEntity updateBusinessTimeDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BUSINESS_TIME_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating GroupType
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GROUP_TYPE_PATH)
    public ResponseEntity updateGroupType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_GROUP_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Queue
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_QUEUE_PATH)
    public ResponseEntity updateQueue(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_QUEUE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RCL Mapping
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RCL_MAPPING_PATH)
    public ResponseEntity updateRCLMapping(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RCL_MAPPING;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RCL Mapping
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ROOT_CAUSE_LEVEL_PATH)
    public ResponseEntity updateRootCauseLevel(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ROOT_CAUSE_LEVEL;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Group Level
     * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GROUP_LEVEL_PATH)
    public ResponseEntity updateGroupLevel(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_GROUP_LEVEL;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Account RoleUser
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SEC_ACCOUNT_ROLE_USER_PATH)
    public ResponseEntity updateSecAccountRoleUser(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SEC_ACCOUNT_ROLE_USER;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Status
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_STATUS_ENTITY_PATH)
    public ResponseEntity updateStatusEntity(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_STATUS_ENTITY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Status Entity Type
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_STATUS_ENTITY_TYPE_PATH)
    public ResponseEntity updateStatusEntityType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_STATUS_ENTITY_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Task
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TASK_PATH)
    public ResponseEntity updateTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_TASK;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Task Group
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TASK_GROUP_PATH)
    public ResponseEntity updateTaskGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_TASK_GROUP;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SEC_AutoClose
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_AUTO_CLOSE_PATH)
    public ResponseEntity updateAutoClose(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_AUTO_CLOSE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Ldap Details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_LDAP_DETAILS_PATH)
    public ResponseEntity updateLdapDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_LDAP_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function fetching changerequest with userfavorites
     * * * @return - changerequest for the user with userfavorites
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = CHANGE_REQUEST2, method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity changeRequest1(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        String endpointName = CHANGE_REQUEST + "2";
        Map<String, Object> endPointDomain = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, POST_METHOD, user);
            String[] approverGroup = new String[]{}, approvers = new String[]{};
            EndpointValidationModel endpointValidationModel =
                    SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            if (endpointValidationModel.getRequestparametermap().containsKey(APPROVER_GROUP)) {
                approverGroup = endpointValidationModel.getRequestparametermap().get(APPROVER_GROUP);
                endpointValidationModel.getRequestparametermap().remove(APPROVER_GROUP);
            }
            if (endpointValidationModel.getRequestparametermap().containsKey(APPROVERS)) {
                approvers = endpointValidationModel.getRequestparametermap().get(APPROVERS);
                endpointValidationModel.getRequestparametermap().remove(APPROVERS);
            }

            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap()
                    , Boolean.TRUE
                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
            if (customWhooshModel.getRequiredColumns() != null && customWhooshModel.getRequiredColumns().length > 0) {
                customWhooshModel.setRequiredColumns(SFInterfaceBase.removevaluefromArray(
                        customWhooshModel.getRequiredColumns(),
                        USER_FAVOURITE));
            }
            List<Map<String, Object>> result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                    , customWhooshModel.getQuery()
                    , customWhooshModel.getSort()
                    , customWhooshModel.getRequiredColumns()
                    , customWhooshModel.getSortfields()
                    , customWhooshModel.getSortorder()
                    , customWhooshModel.getDistinct()
                    , null
                    , customWhooshModel.getCount()
                    , Boolean.FALSE
                    , customWhooshModel.getDistinctfield()
                    , customWhooshModel.getPageno()
                    , Boolean.FALSE, user);

            if (!result.isEmpty()) {

                List<Map<String, Object>> userFavourites = new ArrayList<>();

                userFavourites = fetchWhooshRecords(CR_USER_FAVOURITES, USER_SK + COLON + user.getUserID()
                        , null, new String[]{TICKET}, null
                        , null, TRUE, null, NUM_ZERO
                        , null, null, user);
                List<Map<String, Object>> finalUserFavourites = userFavourites;
                result = result.stream().map(
                        eachRequest -> {
                            eachRequest.put(USER_FAVOURITE,
                                    finalUserFavourites.parallelStream()
                                            .anyMatch(
                                                    stringObjectMap -> stringObjectMap.get(TICKET).toString()
                                                            .equalsIgnoreCase(eachRequest.get(TICKET).toString())
                                            )
                            );
                            return eachRequest;
                        }
                ).collect(Collectors.toList());
                result = result.parallelStream().sorted((o1, o2) -> SFInterfaceBase.booleancomparereverse((boolean) o1.get(USER_FAVOURITE), (boolean) o2.get(USER_FAVOURITE)))
                        .collect(Collectors.toList());
                result = mapChangeRequestApprover(result, endpointValidationModel.getRequestparametermap().get(ACCOUNT), user, approvers, approverGroup);
            }
            return SFInterfaceServices.returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap()
                    , false, user);

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function fetching changerequest with userfavorites
     * * * @return - changerequest for the user with userfavorites
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/" + CHANGE_REQUEST, method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity changeRequest1withUserFavorites(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();

        String endpointName = CHANGE_REQUEST;
        Map<String, Object> endPointDomain = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, POST_METHOD, user);
            EndpointValidationModel endpointValidationModel =
                    SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap()
                    , Boolean.TRUE
                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
            if (customWhooshModel.getRequiredColumns() != null && customWhooshModel.getRequiredColumns().length > 0) {
                customWhooshModel.setRequiredColumns(SFInterfaceBase.removevaluefromArray(
                        customWhooshModel.getRequiredColumns(),
                        USER_FAVOURITE));
            }
            List<Map<String, Object>> result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                    , customWhooshModel.getQuery()
                    , customWhooshModel.getSort()
                    , customWhooshModel.getRequiredColumns()
                    , customWhooshModel.getSortfields()
                    , customWhooshModel.getSortorder()
                    , customWhooshModel.getDistinct()
                    , null
                    , customWhooshModel.getCount()
                    , Boolean.FALSE
                    , customWhooshModel.getDistinctfield()
                    , customWhooshModel.getPageno()
                    , Boolean.FALSE, user);

            if (!result.isEmpty()) {

                List<Map<String, Object>> userFavourites = new ArrayList<>();

                userFavourites = fetchWhooshRecords(CR_USER_FAVOURITES, USER_SK + COLON + user.getUserID()
                        , null, new String[]{TICKET}, null
                        , null, TRUE, null, NUM_ZERO
                        , null, null, user);
                List<Map<String, Object>> finalUserFavourites = userFavourites;
                result = result.stream().map(
                        eachRequest -> {
                            eachRequest.put(USER_FAVOURITE,
                                    finalUserFavourites.parallelStream()
                                            .filter(
                                                    stringObjectMap ->
                                                            checkBlankorEmpty((String) stringObjectMap.get(TICKET))
                                            )
                                            .anyMatch(
                                                    stringObjectMap -> stringObjectMap.get(TICKET).toString()
                                                            .equalsIgnoreCase(eachRequest.get(TICKET).toString())
                                            )
                            );
                            return eachRequest;
                        }
                ).collect(Collectors.toList());
                result = result.parallelStream().sorted((o1, o2) -> SFInterfaceBase.booleancomparereverse((boolean) o1.get(USER_FAVOURITE), (boolean) o2.get(USER_FAVOURITE)))
                        .collect(Collectors.toList());

            }
            return SFInterfaceServices.returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap()
                    , false, user);

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Link_BldDept Details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BLD_DEPT_PATH)
    public ResponseEntity updateBldDeptDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BLD_DEPT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vivek
     * <p>
     * Fetch menus details
     *
     * @return - Returns the menus as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input - Requires User_sk
     * @input - ParentObjectCode is an mandatory input
     */
    @RequestMapping(value = ADMIN_SCREEN_MENUS, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity fetchMenus(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> finalResult1 = result;
        Map<String, Object> errorMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        Map<String, String[]> requestparams = new HashMap<>();
//        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Set<String>> objectTypeCode = new HashMap<>();
        String loginID = "", query = "";
        String[] objType = new String[]{};
        String objTypeQuery = "";
        Set<String> objectTypeSet = new HashSet<>();

        Map<String, Object> resultMap1 = new HashMap<>();

        String endpointName = OBJECT_PERMISSION1;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {

                if (requestFields != null)
                    params = convertmapObject(requestFields);
                params.putAll(request.getParameterMap());
                SFInterfaceLoggerBase.log(LOGGER, endpointName, params.toString());
                requestparams.putAll(params);       //to validate the request params after the modification
                UserAccountNGroup userAccountAGroup = new UserAccountNGroup();

                if (params.containsKey(USER_SK)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(null, (String) params.get(USER_SK)[NUM_ZERO], user);
                    params.remove(USER_SK);
                } else if (params.containsKey(LOGIN_ID)) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup((String) params.get(LOGIN_ID)[NUM_ZERO], null);
                    params.remove(LOGIN_ID);
                } else if (StringUtils.isBlank(atoken) && (StringUtils.isNotBlank(accessToken) || StringUtils.isNotBlank(refreshToken))) {
                    userAccountAGroup = SFInterfaceBase.fetchUserAccountNGroup(user.getAuthUserName(), user.getUserID(), user);
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.UNAUTHORIZED);
                }

                //ObjectTypeQuery
                if (params.containsKey(OBJECT_TYPE)) {
                    objType = params.get(OBJECT_TYPE);
                    List<String> objTypeList = new ArrayList<>();
                    objTypeList.addAll(Arrays.asList(objType));
                    objTypeList.addAll(Arrays.asList(objType[0].split(",")));
                    objTypeQuery = OPEN_BRACKET + objTypeList
                            .stream()
                            .map(s -> OBJECT_TYPE + COLON + ESCAPE_CHARACTER + s + ESCAPE_CHARACTER)
                            .collect(Collectors.joining(OR)) + CLOSE_BRACKET;
                }

                String queryPermissions = STATUS_CODE + COLON + "A";
                String ParentObjectCode = null;
                if (requestparams.containsKey(PARENT_OBJECT_CODE)) {
                    ParentObjectCode = requestparams.get(PARENT_OBJECT_CODE)[0];
                    queryPermissions += AND + PARENT_OBJECT_CODE + COLON + ParentObjectCode;
                }
                if (StringUtils.isNotBlank(objTypeQuery))
                    queryPermissions += AND + objTypeQuery;

                result = fetchWhooshRecords(INDEXERNAME_FETCH_OBJECT_PERMISSIONS, queryPermissions, null
                        , new String[]{OBJECT_TYPE, OBJECT_CODE, PARENT_OBJECT_CODE}, null, null, "true",
                        null, NUM_ZERO, Boolean.FALSE, "ObjectType,ObjectCode", null, null, user);

                result.forEach(eachresult -> {  //
                    Set<String> tempHashSet = new HashSet<>();
                    if (objectTypeCode.containsKey(eachresult.get(OBJECT_TYPE).toString())) {
                        tempHashSet = objectTypeCode.get(eachresult.get(OBJECT_TYPE).toString());
                        tempHashSet.add(eachresult.get(OBJECT_CODE).toString());
                        objectTypeCode.put(eachresult.get(OBJECT_TYPE).toString(), tempHashSet);
                    } else {
                        tempHashSet.add(eachresult.get(OBJECT_CODE).toString());
                        objectTypeCode.put(eachresult.get(OBJECT_TYPE).toString(), tempHashSet);
                    }
                });

            /*    //Group Query
                userAccountAGroup.getUserGroups().addAll(userAccountAGroup.getUserPermission());
                if (!userAccountAGroup.getUserGroups().isEmpty()) {
                    query = OPEN_BRACKET + userAccountAGroup.getUserGroups().stream().map(
                            group -> ASSIGNED_GROUP_NAME + COLON + ESCAPE_CHARACTER + group.get(GROUP_NAME) + ESCAPE_CHARACTER
                    ).collect(Collectors.joining(OR)) + OR + ASSIGNED_GROUP_NAME + COLON + NULL_VALUE + CLOSE_BRACKET;
                }*/

                if (!(userAccountAGroup.getAdministrator() && userAccountAGroup.getPrimaryMasterAccount().equalsIgnoreCase(CTS))) {
                    //AccountGroup_sk Query
                    if (!userAccountAGroup.getAccountGroup_sk().isEmpty()) {
                        query = OPEN_BRACKET + userAccountAGroup.getAccountGroup_sk().stream().map(
                                group -> ACCOUNT_GROUP_SK + COLON + ESCAPE_CHARACTER + group + ESCAPE_CHARACTER
                        ).collect(Collectors.joining(OR)) + OR + ACCOUNT_GROUP_SK + COLON + NULL_VALUE + CLOSE_BRACKET;
                    }

                    //Account Query
                    if (!userAccountAGroup.getUserAccounts().isEmpty()) {
                        query += checkBlankorEmpty(query) ? (query + AND) : query;
                        query += OPEN_BRACKET + userAccountAGroup.getUserAccounts().stream().map(
                                group -> ACCOUNT + COLON + ESCAPE_CHARACTER + group.get(ACCOUNT) + ESCAPE_CHARACTER
                        ).collect(Collectors.joining(OR)) + OR + ACCOUNT + COLON + NULL_VALUE + CLOSE_BRACKET;
                    }

                    //User Query
                    if (checkBlankorEmpty(user.getUserID())) {
                        query += checkBlankorEmpty(query) ? (query + AND) : query;
                        query += OPEN_BRACKET + USER_SK + COLON + ESCAPE_CHARACTER + user.getUserID() + ESCAPE_CHARACTER +
                                OR + USER_SK + COLON + NULL_VALUE + CLOSE_BRACKET;
                    }
                }
                //ObjectTypeQuery
                if (params.containsKey(OBJECT_TYPE)) {
                    params.remove(OBJECT_TYPE);
                    if (StringUtils.isNotBlank(query) && StringUtils.isNotBlank(objTypeQuery)) {
                        query += AND + objTypeQuery;
                    } else if (checkBlankorEmpty(objTypeQuery)) {
                        query += objTypeQuery;
                    }
                }
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                if (StringUtils.isNotBlank(query) && StringUtils.isNotBlank(customWhooshModel.getQuery())) {
                    customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + query);
                } else if (StringUtils.isNotBlank(query)) {
                    customWhooshModel.setQuery(query);
                }

                List<Map<String, Object>> finalResult = fetchWhooshRecords(INDEXERNAME_FETCH_OBJECT_PERMISSIONS, customWhooshModel.getQuery(), null
                        , null, null, null, null, null, NUM_ZERO, Boolean.FALSE, null, null, null, user);

               /* if (result.size() == NUM_ZERO) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity(errorMap, HttpStatus.OK);
                }*/

                if (requestparams.containsKey(OBJECT_TYPE)) {
                    objectTypeSet = Arrays.stream(requestparams.get(OBJECT_TYPE)).collect(Collectors.toSet());
                    objectTypeSet.addAll(Arrays.stream(requestparams.get(OBJECT_TYPE)[0].split(",")).collect(Collectors.toSet()));
                } else {
                    objectTypeSet = objectTypeCode
                            .entrySet()
                            .stream()
                            .map(objectType -> objectType.getKey())
                            .collect(Collectors.toSet());
                }
                String finalParentObjectCode = ParentObjectCode;

                objectTypeSet.stream().forEach(objectType -> {
                    Set<String> objectCode = objectTypeCode.get(objectType);
                    if (objectCode != null && !objectCode.isEmpty()) {
                        List<Map<String, Object>> objCodeResult = new ArrayList<>();
                        objectCode.stream().forEach(objCode -> {
                            String[] viewPermission = {ZERO};
                            String[] addPermission = {ZERO};
                            String[] deletePermission = {ZERO};
                            String[] editPermission = {ZERO};
                            String[] isMandatoryPermission = {ZERO};
                            Map<String, Object> resultMap = new HashMap<>();
                            finalResult.stream().forEach(rs -> {
                                if (rs.get(OBJECT_TYPE).toString().equalsIgnoreCase(objectType) && rs.get(OBJECT_CODE).toString().equalsIgnoreCase(objCode)) {
                                    resultMap.put(OBJECT_CODE, (String) rs.get(OBJECT_CODE));
                                    resultMap.put(OBJECT_TYPE, objectType);
                                    resultMap.put(PARENT_OBJECT_CODE, (String) rs.get(PARENT_OBJECT_CODE));
                                    resultMap.put(PARENT_OBJECT_SK, (String) rs.get(PARENT_OBJECT_SK));
                                    resultMap.put(OBJECT_SK, (String) rs.get(OBJECT_SK));
                                    resultMap.put(THUMBNAIL_URL, (String) rs.get(THUMBNAIL_URL));
                                    resultMap.put(SORTORDER, (String) rs.get(SORTORDER));
                                    resultMap.put(NAVICON, (String) rs.get(NAVICON));
                                    resultMap.put(CSS_CLASS, (String) rs.get(CSS_CLASS));
                                    resultMap.put(TARGET, (String) rs.get(TARGET));
                                    resultMap.put(JSON_ATTRIBUTE, (String) rs.get(JSON_ATTRIBUTE));
                                    resultMap.put(OBJECT, (String) rs.get(OBJECT));
                                    resultMap.put(OBJECT_URL, (String) rs.get(OBJECT_URL));
                                    resultMap.put(DISPLAYNAME, (String) rs.get(DISPLAYNAME));
                                    resultMap.put(OBJECT_DESCRIPTION, (String) rs.get(OBJECT_DESCRIPTION));
                                    viewPermission[0] = (!isNull(rs.get(VIEW)) && rs.get(VIEW).toString().equals(ONE)) ? ONE : viewPermission[0];
                                    addPermission[0] = (!isNull(rs.get(ADD)) && rs.get(ADD).toString().equals(ONE)) ? ONE : addPermission[0];
                                    deletePermission[0] = (!isNull(rs.get(DELETE)) && rs.get(DELETE).toString().equals(ONE)) ? ONE : deletePermission[0];
                                    editPermission[0] = (!isNull(rs.get(EDIT)) && rs.get(EDIT).toString().equals(ONE)) ? ONE : editPermission[0];
                                    isMandatoryPermission[0] = (rs.get(IS_MANDATORY) != null) ? (rs.get(IS_MANDATORY).equals(ONE) ? ONE : isMandatoryPermission[0]) : isMandatoryPermission[0];
                                /*    resultMap.put(VIEW, viewPermission[0]);
                                    resultMap.put(ADD, addPermission[0]);
                                    resultMap.put(DELETE, deletePermission[0]);
                                    resultMap.put(EDIT, editPermission[0]);
                                    resultMap.put(IS_MANDATORY, isMandatoryPermission[0]);
                                    objCodeResult.add(resultMap);*/
                                }
                            });
                           /* if (resultMap.isEmpty()) {
                                resultMap.put(OBJECT_CODE, objCode);
                                resultMap.put(OBJECT_TYPE, objectType);
                                resultMap.put(PARENT_OBJECT_CODE, finalParentObjectCode);
                                resultMap.put(PARENT_OBJECT_SK, null);
                                resultMap.put(OBJECT_SK, null);
                                resultMap.put(THUMBNAIL_URL, null);
                                resultMap.put(SORTORDER, null);
                                resultMap.put(NAVICON, null);
                                resultMap.put(CSS_CLASS, null);
                                resultMap.put(TARGET, null);
                                resultMap.put(JSON_ATTRIBUTE, null);
                                resultMap.put(OBJECT, null);
                                resultMap.put(OBJECT_URL, null);
                                resultMap.put(DISPLAYNAME, null);
                                resultMap.put(OBJECT_DESCRIPTION, null);
                            }*/
                            resultMap.put(VIEW, viewPermission[0]);
                            resultMap.put(ADD, addPermission[0]);
                            resultMap.put(DELETE, deletePermission[0]);
                            resultMap.put(EDIT, editPermission[0]);
                            resultMap.put(IS_MANDATORY, isMandatoryPermission[0]);
                            objCodeResult.add(resultMap);
                        });
                        // resultMap1.put(objectType, objCodeResult);
                        finalResult1.addAll(objCodeResult);
                    }
                });
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(finalResult1, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Link_BldSuite Details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BLD_SUITE_PATH)
    public ResponseEntity updateBldSuiteDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BLD_SUITE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Link_DeptFlr Details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DEPT_FLR_PATH)
    public ResponseEntity updateDepartmentFloorDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DEPT_FLR;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Building Floor Details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BLD_FLR_PATH)
    public ResponseEntity updateBuildingFloorDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BLD_FLR;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating DepartmentSuite etails
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DEPT_SUITE_PATH)
    public ResponseEntity updateDepartmentSuiteDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DEPT_SUITE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating BldCategory etails
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BLD_CATEGORY_PATH)
    public ResponseEntity updateBuildingCategoryDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BLD_CATEGORY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CRLocations etails
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_LOCATIONS_PATH)
    public ResponseEntity updateCRLocations(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CR_LOCATIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateCRDetails(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating OPSBOT_AlertAccountRel details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ALERT_ACCOUNT_REL_PATH)
    public ResponseEntity updateAlertAccountRel(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ALERT_ACCOUNT_REL;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_ContactType details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CONTACT_TYPE_PATH)
    public ResponseEntity updateContactType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CONTACT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Objects details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_OBJECTS_PATH)
    public ResponseEntity updateObjects(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_OBJECTS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating KB_TypeAheadCategories details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_TYPE_AHEAD_CATEGORIES_PATH)
    public ResponseEntity updateKBTypeAheadCategories(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_KB_TYPE_AHEAD_CATEGORIES;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Geo_city details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GEO_CITY_PATH)
    public ResponseEntity updateGeoCity(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_GEO_CITY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Geo_country details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GEO_COUNTRY_PATH)
    public ResponseEntity updateGeoCountry(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_GEO_COUNTRY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Geo_State details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_GEO_STATE_PATH)
    public ResponseEntity updateGeoState(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_GEO_STATE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs1(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Entity details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ENTITY_PATH)
    public ResponseEntity updateEntity(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ENTITY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CFG_EntityLink details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ENTITY_LINK_PATH)
    public ResponseEntity updateEntityLink(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ENTITY_LINK;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CFG_Link details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_LINKS_PATH)
    public ResponseEntity updateLink(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_LINKS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIsWithMultipleInputs(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating AST_Category details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ASSET_CATEGORY_PATH)
    public ResponseEntity updateAssetCategory(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ASSET_CATEGORY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating AST_Attachment details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ASSET_ATTACHMENT_PATH)
    public ResponseEntity updateAssetAttachment(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ASSET_ATTACHMENT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Asset Procurement details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ASSET_PROCUREMENT_PATH)
    public ResponseEntity updateAssetProcurement(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ASSET_PROCUREMENT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Asset Procurement details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ASSET_PROCUREMENT_DETAILS_PATH)
    public ResponseEntity updateAssetProcurementDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ASSET_PROCUREMENT_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CR Cab Member details
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_CAB_MEMBER_PATH)
    public ResponseEntity updateCRCabMeber(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CR_CAB_MEMBER;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CR RequestType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_REQUEST_TYPE_PATH)
    public ResponseEntity updateCRRequestType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CR_REQUEST_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CR Milestone
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_MILESTONE_PATH)
    public ResponseEntity updateCRMilestone(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CR_MILESTONE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating CR Milestone
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CR_PROJECT_PATH)
    public ResponseEntity updateCRProject(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CR_PROJECT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SLM Aggrement Type
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SLM_AGREEMENT_TYPE_PATH)
    public ResponseEntity updateSlmAgreeementType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SLM_AGREEMENT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating OPSBOTAlerts
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_OPSBOT_ALERTS_PATH)
    public ResponseEntity updateOPSBOTAlerts(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_OPSBOT_ALERTS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SLM Definitions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SLM_DEFINITION_PATH)
    public ResponseEntity updateSLMDefintion(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SLM_DEFINTION;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs2(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating SLM Definitions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ENTITY_LINKS_PATH)
    public ResponseEntity updateEntityLinks(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestParam(value = "LinkName") String linkname,
            @RequestParam(value = ACCOUNT_SK) String account_sk,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        String endpointName = UPDATE_ENTITY_LINKS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createORUpdateEntities(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, linkname, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating SLM Definitions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = "/{version}/UpdateEntities")
    public ResponseEntity updateEndpoints(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestParam(value = "entity") String entity,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        String endpointName = "UpdateEntites";
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createORUpdateEntitiess(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, entity, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating SLM Definitions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = FETCH_ENTITY_LINKS_PATH, method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchEntityLinks(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestParam(value = "LinkName") String linkname,
            @RequestParam(value = ACCOUNT_SK, required = false) String account_sk,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        String endpointName = FETCH_ENTITY_LINKS;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.fetchEntityLinks(GET_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, linkname, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Inserting/Updating SLM insert client AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_ACCOUNT_PATH)
    public ResponseEntity createDMartAccount(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_ACCOUNT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Inserting/Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_FDN_PEOPLE_INFO_PATH)
    public ResponseEntity createFdnPeopleInfo(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_FDN_PEOPLE_INFO;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting Group Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_GROUP_PATH)
    public ResponseEntity createDMartGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_GROUP;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting User Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_USER_PATH)
    public ResponseEntity createDMartUser(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_USER;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting Group Options Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_GRP_OPTIONS_PATH)
    public ResponseEntity createDMartGrpOptions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_GRP_OPTIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Update client info  AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_ACCOUNT_PATH)
    public ResponseEntity updateDMartAccount(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_ACCOUNT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Updating Group Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_GROUP_PATH)
    public ResponseEntity updateDMartGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_GROUP;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Updating User Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_USER_PATH)
    public ResponseEntity updateDMartUser(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_USER;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Updating User Info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_GRP_OPTIONS_PATH)
    public ResponseEntity updateDMartGrpOptions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_GRP_OPTIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SLM GoalType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SLM_GOAL_TYPE_PATH)
    public ResponseEntity updateSLMGoalType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SLM_GOALTYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RDY Attachments
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_ATTACHMENTS_PATH)
    public ResponseEntity updateRdyAttachments(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_ATTACHMENTS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RDY Mapping SLA
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_MAPPING_SLA_PATH)
    public ResponseEntity updateRdyMappingSLA(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_MAPPING_SLA;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RDY Mapping
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_MAPPING_PATH)
    public ResponseEntity updateRdyMapping(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_MAPPING;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_FDN_PEOPLE_INFO_PATH)
    public ResponseEntity updateFdnPeopleInfo(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_FDN_PEOPLE_INFO;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.updateDmartAPI(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_CMN_CATEGORIZATION_PATH)
    public ResponseEntity createDMartCMNCategorization(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_CMN_CATEGORIZATION;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.CreateDMartCMNCategorization(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CMN_CATEGORIZATION_PATH)
    public ResponseEntity UpdateDMartCMNCategorization(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CMN_CATEGORIZATION;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.UpdateDMartCMNCategorization(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating KBArticleType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_ARTICLE_TYPE_PATH)
    public ResponseEntity updateKBArticleType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_KB_ARTICLE_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating KBContentType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_CONTENT_TYPE_PATH)
    public ResponseEntity updateKBContentType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_KB_CONTENT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating KBDocContentType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_DOC_CONTENT_TYPE_PATH)
    public ResponseEntity updateKBDocContentType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_KB_DOC_CONTENT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating KBDocumentType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_KB_DOCUMENT_TYPE_PATH)
    public ResponseEntity updateKBDocumentType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_KB_DOCUMENT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating ObjectPermissions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_OBJECT_PERMISSIONS_PATH)
    public ResponseEntity updateObjectPermissions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_OBJECT_PERMISSIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating RDYQueries
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_QUERIES_PATH)
    public ResponseEntity updateRDYQueries(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_QUERIES;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs4(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vani
     * The Function is used for Inserting/Updating RDYRootCause
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_ROOTCAUSE_PATH)
    public ResponseEntity updateRDYRootCause(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_ROOTCAUSE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs4(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = Create_Update_RDYKB_Phrases_PATH)
    public ResponseEntity CreateUpdateRDYKBPhrases(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = Create_Update_RDYKB_Phrases;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.CreateUpdateRDYKBPhrases(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = Create_Update_RDYKBPhraseDetails_PATH)
    public ResponseEntity CreateUpdateRDYKBPhraseDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = Create_Update_RDYKBPhraseDetails;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs3(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = Create_Update_FDNBackUp_Approvers_PATH)
    public ResponseEntity CreateUpdateFDNBackUpApprovers(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = Create_Update_FDNBackUp_Approvers;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.CreateUpdateFDNBackUpApprovers(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = Create_Update_FDN_Controls_PATH)
    public ResponseEntity CreateUpdateFDN_Controls(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = Create_Update_FDN_Controls;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.CreateUpdateFDN_Controls(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Afroze
     * The Function is used for Updating SLM insert FDN people info
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = Create_Update_FDN_Support_Users_PATH)
    public ResponseEntity UpdateFDNSupportUsers(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = Create_Update_FDN_Support_Users;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.CreateUpdateFDNSupportUsers(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, CREATE_CHANGE_REQUEST), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for fetching objects in different level
     * * * @return - Returns objects  as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @GetMapping(path = FETCH_OBJECTS_PATH)
    public ResponseEntity fetchObjects(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = FALSE) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, Object> endPointDomain = new HashMap<>();

        List<Map<String, Object>> result = new ArrayList<>(), finalResult = new ArrayList<>(), mainsetResult = new ArrayList<>(), childResult = new ArrayList<>(), tempfinalResult = new ArrayList<>();
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        String loginID = "";
        String endpointName = "Objects";
        Map<String, String[]> mainsetParams = new HashMap<>();
        Map<String, String[]> childsetParams = new HashMap<>();
        String objectType = "", objectCode = "";

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                if (endPointDomain != null) {
                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(endpointValidationModel.getRequestparametermap());
                    mainsetParams = customWhooshModel.getParams();
                    mainsetParams.put(STATUS, new String[]{"A"});
                    childsetParams.put(STATUS, new String[]{"A"});
                    if (customWhooshModel.getParams().containsKey(OBJECT_CODE))
                        objectCode = customWhooshModel.getParams().get(OBJECT_CODE)[0].toString();
                    if (customWhooshModel.getParams().containsKey(OBJECT_TYPE))
                        objectType = customWhooshModel.getParams().get(OBJECT_TYPE)[0].toString();

                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), childsetParams);
                    //fetch all objects
                    SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, refdbquery, version, endpointValidationModel.getRequestparametermap().get(TICKET) != null ? endpointValidationModel.getRequestparametermap().get(TICKET)[0].toString() : "", user.getUserID(), user.getAuthUserName(), null);
                    result = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                            , refdbquery
                            , customWhooshModel.getRequiredColumns()
                            , customWhooshModel.getSortfields()
                            , customWhooshModel.getSortorder()
                            , customWhooshModel.getDistinct()
                            , customWhooshModel.getDistinctfield()
                            , customWhooshModel.getPageno()
                            , customWhooshModel.getCount()
                            , Boolean.FALSE);

                    mainsetResult = getResultsfilteredbyObjectCode(result, objectCode, objectType);


                    childResult = getResultsfilteredbyObjectType(mainsetResult, objectCode);


//filter based on object permissions
                    String endpointName1 = INDEXERNAME_FETCH_OBJECT_PERMISSIONS;
                    EndpointValidationModel endpointValidationModelobjectPermissions = SFInterfaceBase.validateEndpoint(endpointName1, ticketFields, request.getParameterMap());

                    CustomWhooshModel customWhooshModel1 = SFInterfaceBase.generateWhooshModelForSP(endpointValidationModelobjectPermissions.getRequestparametermap());
                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName1, version, GET_METHOD, user);
                    customWhooshModel1.setRequiredColumns(null);

                    UserAccountNGroup userAccountNGroup = fetchUserAccountNGroup(null, user.getUserID(), user);


                    if (!(userAccountNGroup.getAdministrator() && userAccountNGroup.getPrimaryMasterAccount().equalsIgnoreCase(CTS))) {//load objects based on permissions other than administrator
                        List<Map<String, Object>> objectPermissions = SFInterfaceBase.fetchObjectPermissions(customWhooshModel1, endPointDomain, user);
                        mainsetResult = mainsetResult.stream()
                                .filter(stringObjectMap -> StringUtils.isNotBlank((String) stringObjectMap.get(OBJECT_SK)))
                                .filter(stringObjectMap -> {
                                    return objectPermissions.parallelStream()
                                            .filter(stringObjectMap1 -> StringUtils.isNotBlank((String) stringObjectMap1.get(OBJECT_SK)))
                                            .anyMatch(stringObjectMap1 -> stringObjectMap1.get(OBJECT_SK).toString().equalsIgnoreCase((String) stringObjectMap.get(OBJECT_SK)));

                                }).collect(Collectors.toList());

                        childResult = childResult.stream()
                                .filter(stringObjectMap -> StringUtils.isNotBlank((String) stringObjectMap.get(OBJECT_SK)))
                                .filter(stringObjectMap -> {
                                    return objectPermissions.parallelStream()
                                            .filter(stringObjectMap1 -> StringUtils.isNotBlank((String) stringObjectMap1.get(OBJECT_SK)))
                                            .anyMatch(stringObjectMap1 -> stringObjectMap1.get(OBJECT_SK).toString().equalsIgnoreCase((String) stringObjectMap.get(OBJECT_SK)));
                                }).collect(Collectors.toList());
                    }
                    result = getChildElements(mainsetResult, childResult);//
                    if (result.size() == NUM_ZERO) {
                        Map<String, Object> resultMap = getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.OK);
                    }
                } else {
                    throwException(endpointName + "endpoint Not Found!");
                }
            }

            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating IAM Application
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_IAM_APPLICATION_PATH)
    public ResponseEntity updateIamApplication(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_IAM_APPLICATION;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating WorknotesUpdateType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_WORKNOTES_TYPE_PATH)
    public ResponseEntity updateWorknotesUpdateType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_WORKNOTES_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_EMailOptions
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_EMAIL_OPTIONS_PATH)
    public ResponseEntity updateEmailOptions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_EMAIL_OPTIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_CaseType
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_CASE_TYPE_PATH)
    public ResponseEntity updateCaseType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_CASE_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating Bookmark
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_BOOKMARKS_PATH)
    public ResponseEntity updateBookmark(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_BOOKMARKS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_ApprovalCriteria
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_APPROVAL_CRITERIA_PATH)
    public ResponseEntity updateApprovalCriteria(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_APPROVAL_CRITERIA;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for fetching permissions based on user access
     * * * @return - returns permissions
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = "/{version}/Admin/ObjectPermissions1")
    public ResponseEntity fetchObjectPermissions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> endPointDomain = new HashMap<>();
        String endpointName = "Admin/ObjectPermissions";
        User user = new User();
        EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (user.getAuthUserName() != null) {
                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(endpointValidationModel.getRequestparametermap());
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                result = SFInterfaceBase.fetchObjectPermissions(customWhooshModel, endPointDomain, user, true);

            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }

    /**
     * Author - Vivek
     * The Function is used for fetching permissions based on user access
     * * * @return - returns permissions
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/Approvers1", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchApprovals(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        String endpointName = "Approvers";
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        User user = new User();
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
            endpointValidationModel = validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            UserAccountNGroup userAccountNGroup = fetchUserAccountNGroup(null, user.getUserID(), user);
            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap(), Boolean.TRUE
                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
            SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version,
                    endpointValidationModel.getRequestparametermap().get(TICKET) != null ? endpointValidationModel.getRequestparametermap().get(TICKET)[0].toString() : "",
                    user.getUserID(), user.getAuthUserName(), null);
            result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                    , customWhooshModel.getQuery()
                    , customWhooshModel.getSort()
                    , customWhooshModel.getRequiredColumns()
                    , customWhooshModel.getSortfields()
                    , customWhooshModel.getSortorder()
                    , customWhooshModel.getDistinct()
                    , customWhooshModel.getFieldaliass()
                    , customWhooshModel.getCount()
                    , Boolean.FALSE
                    , customWhooshModel.getDistinctfield()
                    , customWhooshModel.getPageno()
                    , Boolean.TRUE, user);
            if (!result.isEmpty()) {
                User finalUser = user;
                result.stream()
                        .map(stringObjectMap ->
                                appendUserinapproval((List<Map<String, Object>>) stringObjectMap.get(RESULT), userAccountNGroup, finalUser)
                        )
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating FDN_ApprovalCriteria
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @GetMapping(path = "/{version}/CRAttachments1")
    public ResponseEntity fetchAttachments(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {

        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>(), finalresult = new ArrayList<>();

        String loginID = "", businessfnsk = "", ticket_sk = "", platformsk = "", crcategorysk = "", crsystemsk = "", pendingreasonsk = "";
        String endpointName = CHANGE_REQUEST_DETAILS;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(TICKET) || request.getParameterMap().containsKey(ACCOUNT)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    String[] requiredColumns = customWhooshModel.getRequiredColumns();

                    List<String> endpoints = new ArrayList<>();

                    endpoints.add(INDEXERNAME_FETCH_CHANGE_REQUEST1);
                    endpoints.add(INDEXERNAME_FETCH_CR_TASKS1);
                    for (String endpoint : endpoints) {
                        result = new ArrayList<>();
                        List<Map<String, Object>> assignmentResult = new ArrayList<>(), crtAttachments = new ArrayList<>();
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, version, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                if (ticket_sk != null && !ticket_sk.isEmpty()) {
                                    Map<String, String[]> parameter = new HashMap<>();
                                    parameter.put(CR_SK, new String[]{ticket_sk});
                                    String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                                    result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                            , new String[]{TICKET, TICKET_SK, BUSINESS_FUNCTION_SK, BUSINESS_FUNCTION_CODE, TASK_NAME, TASK_TYPE});
                                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(CRT_ATTACHMENTS, version, GET_METHOD, user);
                                    for (Map<String, Object> res : result) {
                                        String CRT_sk = (String) res.get(TICKET_SK);
                                        Map<String, String[]> parameter1 = new HashMap<>();
                                        parameter1.put(TICKET, new String[]{CRT_sk});
                                        String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                        assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                                , null);
                                        if (!assignmentResult.isEmpty()) {
                                            assignmentResult.forEach(stringObjectMap -> {
                                                stringObjectMap.putAll(res);
                                            });
                                            crtAttachments.addAll(assignmentResult);
                                        }
                                    }
                                    result = crtAttachments;

                                }
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CHANGE_REQUEST1)) {

                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , new String[]{TICKET, TICKET_SK, BUSINESS_FUNCTION_SK, BUSINESS_FUNCTION_CODE});
                                endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_ATTACHMENTS, version, GET_METHOD, user);
                                for (Map<String, Object> res : result) {
                                    String CRT_sk = (String) res.get(TICKET_SK);
                                    Map<String, String[]> parameter1 = new HashMap<>();
                                    parameter1.put(TICKET, new String[]{CRT_sk});
                                    res.put(TASK_TYPE, null);
                                    res.put(TASK_NAME, null);
                                    String refdbquery1 = getDBQuery(endPointDomain.get(QUERY).toString(), parameter1);
                                    assignmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery1
                                            , null);

                                    if (!assignmentResult.isEmpty()) {
                                        assignmentResult.forEach(stringObjectMap -> {
                                            stringObjectMap.putAll(res);
                                        });
                                        crtAttachments.addAll(assignmentResult);
                                    }
                                }
                                result = crtAttachments;

                            }

                            if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CHANGE_REQUEST1)) {
                                if (result.size() > NUM_ZERO) {
                                    ticket_sk = result.get(0).get(TICKET_SK).toString();
                                }
                                finalresult.addAll(result);
                            } else if (endpoint.equalsIgnoreCase(INDEXERNAME_FETCH_CR_TASKS1)) {
                                finalresult.addAll(result);
                            } else {
                                resultMap.put(endpoint, result);
                            }
                        } else {
                            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            String message = (String) errorMap.get(MESSAGE);
                            errorMap.put(MESSAGE, endpoint + HYPHEN + message);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYTICKETORACCOUNT", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(finalresult, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Impact
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_IMPACT_PATH)
    public ResponseEntity updateFDNImpact(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_IMPACT;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs5(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * author - Vinodhini
     * <p>
     * Fetch Risk details
     *
     * @return - Returns the Schedule and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @GetMapping(path = RISK_DETAILS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchCRRisksDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, String[]> params1 = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), endPointDomain = new HashMap<>(), errorMap = new HashMap<>();
        List<Map<String, Object>> result;
        List<Map<String, Object>> riskList = new ArrayList<>();
        List<Map<String, Object>> attachmentResult = new ArrayList<>();
        String loginID = "", businessfnsk = "", ticket_sk = "";
        String endpointName = RISK_DETAILS;
        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (request.getParameterMap().containsKey(RISK_SK) || request.getParameterMap().containsKey(TICKET)) {

                    for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
                        if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY", requestparametermaps.getKey(), endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        } else {
                            params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
                        }
                    }

                    CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(params, user);
                    result = new ArrayList<>();

                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_RISKS1, version, GET_METHOD, user);
                    if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                        result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                , customWhooshModel.getRequiredColumns());
                        if (result.size() > NUM_ZERO) {
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_ATTACHMENTS, version, GET_METHOD, user);
                            for (Map<String, Object> result1 : result) {
                                params1.put(RISK_SK, new String[]{result1.get(RISK_SK).toString()});
                                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params1);
                                attachmentResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                        , customWhooshModel.getRequiredColumns());
                                result1.put(ATTACHMENTS, attachmentResult);
                                riskList.add(result1);
                            }
                            result = riskList;
                        }

                    } else {
                        errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYRISKSK", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(result, HttpStatus.OK);
    }


    /* *//**
     * author - Vivek
     * <p>
     * Fetch ChangeRequest Details for Calendar
     *
     * @return - Returns the Schedule and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     *//*
    @RequestMapping(path = "/{version}/ChangeRequest3", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchCRCalendar(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = "ChangeRequest";
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        User user = new User();
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
            endpointValidationModel = validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap(), Boolean.TRUE
                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
            SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version,
                    endpointValidationModel.getRequestparametermap().get(TICKET) != null ? endpointValidationModel.getRequestparametermap().get(TICKET)[0].toString() : "",
                    user.getUserID(), user.getAuthUserName(), null);
            if (endpointValidationModel.isAuth_endpoint()) {
                String authQuery = SFInterfaceBase.fetchAuthQuery(endpointName
                        , user.getUserID()
                        , endpointValidationModel.getAuth_columnMapping(), user);
                if (authQuery != null && !authQuery.isEmpty()) {
                    customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + authQuery);
                }
            }
            result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                    , customWhooshModel.getQuery()
                    , customWhooshModel.getSort()
                    , customWhooshModel.getRequiredColumns()
                    , customWhooshModel.getSortfields()
                    , customWhooshModel.getSortorder()
                    , customWhooshModel.getDistinct()
                    , customWhooshModel.getFieldaliass()
                    , customWhooshModel.getCount()
                    , Boolean.FALSE
                    , customWhooshModel.getDistinctfield()
                    , customWhooshModel.getPageno()
                    , Boolean.TRUE, user);
            if (!result.isEmpty()) {
                result.stream()
                        .map(stringObjectMap ->
                                convertChangeRequesttoCalendar((List<Map<String, Object>>) stringObjectMap.get(RESULT), refreshToken)
                        )
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }*/

    /**
     * author - Vivek
     * <p>
     * Fetch Status for Calendar
     *
     * @return - Returns the Schedule and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @RequestMapping(path = "/{version}/Status3", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchStatusCalendar(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        String endpointName = "Status2";
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        User user = new User();
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
            endpointValidationModel = validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap(), Boolean.TRUE
                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
            SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version,
                    endpointValidationModel.getRequestparametermap().get(TICKET) != null ? endpointValidationModel.getRequestparametermap().get(TICKET)[0].toString() : "",
                    user.getUserID(), user.getAuthUserName(), null);
            if (endpointValidationModel.isAuth_endpoint()) {
                String authQuery = SFInterfaceBase.fetchAuthQuery(endpointName
                        , user.getUserID()
                        , endpointValidationModel.getAuth_columnMapping(), user);
                if (authQuery != null && !authQuery.isEmpty()) {
                    customWhooshModel.setQuery(customWhooshModel.getQuery() + AND + authQuery);
                }
            }
            result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                    , customWhooshModel.getQuery()
                    , customWhooshModel.getSort()
                    , customWhooshModel.getRequiredColumns()
                    , customWhooshModel.getSortfields()
                    , customWhooshModel.getSortorder()
                    , customWhooshModel.getDistinct()
                    , customWhooshModel.getFieldaliass()
                    , customWhooshModel.getCount()
                    , Boolean.FALSE
                    , customWhooshModel.getDistinctfield()
                    , customWhooshModel.getPageno()
                    , Boolean.TRUE, user);
            if (!result.isEmpty()) {
                result.stream()
                        .map(stringObjectMap ->
                                convertStatustoCalendar((List<Map<String, Object>>) stringObjectMap.get(RESULT), refreshToken)
                        )
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return returnResponsewithValidation(endpointName, result, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }

    /**
     * author - Vivek
     * <p>
     * Fetch Status for Calendar
     *
     * @return - Returns the Schedule and dependent table Details as Json response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @RequestMapping(path = "/{version}/Status4", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchStatuswithNull(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws Exception {
        String endpointName = "Status2";

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.processRequest(GET_METHOD, aToken, refreshToken, accessToken, endpointName, version, request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), ticketFields);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Object>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Support for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SUPPORT1_PATH)
    public ResponseEntity updateSupport1(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SUPPORT1;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_SupportAttachments for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SUPPORT_ATTACHMENTS_PATH)
    public ResponseEntity updateSupportAttachments(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SUPPORT_ATTACHMENTS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_TaskDetails for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TASK_DETAILS_PATH)
    public ResponseEntity updateTaskDetails(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_TASK_DETAILS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_user position for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_POSITION_PATH)
    public ResponseEntity updateUserPosition(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_USER_POSITION;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating FDN_user position for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @GetMapping(path = "/{version}/Attachments")
    public ResponseEntity attachmentsbyTicketsk(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestParam(value = "BusinessFunctionCode") String businessfunction,
            @RequestParam(value = "TicketNumber") String ticket,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = "Attachments";

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return returnResponsewithValidation(endpointName, fetchattachments(businessfunction, ticket, user), null, false, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Inserting/Updating FDN_user position for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @GetMapping(path = "/{version}/ConsolidatedApprovals")
    public ResponseEntity ConsolidatedApprovals(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken
            , HttpServletRequest request) throws IOException {
        String endpointName = "ConsolidatedApprovals";
        List<Map<String, Object>> results = new ArrayList<>();
        List<ApprovalModel> resultss = new ArrayList<>();
        Map<String, Object> endPointDomain = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (user.getUserID() != null) {
                //results = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user,SFInterfaceConstants.getDbSspConnectionName()).

                results = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, USER_SK + COLON + user.getUserID(), null, new String[]{USER_SK, ACCOUNT, LOGIN_ID}
                        , null, null, null, null, 1, user);
                Map<String, String[]> params = new HashMap<>();
                params.put(USER_SK, new String[]{results.get(0).get(USER_SK).toString()});
                params.put(ACCOUNT, new String[]{results.get(0).get(ACCOUNT).toString()});
                params.put(LOGIN_ID, new String[]{results.get(0).get(LOGIN_ID).toString()});

                endpointName = "SSPWaitingApproval";
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);

                //    SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, refdbquery, version, requestparametermap.get(TICKET) != null ? requestparametermap.get(TICKET)[0].toString() : "", user.getUserID(), user.getAuthUserName(), null);
                results = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbSspConnectionName())
                        , refdbquery
                        , null
                        , null
                        , null
                        , null
                        , null
                        , 0
                        , 0
                        , Boolean.FALSE);


                resultss.addAll(SFInterfaceBase.mapsspDetails(results, refreshToken));


                endpointName = "ConsolidatedApprovalStatus";
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                //    SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, refdbquery, version, requestparametermap.get(TICKET) != null ? requestparametermap.get(TICKET)[0].toString() : "", user.getUserID(), user.getAuthUserName(), null);
                results = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                        , refdbquery
                        , null
                        , null
                        , null
                        , null
                        , null
                        , 0
                        , 0
                        , Boolean.FALSE);
                resultss.addAll(SFInterfaceBase.mapConsolidatedApprovals(results, refreshToken));
            } else {

            }
            return new ResponseEntity(resultss, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting CR details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_CHANGE_REQUEST_PATH)
    public ResponseEntity createDMartChangeRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_CHANGE_REQUEST;
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartChangeRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CR details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_CHANGE_REQUEST_PATH)
    public ResponseEntity updateDMartChangeRequest(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_CHANGE_REQUEST;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartChangeRequest(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for inserting CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_CRTASK_PATH)
    public ResponseEntity createDMartCRTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_CRTASK;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartCRTask(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_CRTASK_PATH)
    public ResponseEntity updateDMartCRTask(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_CRTASK;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartCRTask(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @GetMapping(path = "/{version}/Admin/ApprovalCriteriaDetails")
    public ResponseEntity fetchCRApprovals(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestParam(value = "ApprovalCriteria_sk") String ApproverCriteria_sk
            , HttpServletRequest request) throws IOException {
        List<Map<String, Object>> approvalcriteria = new ArrayList<>(), approvers = new ArrayList<>();
        Map<String, Object> results = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String endpointName = "ApproverCriteriaDetails", endpointName1 = "Admin/ApprovalCriteria", endpointName2 = INDEXERNAME_FETCH_BACKUP_APPROVER, endpointName3 = "Admin/NotificationMapping";
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName1, null, request.getParameterMap());

            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName1, version, GET_METHOD, user);
            String refdbquery = SFInterfaceBase.getDBQuery(endPointDomain.get(QUERY).toString(), request.getParameterMap());
            approvalcriteria = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery, null);


            if (approvalcriteria.size() == NUM_ONE) {
                endpointName1 = APPROVERS;
                endpointValidationModel.getRequestparametermap().put(SORTFIELD, new String[]{"Level,Sequence"});
                //    endpointValidationModel.getRequestparametermap().put(SELECTED_FIELDS, new String[]{"Level,Sequence,AccountGroup_sk,FullName"});
                endpointValidationModel.getRequestparametermap().put(STATUS_CODE, new String[]{"A"});
                approvers = fetchWhooshDynamic(request, version, endpointName1, user, endpointValidationModel, false);
                approvers = approvers.stream()
                        .map(approver -> {
                            List<Map<String, Object>> backupApprovers = new ArrayList<>();
                            try {
                                endpointValidationModel.getRequestparametermap().clear();
                                if (checkBlankorEmpty((String) approver.get(IS_BACKUP_NEEDED)) && approver.get(IS_BACKUP_NEEDED).toString().equalsIgnoreCase(ONE)) {
                                    endpointValidationModel.getRequestparametermap().put(Approver_sk, new String[]{approver.get(Approver_sk).toString()});
                                    backupApprovers = fetchWhooshRecords(INDEXERNAME_FETCH_BACKUP_APPROVER,
                                            Approver_sk + COLON + approver.get(Approver_sk).toString(),
                                            null, null, null, null, null, null, user);
                                }
                                approver.put(NOTIFICATION_SK, SFInterfaceBase.fetchNotification_sk(approver.get(Approver_sk).toString(), user, endpointName3));
                            } catch (Exception e) {
                                //  e.printStackTrace();
                            }
                            approver.put(INDEXERNAME_FETCH_BACKUP_APPROVER, backupApprovers);
                            return approver;
                        }).collect(Collectors.toList());

                results = mapApproversinApprovalCriteria(approvalcriteria.get(NUM_ZERO), approvers);
            } else {
                throw new Exception("Approval Criteria Not Found");
            }
            return new ResponseEntity(results, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = "/{version}/UpdateApprovalCriteriaDetails")
    public ResponseEntity fetchCRApprovals(
            @PathVariable(VERSION) String version,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> approvalcriteria = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String endpointName = UPDATE_APPROVAL_CRITERIA, endpointName1 = "fdn_approvers";
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            if (StringUtils.isNotBlank(user.getAuthUserName())) {
                Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, POST_METHOD, user);
                String spName = endPointDomain.get(QUERY).toString();
                resultMap = SFInterfaceBase.updateApprovalCriteria(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), spName, ticketFields, endpointName, user);
                if (ticketFields.containsKey(APPROVERS.toLowerCase()) && resultMap.containsKey(APPROVAL_CRITERIA_SK)) {
                    List<Map<String, Object>> objects = (List<Map<String, Object>>) ticketFields.get(APPROVERS.toLowerCase());
                    ticketFields.clear();
                    String approvalCriteria_sk = String.valueOf(resultMap.get(APPROVAL_CRITERIA_SK));
                    objects = objects.parallelStream()
                            .map(stringObjectMap -> {
                                stringObjectMap.put(APPROVAL_CRITERIA_SK, approvalCriteria_sk);
                                return stringObjectMap;
                            })
                            .collect(Collectors.toList());

                    updateApprovers(objects, user, endpointName1, request);


                   /* ticketFields.put(APPROVERS.toLowerCase(), objects);
                    approvalcriteria = createORUpdateEntitiess(ticketFields, endpointName1, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);*/
                    //resultMap.put(APPROVERS, approvalcriteria);
                }
                return new ResponseEntity(resultMap, HttpStatus.OK);
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating RDY_Wizard position for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_RDY_WIZARD_PATH)
    public ResponseEntity updateRDYWizard(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_RDY_WIZARD;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/KBPhrases1", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity fetchKBPhrases(
            @PathVariable(VERSION) String version,
            @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        List<Map<String, Object>> phrasesresults = new ArrayList<>();
        String endpointName = "KBPhrases";
        String[] phrases = new String[]{};
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            if (endpointValidationModel.getRequestparametermap().containsKey(PHRASES)) {
                phrases = endpointValidationModel.getRequestparametermap().get(PHRASES);
                phrases = Arrays.stream(phrases)
                        .map(s -> ASTERISK + s + ASTERISK)
                        .collect(Collectors.toSet()).toArray(new String[0]);
                endpointValidationModel.getRequestparametermap().put(PHRASES, phrases);

            }
            phrasesresults = fetchWhooshDynamicwithLikeOperator(request, version, endpointName, user, endpointValidationModel, true, true);
            return returnResponsewithValidation(endpointName, phrasesresults, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * author - Vinodhini
     * <p>
     * Fetch KB Phrase details
     *
     * @return - Returns the KB Phrase details, Questions as Json Response
     * @header - Requires an valid accesstoken or aToken
     * @input -  ticket or account
     */
    @RequestMapping(path = KB_PHRASE_DETAILS_PATH, method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity fetchKBPhraseDetails(
            @PathVariable(VERSION) String version,
            @RequestBody(required = false) Map<String, Object> ticketFields,
            HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> errorMap = new HashMap<>(), resultMap = new HashMap<>();
        List<Map<String, Object>> kbphraseResult = new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList();
        String loginID = "";
        String endpointName = KB_PHRASE_DETAILS;
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        try {
            endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(endpointValidationModel.getRequestparametermap()));
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {

                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap(), user);

                kbphraseResult = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_KB_PHRASES
                        , customWhooshModel.getQuery(), null, null, null, null
                        , null, null, NUM_ZERO, Boolean.FALSE, null, null, Boolean.FALSE, user);

                if (kbphraseResult.size() > NUM_ZERO) {

                    Map<String, String[]> parameters = new HashMap<>();
                    parameters.put(MAPPING_TYPE, new String[]{(String) kbphraseResult.get(NUM_ZERO).get(MAPPING_TYPE)});
                    parameters.put(XCATEGORY_SK, new String[]{(String) kbphraseResult.get(NUM_ZERO).get(XCATEGORY_SK)});
                    parameters.put(XTYPE_SK, new String[]{(String) kbphraseResult.get(NUM_ZERO).get(XTYPE_SK)});
                    parameters.put(XITEM_SK, new String[]{(String) kbphraseResult.get(NUM_ZERO).get(XITEM_SK)});
                    parameters.put(ACCOUNT, endpointValidationModel.getRequestparametermap().get(ACCOUNT));
                    parameters.put(X_BUSINESS_FUNCTION_SK, new String[]{(String) kbphraseResult.get(NUM_ZERO).get(X_BUSINESS_FUNCTION_SK)});

                    String rdyMapQuery = SFInterfaceBase.generateWhooshQueryDynamic(parameters);

                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_READYMAPPING
                            , rdyMapQuery, null, null, null, null
                            , null, null, NUM_ZERO, Boolean.FALSE, null, null, Boolean.FALSE, user);
                    resultMap.put(INDEXERNAME_FETCH_READYMAPPING, result);

                    result = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_QUESTIONS
                            , customWhooshModel.getQuery(), null, null, "Order", null
                            , null, null, NUM_ZERO, Boolean.FALSE, null, null, Boolean.FALSE, user);
                    resultMap.put(INDEXERNAME_FETCH_QUESTIONS, result);

                } else {
                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.OK);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SCH_RoundRobinRule position for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ROUND_ROBIN_RULE_PATH)
    public ResponseEntity updateRoundRobinRule(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ROUND_ROBIN_RULE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SEC_AuthenticationType for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_AUTHENTICATION_TYPE_PATH)
    public ResponseEntity updateAuthenticationType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_AUTHENTICATION_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs6(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_MasterType for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_MASTER_TYPE_PATH)
    public ResponseEntity updateMasterType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_MASTER_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_AlertStatus for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ALERT_STATUS_PATH)
    public ResponseEntity updateAlertStatus(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ALERT_STATUS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_UserType for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_TYPE_PATH)
    public ResponseEntity updateUserType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_USER_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Severity for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SEVERITY_PATH)
    public ResponseEntity updateSeverity(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SEVERITY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_TaskAssignmentGroup for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TASK_ASSIGNMENT_GROUP_PATH)
    public ResponseEntity updateTaskAssignmentGroup(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_TASK_ASSIGNMENT_GROUP;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Title for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_TITLE_PATH)
    public ResponseEntity updateTitle(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_TITLE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_UserHierarchy for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_HIERARCHY_PATH)
    public ResponseEntity updateUserHierarchy(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_USER_HIERARCHY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_UserNotesVisibility for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_NOTES_VISIBILITY_PATH)
    public ResponseEntity updateUserNotesVisibility(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_USER_NOTES_VISIBILITY;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs7(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_UserSkillsets for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_USER_SKILL_SETS_PATH)
    public ResponseEntity updateUserSkillSets(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_USER_SKILL_SETS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Instructions for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_INSTRUCTIONS_PATH)
    public ResponseEntity updateInstructions(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_INSTRUCTIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_EMailDomains for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_EMAIL_DOMAINS_PATH)
    public ResponseEntity updateEmailDomains(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_EMAIL_DOMAINS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_SkillSet for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_SKILL_SET_PATH)
    public ResponseEntity updateSkillSet(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_SKILL_SET;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_AutomationServices for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_AUTOMATION_SERVICES_PATH)
    public ResponseEntity updateAutomationServices(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_AUTOMATION_SERVICES;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating SEC_AccountType for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ACCOUNT_TYPE_PATH)
    public ResponseEntity updateAccountType(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ACCOUNT_TYPE;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs8(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting CR Consolidated in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = CREATE_DMART_CR_CONSOLIDATED_PATH)
    public ResponseEntity createDMartCRConsolidated(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_DMART_CR_CONSOLIDATED;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartCRConsolidated(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version,
                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()),
                    request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Updating CR Consolidated in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_DMART_CR_CONSOLIDATED_PATH)
    public ResponseEntity updateDMartCRConsolidated(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_DMART_CR_CONSOLIDATED;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateDmartCRConsolidated(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName()),
                    request, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Escalations for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_ESCLATIONS_PATH)
    public ResponseEntity updateEsclations(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_ESCLATIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs9(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Units for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_UNITS_PATH)
    public ResponseEntity updateUnits(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_UNITS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs9(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_Notifications for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_NOTIFICATIONS_PATH)
    public ResponseEntity updateNotifications(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_NOTIFICATIONS;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs9(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting/Updating FDN_NotificationMapping for admin screen
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @PostMapping(path = UPDATE_NOTIFICATION_MAPPING_PATH)
    public ResponseEntity updateNotificationMapping(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String aToken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>(), errorMap = new HashMap<>();
        String loginID = "";
        String endpointName = UPDATE_NOTIFICATION_MAPPING;

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            return SFInterfaceServices.createOrUpdateAdminAPIs9(POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                    ticketFields, endpointName, version, SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/{version}/UserInstances", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity fetchUserInstances(
            @PathVariable(VERSION) String version,
            @RequestParam(value = EMAIL_ADDRESS) String emailAddress,
            HttpServletRequest request
    ) throws IOException {
        String endpointName = "UserInstances";
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            //  User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            SFInterfaceBase.fetchDedicatedInstances()
                    .stream()
                    .filter(
                            s ->
                                    SFInterfaceBase.fetchprofileBasedonEmailID(emailAddress, s) != null)
                    .forEach(s ->
                            response.add(SFInterfaceBase.fetchprofileBasedonEmailID(emailAddress, s)
                            ));
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vinodhini
     * The Function is used for Inserting KB User Favorites
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(value = CREATE_USER_FAVORITE_ARTICLE_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addKBUserFavorites(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>(), resultMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String loginID = "";
        String endpointName = CREATE_KB_USER_FAVORITE_ARTICLE;
        Map<String, Object> endPointDomain = new HashMap<>();
        String refdbquery = "";
        Map<String, String[]> queryParams = new HashMap<>();
        List<Map<String, Object>> kbUserFavorites = new ArrayList<>();

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {
                if (requestFields != null)
                    params = convertmapObject(requestFields);
                params.putAll(request.getParameterMap());
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                //To insert into refdb
                if (!endPointDomain.containsKey(ERRORCODE)) {
                    refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                    result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                            , refdbquery);
                }
                //To insert into AR_SystemReporting
                if (result.size() > NUM_ZERO && result.get(NUM_ZERO).containsKey(USER_FAVORITE_ARTICLES_SK) &&
                        result.get(NUM_ZERO).get(USER_FAVORITE_ARTICLES_SK) != null) {
                    String userFavoriteArticle_sk = result.get(NUM_ZERO).get(USER_FAVORITE_ARTICLES_SK).toString();
                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_KB_USER_FAVORITE_ARTICLES, version, GET_METHOD, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        queryParams.put(USER_FAVORITE_ARTICLES_SK, new String[]{userFavoriteArticle_sk});
                        refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), queryParams);
                        kbUserFavorites = runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                                , refdbquery);
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(CREATE_DMART_KB_USER_FAVORITE_ARTICLES, version, POST_METHOD, user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();
                            SFInterfaceLoggerBase.log(LOGGER, CREATE_DMART_KB_USER_FAVORITE_ARTICLES, " Request:" + kbUserFavorites.get(NUM_ZERO));
                            resultMap = SFInterfaceBase.createDmartKBUserFavoriteArticle(
                                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName())
                                    , spName, kbUserFavorites.get(NUM_ZERO), CREATE_DMART_KB_USER_FAVORITE_ARTICLES);
                            SFInterfaceLoggerBase.log(LOGGER, CREATE_DMART_KB_USER_FAVORITE_ARTICLES, " Response:" + resultMap);
                        }
                    }
                }

            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    /**
     * Author - Vinodhini
     * The Function is used for updaating KB User Favorites
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(value = DELETE_USER_FAVORITE_ARTICLE_PATH, method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity deletKBUserFavorites(
            @PathVariable(VERSION) String version,
            @RequestHeader(value = A_TOKEN, required = FALSE) String atoken,
            @RequestHeader(value = ACCESS_TOKEN, required = FALSE) String accessToken,
            @RequestHeader(value = REFRESH_TOKEN, required = FALSE) String refreshToken,
            @RequestBody(required = false) Map<String, Object> requestFields,
            HttpServletRequest request) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> errorMap = new HashMap<>(), resultMap = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        String loginID = "";
        String endpointName = DELETE_KB_USER_FAVORITE_ARTICLE;
        Map<String, Object> endPointDomain = new HashMap<>();
        String refdbquery = "";
        Map<String, String[]> queryParams = new HashMap<>();
        List<Map<String, Object>> kbUserFavorites = new ArrayList<>();

        try {

            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            loginID = user.getAuthUserName();
            if (loginID != null && !loginID.isEmpty()) {
                if (requestFields != null)
                    params = convertmapObject(requestFields);
                params.putAll(request.getParameterMap());
                //To fecth kb user favorites
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_KB_USER_FAVORITE_ARTICLES, version, GET_METHOD, user);
                if (!endPointDomain.containsKey(ERRORCODE)) {
                    queryParams.put(USER_FAVORITE_ARTICLES_SK, params.get(USER_FAVORITE_SK));
                    refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), queryParams);
                    kbUserFavorites = runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                            , refdbquery);
                }
                //To update refDb
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, GET_METHOD, user);
                if (!endPointDomain.containsKey(ERRORCODE)) {
                    refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                    result = SFInterfaceBase.runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                            , refdbquery);
                }
                //To update into AR_SystemReporting
                if (result.size() > NUM_ZERO && kbUserFavorites.size() > NUM_ZERO) {
                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(UPDATE_DMART_KB_USER_FAVORITE_ARTICLES, version, POST_METHOD, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        SFInterfaceLoggerBase.log(LOGGER, UPDATE_DMART_KB_USER_FAVORITE_ARTICLES, " Request:" + kbUserFavorites.get(NUM_ZERO));
                        resultMap = SFInterfaceBase.updateDmartKBUserFavoriteArticle(
                                SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName())
                                , spName, kbUserFavorites.get(NUM_ZERO), CREATE_DMART_KB_USER_FAVORITE_ARTICLES);
                        SFInterfaceLoggerBase.log(LOGGER, UPDATE_DMART_KB_USER_FAVORITE_ARTICLES, " Response:" + resultMap);
                    }
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<Map<String, Object>>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }


    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/Instruction", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity fetchInstruction(
            @PathVariable(VERSION) String version
            , @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        List<Map<String, Object>> instructions = new ArrayList<>(), approvers = new ArrayList<>();
        String endpointName = "Instruction";
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            instructions = fetchWhooshDynamic(request, version, endpointName, user, endpointValidationModel, false, false, true);

            instructions = instructions.stream()
                    .filter(instruction -> !checkBlankorEmpty((String) instruction.get(Y_VALUE1))
                                    && !checkBlankorEmpty((String) instruction.get(Y_VALUE2))
                          /*  && checkBlankorEmpty((String) instruction.get(INSTRUCTION_TYPE))
                            && instruction.get(INSTRUCTION_TYPE).toString().equalsIgnoreCase("Q")*/
                    )
                    .map(instruction -> {
                        try {
                            instruction.put(QUESTIONS, mapInstructionQuestions((String) instruction.get(INSTRUCTION_SK), user));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return instruction;
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity(instructions, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/{version}/VerifyUser")
    public ResponseEntity verifyUser(
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String username = request.getParameter("username");
            List<Map<String, Object>> profilerecords = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, LOGIN_ID + COLON + username, null
                    , new String[]{USER_SK, EMAIL_ADDRESS, ACCOUNT, MASTER_ACCOUNT}, null, null, null, null, user);
            if (profilerecords.size() == 1) {
                String emailAddress = profilerecords.get(0).get(EMAIL_ADDRESS).toString();
                String user_sk = profilerecords.get(0).get(USER_SK).toString();
                String code = String.format("%06d", new Random().nextInt(999999));
                JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());
                SFInterfaceBase.updateOTPSecret(jdbcTemplate, user_sk, code);
                response.put("OTPSent", SFInterfaceBase.sendemail(code, emailAddress, jdbcTemplate, user));
                response.put(MESSAGE, (boolean) response.get("OTPSent") ? "Success" : "failed to send!");
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("OTPSent", false);
            response.put(MESSAGE, e.getMessage() != null ? e.getMessage() : NA);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping("/{version}/VerifyNRegister")
    public ResponseEntity VerifyNRegister(
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String username = request.getParameter("username");
            String code = request.getParameter("code");
            String password = request.getParameter("password");
            List<Map<String, Object>> profilerecords = fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, LOGIN_ID + COLON + username, null
                    , new String[]{USER_SK, EMAIL_ADDRESS, ACCOUNT, MASTER_ACCOUNT}, null, null, null, null, user);
            if (profilerecords.size() == 1) {
                String emailAddress = profilerecords.get(0).get(EMAIL_ADDRESS).toString();
                String user_sk = profilerecords.get(0).get(USER_SK).toString();
                JdbcTemplate jdbcTemplate = SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName());
                response.put("Status", SFInterfaceBase.updateUserPasswordbySecred(jdbcTemplate, user_sk, password, code) == 0 ? false : true);
                response.put(MESSAGE, (boolean) response.get(STATUS) ? "Success" : "OTP not valid!");
            } else {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("Status", false);
            response.put(MESSAGE, e.getMessage() != null ? e.getMessage() : NA);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/CREffort1", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity fetchCREffort1(
            @PathVariable(VERSION) String version
            , @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>(), approvers = new ArrayList<>();
        Map<String, Object> resultEffort = new HashMap<>();
        String endpointName = "CREffort";
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            EndpointValidationModel endpointValidationModel = validateEndpoint(endpointName, ticketFields, request.getParameterMap());
            Map<String, Object> endPointDomain = fetchEndpointDetails(endpointName, version, GET_METHOD, user);
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), endpointValidationModel.getRequestparametermap());
            result = runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, getDbRefConnectionName())
                    , refdbquery);

            Set<String> taskTypes = result.parallelStream()
                    .filter(stringObjectMap -> checkBlankorEmpty((String) stringObjectMap.get(TASK_TYPE)))
                    .map(stringObjectMap -> stringObjectMap.get(TASK_TYPE).toString())
                    .collect(Collectors.toSet());

            Map<String, Set<String>> entities = new HashMap<>();
            Map<String, Set<String>> finalEntities = entities;

            result.stream()
                    .filter(stringObjectMap -> checkBlankorEmpty((String) stringObjectMap.get(ENTITYNAME)) && checkBlankorEmpty((String) stringObjectMap.get(ENTITY_sk)))
                    .forEach(stringObjectMap -> {
                        if (finalEntities.containsKey(stringObjectMap.get(ENTITYNAME).toString())) {
                            finalEntities.get(stringObjectMap.get(ENTITYNAME)).add(stringObjectMap.get(ENTITY_sk).toString());
                        } else {
                            Set<String> records = new HashSet<>();
                            records.add(stringObjectMap.get(ENTITY_sk).toString());
                            finalEntities.put(stringObjectMap.get(ENTITYNAME).toString(), records);
                        }
                    });
            List<Map<String, Object>> finalResult = result;
            taskTypes.stream()
                    .forEach(s -> {
                        List<Map<String, Object>> tasktyperesult = new ArrayList<>();
                        finalEntities.entrySet()
                                .stream()
                                .forEach(entity -> {
                                    Map<String, Object> tempEntity = new HashMap<>();
                                    final String[] taskName = {""};
                                    final String[] TaskActualEffort = {""};
                                    final String[] TaskActualStartTime = {""};
                                    final String[] TaskActualEndTime = {""};
                                    final String[] TaskEstimatedEffort = {""};
                                    final String[] TaskDownTime = {""};
                                    final String[] TaskEstStartTime = {""};
                                    final String[] TaskEstEndTime = {""};
                                    entity.getValue().stream()
                                            .forEach(entity_sk -> {
                                                List<Map<String, Object>> actionitemsresult = new ArrayList<>();
                                                finalResult.stream()
                                                        .filter(actionitem -> checkBlankorEmpty((String) actionitem.get(ENTITYNAME))
                                                                && checkBlankorEmpty((String) actionitem.get(ENTITY_sk))
                                                                && checkBlankorEmpty((String) actionitem.get(TASK_TYPE))
                                                        ).filter(actionitem ->
                                                        actionitem.get(ENTITYNAME).toString().equalsIgnoreCase(entity.getKey())
                                                                && actionitem.get(ENTITY_sk).toString().equalsIgnoreCase(entity_sk)
                                                                && actionitem.get(TASK_TYPE).toString().equalsIgnoreCase(s)
                                                )
                                                        .forEach(actionitem -> {
                                                            taskName[0] = actionitem.get(TASK_NAME) != null ? actionitem.get(TASK_NAME).toString() : null;
                                                            TaskActualEffort[0] = actionitem.get("TaskActualEffort") != null ? actionitem.get("TaskActualEffort").toString() : null;
                                                            TaskActualStartTime[0] = actionitem.get("TaskActualStartTime") != null ? actionitem.get("TaskActualStartTime").toString() : null;
                                                            TaskActualEndTime[0] = actionitem.get("TaskActualEndTime") != null ? actionitem.get("TaskActualEndTime").toString() : null;
                                                            TaskEstimatedEffort[0] = actionitem.get("TaskEstimatedEffort") != null ? actionitem.get("TaskEstimatedEffort").toString() : null;
                                                            TaskDownTime[0] = actionitem.get("TaskDownTime") != null ? actionitem.get("TaskDownTime").toString() : null;
                                                            TaskEstStartTime[0] = actionitem.get("TaskEstStartTime") != null ? actionitem.get("TaskEstStartTime").toString() : null;
                                                            TaskEstEndTime[0] = actionitem.get("TaskEstEndTime") != null ? actionitem.get("TaskEstEndTime").toString() : null;
                                                            actionitemsresult.add(actionitem);
                                                        });
                                                if (!actionitemsresult.isEmpty()) {
                                                    tempEntity.put(ACTION_ITEMS, actionitemsresult);
                                                    tempEntity.put(ENTITYNAME, entity.getKey());
                                                    tempEntity.put(ENTITY_sk, entity_sk);
                                                    tempEntity.put(TASK_TYPE, s);
                                                    tempEntity.put(TASK_NAME, taskName[0]);
                                                    tempEntity.put("TaskActualEffort", TaskActualEffort[0]);
                                                    tempEntity.put("TaskActualStartTime", TaskActualStartTime[0]);
                                                    tempEntity.put("TaskActualEndTime", TaskActualEndTime[0]);
                                                    tempEntity.put("TaskEstimatedEffort", TaskEstimatedEffort[0]);
                                                    tempEntity.put("TaskDownTime", TaskDownTime[0]);
                                                    tempEntity.put("TaskEstStartTime", TaskEstStartTime[0]);
                                                    tempEntity.put("TaskEstEndTime", TaskEstEndTime[0]);
                                                }
                                            });
                                    if (!tempEntity.isEmpty())
                                        tasktyperesult.add(tempEntity);
                                });
                        resultEffort.put(s, tasktyperesult);
                    });
            return new ResponseEntity(resultEffort, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Author - Vivek
     * The Function is used for Updating CRTask details in AR System Reporting
     * * * @return - Returns sk and success message as Json response
     *
     * @header- Requires an valid accesstoken or aToken
     * @input -  Receives an Json request and validates the input field.
     */
    @RequestMapping(path = "/{version}/RiskMatrix2", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity fetchRiskMatrix(
            @PathVariable(VERSION) String version
            , @RequestBody(required = false) Map<String, Object> ticketFields
            , HttpServletRequest request) throws IOException {
        List<Map<String, Object>> instructions = new ArrayList<>(), approvers = new ArrayList<>();
        String endpointName = "RiskMatrix";
        String weightage = "";
        User user;
        try {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());

            if (endpointValidationModel.getRequestparametermap().containsKey(WEIGHTAGE)) {
                weightage = endpointValidationModel.getRequestparametermap().get(WEIGHTAGE)[0];
                endpointValidationModel.getRequestparametermap().remove(WEIGHTAGE);
            }

            instructions = fetchWhooshDynamic(request, version, endpointName, user, endpointValidationModel, false, false, true);

            instructions = instructions.stream().sorted((o1, o2) -> {
                return Comparator.nullsLast(Float::compareTo).compare(Float.valueOf((String) o1.get(WEIGHTAGE)), Float.valueOf((String) o2.get(WEIGHTAGE)));
            }).collect(Collectors.toList());

            if (checkBlankorEmpty(weightage)) {
                String finalWeightage = weightage;
                instructions = instructions.stream()
                        .filter(stringObjectMap -> checkBlankorEmpty((String) stringObjectMap.get(WEIGHTAGE)))
                        .filter(stringObjectMap ->
                                Float.valueOf((String) finalWeightage) <= Float.valueOf((String) stringObjectMap.get(WEIGHTAGE))
                        ).limit(1).collect(Collectors.toList());
            }


       /*     instructions = instructions.stream()
                    .filter(instruction -> !checkBlankorEmpty((String) instruction.get(Y_VALUE1))
                                    && !checkBlankorEmpty((String) instruction.get(Y_VALUE2))
                          *//*  && checkBlankorEmpty((String) instruction.get(INSTRUCTION_TYPE))
                            && instruction.get(INSTRUCTION_TYPE).toString().equalsIgnoreCase("Q")*//*
                    )
                    .map(instruction -> {
                        try {
                            instruction.put(QUESTIONS, mapInstructionQuestions((String) instruction.get(INSTRUCTION_SK), user));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return instruction;
                    })
                    .collect(Collectors.toList());*/
            return new ResponseEntity(instructions, HttpStatus.OK);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}



