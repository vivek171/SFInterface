package com.htc.remedy.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.base.SFInterfaceConnectionBase;
import com.htc.remedy.base.SFInterfaceLoggerBase;
import com.htc.remedy.base.SFInterfaceMessages;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.core.MediaTypeUtils;
import com.htc.remedy.domain.IndexerDomain;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.model.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.htc.remedy.base.SFInterfaceBase.*;
import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Service
@Component
public class SFInterfaceServices {

    private static final Logger LOGGER = LogManager.getLogger(SFInterfaceServices.class);

    private static
    SFInterfaceMessages sfInterfaceMessages;

    private static
    ServletContext servletContext;

    private static
    Gson gson;

    private static
    JmsTemplate jmsTemplate;

    @Autowired
    public SFInterfaceServices(SFInterfaceMessages sfInterfaceMessages, ServletContext servletContext, Gson gson, JmsTemplate jmsTemplate) throws FileNotFoundException {
        this.sfInterfaceMessages = sfInterfaceMessages;
        this.servletContext = servletContext;
        this.gson = gson;
        this.jmsTemplate = jmsTemplate;
    }

    public static Boolean isRequestContainToken(String aToken, String refreshToken, String accessToken) {
        if (StringUtils.isNotBlank(aToken) || (StringUtils.isNotBlank(refreshToken) && StringUtils.isNotBlank(accessToken))) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static ResponseEntity processRequest(String methodType, String aToken, String refreshToken, String accessToken, String endpointName
            , String version, HttpServletRequest request, JdbcTemplate refLabJDBCTemplate, Map<String, Object> requestFields) throws Exception {
        String refdbquery = "", query = "";
        EndpointValidationModel endpointValidationModel = new EndpointValidationModel();
        Boolean isValidQuery = Boolean.FALSE;
        Map<String, Object> errorMap = new HashMap<>(), endPointDomain = new HashMap<>();
        Map<String, String> SPQuery = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> requestparametermap = new HashMap<>();
        User user = new User();

        endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, requestFields, request.getParameterMap());
        requestparametermap = endpointValidationModel.getRequestparametermap();
        if (isRequestContainToken(aToken, refreshToken, accessToken)) {
            user = SFInterfaceConnectionBase.fetchRequestInfo(request);
            String loginID = user.getAuthUserName();
            if (StringUtils.isNotBlank(loginID)) {
                if (!endpointValidationModel.isEmptyParams_Whitelisted()
                        && endpointValidationModel.isMissingParam_Value()) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGVALUEFORKEY"
                            , endpointValidationModel.getMissingParam_name()
                            , endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                }
                endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                if (endPointDomain.containsKey(ERRORCODE) || endPointDomain.containsKey(INFOCODE)) {
                    return new ResponseEntity<>(endPointDomain, HttpStatus.BAD_REQUEST);
                } else if (endPointDomain.size() > NUM_ZERO
                        && (endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(ISP)
                        || StringUtils.isNotBlank((String) endPointDomain.get(INDEX_NAME)))) {
                    Boolean isValidRequest = Boolean.FALSE;
                    if (endpointValidationModel.isMandatoryParams_Whitelisted()) {
                        isValidRequest = Boolean.TRUE;
                    } else if (requestparametermap.containsKey(LOGIN_ID)
                            || requestparametermap.containsKey(USER_SK)) {//TODO Check loginid is valid or not
                        isValidRequest = Boolean.TRUE;
                    } else if (requestparametermap.containsKey(ACCOUNT)
                            || requestparametermap.containsKey(ACCOUNT_SK)
                            || requestparametermap.containsKey(MASTER_ACCOUNT)
                            || requestparametermap.containsKey(MASTER_ACCOUNT_SK)
                            || requestparametermap.containsKey(DISPLAY_ACCOUNT)) {

                        if (endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(Constants.getWhooshKey())) {
                            if (!endPointDomain.get(QUERY).toString().contains(ACCOUNT)) {
                                Boolean isAccountValid = SFInterfaceBase.isValidAccount(requestparametermap.containsKey(ACCOUNT)
                                                ? request.getParameter(ACCOUNT) : null
                                        , requestparametermap.containsKey(MASTER_ACCOUNT) ? request.getParameter(MASTER_ACCOUNT) : null
                                        , requestparametermap.containsKey(MASTER_ACCOUNT_SK) ? request.getParameter(MASTER_ACCOUNT_SK) : null
                                        , requestparametermap.containsKey(ACCOUNT_SK) ? request.getParameter(ACCOUNT_SK) : null, user);

                                /*if (isAccountValid.get("AccountValid").toString().equalsIgnoreCase("true")) {*/
                                if (isAccountValid) {
                                    isValidRequest = Boolean.TRUE;
                                } /*else if (isAccountValid.get("AccountValid").toString().equalsIgnoreCase("false") && isAccountValid.get("message").toString().equalsIgnoreCase("Invalid Account")) {
                                    isValidRequest = Boolean.FALSE;
                                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.REQUEST.INVALIDACCOUNT", null, endpointName);
                                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                                } */ else {
                                    isValidRequest = Boolean.FALSE;
                                }
                            } else {
                                isValidRequest = Boolean.TRUE;
                            }
                        } else {
                            isValidRequest = Boolean.TRUE;
                        }
                    }
                    if (isValidRequest) {
                        if (endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(Constants.getWhooshDynamicKey())) {
                            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(requestparametermap, Boolean.TRUE
                                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
                            SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version, requestparametermap.get(TICKET) != null ? requestparametermap.get(TICKET)[0].toString() : "", null, user);
                            if (customWhooshModel.getParams().isEmpty() &&
                                    !endpointValidationModel.isEmptyParams_Whitelisted()) {
                                errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYPARAMETERS", null, endpointName);
                                return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                            }
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
                            result = isEndpointWhiteListedwithNull(result, customWhooshModel, endpointValidationModel, endPointDomain, user);
                        } else if (endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(Constants.getWhooshKey())) {  //whooshstatic
                            CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(requestparametermap
                                    , endPointDomain.get(ENDPOINT_SK).toString(), user);
                            if (!endpointValidationModel.isEmptyParams_Whitelisted()
                                    && customWhooshModel.getParams().isEmpty()) {
                                errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYPARAMETERS", null, endpointName);
                                return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                            }
                            SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, customWhooshModel.getQuery(), version, requestparametermap.get(TICKET) != null ? requestparametermap.get(TICKET)[0].toString() : "", null, user);
                            result = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
                                    , SFInterfaceBase.getWhooshQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams())
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
                        } else if (endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(Constants.getiQ())
                                || endPointDomain.get(QUERY_TYPE).toString().equalsIgnoreCase(Constants.getiSP())) {  //reference db
                            query = endPointDomain.get(QUERY).toString();
                            SPQuery = SFInterfaceServices.convertSPQueryToMap(query);
                            for (Map.Entry<String, String[]> params : requestparametermap.entrySet()) {
                                if (SPQuery.containsKey(params.getKey())
                                        || params.getKey().equalsIgnoreCase(ACCOUNT)
                                        || params.getKey().contains(SELECTED_FIELDS) || params.getKey().contains(SORTFIELD)
                                        || params.getKey().contains(DISTINCT) || params.getKey().contains(SORTORDER) || params.getKey().contains("Pagination")) {
                                    isValidQuery = Boolean.TRUE;
                                } else {
                                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDQUERYFORSP", params.getKey(), endpointName);
                                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                                }
                            }
                            if (endpointValidationModel.isMandatoryParams_Whitelisted()) {
                                isValidQuery = Boolean.TRUE;
                            }
                            if (isValidQuery) {
                                CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(requestparametermap);

                                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                                try {
                                    SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, endpointName, refdbquery, version, requestparametermap.get(TICKET) != null ? requestparametermap.get(TICKET)[0].toString() : "", null, user);
                                    result = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                                            , refdbquery
                                            , customWhooshModel.getRequiredColumns()
                                            , customWhooshModel.getSortfields()
                                            , customWhooshModel.getSortorder()
                                            , customWhooshModel.getDistinct()
                                            , customWhooshModel.getDistinctfield()
                                            , customWhooshModel.getPageno()
                                            , customWhooshModel.getCount()
                                            , Boolean.TRUE);
                                } catch (Exception e) {
                                    return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e
                                            , query.substring(query.indexOf(SINGLE_QUOTE), query.length()), endpointName), HttpStatus.FAILED_DEPENDENCY);
                                }
                            }
                        }
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDS", null, endpointName);
                        return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                return new ResponseEntity<Object>(errorMap, HttpStatus.UNAUTHORIZED);
            }
        } else {
            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
            return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return returnResponsewithValidation(endpointName, result, requestparametermap, endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }


    public static ResponseEntity returnResponsewithValidation(String endpointName,
                                                              List<Map<String, Object>> result,
                                                              Map<String, String[]> requestparametermap,
                                                              Boolean is_emptyParams_Whitelisted
            , User user
    ) throws IOException {
        Map<String, Object> errorMap = new HashMap<>();
        if (result == null) {
            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDCOLUMN", null, endpointName);
            return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
        } else if (result.size() > 0) {
            return returnResponseWithPagination(result);
        } else {
            Boolean isValidAccountAndUser = Boolean.FALSE;

            String account = requestparametermap.containsKey(ACCOUNT) && !requestparametermap.get(ACCOUNT)[0].equalsIgnoreCase(VOID)?
                    requestparametermap.get(ACCOUNT)[0] : null;
            String accountSK = requestparametermap.containsKey(ACCOUNT_SK) && !requestparametermap.get(ACCOUNT_SK)[0].equalsIgnoreCase(VOID)?
                    requestparametermap.get(ACCOUNT_SK)[0] : null;
            String masterAccount = requestparametermap.containsKey(MASTER_ACCOUNT) && !requestparametermap.get(MASTER_ACCOUNT)[0].equalsIgnoreCase(VOID)?
                    requestparametermap.get(MASTER_ACCOUNT)[0] : null;
            String masterAccountSK = requestparametermap.containsKey(MASTER_ACCOUNT_SK) && !requestparametermap.get(MASTER_ACCOUNT_SK)[0].equalsIgnoreCase(VOID) ?
                    requestparametermap.get(MASTER_ACCOUNT_SK)[0] : null;
            String userSK = requestparametermap.containsKey(USER_SK) && !requestparametermap.get(USER_SK)[0].equalsIgnoreCase(VOID)?
                    requestparametermap.get(USER_SK)[0] : null;
            String loginID = requestparametermap.containsKey(LOGIN_ID) && !requestparametermap.get(LOGIN_ID)[0].equalsIgnoreCase(VOID)?
                    requestparametermap.get(LOGIN_ID)[0] : null;

            if ((StringUtils.isNotBlank(account) || StringUtils.isNotBlank(accountSK) || StringUtils.isNotBlank(masterAccount)
                    || StringUtils.isNotBlank(masterAccountSK)) && !is_emptyParams_Whitelisted) {
                Boolean validAccount = SFInterfaceBase.isValidAccount(account, masterAccount, masterAccountSK, accountSK, user);
                if (!validAccount) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDACCOUNT", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                }
            }
            if ((StringUtils.isNotBlank(userSK) || StringUtils.isNotBlank(loginID)) && !is_emptyParams_Whitelisted) {
                Boolean validUser = SFInterfaceBase.isValidUser(userSK, loginID, user);
                if (!validUser) {
                    errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSERORLOGINID", null, endpointName);
                    return new ResponseEntity<Object>(errorMap, HttpStatus.BAD_REQUEST);
                }
            }

            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.NORECORDSFOUND", null, endpointName);
            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
        }
    }

    public static Map<String, String[]> convertmapObject(Map<String, Object> requestFields) {
        Map<String, String[]> requestparametermap = new HashMap<>();
        requestFields.keySet().stream().forEach(strKey -> {
            String[] values = null;
            if (requestFields.get(strKey) instanceof ArrayList)
                values = GetStringArray((ArrayList<String>) requestFields.get(strKey));
            else if (requestFields.get(strKey) instanceof String)
                values = new String[]{requestFields.get(strKey).toString()};
            requestparametermap.put(strKey, values);
        });

        return requestparametermap;
    }

    public static String[] GetStringArray(List<String> arr) {
        return arr.toArray(new String[arr.size()]);
    }

    public static ResponseEntity<Map<String, Object>> createTicket(String busFunctionCode, String busFunctionDescription, String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> ticketFields, String endpointName, String version
            , HttpServletRequest request, JdbcTemplate refLabJDBCTemplate, JmsTemplate jmsTemplate) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>(), validateAPIResult = new LinkedHashMap<>(), toInitiateWorkFlow = new LinkedHashMap<>();
        Map<String, Object> ticketNumberMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String[] ticketNumberValidation = new String[10];
        Boolean toValidateAPI = Boolean.FALSE;
        String loginID = "";
        User user = new User();

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                loginID = user.getAuthUserName();
                SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, INPUT_JSON, ticketFields.toString(), user);
                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredTicketFieldsPresent(ticketFields
                            , getRequiredFieldsToCreateTicket(busFunctionCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)) {
                            validateAPIResult = SFInterfaceServices.validateTicketRequiredFields(ticketFields, null, aToken, refreshToken, accessToken, busFunctionCode, endpointName, user);
                            if (!validateAPIResult.containsKey(WF_VALIDATE_API_ERROR) && (int) validateAPIResult.get(STATUS) == NUM_ONE) {
                                toValidateAPI = Boolean.TRUE;
                            } else {
                                toValidateAPI = Boolean.FALSE;
                            }
                        } else {
                            toValidateAPI = Boolean.TRUE;
                        }

                        if (toValidateAPI) {
                            if (!ticketFields.containsKey(TICKET_NUMBER) || (ticketFields.containsKey(TICKET_NUMBER) &&
                                    StringUtils.isBlank((String) ticketFields.get(TICKET_NUMBER)))) {
                                ticketNumberMap = SFInterfaceBase.generateTicketNumber(refLabJDBCTemplate
                                        , Constants.TicketNumberGeneration, version, ticketFields, request, user);
                                if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                                    return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                                    ticketFields.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                                } else {
                                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                                    return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                }
                                SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, Json_AFTER_Manupulation, ticketFields.toString(), user);
                            }
                            if (ticketFields.containsKey(TICKET_NUMBER) && StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER))) {
                                Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName
                                        , version, methodType, user);
                                String spName = endPointDomain.get(QUERY).toString();
                                ticketNumberValidation = ticketFields.get(TICKET_NUMBER).toString().split(HYPHEN);
                                if (busFunctionCode.equalsIgnoreCase(INCIDENTS)) {
                                    if (INC.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {
                                        resultMap = SFInterfaceBase.createIncidentTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                        ticketFields.put(TICKETSK, resultMap.get(INCSK).toString());
                                    } else {
                                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                    }
                                } else if (busFunctionCode.equalsIgnoreCase(SR)) {
                                    if (SR.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {
                                        resultMap = SFInterfaceBase.createSRTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                        ticketFields.put(TICKETSK, resultMap.get(SRSK).toString());
                                    } else {
                                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                    }
                                } else if (busFunctionCode.equalsIgnoreCase(SRT)) {
                                    if (SRT.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {
                                        resultMap = SFInterfaceBase.createSRTTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                        ticketFields.put(TICKETSK, resultMap.get(SRTSK).toString());
                                    } else {
                                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                    }
                                }

                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                    return new ResponseEntity<>(resultMap, HttpStatus.UNPROCESSABLE_ENTITY);
                                } else {
                                    jmsTemplate.convertAndSend(INDEXER_UPDATE,
                                            new IndexerDomain(busFunctionDescription, ticketFields.get(TICKET_NUMBER).toString(), user.getClientInstance()));
                                    SFInterfaceLoggerBase.log(LOGGER, Constants.INSERT, busFunctionDescription, null, version, ticketFields.get(TICKET_NUMBER).toString(), ticketFields.toString(), user);

                                    if (!resultMap.containsKey(ERROR_MESSAGE) && ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)
                                            && ticketFields.get(INITIATE_R_FLOW).toString().equalsIgnoreCase(TRUE)) {
                                        toInitiateWorkFlow = SFInterfaceServices.initiateRFLOWAPI(ticketFields, INSERT, aToken, refreshToken, accessToken, busFunctionCode, endpointName, user);
                                        if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == NUM_ONE) {
                                            resultMap.put(R_FLOW, INITIATED);
                                        } else if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == MINUS_ONE) {
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(ERROR)));
                                        } else {
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(WF_INITIATE_API_ERROR)));
                                        }
                                    }
                                    SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, resultMap.toString(), user);
                                    return new ResponseEntity<>(resultMap, HttpStatus.OK);
                                }
                            }
                        } else {
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                    , (String) (validateAPIResult.get(WF_VALIDATE_API_ERROR) != NULL ? validateAPIResult.get(WF_VALIDATE_API_ERROR)
                                            : validateAPIResult.get(ERROR)));
                            SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.log(LOGGER, methodType, endpointName, null, version, RESPONSE, gson.toJson(resultMap), user);
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createTicketDetails(String busFunctionCode, String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>(), validateAPIResult = new LinkedHashMap<>(), toInitiateWorkFlow = new LinkedHashMap<>();
        Boolean toValidateAPI = Boolean.FALSE;
        String loginID = "";
        User user = new User();

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, NA, NA, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                loginID = user.getAuthUserName();

                if (StringUtils.isNotBlank(loginID)) {

                    ValidateRequiredField isValidRequiredField = isRequiredTicketDetailsFieldsPresent(ticketFields, getRequiredFieldsToCreateTicketDetails(busFunctionCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)) {
                            validateAPIResult = SFInterfaceServices.validateTicketRequiredFields(ticketFields, null, aToken, refreshToken, accessToken, busFunctionCode, endpointName, user);
                            if (!validateAPIResult.containsKey(WF_VALIDATE_API_ERROR) && (int) validateAPIResult.get(STATUS) == NUM_ONE) {
                                toValidateAPI = Boolean.TRUE;
                            } else {
                                toValidateAPI = Boolean.FALSE;
                            }
                        } else {
                            toValidateAPI = Boolean.TRUE;
                        }

                        if (toValidateAPI) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain.containsKey(ERROR)) {
                                String spName = endPointDomain.get(QUERY).toString();

                                if (ticketFields.containsKey(ATTACHMENTS)) {
                                    ticketFields.put(ATTACHMENTS, getAttachmentDetails(ticketFields.get(ATTACHMENTS)));
                                }
                                if (ticketFields.containsKey(COM_RESPONSES)) {
                                    ticketFields.put(COM_RESPONSES, getComResponseDetails(ticketFields.get(COM_RESPONSES)));
                                }
                                if (ticketFields.containsKey(KB_TICKET_MAPPING)) {
                                    ticketFields.put(KB_TICKET_MAPPING, getCreateKBTicketMapping(ticketFields.get(KB_TICKET_MAPPING)));
                                }
                                if (ticketFields.containsKey(KB_ARTICLE_LOG)) {
                                    ticketFields.put(KB_ARTICLE_LOG, getKBArticleLog(ticketFields.get(KB_ARTICLE_LOG)));
                                }
                                if (ticketFields.containsKey(MASTER_TYPE)) {
                                    ticketFields.put(MASTER_TYPE, getCreateMasterTypeMapping(ticketFields.get(MASTER_TYPE)));
                                }
                                if (busFunctionCode.equalsIgnoreCase(INCIDENTS)) {
                                    resultMap = SFInterfaceBase.createIncidentDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SR)) {
                                    resultMap = SFInterfaceBase.createSRDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SRT)) {
                                    resultMap = SFInterfaceBase.createSRTaskDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                }

                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                } else {
                                    if (!resultMap.containsKey(ERROR_MESSAGE) && ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)
                                            && ticketFields.get(INITIATE_R_FLOW).toString().equalsIgnoreCase(TRUE)) {
                                        toInitiateWorkFlow = SFInterfaceServices.initiateRFLOWAPI(ticketFields, INSERT, aToken, refreshToken, accessToken, busFunctionCode, endpointName, user);
                                        if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == NUM_ONE) {
                                            resultMap.put(R_FLOW, INITIATED);
                                        } else if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == MINUS_ONE) {
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(ERROR)));
                                        } else {
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(WF_INITIATE_API_ERROR)));
                                        }
                                    }
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                    , (String) (validateAPIResult.get(WF_VALIDATE_API_ERROR) != NULL ? validateAPIResult.get(WF_VALIDATE_API_ERROR)
                                            : validateAPIResult.get(ERROR)));
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SFInterfaceLoggerBase.response(LOGGER, endpointName, null, user.getAuthUserName(), resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> updateTicket(String busFunctionCode, String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, String indexername, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>(), validateAPIResult = new LinkedHashMap<>(), toInitiateWorkFlow = new LinkedHashMap<>();
        Boolean toValidateAPI = Boolean.FALSE;
        Map<String, Object> previousData = new HashMap<>();
        ;
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, NA, NA, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();

                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredTicketFieldsPresent(ticketFields, getRequiredFieldsToUpdateTicket(busFunctionCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)) {
                            previousData = fetchpreviousData(busFunctionCode, ticketFields.get(TICKETSK).toString(), aToken, refreshToken
                                    , accessToken, endpointName, user);
                            validateAPIResult = SFInterfaceServices.validateTicketRequiredFields(ticketFields, null, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);
                            if (!validateAPIResult.containsKey(WF_VALIDATE_API_ERROR) && (int) validateAPIResult.get(STATUS) == NUM_ONE) {
                                toValidateAPI = Boolean.TRUE;
                            } else {
                                toValidateAPI = Boolean.FALSE;
                            }
                        } else {
                            toValidateAPI = Boolean.TRUE;
                        }

                        if (toValidateAPI) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain.containsKey(ERRORCODE)) {
                                String spName = endPointDomain.get(QUERY).toString();
                                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, ticketFields.toString(), INPUT_JSON);
                                if (busFunctionCode.equalsIgnoreCase(INCIDENTS)) {
                                    resultMap = SFInterfaceBase.updateIncidentTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SR)) {
                                    resultMap = SFInterfaceBase.updateSRTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SRT)) {
                                    resultMap = SFInterfaceBase.updateSRTTicket(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                }

                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                } else {
                                    SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                                    if (ticketFields.containsKey(TICKET_NUMBER) && StringUtils.isNotBlank(indexername))
                                        jmsTemplate.convertAndSend(INDEXER_UPDATE,
                                                new IndexerDomain(indexername, ticketFields.get(TICKET_NUMBER).toString(), user.getClientInstance()));
                                    if (!resultMap.containsKey(ERROR_MESSAGE) && ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)
                                            && ticketFields.get(INITIATE_R_FLOW).toString().equalsIgnoreCase(TRUE)) {
                                        toInitiateWorkFlow = SFInterfaceServices.initiateRFLOWAPI(ticketFields, UPDATE, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);

                                        if (!toInitiateWorkFlow.containsKey(WF_TICKETINFO_API_ERROR)) {
                                            if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == NUM_ONE) {
                                                resultMap.put(R_FLOW, INITIATED);
                                            } else if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == MINUS_ONE) {
                                                /*resultMap.put(R_FLOW, NOT_INITIATED + toInitiateWorkFlow.get(ERROR));*/
                                                resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                        , (String) (toInitiateWorkFlow.get(ERROR)));
                                            } else {
                                                /*resultMap.put(WF_INITIATE_API_ERROR, toInitiateWorkFlow.get(WF_INITIATE_API_ERROR));*/
                                                resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                        , (String) (toInitiateWorkFlow.get(WF_INITIATE_API_ERROR)));
                                            }
                                        } else {
                                            /*resultMap.put(WF_TICKETINFO_API_ERROR, toInitiateWorkFlow.get(WF_TICKETINFO_API_ERROR));*/
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(WF_TICKETINFO_API_ERROR)));
                                        }
                                    }
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                    , (String) (validateAPIResult.get(WF_VALIDATE_API_ERROR) != NULL ? validateAPIResult.get(WF_VALIDATE_API_ERROR)
                                            : validateAPIResult.get(ERROR)));
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> updateTicketDetails(String busFunctionCode, String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>(), validateAPIResult = new LinkedHashMap<>(), toInitiateWorkFlow = new LinkedHashMap<>();
        Boolean toValidateAPI = Boolean.FALSE;
        Map<String, Object> previousData = new HashMap<>();

        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();

                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredTicketFieldsPresent(ticketFields, getRequiredFieldsToUpdateTicketDetails(busFunctionCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)) {
                            previousData = fetchpreviousData(busFunctionCode, ticketFields.get(TICKETSK).toString(),
                                    aToken, refreshToken, accessToken, endpointName, user);
                            validateAPIResult = SFInterfaceServices.validateTicketRequiredFields(ticketFields, null, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);
                            if (!validateAPIResult.containsKey(WF_VALIDATE_API_ERROR) && (int) validateAPIResult.get(STATUS) == NUM_ONE) {
                                toValidateAPI = Boolean.TRUE;
                            } else {
                                toValidateAPI = Boolean.FALSE;
                            }
                        } else {
                            toValidateAPI = Boolean.TRUE;
                        }

                        if (toValidateAPI) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain.containsKey(ERRORCODE)) {
                                String spName = endPointDomain.get(QUERY).toString();
                                if (ticketFields.containsKey(ATTACHMENTS)) {
                                    ticketFields.put(ATTACHMENTS, getAttachmentDetails(ticketFields.get(ATTACHMENTS)));
                                }
                                if (ticketFields.containsKey(COM_RESPONSES)) {
                                    ticketFields.put(COM_RESPONSES, getComResponseDetails(ticketFields.get(COM_RESPONSES)));
                                }
                                if (ticketFields.containsKey(KB_TICKET_MAPPING)) {
                                    ticketFields.put(KB_TICKET_MAPPING, getKBTicketMapping(ticketFields.get(KB_TICKET_MAPPING)));
                                }
                                if (ticketFields.containsKey(KB_ARTICLE_LOG)) {
                                    ticketFields.put(KB_ARTICLE_LOG, getKBArticleLog(ticketFields.get(KB_ARTICLE_LOG)));
                                }
                                if (ticketFields.containsKey(MASTER_TYPE)) {
                                    ticketFields.put(MASTER_TYPE, getMasterTypeMapping(ticketFields.get(MASTER_TYPE)));
                                }
                                if (ticketFields.containsKey(ROOT_CAUSE)) {
                                    ticketFields.put(ROOT_CAUSE, getRootCause(ticketFields.get(ROOT_CAUSE)));
                                }
                                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, ticketFields.toString());
                                if (busFunctionCode.equalsIgnoreCase(INCIDENTS)) {
                                    resultMap = SFInterfaceBase.updateIncidentDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SR)) {
                                    resultMap = SFInterfaceBase.updateServiceRequestDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                } else if (busFunctionCode.equalsIgnoreCase(SRT)) {
                                    resultMap = SFInterfaceBase.updateSRTaskDetails(refLabJDBCTemplate, spName, ticketFields, endpointName);
                                }

                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                } else {
                                    SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                                    if (!resultMap.containsKey(ERROR_MESSAGE) && ticketFields.get(ENGINE_TYPE).toString().equalsIgnoreCase(K_FLOW)
                                            && ticketFields.get(INITIATE_R_FLOW).toString().equalsIgnoreCase(TRUE)) {
                                        toInitiateWorkFlow = SFInterfaceServices.initiateRFLOWAPI(ticketFields, UPDATE, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);

                                        if (!toInitiateWorkFlow.containsKey(WF_TICKETINFO_API_ERROR)) {
                                            if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == NUM_ONE) {
                                                resultMap.put(R_FLOW, INITIATED);
                                            } else if (!toInitiateWorkFlow.containsKey(WF_INITIATE_API_ERROR) && (int) toInitiateWorkFlow.get(STATUS) == MINUS_ONE) {
                                                /*resultMap.put(R_FLOW, NOT_INITIATED + toInitiateWorkFlow.get(ERROR));*/
                                                resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                        , (String) (toInitiateWorkFlow.get(ERROR)));
                                            } else {
                                                /*resultMap.put(WF_INITIATE_API_ERROR, toInitiateWorkFlow.get(WF_INITIATE_API_ERROR));*/
                                                resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                        , (String) (toInitiateWorkFlow.get(WF_INITIATE_API_ERROR)));
                                            }
                                        } else {
                                            /*resultMap.put(WF_TICKETINFO_API_ERROR, toInitiateWorkFlow.get(WF_TICKETINFO_API_ERROR));*/
                                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                                    , (String) (toInitiateWorkFlow.get(WF_TICKETINFO_API_ERROR)));
                                        }
                                    }
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.WORKFLOWERROR", null, endpointName
                                    , (String) (validateAPIResult.get(WF_VALIDATE_API_ERROR) != NULL ? validateAPIResult.get(WF_VALIDATE_API_ERROR)
                                            : validateAPIResult.get(ERROR)));
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static Map<String, Object> validateTicketRequiredFields(Map<String, Object> ticketFields, String transactionType, String aToken
            , String refreshToken, String accessToken, String busFunctionCode, String endpointName, User user) throws Exception {
        return validateTicketRequiredFields(ticketFields, transactionType, aToken, refreshToken, accessToken, busFunctionCode, endpointName, null, user);
    }

    public static Map<String, Object> validateTicketRequiredFields(Map<String, Object> ticketFields, String transactionType, String aToken
            , String refreshToken, String accessToken, String busFunctionCode, String endpointName, Map<String, Object> previousData, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();

        try {
            resultMap = SFInterfaceServices.executeWorkFlowAPI(ticketFields, generateWorkflowAPIURL(busFunctionCode) + WF_VALIDATE
                    , transactionType, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            resultMap.put(WF_VALIDATE_API_ERROR, e.getMessage());
        }

        return resultMap;
    }

    public static Map<String, Object> initiateRFLOWAPI(Map<String, Object> ticketFields, String transactionType, String aToken
            , String refreshToken, String accessToken, String busFunctionCode, String endpointName, User user) throws Exception {
        return initiateRFLOWAPI(ticketFields, transactionType, aToken, refreshToken, accessToken, busFunctionCode, endpointName, null, user);
    }

    public static Map<String, Object> initiateRFLOWAPI(Map<String, Object> ticketFields, String transactionType, String aToken
            , String refreshToken, String accessToken, String busFunctionCode, String endpointName, Map<String, Object> previousData, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();

        try {
            resultMap = SFInterfaceServices.executeWorkFlowAPI(ticketFields, generateWorkflowAPIURL(busFunctionCode) + WF_INITIATE
                    , transactionType, aToken, refreshToken, accessToken, busFunctionCode, endpointName, previousData, user);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName + user.toString(), e);
            //resultMap.put(WF_INITIATE_API_ERROR, SFInterfaceServices.ISPExceptionHandler(e, null, endpointName).get(ERROR));
            resultMap.put(WF_INITIATE_API_ERROR, e.getMessage());
        }

        return resultMap;
    }

    public static Map<String, Object> getTicketInfoForUpdateRFLOWAPI(String request, String aToken, String refreshToken, String accessToken
            , String endpointName, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String resultString = "";
        String clientInstance = user.getClientInstance();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(refreshToken)) {
                headers.set(ACCESS_TOKEN, accessToken);
                headers.set(REFRESH_TOKEN, refreshToken);
                headers.set(CLIENT_INSTANCE, clientInstance);
            } else {
                headers.set(A_TOKEN, aToken);
                headers.set(CLIENT_INSTANCE, clientInstance);
            }

            HttpEntity<String> entity = new HttpEntity<String>(headers);
            //resultString = restTemplate.getForObject(request, String.class, entity);
            resultString = restTemplate.exchange(request, HttpMethod.GET, entity, String.class).getBody();
            resultMap.put(TICKETINFOAPI_RESULT, resultString);
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            //resultMap.put(WF_TICKETINFO_API_ERROR, SFInterfaceServices.ISPExceptionHandler(e, null, endpointName).get(ERROR));
            resultMap.put(WF_TICKETINFO_API_ERROR, e.getMessage());
        }

        return resultMap;
    }

    public static Map<String, Object> executeWorkFlowAPI(Map<String, Object> ticketFields, String apiURL, String transactionType, String aToken
            , String refreshToken, String accessToken, String busFunctionCode, String endpointName, Map<String, Object> previousData, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();

        Map<String, Object> inputMapToValidateAPI = new LinkedHashMap<>();
        String clientInstance = (String) user.getClientInstance();

        SFInterfaceLoggerBase.log(LOGGER, "Inside WorkFlow API call");
        String ticketNumber = StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER)) ? ticketFields.get(TICKET_NUMBER).toString() : "";
        SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketNumber, null, ticketFields.toString());
        String masteraccountCode = StringUtils.isNotBlank((String) ticketFields.get(MASTER_ACCOUNT_CODE)) ? ticketFields.get(MASTER_ACCOUNT_CODE).toString() : "";
        SFInterfaceLoggerBase.log(LOGGER, "MasterAccountCode:" + masteraccountCode);

        inputMapToValidateAPI.put(REFERENCE_NUMBER, StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER)) ? ticketFields.get(TICKET_NUMBER) : "");
        inputMapToValidateAPI.put(MASTER_ACCOUNT_CODE, ticketFields.get(MASTER_ACCOUNT_CODE));
        inputMapToValidateAPI.put(APPLICATION_CODE, ticketFields.get(APPLICATION_CODE));
        inputMapToValidateAPI.put(DATA, ticketFields);
        if (UPDATE.equalsIgnoreCase(transactionType)) {
            Map<String, Object> ticketInfoResult = previousData;
            if (ticketInfoResult.containsKey(TICKETINFOAPI_RESULT)) {
                String ticketInfo = (String) ticketInfoResult.get(TICKETINFOAPI_RESULT);
                List temp = new ArrayList<>();
                temp.add(ticketInfo);
                Map<String, Object> data = new HashMap<>();
                data.put(DATA, ticketInfo);
                inputMapToValidateAPI.put(PREV_DATA, data);
            } else {
                resultMap.put(WF_TICKETINFO_API_ERROR, ticketInfoResult.get(WF_TICKETINFO_API_ERROR));
                return resultMap;
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationConfig.Feature.USE_BIG_INTEGER_FOR_INTS);
        String jsonString = mapper.writeValueAsString(inputMapToValidateAPI);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(refreshToken)) {
            headers.set(ACCESS_TOKEN, accessToken);
            headers.set(REFRESH_TOKEN, refreshToken);
            headers.set(CLIENT_INSTANCE, clientInstance);
        } else {
            headers.set(A_TOKEN, aToken);
            headers.set(CLIENT_INSTANCE, clientInstance);
        }
        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        SFInterfaceLoggerBase.log(LOGGER, INITIATE_R_FLOW, endpointName, null, null, ticketNumber
                , null, null, entity.toString());
        resultMap = restTemplate.postForObject(apiURL, entity, HashMap.class);

        return resultMap;
    }

    public static Map<String, Object> fetchpreviousData(String busFunctionCode, String ticket_sk,
                                                        String aToken, String refreshToken,
                                                        String accessToken, String endpointName, User user) throws Exception {
        String reqUrl = Constants.getTicketInfoForUpdateRFlowAPI()
                + QUESTION_MARK
                + TICKETSK_LOWERCASE + EQUAL + ticket_sk
                + AND_SYMBOL
                + OBJECTCODE + EQUAL + SFInterfaceConstants.getStatusEntityCode().get(busFunctionCode);
        return SFInterfaceServices.getTicketInfoForUpdateRFLOWAPI(reqUrl, aToken, refreshToken, accessToken, endpointName, user);
    }

    public static String generateWorkflowAPIURL(String busFunctionCode) throws Exception {

        String apiURL = "";

        if (busFunctionCode.equalsIgnoreCase(INCIDENTS)) {
            apiURL = Constants.getWorkFlowAPIURL() + WF_INCIDENT + SLASH_SYMBOL;
        }
        if (busFunctionCode.equalsIgnoreCase(SR)) {
            apiURL = Constants.getWorkFlowAPIURL() + WF_SR + SLASH_SYMBOL;
        }
        if (busFunctionCode.equalsIgnoreCase(SRT)) {
            apiURL = Constants.getWorkFlowAPIURL() + WF_SRT + SLASH_SYMBOL;
        }

        return apiURL;
    }

    public static String trimToken(String token) throws Exception {
        return StringUtils.isNotBlank(token) ? token.trim() : token;
    }

    public static Map<String, String[]> checkMissingValueForKey(HttpServletRequest request) throws Exception {

        Map<String, String[]> params = new HashMap<>();

        for (Map.Entry<String, String[]> requestparametermaps : request.getParameterMap().entrySet()) {
            if (StringUtils.isBlank(requestparametermaps.getValue()[NUM_ZERO])) {
                Map<String, String[]> errorParam = new HashMap<>();
                errorParam.put(ERROR, new String[]{requestparametermaps.getKey() + sfInterfaceMessages.get("ERROR.MESSAGE.MISSINGVALUEFORKEY")});
                return errorParam;
            } else {
                params.put(requestparametermaps.getKey(), new String[]{requestparametermaps.getValue()[NUM_ZERO].trim()});
            }
        }

        return params;
    }

    public static Map<String, Object> convertTimestamptoStringFromMap(Map<String, Object> map) throws Exception {
        Map<String, Object> mapResultSet = new HashMap<>();
        for (Map.Entry<String, Object> maps : map.entrySet()) {
            mapResultSet.put(maps.getKey(), (maps.getValue() != NULL && maps.getValue() != "") ? maps.getValue().toString() : maps.getValue());
        }
        return mapResultSet;
    }

    public static List<Map<String, Object>> convertTimestamptoStringFromList(List<Map<String, Object>> list) throws Exception {

        List<Map<String, Object>> listResultSet = new ArrayList<>();

        if (list != NULL) {
            for (Map<String, Object> lists : list) {
                Map<String, Object> mapResultSet = new HashMap<>();
                for (Map.Entry<String, Object> maps : lists.entrySet()) {
                    mapResultSet.put(maps.getKey(), (maps.getValue() != NULL && maps.getValue() != "") ? maps.getValue().toString() : maps.getValue());
                }
                listResultSet.add(mapResultSet);
            }
        }

        return listResultSet;
    }

    public static CustomWhooshModel createCustomWhooshModel(Map<String, Object> fields) throws Exception {

        CustomWhooshModel customWhooshModel = new CustomWhooshModel();

        if (fields.containsKey(SELECTED_FIELDS)) {
            customWhooshModel.setRequiredColumns(fields.get(SELECTED_FIELDS).toString().split(COMMA));
        } else {
            customWhooshModel.setRequiredColumns(null);
        }
        if (fields.containsKey(SORTFIELD)) {
            customWhooshModel.setSortfields(fields.get(SORTFIELD).toString());
        } else {
            customWhooshModel.setSortfields(null);
        }
        if (fields.containsKey(DISTINCT)) {
            customWhooshModel.setDistinct(fields.get(DISTINCT).toString());
        } else {
            customWhooshModel.setDistinct(null);
        }
        if (fields.containsKey(DISTINCTFIELD)) {
            customWhooshModel.setDistinctfield(fields.get(DISTINCTFIELD).toString());
        } else {
            customWhooshModel.setDistinctfield(null);
        }
        if (fields.containsKey(PAGE_NUMBER)) {
            customWhooshModel.setPageno(Integer.parseInt(fields.get(PAGE_NUMBER).toString()));
        } else {
            customWhooshModel.setPageno(NUM_ONE);
        }
        if (fields.containsKey(COUNT)) {
            customWhooshModel.setCount(Integer.parseInt(fields.get(COUNT).toString()));
        } else {
            customWhooshModel.setCount(NUM_ZERO);
        }
        return customWhooshModel;
    }

    public static Map<String, String> convertSPQueryToMap(String query) {
        String[] alternateQuery = StringUtils.substringsBetween(query, "{{", "}}");
        return Arrays.stream(alternateQuery).collect(Collectors.toMap(o -> o.toString(), o -> o.toString()));
    }

    public static ResponseEntity createOrUpdateRequest(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, NA, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(CREATE_VACATION)) {
                            resultMap = SFInterfaceBase.createVacationDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_VACATION)) {
                            resultMap = SFInterfaceBase.updateVacationDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_PROFILE)) {
                            resultMap = SFInterfaceBase.updateProfileInfo(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_WATCHLIST)) {
                            resultMap = SFInterfaceBase.updateWatchlist(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_USER_FAVORITIES)) {
                            resultMap = SFInterfaceBase.updateCRUserFavorities(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(CREATE_WATCHLIST_REPORT)) {
                            resultMap = SFInterfaceBase.createWatchList(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_PROFILE_PREFRENCE)) {
                            resultMap = SFInterfaceBase.updateProfilePrefrence(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_USERNOTES)) {
                            resultMap = SFInterfaceBase.updateUserNotes(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            arrayList.add(resultMap);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SECURE_INFO_HISTORY)) {
                            resultMap = SFInterfaceBase.updateSecureInfoHistory(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SECUREINFO_DOWNLADED_HISTORY)) {
                            resultMap = SFInterfaceBase.updateDownloadedAttachmentsHistory(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (endpointName.equalsIgnoreCase(UPDATE_USERNOTES)) {
            return new ResponseEntity<>(convertTimestamptoStringFromList(arrayList), HttpStatus.OK);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateRequestWithMultipleInputs(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_USER_PREFRENCE)) {
                            arrayList = SFInterfaceBase.updateUserPrefrence(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACCOUNTGROUPUSER)) {
                            arrayList = SFInterfaceBase.updateAccountGroupUser(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SECURE_ANSWERS1)) {
                            arrayList = SFInterfaceBase.updateSecureAnswers1(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_INCIDENT_MANAGER)) {
                            parameters.put(PROBLEM_MANAGER_SK, convertListToCommaSeparatedValue(parameters.get(PROBLEM_MANAGER_SK)));
                            arrayList = SFInterfaceBase.updateProblemManager(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            if (arrayList.size() > NUM_ZERO) {
                                resultMap = arrayList.get(NUM_ZERO);
                            }

                        } else if (endpointName.equalsIgnoreCase(CREATE_INCIDENT_MANAGER)) {
                            parameters.put(ACCOUNT_SK, convertListToCommaSeparatedValue(parameters.get(ACCOUNT_SK)));
                            arrayList = SFInterfaceBase.createProblemManager(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_APPROVERS)) {
                            arrayList = SFInterfaceBase.updateApprovers(refLabJDBCTemplate, spName, parameters, endpointName);
                            resultMap = arrayList.get(NUM_ZERO);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (endpointName.equalsIgnoreCase(UPDATE_USER_PREFRENCE) || endpointName.equalsIgnoreCase(UPDATE_ACCOUNTGROUPUSER)
                || endpointName.equalsIgnoreCase(UPDATE_SECURE_ANSWERS1) || endpointName.equalsIgnoreCase(UPDATE_INCIDENT_MANAGER)
                || endpointName.equalsIgnoreCase(CREATE_INCIDENT_MANAGER) || endpointName.equalsIgnoreCase(UPDATE_CR_DISCUSSION_BOARD)
                || endpointName.equalsIgnoreCase(UPDATE_APPROVERS)) {
            return new ResponseEntity<>(convertTimestamptoStringFromList(arrayList), HttpStatus.OK);
        }

        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createOrUpdateRequestWithAttachments(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        if (parameters.containsKey(ATTACHMENTS)) {
                            String attachments = getAttachmentDetails(parameters.get(ATTACHMENTS));
                            parameters.put(ATTACHMENTS, attachments);
                        }
                        if (endpointName.equalsIgnoreCase(CREATE_DOCUMENT)) {
                            resultMap = SFInterfaceBase.createDocumentDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DOCUMENT)) {
                            resultMap = SFInterfaceBase.updateDocumentDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_SUPPORT)) {
                            resultMap = SFInterfaceBase.createSupportDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SUPPORT)) {
                            resultMap = SFInterfaceBase.updateSupportDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }
                        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createOrUpdateEmailRequest(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        if (endpointName.equalsIgnoreCase(UPDATE_INC_EMAIL)) {
                            resultMap = SFInterfaceBase.updateINCEmailDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SR_EMAIL)) {
                            resultMap = SFInterfaceBase.updateSREmailDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SRTASK_EMAIL)) {
                            resultMap = SFInterfaceBase.updateSRTEmailDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (parameters.containsKey(ATTACHMENTS)) {
                            createMailAttachments(getStatusEntityCode().get(resultMap.get("BusinessfunctionType").toString().equalsIgnoreCase(SRTASK) ? SRT : resultMap.get("BusinessfunctionType")).toString() + UNDERSCORE + "EMAIL"
                                    , resultMap.get("EmailSK").toString(), convertemailattachmentStringArray((List<Map<String, Object>>) parameters.get(ATTACHMENTS))
                                    , parameters.containsKey(USER_SK) ? parameters.get(USER_SK).toString() : parameters.get(MODIFIED_BY).toString(), user);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else {
                            SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, e.getMessage());
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createNotification(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, String[]> requestparametermap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String query = endPointDomain.get(QUERY).toString();
                        if (endpointName.equalsIgnoreCase(CREATE_NOTIFICATION)) {
                            if (parameters != null)
                                requestparametermap = convertmapObject(parameters);
                            query = getDBQuery(query, requestparametermap);
                            List<Map<String, Object>> result = runjdbcQuery(refLabJDBCTemplate, query);
                            if (result == null || result.isEmpty()) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            } else {
                                resultMap = result.get(NUM_ZERO);
                            }
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> updateFrontEndMessages(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, String[]> requestparametermap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String query = endPointDomain.get(QUERY).toString();
                        if (endpointName.equalsIgnoreCase(UPDATE_FRONTENDMESSAGES)) {
                            resultMap = SFInterfaceBase.updateFrontEndMessages(refLabJDBCTemplate, query, parameters, endpointName);
                            resultMap = convertTimestamptoStringFromMap(resultMap);
                        } else if (endpointName.equalsIgnoreCase(CREATE_FRONTENDMESSAGES)) {
                            resultMap = SFInterfaceBase.createFrontEndMessages(refLabJDBCTemplate, query, parameters, endpointName);
                            resultMap = convertTimestamptoStringFromMap(resultMap);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> reportingCreateTicket(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> ticketFields, String endpointName, String version
            , HttpServletRequest request, JdbcTemplate endpointJDBCTemplate, JmsTemplate jmsTemplate, JdbcTemplate refLabJDBCTemplate, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> workNotes = new HashMap<>();
        String loginID = "";
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> ticket = new ArrayList<>();
        String endpoint = "", worknotesEndpoint = "";


        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        String ticketNumber = "", createdDate = "";
                        //To get TicketNumber
                        if (ticketFields.containsKey(TICKET_NUMBER)) {
                            ticketNumber = (String) ticketFields.get(TICKET_NUMBER);
                        } else if (ticketFields.containsKey(RECORD_ENTRY_ID)) {
                            ticketNumber = (String) ticketFields.get(RECORD_ENTRY_ID);
                        } else if (ticketFields.containsKey(TASK_ID)) {
                            ticketNumber = (String) ticketFields.get(TASK_ID);
                        }
                        //To get endpointName
                        if (ticketNumber.contains(INC_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_INCIDENT_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_INC_WORKNOTES;
                        } else if (ticketNumber.contains(SR_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_SR_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_SR_WORKNOTES;
                        } else if (ticketNumber.contains(SRT_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_SRT_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_SRT_WORKNOTES;
                        }
                        //To fetch Ticket Details
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, V1, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            params.put(TICKET, new String[]{ticketNumber});
                            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                            ticket = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, new String[]{SUBMITTER_LOGINID, CREATED_ON, SD_PROVIDED_DETAILS});
                        }
                        if (ticket.size() > NUM_ZERO) {
                            createdDate = (String) ticket.get(NUM_ZERO).get(CREATED_ON);
                            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                            Date date = df.parse(createdDate);
                            long createdOn = date.getTime();
                            ticketFields.put(ADEQUATE_DETAILS, ticket.get(NUM_ZERO).get(SD_PROVIDED_DETAILS) != null ? createdOn + "\u0004" + (String) ticket.get(NUM_ZERO).get(SUBMITTER_LOGINID) + "\u0004" +
                                    (String) ticket.get(NUM_ZERO).get(SD_PROVIDED_DETAILS) : NULL);
                        }

                        workNotes = worklogConcatenation(ticketNumber, createdDate, refLabJDBCTemplate, worknotesEndpoint, user);
                        if (!workNotes.isEmpty()) {
                            ticketFields.put(WORK_LOG, workNotes.containsKey(WORK_LOG) ? workNotes.get(WORK_LOG) : null);
                        }

                        if (endpointName.equalsIgnoreCase(CREATE_DMART_INC)) {
                            resultMap = SFInterfaceBase.createINCReport(endpointJDBCTemplate, spName, ticketFields, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_SR)) {
                            resultMap = SFInterfaceBase.createSRReport(endpointJDBCTemplate, spName, ticketFields, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_SRTASK)) {
                            resultMap = SFInterfaceBase.createSRTReport(endpointJDBCTemplate, spName, ticketFields, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_CONSOLIDATED)) {
                            resultMap = SFInterfaceBase.createConsolidatedList(endpointJDBCTemplate, spName, ticketFields, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else {
                            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> updateTicketReportsDetails(String methodType, String aToken
            , String refreshToken, String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate endpointJDBCTemplate, HttpServletRequest request, JdbcTemplate refLabJDBCTemplate, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";
        Map<String, Object> workNotes = new HashMap<>();
        String endpoint = "", worknotesEndpoint = "";
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> ticket = new ArrayList<>();

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        String ticketNumber = "", modifiedDate = "";
                        if (parameters.containsKey(TICKET_NUMBER)) {
                            ticketNumber = (String) parameters.get(TICKET_NUMBER);
                        } else if (parameters.containsKey(RECORD_ENTRY_ID)) {
                            ticketNumber = (String) parameters.get(RECORD_ENTRY_ID);
                        }
                        //To get endpointName
                        if (ticketNumber.contains(INC_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_INCIDENT_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_INC_WORKNOTES;
                        } else if (ticketNumber.contains(SR_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_SR_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_SR_WORKNOTES;
                        } else if (ticketNumber.contains(SRT_WITH_HYPHEN)) {
                            endpoint = INDEXERNAME_FETCH_SRT_SP;
                            worknotesEndpoint = INDEXERNAME_FETCH_SRT_WORKNOTES;
                        }
                        //To fetch Ticket Details
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpoint, V1, GET_METHOD, user);
                        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                            params.put(TICKET, new String[]{ticketNumber});
                            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                            ticket = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, new String[]{MODIFIED_BY_LOGINID, MODIFIED_ON, SD_PROVIDED_DETAILS, SD_ASSIGNED_WRONG_GROUP_COMMENTS, SD_RESOLVED_COMMENTS});
                        }
                        if (ticket.size() > NUM_ZERO) {
                            modifiedDate = (String) ticket.get(NUM_ZERO).get(MODIFIED_ON);
                            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                            Date date = df.parse(modifiedDate);
                            long modifiedOn = date.getTime();
                            parameters.put(ADEQUATE_DETAILS, ticket.get(NUM_ZERO).get(SD_PROVIDED_DETAILS) != null ? modifiedOn + "\u0004" + (String) ticket.get(NUM_ZERO).get(MODIFIED_BY_LOGINID) + "\u0004" +
                                    (String) ticket.get(NUM_ZERO).get(SD_PROVIDED_DETAILS) : NULL);
                            parameters.put(ZASSIGNED_TO_WRONG_GROUP_LOG, ticket.get(NUM_ZERO).get(SD_ASSIGNED_WRONG_GROUP_COMMENTS) != null ? modifiedOn + "\u0004" + (String) ticket.get(NUM_ZERO).get(MODIFIED_BY_LOGINID) + "\u0004" +
                                    (String) ticket.get(NUM_ZERO).get(SD_ASSIGNED_WRONG_GROUP_COMMENTS) : NULL);
                            parameters.put(ZCOULD_RESOLVED_BY_HELPDESK, ticket.get(NUM_ZERO).get(SD_RESOLVED_COMMENTS) != null ? modifiedDate + " " + (String) ticket.get(NUM_ZERO).get(MODIFIED_BY_LOGINID) + " " +
                                    (String) ticket.get(NUM_ZERO).get(SD_RESOLVED_COMMENTS) : NULL);
                        }

                        workNotes = worklogConcatenation(ticketNumber, modifiedDate, refLabJDBCTemplate, worknotesEndpoint, user);
                        if (!workNotes.isEmpty()) {
                            parameters.put(TICKET_UPDATE_TYPE, workNotes.containsKey(TICKET_UPDATE_TYPE) ? workNotes.get(TICKET_UPDATE_TYPE) : null);
                            parameters.put(ACD_CALL, workNotes.containsKey(ACD_CALL) ? workNotes.get(ACD_CALL) : null);
                            parameters.put(WORK_LOG, workNotes.containsKey(WORK_LOG) ? workNotes.get(WORK_LOG) : null);
                            parameters.put(WORKLOG_UPDATE, workNotes.containsKey(WORKLOG_UPDATE) ? workNotes.get(WORKLOG_UPDATE) : null);

                        }

                        if (endpointName.equalsIgnoreCase(UPDATE_DMART_INC)) {
                            resultMap = SFInterfaceBase.updateINCREport(endpointJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_SR)) {
                            resultMap = SFInterfaceBase.updateSRReport(endpointJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_SRTASK)) {
                            resultMap = SFInterfaceBase.updateSRTaskReport(endpointJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_CONSOLIDATED)) {
                            resultMap = SFInterfaceBase.updateConsolidatedList(endpointJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createOrUpdateAPI(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID;

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_SECURE_ANSWERS)) {
                            resultMap = SFInterfaceBase.updateSecureAnswers(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(CREATE_COM_RESPONSES)) {
                            resultMap = SFInterfaceBase.createResponses(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_COM_RESPONSES)) {
                            resultMap = SFInterfaceBase.updateResponses(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_HANGUPS)) {
                            resultMap = SFInterfaceBase.updateHangupsDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_HANGUPS_REPORTING)) {
                            resultMap = SFInterfaceBase.createHangUpsReporting(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_APPLICATION)) {
                            resultMap = SFInterfaceBase.updateApplication(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACC_APPLICATION)) {
                            resultMap = SFInterfaceBase.updateAccApplication(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACC_GROUP_APP)) {
                            resultMap = SFInterfaceBase.updateAccGroupApp(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> createOrUpdateKBAPI(String methodType, String aToken, String refreshToken
            , String accessToken, Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";


        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(CREATE_KBARTICLET_LOG)) {
                            resultMap = SFInterfaceBase.createKBArticleLogDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KBARTICLET_LOG)) {
                            resultMap = SFInterfaceBase.updateKBArticleLogDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_TICKET_MAPPING)) {
                            resultMap = SFInterfaceBase.updateKBTicketMapping(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateLocationDetails(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_BUILDING)) {
                            resultMap = SFInterfaceBase.updateBuilding(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DEPARTMENT)) {
                            resultMap = SFInterfaceBase.updateDepartment(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_FLOOR)) {
                            resultMap = SFInterfaceBase.updateFloor(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SUITE)) {
                            resultMap = SFInterfaceBase.updateSuite(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACCOUNT)) {
                            resultMap = SFInterfaceBase.updateAccount(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GROUP)) {
                            resultMap = SFInterfaceBase.updateGroup(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACCOUNT_GROUP)) {
                            resultMap = SFInterfaceBase.updateAccountGroup(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CATEGORY)) {
                            resultMap = SFInterfaceBase.updateCategory(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateLocationDetails1(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_TYPE)) {
                            resultMap = SFInterfaceBase.updateType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ITEM)) {
                            resultMap = SFInterfaceBase.updateItem(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CTI)) {
                            resultMap = SFInterfaceBase.updateCTI(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_PENDING_REASON)) {
                            resultMap = SFInterfaceBase.UpdatePendingReasons(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DESIGNATION)) {
                            resultMap = SFInterfaceBase.UpdateDesignation(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            resultMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }


    public static ResponseEntity createProfile(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {

                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresentForProfileCreate(parameters, getCreateProfileReqFields());
                    if (isValidRequiredField.getIsvalid()) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();

                            if (endpointName.equalsIgnoreCase(CREATE_PROFILE)) {
                                resultMap = SFInterfaceBase.createProfile(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            }
                            if (resultMap.size() == NUM_ZERO) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            } else {
                                SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity saveAttachments(MultipartHttpServletRequest multiPartRequest, Map<String, String[]> ticketFields
            , String aToken, String refreshToken, String accessToken, HttpServletRequest request, String endpointName) throws Exception {

        List<Map<String, Object>> resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    if (ticketFields.containsKey(FILE_PATH) && ticketFields.containsKey(ACCOUNT)) {
                        resultMap = returnAttachmentResults(multiPartRequest, ticketFields, endpointName);
                        if (resultMap.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            return new ResponseEntity<>(errorMap, HttpStatus.OK);
                        }
                    } else {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSTOSAVEATTACHMENTS", null, endpointName);
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static List<Map<String, Object>> returnAttachmentResults(MultipartHttpServletRequest multiPartRequest
            , Map<String, String[]> ticketFields, String endpointName) throws Exception {

        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> resultMap = new ArrayList();
        String fileNameWithTimestamp = "";
        File newFile = null;

        String filePath = SFInterfaceConstants.getFileserverURL() + ticketFields.get(FILE_PATH)[NUM_ZERO].toString();

        try {
            if (StringUtils.isNotBlank(multiPartRequest.getContentType()) && multiPartRequest.getContentType().startsWith(MULTIPART_FORM_DATA)) {
                MultiValueMap<String, MultipartFile> attachments = multiPartRequest.getMultiFileMap();
                for (Map.Entry<String, List<MultipartFile>> stringListEntry : attachments.entrySet()) {
                    File[] files = new File[stringListEntry.getValue().size() + NUM_ONE];
                    int i = NUM_ZERO;
                    for (MultipartFile multipartFile : stringListEntry.getValue()) {
                        fileNameWithTimestamp = new SimpleDateFormat(DATETIME_FORMAT).format(new Date()) + UNDERSCORE
                                + multipartFile.getOriginalFilename();
                        newFile = SFInterfaceBase.convertMultiPartToFile(multipartFile, fileNameWithTimestamp, filePath);
                        files[i] = newFile;
                        i++;
                        result.put(FILE_NAME, multipartFile.getOriginalFilename());
                        result.put(FILE_PATH, ticketFields.get(FILE_PATH)[NUM_ZERO].toString() + fileNameWithTimestamp);
                        result.put(FILE_TYPE, FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
                        result.put(FILE_SIZE, String.valueOf(multipartFile.getSize()));
                        resultMap.add(result);
                        result = new LinkedHashMap<>();
                    }
                }
            }

            return resultMap;
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            Map<String, Object> errorMap = new LinkedHashMap<>();
            errorMap.put(ERROR, SFInterfaceServices.ISPExceptionHandler(e, null, endpointName).get(ERROR));
            return (List<Map<String, Object>>) errorMap;
        }
    }

    public static ResponseEntity downloadAttachment(String refreshToken, String accessToken, HttpServletRequest request, String endpointName) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        byte[] fileContent = null;
        String loginID = "", fileName = "", filePath = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, gson.toJson(request.getParameterMap()));
            if (isRequestContainToken(null, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();

                if (StringUtils.isNotBlank(loginID)) {
                    if (request.getParameterMap().containsKey(ACCOUNT) && request.getParameterMap().containsKey(FILE_NAME)
                            && request.getParameterMap().containsKey(FILE_PATH)) {

                        fileName = request.getParameter(FILE_NAME);
                        filePath = Constants.getFileserverURL() + request.getParameter(FILE_PATH);

                        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(servletContext, fileName);

                        fileContent = Files.readAllBytes(Paths.get(String.valueOf(filePath)));

                        return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName)
                                .contentType(mediaType)
                                .contentLength(fileContent.length)
                                .body(fileContent);
                    } else {
                        resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORATTACHMENTS", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static Map<String, Object> ticketSummaryCount(List<Map<String, Object>> result, Set<String> ticketStatus) {

        Map<String, Object> resultSet = new HashMap<>(), incTickets = new HashMap<>(), srTickets = new HashMap<>(), srtTickets = new HashMap<>(), crTickets = new HashMap<>();
        Map<String, Object> incResultSet = new HashMap<>(), srResultSet = new HashMap<>(), srtResultSet = new HashMap<>(), crResultSet = new HashMap<>();
        String ticketNumber = "";
        int incTotal = NUM_ZERO, srTotal = NUM_ZERO, srtTotal = NUM_ZERO, crTotal = NUM_ZERO, total = NUM_ZERO;

        if (result != NULL) {
            for (String status : ticketStatus) {
                int incCount = NUM_ZERO, srCount = NUM_ZERO, srtCount = NUM_ZERO, crCount = NUM_ZERO;
                for (Map<String, Object> resultSet1 : result) {
                    if (resultSet1.get(STATUS_DESCRIPTION).toString().equalsIgnoreCase(status)) {
                        if (resultSet1.get(BUSINESS_FUNCTION_CODE).toString().equalsIgnoreCase(INCIDENTS)) {
                            incCount++;
                            incTotal++;
                        } else if (resultSet1.get(BUSINESS_FUNCTION_CODE).toString().equalsIgnoreCase(SR)) {
                            srCount++;
                            srTotal++;
                        } else if (resultSet1.get(BUSINESS_FUNCTION_CODE).toString().equalsIgnoreCase(SRT)) {
                            srtCount++;
                            srtTotal++;
                        } else if (resultSet1.get(BUSINESS_FUNCTION_CODE).toString().equalsIgnoreCase(CR)) {
                            crCount++;
                            crTotal++;
                        }
                    }
                }
                incTickets.put(status, incCount);
                srTickets.put(status, srCount);
                srtTickets.put(status, srtCount);
                crTickets.put(status, crCount);
            }
        }
        total = incTotal + srTotal + srtTotal + crTotal;

        incResultSet.put(STATUS, incTickets);
        incResultSet.put(TOTAL, incTotal);
        srResultSet.put(STATUS, srTickets);
        srResultSet.put(TOTAL, srTotal);
        srtResultSet.put(STATUS, srtTickets);
        srtResultSet.put(TOTAL, srtTotal);
        crResultSet.put(STATUS, crTickets);
        crResultSet.put(TOTAL, crTotal);
        resultSet.put(INC, incResultSet);
        resultSet.put(SR, srResultSet);
        resultSet.put(SRT, srtResultSet);
        resultSet.put(CR, crResultSet);
        resultSet.put(TOTAL, total);

        return resultSet;
    }

    public static Map<String, Object> fetchTicketCounts(Set<String> businessFunction, Map<String, Object> assignedIndividualResultSet
            , Map<String, Object> groupResultSet, Map<String, Object> submitterResultSet) {

        Map<String, Object> countResult = new HashMap<>(), incCountResult = new HashMap<>(), srCountResult = new HashMap<>();
        Map<String, Object> srtCountResult = new HashMap<>(), raisedCount = new HashMap<>(), crCountResult = new HashMap<>();
        int totalraisedcount = NUM_ZERO;

        if (businessFunction.contains(INCIDENTS)) {
            Map<String, Object> result1 = (Map<String, Object>) assignedIndividualResultSet.get(INC);
            Map<String, Object> result2 = (Map<String, Object>) groupResultSet.get(INC);
            Map<String, Object> result3 = (Map<String, Object>) submitterResultSet.get(INC);
            int total = Integer.parseInt(result1.get(TOTAL).toString()) + Integer.parseInt(result2.get(TOTAL).toString());
            incCountResult.put(MY_INC, assignedIndividualResultSet.get(INC));
            incCountResult.put(MY_GROUP_INC, groupResultSet.get(INC));
            incCountResult.put(TOTAL, total);
            raisedCount.put(INC, submitterResultSet.get(INC));
            totalraisedcount = Integer.parseInt(result3.get(TOTAL).toString()) + totalraisedcount;
            raisedCount.put(TOTAL, totalraisedcount);
            countResult.put(INC, incCountResult);
            countResult.put(RAISED_BY_ME, raisedCount);
        }
        if (businessFunction.contains(SR)) {
            Map<String, Object> result1 = (Map<String, Object>) assignedIndividualResultSet.get(SR);
            Map<String, Object> result2 = (Map<String, Object>) groupResultSet.get(SR);
            Map<String, Object> result3 = (Map<String, Object>) submitterResultSet.get(SR);
            int total = Integer.parseInt(result1.get(TOTAL).toString()) + Integer.parseInt(result2.get(TOTAL).toString());
            srCountResult.put(MY_SR, assignedIndividualResultSet.get(SR));
            srCountResult.put(MY_GROUP_SR, groupResultSet.get(SR));
            srCountResult.put(TOTAL, total);
            raisedCount.put(SR, submitterResultSet.get(SR));
            totalraisedcount = Integer.parseInt(result3.get(TOTAL).toString()) + totalraisedcount;
            raisedCount.put(TOTAL, totalraisedcount);
            countResult.put(SR, srCountResult);
            countResult.put(RAISED_BY_ME, raisedCount);

        }
        if (businessFunction.contains(SRT)) {
            Map<String, Object> result1 = (Map<String, Object>) assignedIndividualResultSet.get(SRT);
            Map<String, Object> result2 = (Map<String, Object>) groupResultSet.get(SRT);
            Map<String, Object> result3 = (Map<String, Object>) submitterResultSet.get(SRT);
            int total = Integer.parseInt(result1.get(TOTAL).toString()) + Integer.parseInt(result2.get(TOTAL).toString());
            srtCountResult.put(MY_SRT, assignedIndividualResultSet.get(SRT));
            srtCountResult.put(MY_GROUP_SRT, groupResultSet.get(SRT));
            srtCountResult.put(TOTAL, total);
            raisedCount.put(SRT, submitterResultSet.get(SRT));
            totalraisedcount = Integer.parseInt(result3.get(TOTAL).toString()) + totalraisedcount;
            raisedCount.put(TOTAL, totalraisedcount);
            countResult.put(SRT, srtCountResult);
            countResult.put(RAISED_BY_ME, raisedCount);

        }
        if (businessFunction.contains(CR)) {
            Map<String, Object> result1 = (Map<String, Object>) assignedIndividualResultSet.get(CR);
            Map<String, Object> result2 = (Map<String, Object>) groupResultSet.get(CR);
            Map<String, Object> result3 = (Map<String, Object>) submitterResultSet.get(CR);
            int total = Integer.parseInt(result1.get(TOTAL).toString()) + Integer.parseInt(result2.get(TOTAL).toString());
            crCountResult.put(MY_CR, assignedIndividualResultSet.get(CR));
            crCountResult.put(MY_GROUP_CR, groupResultSet.get(CR));
            crCountResult.put(TOTAL, total);
            raisedCount.put(CR, submitterResultSet.get(CR));
            totalraisedcount = Integer.parseInt(result3.get(TOTAL).toString()) + totalraisedcount;
            raisedCount.put(TOTAL, totalraisedcount);
            countResult.put(CR, crCountResult);
            countResult.put(RAISED_BY_ME, raisedCount);
        }
        return countResult;
    }


    public static String getAttachmentDetails(Object attachments) {

        ArrayList<Map> attachmentDetails = (ArrayList) attachments;
        String fileDetails = "";

        for (Map<String, String> file : attachmentDetails) {

            if (!fileDetails.isEmpty()) {
                fileDetails += SINGLE_PIPE;
            }
            fileDetails += file.get(FILE_NAME) + QUESTION_MARK + file.get(FILE_SIZE) + QUESTION_MARK + file.get(FILE_TYPE)
                    + QUESTION_MARK + file.get(FILE_PATH);

            if (file.containsKey(TYPE)) {
                fileDetails += QUESTION_MARK + file.get(TYPE);
            }

            if (file.containsKey(DESCRIPTION) && StringUtils.isNotBlank(file.get(DESCRIPTION))) {
                fileDetails += QUESTION_MARK + file.get(DESCRIPTION);
            }
        }

        return fileDetails;
    }

    public static String getCRAttachmentDetails(Object attachments) {

        ArrayList<Map> attachmentDetails = (ArrayList) attachments;
        String fileDetails = "";

        for (Map<String, String> file : attachmentDetails) {

            if (!fileDetails.isEmpty()) {
                fileDetails += SINGLE_PIPE;
            }
            fileDetails += file.get(FILE_NAME) + QUESTION_MARK + file.get(FILE_SIZE) + QUESTION_MARK + file.get(FILE_TYPE)
                    + QUESTION_MARK + file.get(FILE_PATH);

            fileDetails += QUESTION_MARK;
            if (file.containsKey(DESCRIPTION) && StringUtils.isNotBlank(file.get(DESCRIPTION))) {
                fileDetails += file.get(DESCRIPTION);
            }
            fileDetails += QUESTION_MARK;

            if (file.containsKey(RISK_SK) && StringUtils.isNotBlank(file.get(RISK_SK))) {
                fileDetails += file.get(RISK_SK);
            }
        }

        return fileDetails;
    }

    public static String getCRTAttachmentDetails(Object attachments) {

        ArrayList<Map> attachmentDetails = (ArrayList) attachments;
        String fileDetails = "";

        for (Map<String, String> file : attachmentDetails) {

            if (!fileDetails.isEmpty()) {
                fileDetails += SINGLE_PIPE;
            }
            fileDetails += file.get(FILE_NAME) + QUESTION_MARK + file.get(FILE_SIZE) + QUESTION_MARK + file.get(FILE_TYPE)
                    + QUESTION_MARK + file.get(FILE_PATH);

            fileDetails += QUESTION_MARK;
            if (file.containsKey(DESCRIPTION) && StringUtils.isNotBlank(file.get(DESCRIPTION))) {
                fileDetails += file.get(DESCRIPTION);
            }
            fileDetails += QUESTION_MARK;

            if (file.containsKey(ITEM) && StringUtils.isNotBlank(file.get(ITEM))) {
                fileDetails += file.get(ITEM);
            }
        }

        return fileDetails;
    }

    public static String getCRAssets(Object assets) {

        ArrayList<Map> assetDetails = (ArrayList) assets;
        String result = "";

        for (Map<String, String> resultMap : assetDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (resultMap.containsKey(CR_ASSET_SK)) {
                result += resultMap.get(CR_ASSET_SK);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(ASSET_TAG)) {
                result += resultMap.get(ASSET_TAG);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(ASSET_NAME)) {
                result += resultMap.get(ASSET_NAME);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(TYPE) && StringUtils.isNotBlank(resultMap.get(TYPE))) {
                result += resultMap.get(TYPE);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(STATUS) && StringUtils.isNotBlank(resultMap.get(STATUS))) {
                result += resultMap.get(STATUS);
            }
        }

        return result;
    }

    public static String getCRGroups(Object groups) {

        ArrayList<Map> groupDetails = (ArrayList) groups;
        String result = "";

        for (Map<String, String> resultMap : groupDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (resultMap.containsKey(CR_GROUP_SK)) {
                result += resultMap.get(CR_GROUP_SK);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(ACCOUNT_GROUP_SK)) {
                result += resultMap.get(ACCOUNT_GROUP_SK);
            }
            result += QUESTION_MARK;
            if (resultMap.containsKey(TYPE)) {
                result += resultMap.get(TYPE);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(STATUS)) {
                result += resultMap.get(STATUS);
            }
        }

        return result;
    }

    public static String getCRBuildings(Object buildings) {

        ArrayList<Map> buildingDetails = (ArrayList) buildings;
        String result = "";

        for (Map<String, String> resultMap : buildingDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (resultMap.containsKey(CR_BUILDING_SK)) {
                result += resultMap.get(CR_BUILDING_SK);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(BUILDING_SK)) {
                result += resultMap.get(BUILDING_SK);
            }
            result += QUESTION_MARK;
            if (resultMap.containsKey(TYPE)) {
                result += resultMap.get(TYPE);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(STATUS)) {
                result += resultMap.get(STATUS);
            }
        }

        return result;
    }

    public static String getCRLocations(Object locations) {

        ArrayList<Map> locationDetails = (ArrayList) locations;
        String result = "";

        for (Map<String, String> resultMap : locationDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (resultMap.containsKey(LOCATION_SK)) {
                result += resultMap.get(LOCATION_SK);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(CR_LOCATION_SK)) {
                result += resultMap.get(CR_LOCATION_SK);
            }
            result += QUESTION_MARK;
            if (resultMap.containsKey(TYPE)) {
                result += resultMap.get(TYPE);
            }
            result += QUESTION_MARK;

            if (resultMap.containsKey(STATUS)) {
                result += resultMap.get(STATUS);
            }
        }

        return result;
    }

    public static String getComResponseDetails(Object request) {

        ArrayList<Map> comResponseDetails = (ArrayList) request;
        String result = "";

        for (Map<String, String> comResponse : comResponseDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }

            result += comResponse.get(RDY_QUERIES_SK) + QUESTION_MARK + comResponse.get(RESPONSE);
            if (comResponse.containsKey(RESPONSE_SK)) {
                result += QUESTION_MARK + comResponse.get(RESPONSE_SK);
            } else {
                result += QUESTION_MARK;
            }
            if (comResponse.containsKey(STATUS)) {
                result += QUESTION_MARK + comResponse.get(STATUS);
            } else {
                result += QUESTION_MARK;
            }
            if (comResponse.containsKey(COMMENTS)) {
                result += QUESTION_MARK + comResponse.get(COMMENTS);
            } else {
                result += QUESTION_MARK;
            }
            if (comResponse.containsKey(JSON_RESPONSE)) {
                result += QUESTION_MARK + comResponse.get(JSON_RESPONSE);
            }
        }

        return result;
    }


    public static String getKBTicketMapping(Object request) {

        ArrayList<Map> articleMetaDataDetails = (ArrayList) request;
        String result = "";

        for (Map<String, String> articleMetaData : articleMetaDataDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (articleMetaData.containsKey(TICKET_MAPPING_SK)) {
                result += articleMetaData.get(TICKET_MAPPING_SK);
            }
            result += QUESTION_MARK;

            if (articleMetaData.containsKey(ARTICLE_METADATA_SK)) {
                result += articleMetaData.get(ARTICLE_METADATA_SK);
            }
            result += QUESTION_MARK;

            if (articleMetaData.containsKey(STATUS)) {
                result += articleMetaData.get(STATUS);
            }

        }

        return result;
    }

    public static String getCreateKBTicketMapping(Object request) {

        ArrayList<Map> articleMetaDataDetails = (ArrayList) request;
        String result = "";

        for (Map<String, String> articleMetaData : articleMetaDataDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }

            if (articleMetaData.containsKey(ARTICLE_METADATA_SK)) {
                result += articleMetaData.get(ARTICLE_METADATA_SK);
            }

        }

        return result;
    }

    public static String getKBArticleLog(Object request) {

        ArrayList<Map> kbArticleLogDetails = (ArrayList) request;
        String result = "";

        for (Map<String, String> kbArticleLog : kbArticleLogDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            result += kbArticleLog.get(ARTICLE_LOG_SK);
        }

        return result;
    }


    public static String getCreateMasterTypeMapping(Object request) {

        ArrayList<Map> masterTypeMappingDetails = (ArrayList) request;

        String result = "";

        for (Map<String, String> masterTypeMapping : masterTypeMappingDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            result += masterTypeMapping.get(MASTER_TYPE_SK);
        }

        return result;
    }

    public static String getMasterTypeMapping(Object request) {
        ArrayList<Map> masterTypeMappingDetails = (ArrayList) request;

        String result = "";
        for (Map<String, String> masterTypeMapping : masterTypeMappingDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (masterTypeMapping.containsKey(MASTERTYPE_MAPPING_SK)) {
                result += masterTypeMapping.get(MASTERTYPE_MAPPING_SK);
            }
            result += QUESTION_MARK;

            if (masterTypeMapping.containsKey(MASTER_TYPE_SK)) {
                result += masterTypeMapping.get(MASTER_TYPE_SK);
            }
            result += QUESTION_MARK;

            if (masterTypeMapping.containsKey(STATUS)) {
                result += masterTypeMapping.get(STATUS);
            }
        }
        return result;
    }

    public static String getComRefrences(Object request) {
        ArrayList<Map> comRefrencesDetails = (ArrayList) request;

        String result = "";
        for (Map<String, String> comRefrences : comRefrencesDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (comRefrences.containsKey(TICKET_SK)) {
                result += comRefrences.get(TICKET_SK);
            }
            result += QUESTION_MARK;

            if (comRefrences.containsKey(BUSINESS_FUNCTION_SK)) {
                result += comRefrences.get(BUSINESS_FUNCTION_SK);
            }
            result += QUESTION_MARK;

            if (comRefrences.containsKey(CUSTOM_REFERNCE)) {
                result += comRefrences.get(CUSTOM_REFERNCE);
            }
        }
        return result;
    }

    public static String getRootCause(Object request) {
        ArrayList<Map> rootCauseDetails = (ArrayList) request;

        String result = "";
        for (Map<String, String> rootCause : rootCauseDetails) {

            if (!result.isEmpty()) {
                result += SINGLE_PIPE;
            }
            if (rootCause.containsKey(INC_ROOT_CAUSE_SK)) {
                result += rootCause.get(INC_ROOT_CAUSE_SK);
            } else if (rootCause.containsKey(SR_ROOT_CAUSE_SK)) {
                result += rootCause.get(SR_ROOT_CAUSE_SK);
            }
            result += QUESTION_MARK;

            if (rootCause.containsKey(RCL_MAPPING_SK)) {
                result += rootCause.get(RCL_MAPPING_SK);
            }
            result += QUESTION_MARK;

            if (rootCause.containsKey(STATUS) && StringUtils.isNotBlank(rootCause.get(STATUS))) {
                result += rootCause.get(STATUS);
            }
        }
        return result;
    }

    public static Map<String, Object> exceptionHandler(Exception e, String endpointName) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        if (StringUtils.isNotBlank(e.getMessage()) && e.getMessage().contains(SQL_SERVER_EXC)) {
            if (e.getMessage().contains(FOREIGN_KEY)) {
                String entityName = SFInterfaceServices.foreignKeyViolationExceptionHandler(e);
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.FOREIGNKEYVIOLATION", entityName, endpointName);
                return errorMap;
            } else if (e.getMessage().contains(COLUMN_DOESNOT_ALLOW_NULLS)) {
                String column = e.getMessage();
                column = column.substring(column.indexOf(SINGLE_QUOTE) + NUM_ONE, column.indexOf(COMMA) - NUM_ONE);
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.COLUMNDOESNOTALLOWNULLS", column, endpointName);
                return errorMap;
            } else {
                errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.UNHANDLEDERROR", null, endpointName
                        , e.getCause().getMessage());
                return errorMap;
            }
        }
        errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.UNHANDLEDERROR", null, endpointName
                , e.getMessage());
        return errorMap;
    }

    public static Map<String, Object> ISPExceptionHandler(Exception e, String SPQuery, String endpointName) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        if (StringUtils.isNotBlank(SPQuery) && (SPQuery.contains(DOUBLE_OPEN_BRACE) || SPQuery.contains(DOUBLE_CLOSE_BRACE))) {
            SPQuery = SPQuery.replaceAll("\\{\\{|\\}\\}", "");
        }

        if (StringUtils.isNotBlank(e.getMessage()) && e.getLocalizedMessage().contains(EMPTY_RESULTSET)) {
            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYRESULSET", (StringUtils.isNotBlank(SPQuery) ? SPQuery : ""), endpointName);
        } else if (StringUtils.isNotBlank(e.getMessage()) && e.getLocalizedMessage().contains(CONVERSION_FAILED)) {
            errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.DATATYPECONVERSIONERROR", null, endpointName);
        } else if (StringUtils.isNotBlank(e.getMessage()) && e.getLocalizedMessage().contains(INVALID_USER)) {
            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDUSER", (StringUtils.isNotBlank(SPQuery) ? SPQuery : ""), endpointName);
        } else {
            errorMap = SFInterfaceServices.exceptionHandler(e, endpointName);
        }

        return errorMap;
    }

    public static String foreignKeyViolationExceptionHandler(Exception e) {

        String errorMessage = e.getCause().getMessage();
        errorMessage = errorMessage.substring(errorMessage.indexOf(SINGLE_QUOTE) + NUM_ONE, errorMessage.length() - NUM_FIVE);
        if (errorMessage.equalsIgnoreCase(PEOPLE_INFO)) {
            errorMessage = PEOPLE_INFORMATION;
        }
        return errorMessage;
    }

    public static String getSPCallableStatement(String spName, int paddingSize) {
        return "{call " + spName + " (?" + StringUtils.rightPad("", paddingSize, ",?") + ")}";
    }

  /*  public static User getUser(HttpServletRequest request) {
        User user = new User();
        if (request != NULL && request.getUserPrincipal() != NULL && request.getUserPrincipal().getName() != NULL) {
            String[] userDetail = request.getUserPrincipal().getName().split(TILDE);
            user.setAuthUserName(userDetail[NUM_ZERO]);
            user.setClient(userDetail[NUM_ONE]);
            user.setUserID(userDetail[NUM_TWO]);
            user.setAuthTokenType(AUTH_TOKEN);
            user.setClientInstance(userDetail[NUM_THREE]);
        } else if (request.getHeader(A_TOKEN) != NULL && StringUtils.isNotBlank(request.getHeader(A_TOKEN))
                && request.getHeader(A_TOKEN).trim().equalsIgnoreCase(SFInterfaceConstants.getaTokenValue())) {
            user.setUserID(SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_PROFILE, LOGIN_ID + COLON + Constants.getAdminusername()
                    , null, new String[]{USER_SK}, null, null, TRUE, null, user).get(0).get(USER_SK).toString());
            user.setAuthUserName(Constants.getAdminusername());
            user.setAuthTokenType(ATOKEN);
        }
        return user;
    }*/

    public static ValidateRequiredField isRequiredTicketFieldsPresent(Map<String, Object> ticketFields, List<String> requiredFields) {

        ValidateRequiredField validateRequiredField = new ValidateRequiredField();
        if (ticketFields.containsKey(WORKNOTES) && ticketFields.get(WORKNOTES) != NULL && !ticketFields.get(WORKNOTES).toString().isEmpty()) {
            requiredFields.add(WORKNOTE_TYPE);
        }
        for (String param : requiredFields) {
            if (ticketFields.containsKey(param)) {
                if (param.equals(Constants.STATUS) || param.equals(IS_ALTERNATE_USER) || param.equals(ALT_IS_SYSTEM_USER)) {
                    if (ticketFields.get(param) == NULL) {
                        validateRequiredField.getMandatoryfields().add(param);
                        validateRequiredField.setIsvalid(Boolean.FALSE);
                    }
                } else {
                    if (ticketFields.get(param) == NULL || ticketFields.get(param).toString().isEmpty() || ticketFields.get(param).equals(NUM_ZERO)) {
                        validateRequiredField.getMandatoryfields().add(param);
                        validateRequiredField.setIsvalid(Boolean.FALSE);
                    }
                }
            } else {
                validateRequiredField.getMandatoryfields().add(param);
                validateRequiredField.setIsvalid(Boolean.FALSE);
            }
        }

        return validateRequiredField;
    }

    public static ValidateRequiredField isRequiredTicketDetailsFieldsPresent
            (Map<String, Object> ticketFields, List<String> requiredFields) {

        ValidateRequiredField validateRequiredField = new ValidateRequiredField();
        if (ticketFields.containsKey(IS_ALTERNATE_USER)) {
            if (ticketFields.get(IS_ALTERNATE_USER).toString().equalsIgnoreCase(ONE))
                requiredFields.add(ALT_IS_SYSTEM_USER);
        }
        for (String param : requiredFields) {
            if (ticketFields.containsKey(param)) {
                if (param.equals(ALT_IS_SYSTEM_USER) || param.equals(IS_ALTERNATE_USER)) {
                    if (ticketFields.get(param) == NULL) {
                        validateRequiredField.getMandatoryfields().add(param);
                        validateRequiredField.setIsvalid(Boolean.FALSE);
                    }
                } else {
                    if (ticketFields.get(param) == NULL || ticketFields.get(param).toString().isEmpty() || ticketFields.get(param).equals(NUM_ZERO)) {
                        validateRequiredField.getMandatoryfields().add(param);
                        validateRequiredField.setIsvalid(Boolean.FALSE);
                    }
                }
            } else {
                validateRequiredField.getMandatoryfields().add(param);
                validateRequiredField.setIsvalid(Boolean.FALSE);
            }
        }

        return validateRequiredField;
    }

    public static ValidateRequiredField isRequiredFieldsPresent
            (Map<String, Object> ticketFields, List<String> requiredFields) {


        ValidateRequiredField validateRequiredField = new ValidateRequiredField();
        for (String param : requiredFields) {
            if (ticketFields.containsKey(param)) {
                if (ticketFields.get(param) == NULL || ticketFields.get(param).toString().isEmpty()) {
                    validateRequiredField.getMandatoryfields().add(param);
                    validateRequiredField.setIsvalid(Boolean.FALSE);
                }
            } else {
                validateRequiredField.getMandatoryfields().add(param);
                validateRequiredField.setIsvalid(Boolean.FALSE);
            }
        }

        return validateRequiredField;
    }

    public static ValidateRequiredField isRequiredFieldsPresentForProfileCreate
            (Map<String, Object> ticketFields, List<String> requiredFields) {

        ValidateRequiredField validateRequiredField = new ValidateRequiredField();
        for (String param : requiredFields) {
            if (ticketFields.containsKey(param)) {
                if (ticketFields.get(param) == NULL || ticketFields.get(param).toString().isEmpty()) {
                    validateRequiredField.getMandatoryfields().add(param);
                    validateRequiredField.setIsvalid(Boolean.FALSE);
                }
            } else {
                validateRequiredField.getMandatoryfields().add(param);
                validateRequiredField.setIsvalid(Boolean.FALSE);
            }
        }

        return validateRequiredField;
    }

    public static List<LDAPAccounts> getLdapAccountsForClient(String client, User user) throws IOException {
        List<LDAPAccounts> ldapAccountsList = new ArrayList<>();
        String query = "(MasterAccount:\"" + client + "\" " +
                " OR Account:\"" + client + "\") "
                + AND + SSO + COLON + NUM_ONE
                + AND + NOT + PORT + COLON + NULL_VALUE;
        List<Map<String, Object>> result = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_LDAPDETAILS, query, null, null, null, null, null, null, 0, Boolean.FALSE, null, user);

        result.stream().forEach(ldapaccount -> {
            LDAPAccounts ldapAccounts = new LDAPAccounts();
            ldapAccounts.setBase((String) ldapaccount.get("Base"));
            ldapAccounts.setClient((String) ldapaccount.get("Account"));
            ldapAccounts.setConnName((String) ldapaccount.get("ConnectionName"));
            ldapAccounts.setDomain((String) ldapaccount.get("Domain"));
            ldapAccounts.setFilter((String) ldapaccount.get("Filter"));
            ldapAccounts.setHost((String) ldapaccount.get("Host"));
            ldapAccounts.setUsername((String) ldapaccount.get("UserName"));
            ldapAccounts.setPassword((String) ldapaccount.get("Password"));
            ldapAccounts.setPort(Integer.parseInt((String) ldapaccount.get("Port")));
            ldapAccounts.setSearchDN((String) ldapaccount.get("SearchDN"));
            ldapAccounts.setPosttext((String) ldapaccount.get("PostText"));
            ldapAccounts.setPretext((String) ldapaccount.get("PreText"));
            ldapAccountsList.add(ldapAccounts);
        });

        return ldapAccountsList;
    }

  /*  public static User getUserLogin(String userName, String client) throws Exception {
        return getUserLogin(userName, client, null);
    }*/

    public static User getUserLogin(String userName, String client, User tempUser, String auth_type) throws Exception {
        User user = new User();
        try {
            String query = OPEN_BRACKET + MASTER_ACCOUNT + COLON + "\"" + client + "\" " + OR + ACCOUNT + COLON + "\"" + client + "\"" + CLOSE_BRACKET
                    + AND
                    + OPEN_BRACKET + LOGIN_ID + COLON + "\"" + userName + "\"" + OR + " NetworkLogin:\"" + userName + "\"" + OR + " RefID3:\"" + userName + "\"" + OR + " RefID4:\"" + userName + "\"" + CLOSE_BRACKET
                    + AND
                    + OPEN_BRACKET + STATUS + COLON + "\"Active\"" + OR + STATUS + COLON + "\"Hidden\"" + OR + OPEN_BRACKET + STATUS + COLON + "\"Inactive\" AND" + NOT + " ReturnOn:\"" + NULL_VALUE + "\"" + CLOSE_BRACKET + CLOSE_BRACKET;
            List<Map<String, Object>> result = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_PROFILE, query, null, new String[]{"Account", "User_sk", "NetworkLogin", MASTER_ACCOUNT}, null, null, null, null, 0, Boolean.FALSE, tempUser);

            if (result != null && !result.isEmpty()) {
                result.parallelStream().limit(1).forEach(stringObjectMap -> {
                    user.setClient(stringObjectMap.get("Account").toString());
                    user.setUserID(stringObjectMap.get("User_sk").toString());
                    user.setAuthUserName(userName + "~" + client + "~" + stringObjectMap.get("User_sk").toString() + "~" + tempUser.getClientInstance() + "~" + auth_type);
                    user.setNetworkLogin(stringObjectMap.get("NetworkLogin") != null ? stringObjectMap.get("NetworkLogin").toString() : null);
                    user.setClientInstance(tempUser.getClientInstance());
                    user.setMasterclient(stringObjectMap.get(MASTER_ACCOUNT).toString());
                    user.setAuthTokenType(auth_type);
                });
            } else {
                return null;
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, LOGIN, e);
            throw new Exception(sfInterfaceMessages.get("AUTHENTICATION.ERROR.PROFILE.NOTFOUND"));
        }
        return user;
    }


    public static Map<String, Object> worklogConcatenation(String ticketNumber, String modifiedOn, JdbcTemplate refLabJDBCTemplate, String endpointName, User user) throws ParseException, IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        List<Map<String, Object>> workLogs = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> WorkLog = new HashMap<>();

        endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET, new String[]{ticketNumber});
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
            workLogs = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
            if (workLogs.size() > NUM_ZERO) {

                String workLogString = "";
                SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                Date incModifiedDate = df.parse(modifiedOn);

                for (Map<String, Object> workLog : workLogs) {
                    Boolean flag = Boolean.FALSE;
                    String date_str = (String) workLog.get(CREATED_ON);
                    Date worknoteDate = df.parse(date_str);
                    long worknoteEpochDate = worknoteDate.getTime();
                    if (incModifiedDate.compareTo(worknoteDate) == 0) {
                        flag = Boolean.TRUE;
                    }

                    if (StringUtils.isBlank(workLogString)) {
                        workLogString = worknoteEpochDate + "\u0004" + workLog.get(WORK_LOG)
                                + "\u0004" + workLog.get(CREATED_BY_LOGINID);
                    } else {
                        workLogString += "\u0003" + worknoteEpochDate + "\u0004" + workLog.get(WORK_LOG)
                                + "\u0004" + workLog.get(CREATED_BY_LOGINID);
                    }

                    if (flag == Boolean.TRUE) {
                        WorkLog.put(TICKET_UPDATE_TYPE, workLog.get(WORKNOTES_UPDATETYPE));
                        if (StringUtils.isNotBlank((String) workLog.get(ACD))) {
                            WorkLog.put(ACD_CALL, workLog.get(ACD).equals("1") ? "Yes" : "No");
                        }

                        WorkLog.put(WORKLOG_UPDATE, workLog.get(WORK_LOG));

                    }

                }
                WorkLog.put(WORK_LOG, workLogString);
            }

        }

        return WorkLog;
    }

    public static Map<String, Object> CreateEmailTicket(HttpServletRequest request, String endpointName, Map<String, Object> parameters, MailObject mailObject, User user) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result =
                createOrUpdateEmailRequest(POST_METHOD, request.getHeader(A_TOKEN)
                        , request.getHeader(REFRESH_TOKEN), request.getHeader(ACCESS_TOKEN), parameters, endpointName, "V1"
                        , SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), request, user).getBody();
        if (mailObject.getMailAttachments() != null)
            createMailAttachments(getStatusEntityCode().get(result.get("BusinessfunctionType").toString().equalsIgnoreCase(SRTASK) ? SRT : result.get("BusinessfunctionType")).toString() + UNDERSCORE + "EMAIL"
                    , result.get("EmailSK").toString(), convertAttachmenttoStringArray(mailObject), parameters.get(USER_SK).toString(), user);
        return result;
    }

    public static String convertAttachmenttoStringArray(MailObject mailObject) {

        return mailObject.getMailAttachments()
                .stream()
                .map(mailAttachment ->
                                mailAttachment.getFilename() + "?" + mailAttachment.getFilesize() + "?" + mailAttachment.getFiletype() + "?" + mailAttachment.getFilepath() + "??"
                        //'ABC?30MB?xls?D:\Ashok?S?secinfo|xyz?10MB?pdf?D:\Ashok\temp?U?secinfo2'

                ).collect(Collectors.joining("|"));

    }

    public static String convertemailattachmentStringArray(List<Map<String, Object>> mailObject) {
        return mailObject
                .stream()
                .map(mailAttachment ->
                                StringEscapeUtils.escapeSql((String) mailAttachment.get(FILE_NAME))
                                        + QUESTION_MARK + StringEscapeUtils.escapeSql((String) mailAttachment.get(FILE_SIZE))
                                        + QUESTION_MARK + StringEscapeUtils.escapeSql((String) mailAttachment.get(FILE_TYPE))
                                        + QUESTION_MARK + StringEscapeUtils.escapeSql((String) mailAttachment.get(FILE_PATH))
                                        + QUESTION_MARK + QUESTION_MARK
                        //'ABC?30MB?xls?D:\Ashok?S?secinfo|xyz?10MB?pdf?D:\Ashok\temp?U?secinfo2'
                ).collect(Collectors.joining("|"));
    }

    public static Map<String, Object> fetchemailparams(MailObject mailObject, String ticketid, String ticknumber, String businessFunctionCode, String usersk, String account, User user) {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> statusrecords = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_STATUS,
                ENTITYCODE + COLON + getStatusEntityCode().get(businessFunctionCode) + "MAIL" + AND + STATUS_CODE + COLON + (mailObject.getStatussk() != null ? mailObject.getStatussk() : SFInterfaceConstants.getEmail_status_code()),
                null, null, null, null, null, null, 1, user).get(0);
        params.put(SOURCE, SFInterfaceConstants.getEmail_Source());
        params.put(FROM, mailObject.getFrom());
        params.put(TO, mailObject.getTo());
        params.put(CC, mailObject.getCc());
        params.put(SUBJECT, mailObject.getSubject());
        params.put(ACCOUNT_SK, account);
        params.put(MAIL_BODY, mailObject.getMailbody());
        params.put(STATUS_SK, statusrecords.get(STATUS_SK));
        params.put(USER_SK, usersk);
        params.put(MAILID, mailObject.getMailid());
        params.put(OFFLINE_UNIQUE_ID, mailObject.getOfflineUniqueID());
        if (businessFunctionCode.equalsIgnoreCase(SR)) {
            params.put(SR_SK, ticketid);
            params.put(SR_NUMBER, ticknumber);
        } else if (businessFunctionCode.equalsIgnoreCase(SRT)) {
            params.put(SRT_SK, ticketid);
            params.put(SRT_NUMBER, ticknumber);

        } else {
            params.put(INC_SK, ticketid);
            params.put(INC_NUMBER, ticknumber);
        }
        return params;
    }

    public static void createMailAttachments(String entityname, String entitysk, String attachments, String user_sk, User user) {

        runjdbcQuery(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), String.format("EXEC [API_CreateComAttachments] '%s','%s','%s','%s'", attachments, entityname, entitysk, user_sk));

    }

    public static Map<String, Object> getErrorMap(String errorMessage, String extraMessage, String endpointName) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        JsonObject jsonObject = SFInterfaceBase.fetchTicketJsonfromPath(ERROR_MAPPING_JSON_PATH).get(ERROR).getAsJsonObject();
        String errorCode = jsonObject.get(errorMessage) != NULL ? jsonObject.get(errorMessage).getAsString() : "";
        if (StringUtils.isBlank(errorCode)) {
            errorMap.put(MESSAGE, (StringUtils.isNotBlank(errorMessage) ? errorMessage : ""));
        } else {
            if (sfInterfaceMessages.get(errorMessage).substring(NUM_ZERO, NUM_ONE).contains(HYPHEN)) {
                errorMap.put(MESSAGE, (StringUtils.isNotBlank(extraMessage) ? extraMessage : "") + sfInterfaceMessages.get(errorMessage));
            } else {
                errorMap.put(MESSAGE, sfInterfaceMessages.get(errorMessage) + (StringUtils.isNotBlank(extraMessage) ? extraMessage : ""));
            }
        }
        errorMap.put(ERRORCODE, errorCode);
        errorMap.put(MOREINFO, MOREINFO_URL + (StringUtils.isNotBlank(endpointName) ? endpointName : "") + SLASH_SYMBOL);
        SFInterfaceLoggerBase.log(LOGGER, endpointName, " Response:" + gson.toJson(errorMap));
        return errorMap;
    }

    public static Map<String, Object> getDefaultErrorMap(String errorMessage, String extraMessage, String endpointName, String error) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        JsonObject jsonObject = SFInterfaceBase.fetchTicketJsonfromPath(ERROR_MAPPING_JSON_PATH).get(ERROR).getAsJsonObject();
        String errorCode = jsonObject.get(errorMessage) != NULL ? jsonObject.get(errorMessage).getAsString() : "";
        if (StringUtils.isNotBlank(errorCode)) {
            errorMap.put(MESSAGE, (StringUtils.isNotBlank(error) ? error : ""));
        }
        errorMap.put(ERRORCODE, errorCode);
        errorMap.put(MOREINFO, MOREINFO_URL + (StringUtils.isNotBlank(endpointName) ? endpointName : "") + SLASH_SYMBOL);
        SFInterfaceLoggerBase.log(LOGGER, endpointName, " Response:" + gson.toJson(errorMap));
        return errorMap;
    }

    public static Map<String, Object> getInfoMap(String infoMessage, String extraMessage, String endpointName) throws IOException {

        Map<String, Object> errorMap = new HashMap<>();

        JsonObject jsonObject = SFInterfaceBase.fetchTicketJsonfromPath(ERROR_MAPPING_JSON_PATH).get(INFO).getAsJsonObject();
        String infoCode = jsonObject.get(infoMessage) != NULL ? jsonObject.get(infoMessage).getAsString() : "";
        if (StringUtils.isBlank(infoCode)) {
            errorMap.put(MESSAGE, (StringUtils.isNotBlank(infoMessage) ? infoMessage : ""));
        } else {
            if (sfInterfaceMessages.get(infoMessage).substring(NUM_ZERO, NUM_ONE).contains(HYPHEN)) {
                errorMap.put(MESSAGE, (StringUtils.isNotBlank(extraMessage) ? extraMessage : "") + sfInterfaceMessages.get(infoMessage));
            } else {
                errorMap.put(MESSAGE, sfInterfaceMessages.get(infoMessage) + (StringUtils.isNotBlank(extraMessage) ? extraMessage : ""));
            }
        }
        errorMap.put(INFOCODE, infoCode);
        errorMap.put(MOREINFO, MOREINFO_URL + (StringUtils.isNotBlank(endpointName) ? endpointName : "") + SLASH_SYMBOL);
        SFInterfaceLoggerBase.log(LOGGER, endpointName, " Response:" + gson.toJson(errorMap));
        return errorMap;
    }

    public static ResponseEntity createChangeRequest(String businessFunCode, String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), ticketNumberMap = new LinkedHashMap<>(), riskMap = new LinkedHashMap<>();
        Map<String, Object> impactMap = new LinkedHashMap<>();
        String[] ticketNumberValidation = new String[10];

        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(ticketFields, getCreateChangeRequestReqFields(businessFunCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (!ticketFields.containsKey(TICKET_NUMBER) || (ticketFields.containsKey(TICKET_NUMBER) &&
                                StringUtils.isBlank((String) ticketFields.get(TICKET_NUMBER)))) {
                            ticketNumberMap = SFInterfaceBase.generateTicketNumber(refLabJDBCTemplate
                                    , Constants.TicketNumberGeneration, version, ticketFields, request, user);
                            if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                            } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                                ticketFields.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                            }
                        }
                        if (ticketFields.containsKey(TICKET_NUMBER) && StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER))) {
                            String type = (String) ticketFields.get(TYPE);
                            if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(DFT) && type.equalsIgnoreCase(TMP)) {
                                String ticketNumberMapSplit[] = ticketNumberMap.get(TICKET_NUMBER).toString().split(HYPHEN);
                                if (type.equalsIgnoreCase(DFT)) {
                                    ticketFields.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER).toString().replace(ticketNumberMapSplit[NUM_ZERO], DFT));
                                } else if (type.equalsIgnoreCase(TMP)) {
                                    ticketFields.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER).toString().replace(ticketNumberMapSplit[NUM_ZERO], TMP));
                                }
                            }
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain.containsKey(ERRORCODE)) {
                                String spName = endPointDomain.get(QUERY).toString();

                                ticketNumberValidation = ticketFields.get(TICKET_NUMBER).toString().split(HYPHEN);
                                if (endpointName.equalsIgnoreCase(CREATE_CHANGE_REQUEST)) {
                                    if (CR.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO]) || DFT.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {

                                        if (ticketFields.containsKey(ATTACHMENTS)) {
                                            ticketFields.put(ATTACHMENTS, getCRAttachmentDetails(ticketFields.get(ATTACHMENTS)));
                                        }
                                        if (ticketFields.containsKey(COM_RESPONSES)) {
                                            ticketFields.put(COM_RESPONSES, getComResponseDetails(ticketFields.get(COM_RESPONSES)));
                                        }
                                        if (ticketFields.containsKey(CR_ASSESTS)) {
                                            ticketFields.put(CR_ASSESTS, getCRAssets(ticketFields.get(CR_ASSESTS)));
                                        }
                                        if (ticketFields.containsKey(CR_GROUPS)) {
                                            ticketFields.put(CR_GROUPS, getCRGroups(ticketFields.get(CR_GROUPS)));
                                        }
                                        if (ticketFields.containsKey(CR_BUILDINGS)) {
                                            ticketFields.put(CR_BUILDINGS, getCRBuildings(ticketFields.get(CR_BUILDINGS)));
                                        }
                                        if (ticketFields.containsKey(CR_LOCATIONS)) {
                                            ticketFields.put(CR_LOCATIONS, getCRLocations(ticketFields.get(CR_LOCATIONS)));
                                        }
                                        if (ticketFields.containsKey(COM_REFRENCES)) {
                                            ticketFields.put(COM_REFRENCES, getComRefrences(ticketFields.get(COM_REFRENCES)));
                                        }
                                        if (ticketFields.containsKey(CR_WORKNOTES)) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(WORKNOTES, ticketFields.get(CR_WORKNOTES));
                                            ticketFields.put(CR_WORKNOTES, jsonData);
                                        }
                                        if (ticketFields.containsKey(COM_PERMISSIONS)) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(COM_PERMISSIONS, ticketFields.get(COM_PERMISSIONS));
                                            ticketFields.put(COM_PERMISSIONS, jsonData);
                                        }
                                        if (ticketFields.containsKey(CR_RISKS)) {
                                            riskMap.put(RISKS, ticketFields.get(CR_RISKS));
                                            ticketFields.put(CR_RISKS, riskMap);
                                        }
                                        if (ticketFields.containsKey(CR_IMPACTED)) {
                                            impactMap.put(IMPACTED, ticketFields.get(CR_IMPACTED));
                                            ticketFields.put(CR_IMPACTED, impactMap);
                                        }
                                        SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, ticketFields.toString(), Json_AFTER_Manupulation);

                                        resultMap = SFInterfaceBase.createChangeRequest(refLabJDBCTemplate, spName, ticketFields, endpointName, user);
                                        /*if (resultMap.containsKey(CR_SK)) {
                                            ticketFields.put(TICKET_SK, resultMap.get(CR_SK).toString());
                                            SFInterfaceServices.createChangeRequestDetails(CR, POST_METHOD, trimToken(aToken), trimToken(refreshToken), trimToken(accessToken),
                                                    ticketFields, CREATE_CHANGE_REQUEST_DETAILS, version, jdbcTemplate, request);
                                        }*/
                                    } else {
                                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                    }
                                }
                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity createChangeRequestTask(String businessFunCode, String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> ticketNumberMap = new LinkedHashMap<>();
        String[] ticketNumberValidation = new String[10];

        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(ticketFields, getCreateChangeRequestReqFields(businessFunCode));
                    if (isValidRequiredField.getIsvalid()) {
                        if (!ticketFields.containsKey(TICKET_NUMBER) || (ticketFields.containsKey(TICKET_NUMBER) &&
                                StringUtils.isBlank((String) ticketFields.get(TICKET_NUMBER)))) {
                            ticketNumberMap = SFInterfaceBase.generateTicketNumber(refLabJDBCTemplate
                                    , Constants.TicketNumberGeneration, version, ticketFields, request, user);
                            if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                            } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                                ticketFields.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                            }
                        }
                        if (ticketFields.containsKey(TICKET_NUMBER) && StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER))) {
                            Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain.containsKey(ERRORCODE)) {
                                String spName = endPointDomain.get(QUERY).toString();

                                ticketNumberValidation = ticketFields.get(TICKET_NUMBER).toString().split(HYPHEN);
                                if (endpointName.equalsIgnoreCase(CREATE_CHANGE_REQUEST_TASKS)) {
                                    if (CRT.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {
                                        if (ticketFields.containsKey(ATTACHMENTS)) {
                                            ticketFields.put(ATTACHMENTS, getCRTAttachmentDetails(ticketFields.get(ATTACHMENTS)));
                                        }

                                        if (ticketFields.containsKey(CRT_STAKE_HOLDERS) && ticketFields.get(CRT_STAKE_HOLDERS) != null) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(CRT_STAKE_HOLDERS, ticketFields.get(CRT_STAKE_HOLDERS));
                                            ticketFields.put(CRT_STAKE_HOLDERS, jsonData);
                                        }
                                        if (ticketFields.containsKey(CRT_ASSIGNMENTS) && ticketFields.get(CRT_ASSIGNMENTS) != null) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(CRT_ASSIGNMENTS, ticketFields.get(CRT_ASSIGNMENTS));
                                            ticketFields.put(CRT_ASSIGNMENTS, jsonData);
                                        }
                                        if (ticketFields.containsKey(DESIGN_BOARD) && ticketFields.get(DESIGN_BOARD) != null) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(DESIGN_BOARD, ticketFields.get(DESIGN_BOARD));
                                            ticketFields.put(DESIGN_BOARD, jsonData);
                                        }
                                        if (ticketFields.containsKey(CRT_WORKNOTES)) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(WORKNOTES, ticketFields.get(CRT_WORKNOTES));
                                            ticketFields.put(WORKNOTES, jsonData);
                                        }
                                        if (ticketFields.containsKey(ACTION_ITEMS) && ticketFields.get(ACTION_ITEMS) != null) {
                                            Map<String, Object> jsonData = new HashMap<>();
                                            jsonData.put(ACTION_ITEMS, ticketFields.get(ACTION_ITEMS));
                                            ticketFields.put(ACTION_ITEMS, jsonData);
                                        }
                                        SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, ticketFields.toString(), Json_AFTER_Manupulation);
                                        resultMap = SFInterfaceBase.createChangeRequestTask(refLabJDBCTemplate, spName, ticketFields, endpointName, user);
                                    } else {
                                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                    }
                                }
                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity updateChangeRequest(String businessFunCode, String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), riskMap = new LinkedHashMap<>(), impactMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(parameters, getUpdateChangeRequestReqFields());
                    if (isValidRequiredField.getIsvalid()) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();

                            if (parameters.containsKey(ATTACHMENTS)) {
                                parameters.put(ATTACHMENTS, getCRAttachmentDetails(parameters.get(ATTACHMENTS)));
                            }
                            if (parameters.containsKey(COM_REFRENCES)) {
                                parameters.put(COM_REFRENCES, getComRefrences(parameters.get(COM_REFRENCES)));
                            }
                            if (parameters.containsKey(CR_ASSESTS)) {
                                parameters.put(CR_ASSESTS, getCRAssets(parameters.get(CR_ASSESTS)));
                            }
                            if (parameters.containsKey(COM_RESPONSES)) {
                                parameters.put(COM_RESPONSES, getComResponseDetails(parameters.get(COM_RESPONSES)));
                            }
                            if (parameters.containsKey(CR_GROUPS)) {
                                parameters.put(CR_GROUPS, getCRGroups(parameters.get(CR_GROUPS)));
                            }
                            if (parameters.containsKey(CR_BUILDINGS)) {
                                parameters.put(CR_BUILDINGS, getCRBuildings(parameters.get(CR_BUILDINGS)));
                            }
                            if (parameters.containsKey(CR_LOCATIONS)) {
                                parameters.put(CR_LOCATIONS, getCRLocations(parameters.get(CR_LOCATIONS)));
                            }
                            if (parameters.containsKey(CR_WORKNOTES)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(WORKNOTES, parameters.get(CR_WORKNOTES));
                                parameters.put(CR_WORKNOTES, jsonData);
                            }
                            if (parameters.containsKey(COM_PERMISSIONS)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(COM_PERMISSIONS, parameters.get(COM_PERMISSIONS));
                                parameters.put(COM_PERMISSIONS, jsonData);
                            }
                            if (parameters.containsKey(CR_RISKS)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(RISKS, parameters.get(CR_RISKS));
                                parameters.put(CR_RISKS, jsonData);
                            }
                            if (parameters.containsKey(CR_IMPACTED)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(IMPACTED, parameters.get(CR_IMPACTED));
                                parameters.put(CR_IMPACTED, jsonData);
                            }

                            List<Map<String, Object>> statusResult = new ArrayList<>();

                            if (endpointName.equalsIgnoreCase(UPDATE_CHANGE_REQUEST)) {
                               /* if (parameters.containsKey(STATUS_SK)) {
                                    statusResult = fetchWhooshRecords(INDEXERNAME_FETCH_STATUS
                                            , STATUS_SK + COLON + parameters.get(STATUS_SK) + AND + ENTITYCODE + COLON + ESCAPE_CHARACTER + CR + ESCAPE_CHARACTER,
                                            null, null, null, ASC, TRUE, null, 1, null);

                                }*/
                                /*   String ticketType[] = parameters.get(TICKET_NUMBER).toString().split(HYPHEN);*/

                                // if the status is published and in Draft type then a new CR will be created
                               /* if (statusResult.size() > NUM_ZERO && statusResult.get(NUM_ZERO).containsKey(STATUS) && statusResult.get(NUM_ZERO).get(STATUS).toString().equalsIgnoreCase(PUBLISHED)
                                        && ticketType[NUM_ZERO].equalsIgnoreCase(DFT)) {*/
                                if (parameters.containsKey(PUBLISH) && parameters.containsKey(TYPE) &&
                                        parameters.get(PUBLISH).toString().equalsIgnoreCase(ONE)
                                    //        && parameters.get(TYPE).toString().equalsIgnoreCase("R")
                                ) {
                                    String type = "";
                                    Map<String, Object> endPointDomainfetchCR = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CHANGE_REQUEST1, version, GET_METHOD, user);
                                    Map<String, String[]> params = new HashMap<>();
                                    params.put(TICKET, new String[]{parameters.get(TICKET_SK).toString()});
                                    String refdbquery = getDBQuery(endPointDomainfetchCR.get(QUERY).toString(), params);
                                    List<Map<String, Object>> crs = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                                            , refdbquery
                                            , new String[]{ACCOUNT_SK, ACCOUNT, MASTER_ACCOUNT_SK, MASTER_ACCOUNT, TYPE, TICKET}
                                            , null
                                            , null
                                            , null
                                            , null
                                            , 0
                                            , 0
                                            , Boolean.FALSE);


                                       /* fetchWhooshRecords(INDEXERNAME_FETCH_CHANGE_REQUEST, TICKET_SK + COLON + parameters.get(TICKET_SK), null
                                                , new String[]{ACCOUNT_SK, ACCOUNT, MASTER_ACCOUNT_SK, MASTER_ACCOUNT, TYPE},
                                                null, null, null, null, 0);*/
                                    if (!crs.isEmpty()) {
                                        parameters.put(ACCOUNT_SK, crs.get(0).get(ACCOUNT_SK));
                                        parameters.put(MASTER_ACCOUNT_SK, crs.get(0).get(MASTER_ACCOUNT_SK));
                                        parameters.put(MASTER_ACCOUNT_CODE, crs.get(0).get(MASTER_ACCOUNT));
                                        type = (String) crs.get(0).get(TYPE);
                                    }
                                    if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase(DFT)) {
                                        Map<String, Object> ticketNumberMap = SFInterfaceBase.generateTicketNumber(refLabJDBCTemplate
                                                , Constants.TicketNumberGeneration, version, parameters, request, user);
                                        if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                                            return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                        } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                                            parameters.put(TICKET_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                                        } else {
                                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                                            return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                        }
                                    } else {
                                        parameters.put(TICKET_NUMBER, crs.get(0).get(TICKET));
                                    }
                                    resultMap = SFInterfaceBase.updateChangeRequest(refLabJDBCTemplate, spName, parameters, endpointName, user);
                                    //  Map<String, Object> endPointDomain1 = SFInterfaceBase.fetchEndpointDetails(CREATE_CHANGE_REQUEST, version, methodType);
                                     /*     if (!endPointDomain1.containsKey(ERRORCODE)) {
                                          String spName1 = endPointDomain1.get(QUERY).toString();

                                            String ticketNumberValidation[] = parameters.get(TICKET_NUMBER).toString().split(HYPHEN);

                                            if (CR.equalsIgnoreCase(ticketNumberValidation[NUM_ZERO])) {
                                                parameters.put(CREATED_BY, parameters.get(MODIFIED_BY));
                                                SFInterfaceLoggerBase.log(LOGGER, CREATE_CHANGE_REQUEST, null, null, parameters.toString(), Json_AFTER_Manupulation);
                                                resultMap = SFInterfaceBase.createChangeRequest(refLabJDBCTemplate, spName1, parameters, CREATE_CHANGE_REQUEST);
                                            } else {
                                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDBUSIFUNCTION", null, endpointName);
                                                return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                                            }

                                            if (resultMap.size() == NUM_ZERO) {
                                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                            }
                                        } else {
                                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                                        }*/

                                } else {
                                    SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, parameters.toString(), Json_AFTER_Manupulation);
                                    resultMap = SFInterfaceBase.updateChangeRequest(refLabJDBCTemplate, spName, parameters, endpointName, user);
                                }
                            }
                            if (resultMap.size() == NUM_ZERO) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity updateChangeRequestTask(String businessFunCode, String methodType, String
            aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(parameters, getUpdateChangeRequestReqFields());
                    if (isValidRequiredField.getIsvalid()) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();

                            if (parameters.containsKey(ATTACHMENTS)) {
                                parameters.put(ATTACHMENTS, getCRTAttachmentDetails(parameters.get(ATTACHMENTS)));
                            }

                            if (parameters.containsKey(CRT_STAKE_HOLDERS) && parameters.get(CRT_STAKE_HOLDERS) != null) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(CRT_STAKE_HOLDERS, parameters.get(CRT_STAKE_HOLDERS));
                                parameters.put(CRT_STAKE_HOLDERS, jsonData);
                            }
                            if (parameters.containsKey(CRT_ASSIGNMENTS) && parameters.get(CRT_ASSIGNMENTS) != null) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(CRT_ASSIGNMENTS, parameters.get(CRT_ASSIGNMENTS));
                                parameters.put(CRT_ASSIGNMENTS, jsonData);
                            }
                            if (parameters.containsKey(CRT_WORKNOTES)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(WORKNOTES, parameters.get(CRT_WORKNOTES));
                                parameters.put(WORKNOTES, jsonData);
                            }
                            if (parameters.containsKey(ACTION_ITEMS) && parameters.get(ACTION_ITEMS) != null) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(ACTION_ITEMS, parameters.get(ACTION_ITEMS));
                                parameters.put(ACTION_ITEMS, jsonData);
                            }
                            if (parameters.containsKey(DESIGN_BOARD) && parameters.get(DESIGN_BOARD) != null) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(DESIGN_BOARD, parameters.get(DESIGN_BOARD));
                                parameters.put(DESIGN_BOARD, jsonData);
                            }
                            if (endpointName.equalsIgnoreCase(UPDATE_CHANGE_REQUEST_TASKS)) {
                                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, parameters.toString(), Json_AFTER_Manupulation);
                                resultMap = SFInterfaceBase.updateChangeRequestTask(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            }
                            if (resultMap.size() == NUM_ZERO) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }


    /*public static ResponseEntity createChangeRequestDetails(String businessFunCode, String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {

                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(parameters, getCreateChangeRequestDetailsReqFields());
                    if (isValidRequiredField.getIsvalid()) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType,user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();

                            if (parameters.containsKey(ATTACHMENTS)) {
                                parameters.put(ATTACHMENTS, getCRAttachmentDetails(parameters.get(ATTACHMENTS)));
                            }
                            if (parameters.containsKey(COM_RESPONSES)) {
                                parameters.put(COM_RESPONSES, getComResponseDetails(parameters.get(COM_RESPONSES)));
                            }
                            if (parameters.containsKey(CR_ASSESTS)) {
                                parameters.put(CR_ASSESTS, getCRAssets(parameters.get(CR_ASSESTS)));
                            }
                            if (parameters.containsKey(CR_GROUPS)) {
                                parameters.put(CR_GROUPS, getCRGroups(parameters.get(CR_GROUPS)));
                            }
                            if (parameters.containsKey(CR_BUILDINGS)) {
                                parameters.put(CR_BUILDINGS, getCRBuildings(parameters.get(CR_BUILDINGS)));
                            }
                            if (parameters.containsKey(CR_LOCATIONS)) {
                                parameters.put(CR_LOCATIONS, getCRLocations(parameters.get(CR_LOCATIONS)));
                            }
                            if (parameters.containsKey(COM_REFRENCES)) {
                                parameters.put(COM_REFRENCES, getComRefrences(parameters.get(COM_REFRENCES)));
                            }
                            if (endpointName.equalsIgnoreCase(CREATE_CHANGE_REQUEST_DETAILS)) {
                                resultMap = SFInterfaceBase.createChangeRequestDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                            }
                            if (resultMap.size() == NUM_ZERO) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity updateChangeRequestDetails(String businessFunCode, String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {

                    ValidateRequiredField isValidRequiredField = isRequiredFieldsPresent(parameters, getUpdateChangeRequestDetailsReqFields());
                    if (isValidRequiredField.getIsvalid()) {
                        Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType,user);
                        if (!endPointDomain.containsKey(ERRORCODE)) {
                            String spName = endPointDomain.get(QUERY).toString();

                            if (parameters.containsKey(ATTACHMENTS)) {
                                parameters.put(ATTACHMENTS, getAttachmentDetails(parameters.get(ATTACHMENTS)));
                            }
                            if (parameters.containsKey(COM_REFRENCES)) {
                                parameters.put(COM_REFRENCES, getComRefrences(parameters.get(COM_REFRENCES)));
                            }
                            if (parameters.containsKey(CR_ASSESTS)) {
                                parameters.put(CR_ASSESTS, getCRAssets(parameters.get(CR_ASSESTS)));
                            }
                            if (parameters.containsKey(CR_GROUPS)) {
                                parameters.put(CR_GROUPS, getCRGroups(parameters.get(CR_GROUPS)));
                            }
                            if (parameters.containsKey(CR_BUILDINGS)) {
                                parameters.put(CR_BUILDINGS, getCRBuildings(parameters.get(CR_BUILDINGS)));
                            }
                            if (parameters.containsKey(CR_LOCATIONS)) {
                                parameters.put(CR_LOCATIONS, getCRLocations(parameters.get(CR_LOCATIONS)));
                            }
                            if (endpointName.equalsIgnoreCase(UPDATE_CHANGE_REQUEST_DETAILS)) {
                                resultMap = SFInterfaceBase.updateChangeRequestDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                            }

                            if (resultMap.size() == NUM_ZERO) {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                        }
                    } else {
                        resultMap.putAll((Map<? extends String, ?>) SFInterfaceBase.mandatoryFieldErrorMessage(isValidRequiredField.getMandatoryfields(), endpointName));
                        return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }*/

    public static ResponseEntity updateCRMultipleInputRequest(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {

                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_ACTION_ITEMS)) {
                            arrayList = SFInterfaceBase.updateActionItemDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_IMPACTED)) {
                            arrayList = SFInterfaceBase.updateImpactDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_RISKS)) {
                            arrayList = SFInterfaceBase.updateRisksDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(convertTimestamptoStringFromList(arrayList), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateCRApprovalsRequest(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";
        Map<String, Object> ticketNumberMap = new HashMap<>();

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (parameters.containsKey(CR_APPROVAL_STATUS) && parameters.get(CR_APPROVAL_STATUS) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(APPROVAL_STATUS, parameters.get(CR_APPROVAL_STATUS));
                            parameters.put(CR_APPROVAL_STATUS, jsonData);
                        }
                        if (parameters.containsKey(DESIGN_BOARD) && parameters.get(DESIGN_BOARD) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(DESIGN_BOARD, parameters.get(DESIGN_BOARD));
                            parameters.put(DESIGN_BOARD, jsonData);
                        }

                        if (endpointName.equalsIgnoreCase(CREATE_CR_APPROVALS)) {
                            if (!parameters.containsKey(APPROVAL_NUMBER) || (parameters.containsKey(APPROVAL_NUMBER) &&
                                    StringUtils.isBlank((String) parameters.get(APPROVAL_NUMBER)))) {
                                //temp fix for handling account_sk and businessfunction_sk internally
                                if (!parameters.containsKey(ACCOUNT_SK)) {
                                    parameters.put(ACCOUNT_SK, parameters.get(MASTER_ACCOUNT_SK));
                                }
                                if (!parameters.containsKey(BUSINESS_FUNCTION_SK)) {
                                    Map<String, String[]> params = new HashMap<>();
                                    params.put(ACCOUNT_SK, new String[]{(String) parameters.get(ACCOUNT_SK), (String) parameters.get(MASTER_ACCOUNT_SK)});
                                    params.put(BUSINESS_FUNCTION_CODE, new String[]{BUSINESS_FUNCTION_CODE_CRAPPR});
                                    String query = SFInterfaceBase.generateWhooshQueryDynamic(params);
                                    List<Map<String, Object>> records = fetchWhooshRecords(INDEXERNAME_FETCH_BUSINESSFUNCTION, query, null, new String[]{BUSINESS_FUNCTION_SK}, null, null, null, null, user);
                                    if (records.isEmpty()) {
                                        params.put(ACCOUNT_SK, new String[]{(String) NULL_VALUE});
                                        query = SFInterfaceBase.generateWhooshQueryDynamic(params);
                                    }
                                    records = fetchWhooshRecords(INDEXERNAME_FETCH_BUSINESSFUNCTION, query, null, new String[]{BUSINESS_FUNCTION_SK}, null, null, null, null, user);
                                    parameters.put(BUSINESS_FUNCTION_SK, records.get(0).get(BUSINESS_FUNCTION_SK));
                                }
                                ticketNumberMap = generateTicketNumber(refLabJDBCTemplate
                                        , Constants.TicketNumberGeneration, version, parameters, request, user);
                                if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                                    return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                } else if (ticketNumberMap.containsKey(TICKET_NUMBER)) {
                                    parameters.put(APPROVAL_NUMBER, ticketNumberMap.get(TICKET_NUMBER));
                                } else {
                                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                                    return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                                }
                                SFInterfaceLoggerBase.log(LOGGER, endpointName, null, null, parameters.toString(), Json_AFTER_Manupulation);
                            }
                            arrayList = SFInterfaceBase.createCRApprovalsDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_APPROVALS)) {
                            arrayList = SFInterfaceBase.updateCRApprovalsDetails(refLabJDBCTemplate, spName, parameters, endpointName, user);
                            resultMap = arrayList.get(NUM_ZERO);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(convertTimestamptoStringFromList(arrayList), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateCRDetails(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_CHANGE_TYPE)) {
                            resultMap = SFInterfaceBase.updateChangeType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_PLATFORMS)) {
                            resultMap = SFInterfaceBase.updatePlatforms(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_CATEGORY)) {
                            resultMap = SFInterfaceBase.updateCRCategory(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_LOCATIONS)) {
                            resultMap = SFInterfaceBase.updateCRLocations(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_SYSTEMS)) {
                            resultMap = SFInterfaceBase.updateCRSystems(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_DISCUSSION_BOARD)) {
                            if (parameters.containsKey(CR_DB_RECIPIENTS)) {
                                Map<String, Object> jsonData = new HashMap<>();
                                jsonData.put(CR_DB_RECIPIENTS, parameters.get(CR_DB_RECIPIENTS));
                                parameters.put(CR_DB_RECIPIENTS, jsonData);
                            }
                            resultMap = SFInterfaceBase.updateCRDiscussionBoard(refLabJDBCTemplate, spName, parameters, endpointName, user);

                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateMasterDetails(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_LOV)) {
                            resultMap = SFInterfaceBase.updateLOV(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RISKLEVEL)) {
                            resultMap = SFInterfaceBase.updateRiskLevel(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_QUESTIONS)) {
                            resultMap = SFInterfaceBase.updateQuestions(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_TASKTYPE)) {
                            resultMap = SFInterfaceBase.updateTaskType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_LOV_NAME)) {
                            resultMap = SFInterfaceBase.updateLOVName(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateMasterDetails1(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_BUSINESS_ORGANIZATION)) {
                            resultMap = SFInterfaceBase.updateBusinessOrganization(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_PRIORITY)) {
                            resultMap = SFInterfaceBase.updatePriority(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACCOUNT_ROLE)) {
                            resultMap = SFInterfaceBase.updateAccountRole(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_APPROVAL_TYPE)) {
                            resultMap = SFInterfaceBase.updateApprovalType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_VENDOR)) {
                            resultMap = SFInterfaceBase.updateVendor(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }else if (endpointName.equalsIgnoreCase(UpdateAccGroupUser)) {
                            resultMap = SFInterfaceBase.updateAccGroupUser(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createKBArticle(String methodType, String aToken, String refreshToken, String
            accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (parameters.containsKey(TYPE_AHEAD_PHRASE) && parameters.get(TYPE_AHEAD_PHRASE) != null) {
                            parameters.put(TYPE_AHEAD_PHRASE, convertListToCommaSeparatedValue(parameters.get(TYPE_AHEAD_PHRASE)));
                        }
                        if (parameters.containsKey(ARTILCE_KEYWORDS) && parameters.get(ARTILCE_KEYWORDS) != null) {
                            parameters.put(ARTILCE_KEYWORDS, convertListToCommaSeparatedValue(parameters.get(ARTILCE_KEYWORDS)));
                        }
                        if (parameters.containsKey(TYPE_AHEAD_CATEGORY) && parameters.get(TYPE_AHEAD_CATEGORY) != null) {
                            parameters.put(TYPE_AHEAD_CATEGORY, convertListToCommaSeparatedValue(parameters.get(TYPE_AHEAD_CATEGORY)));
                        }
                        if (parameters.containsKey(PHRASE_HISTORY) && parameters.get(PHRASE_HISTORY) != null) {
                            parameters.put(PHRASE_HISTORY, convertListToCommaSeparatedValue(parameters.get(PHRASE_HISTORY)));
                        }

                        if (parameters.containsKey(RDY_MAP) && parameters.get(RDY_MAP) != null) {
                            Map<String, Object> rdyMap = new HashMap<>();
                            rdyMap.put(RDY_MAP, parameters.get(RDY_MAP));
                            parameters.put(RDY_MAP, rdyMap);
                        }

                        if (endpointName.equalsIgnoreCase(CREATE_KBARTICLE)) {
                            resultMap = SFInterfaceBase.createKBArticle(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.exceptionHandler(e, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity updateKBArticle(String methodType, String aToken, String refreshToken, String
            accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (parameters.containsKey(ARTILCE_KEYWORDS) && parameters.get(ARTILCE_KEYWORDS) != null) {
                            parameters.put(ARTILCE_KEYWORDS, convertListToCommaSeparatedValue(parameters.get(ARTILCE_KEYWORDS)));
                        }
                        if (parameters.containsKey(TYPE_AHEAD_CATEGORY) && parameters.get(TYPE_AHEAD_CATEGORY) != null) {
                            parameters.put(TYPE_AHEAD_CATEGORY, convertListToCommaSeparatedValue(parameters.get(TYPE_AHEAD_CATEGORY)));
                        }
                        if (parameters.containsKey(PHRASE_HISTORY) && parameters.get(PHRASE_HISTORY) != null) {
                            parameters.put(PHRASE_HISTORY, convertListToCommaSeparatedValue(parameters.get(PHRASE_HISTORY)));
                        }
                        if (parameters.containsKey(TYPE_AHEAD_PHRASE) && parameters.get(TYPE_AHEAD_PHRASE) != null) {
                            Map<String, Object> typeAheadPhrase = new HashMap<>();
                            typeAheadPhrase.put(TYPE_AHEAD_PHRASE, parameters.get(TYPE_AHEAD_PHRASE));
                            parameters.put(TYPE_AHEAD_PHRASE, typeAheadPhrase);
                        }
                        if (parameters.containsKey(RDY_MAP) && parameters.get(RDY_MAP) != null) {
                            Map<String, Object> rdyMap = new HashMap<>();
                            rdyMap.put(RDY_MAP, parameters.get(RDY_MAP));
                            parameters.put(RDY_MAP, rdyMap);
                        }

                        if (endpointName.equalsIgnoreCase(UPDATE_KBARTICLE)) {
                            resultMap = SFInterfaceBase.updateKBArticle(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.exceptionHandler(e, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    static String convertListToCommaSeparatedValue(Object parameter) {
        ArrayList<String> param = (ArrayList) parameter;

        String stringValue = "";
        for (String str : param) {
            if (StringUtils.isNotBlank(stringValue))
                stringValue += COMMA + str;
            else
                stringValue = str;
        }
        return stringValue;
    }

    public static ResponseEntity<Object> returnResponseWithPagination(List<Map<String, Object>> result) {
        List<Map<String, Object>> list = new ArrayList<>();
        HttpHeaders responseHeader = new HttpHeaders();

        if (result.size() > 0 && result.get(0).get(PAGINATION) != null) {
            Map<String, Object> map = (Map<String, Object>) result.get(0).get(PAGINATION);
            responseHeader.set(TOTAL_COUNT, map.get(TOTAL_COUNT).toString());
            responseHeader.set(RECORD_COUNT, map.get(RECORD_COUNT).toString());
            responseHeader.set(PAGE_NUM, map.get(PAGE_NUM).toString());
            responseHeader.set(TOTAL_PAGES, map.get(TOTAL_PAGES).toString());
        }
        if (result.size() > 0 && result.get(0).get(RESULT) != null) {
            return new ResponseEntity<>(result.get(0).get(RESULT), responseHeader, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, responseHeader, HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateCRSchedules(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (parameters.containsKey(CR_DISCUSSION_POINTS) && parameters.get(CR_DISCUSSION_POINTS) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(CR_DISCUSSION_POINTS, parameters.get(CR_DISCUSSION_POINTS));
                            parameters.put(CR_DISCUSSION_POINTS, jsonData);
                        }
                        if (parameters.containsKey(DESIGN_BOARD) && parameters.get(DESIGN_BOARD) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(DESIGN_BOARD, parameters.get(DESIGN_BOARD));
                            parameters.put(DESIGN_BOARD, jsonData);
                        }
                        if (parameters.containsKey(ACTION_ITEMS) && parameters.get(ACTION_ITEMS) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(ACTION_ITEMS, parameters.get(ACTION_ITEMS));
                            parameters.put(ACTION_ITEMS, jsonData);
                        }
                        if (parameters.containsKey(CR_MEETING_PARTICIPANTS) && parameters.get(CR_MEETING_PARTICIPANTS) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(CR_MEETING_PARTICIPANTS, parameters.get(CR_MEETING_PARTICIPANTS));
                            parameters.put(CR_MEETING_PARTICIPANTS, jsonData);
                        }
                        if (endpointName.equalsIgnoreCase(CREATE_CR_SCHEDULES)) {
                            resultMap = SFInterfaceBase.createCRSchedules(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_SCHEDULES)) {
                            resultMap = SFInterfaceBase.updateCRSchedules(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateCRClosure(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();
                        if (parameters.containsKey(CR_MEETING_PARTICIPANTS) && parameters.get(CR_MEETING_PARTICIPANTS) != null) {
                            Map<String, Object> jsonData = new HashMap<>();
                            jsonData.put(CR_MEETING_PARTICIPANTS, parameters.get(CR_MEETING_PARTICIPANTS));
                            parameters.put(CR_MEETING_PARTICIPANTS, jsonData);
                        }
                        if (parameters.containsKey(COM_RESPONSES)) {
                            parameters.put(COM_RESPONSES, getComResponseDetails(parameters.get(COM_RESPONSES)));
                        }
                        if (endpointName.equalsIgnoreCase(CREATE_CR_CLOSURE)) {
                            resultMap = SFInterfaceBase.createCRClosure(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_CLOSURE)) {
                            resultMap = SFInterfaceBase.updateCRClosure(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity<Map<String, Object>> cloneCR(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws IOException {
        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), ticketNumberMap = new LinkedHashMap<>(), params = new HashMap<>();
        Map<String, Object> endPointDomain = new HashMap<>();
        String[] ticketNumberValidation = new String[10];
        List<Map<String, Object>> result = new ArrayList<>();

        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, ticketFields.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    if (ticketFields.containsKey(TICKET_NUMBER) || (ticketFields.containsKey(TICKET_NUMBER) &&
                            StringUtils.isNotBlank((String) ticketFields.get(TICKET_NUMBER)))) {
                        // To get Account, MasterAccountCode,MasterAccount_sk for ticket number generation
                        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CHANGE_REQUEST1, version, GET_METHOD, user);
                        Map<String, String[]> parameter = new HashMap<>();
                        parameter.put(TICKET, new String[]{ticketFields.get(TICKET_NUMBER).toString()});
                        String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), parameter);
                        result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()), refdbquery
                                , new String[]{ACCOUNT_SK, MASTER_ACCOUNT_SK, MASTER_ACCOUNT, BUSINESS_FUNCTION_SK});
                        params.putAll(result.get(NUM_ZERO));
                        params.put(MASTER_ACCOUNT_CODE, result.get(NUM_ZERO).get(MASTER_ACCOUNT));
                        params.remove(MASTER_ACCOUNT);

                        ticketNumberMap = SFInterfaceBase.generateTicketNumber(refLabJDBCTemplate
                                , Constants.TicketNumberGeneration, version, params, request, user);
                        if (ticketNumberMap.containsKey(ERROR) && ticketNumberMap.get(ERROR) == NULL) {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDACCOUNTANDMASTERACCOUNTFORTICKETNUMBER", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                        } else if (ticketNumberMap.containsKey(TICKET_NUMBER) && StringUtils.isNotBlank((String) ticketNumberMap.get(TICKET_NUMBER))) {
                            //Replacing the ticket number to DFT
                            String ticketNumberMapSplit[] = ticketNumberMap.get(TICKET_NUMBER).toString().split(HYPHEN);
                            ticketFields.put(CLONED_CR, ticketNumberMap.get(TICKET_NUMBER).toString().replace(ticketNumberMapSplit[NUM_ZERO], DFT));

                            Map<String, Object> endPointDomain1 = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                            if (!endPointDomain1.containsKey(ERRORCODE)) {
                                String spName = endPointDomain1.get(QUERY).toString();

                                resultMap = SFInterfaceBase.createCloneCR(refLabJDBCTemplate, spName, ticketFields, endpointName);


                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                }
                            } else {
                                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                            }
                        } else {
                            resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.TICKETNUMBERNOTRETURNED", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.FAILED_DEPENDENCY);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.MISSINGREQUIREDFIELDSFORCLONECR ", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_BUSINESS_FUNCTION)) {
                            resultMap = SFInterfaceBase.updateBusinessFunction(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SOURCE)) {
                            resultMap = SFInterfaceBase.updateSource(refLabJDBCTemplate, spName, parameters, endpointName,user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_BUSINESS_TIME_DETAILS)) {
                            resultMap = SFInterfaceBase.updateBusinessTimeDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GROUP_TYPE)) {
                            resultMap = SFInterfaceBase.updateGroupType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_QUEUE)) {
                            resultMap = SFInterfaceBase.updateQueue(refLabJDBCTemplate, spName, parameters, endpointName,user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RCL_MAPPING)) {
                            resultMap = SFInterfaceBase.updateRCLMapping(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ROOT_CAUSE_LEVEL)) {
                            resultMap = SFInterfaceBase.updateRootCauseLevel(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GROUP_LEVEL)) {
                            resultMap = SFInterfaceBase.updateGroupLevel(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SEC_ACCOUNT_ROLE_USER)) {
                            resultMap = SFInterfaceBase.updateAccountRoleUser(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_STATUS_ENTITY)) {
                            resultMap = SFInterfaceBase.updateStatusEntity(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_STATUS_ENTITY_TYPE)) {
                            resultMap = SFInterfaceBase.updateStatusEntityType(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs1(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_TASK)) {
                            resultMap = SFInterfaceBase.updateTask(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_TASK_GROUP)) {
                            resultMap = SFInterfaceBase.updateTaskGroup(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_AUTO_CLOSE)) {
                            resultMap = SFInterfaceBase.updateAutoClose(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_LDAP_DETAILS)) {
                            resultMap = SFInterfaceBase.updateLdapDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ALERT_ACCOUNT_REL)) {
                            resultMap = SFInterfaceBase.updateAlertAccountRel(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CONTACT_TYPE)) {
                            resultMap = SFInterfaceBase.updateContactType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_OBJECTS)) {
                            resultMap = SFInterfaceBase.updateObjects(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_TYPE_AHEAD_CATEGORIES)) {
                            resultMap = SFInterfaceBase.updateKBTypeAheadCategories(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GEO_CITY)) {
                            resultMap = SFInterfaceBase.updateGeoCity(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GEO_COUNTRY)) {
                            resultMap = SFInterfaceBase.updateGeoCountry(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_GEO_STATE)) {
                            resultMap = SFInterfaceBase.updateGeoState(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIsWithMultipleInputs(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_BLD_DEPT)) {
                            arrayList = SFInterfaceBase.updateBuildingDepartment(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_BLD_SUITE)) {
                            arrayList = SFInterfaceBase.updateBuildingSuite(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DEPT_FLR)) {
                            arrayList = SFInterfaceBase.updateDepartmentFloor(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_BLD_FLR)) {
                            arrayList = SFInterfaceBase.updateBuildingFloor(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DEPT_SUITE)) {
                            arrayList = SFInterfaceBase.updateDepartmentSuite(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_BLD_CATEGORY)) {
                            arrayList = SFInterfaceBase.updateBuildingCategory(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ENTITY_LINK)) {
                            arrayList = SFInterfaceBase.updateEntityLink(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_LINKS)) {
                            arrayList = SFInterfaceBase.updateLinks(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (arrayList.get(NUM_ZERO).size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(convertTimestamptoStringFromList(arrayList), HttpStatus.OK);

    }

    public static ResponseEntity createOrUpdateAdminAPIs2(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_ASSET_CATEGORY)) {
                            resultMap = SFInterfaceBase.updateAssetCategory(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ASSET_ATTACHMENT)) {
                            resultMap = SFInterfaceBase.updateAssetAttachment(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ASSET_PROCUREMENT)) {
                            resultMap = SFInterfaceBase.updateAssetProcurement(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ASSET_PROCUREMENT_DETAILS)) {
                            resultMap = SFInterfaceBase.updateAssetProcurementDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_CAB_MEMBER)) {
                            resultMap = SFInterfaceBase.updateCRCabMember(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_REQUEST_TYPE)) {
                            resultMap = SFInterfaceBase.updateCRRequestType(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_MILESTONE)) {
                            resultMap = SFInterfaceBase.updateCRMilestone(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CR_PROJECT)) {
                            resultMap = SFInterfaceBase.updateCRProject(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SLM_AGREEMENT_TYPE)) {
                            resultMap = SFInterfaceBase.updateSlmAgreementType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_OPSBOT_ALERTS)) {
                            resultMap = SFInterfaceBase.updateOPSBOTAlerts(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SLM_DEFINTION)) {
                            resultMap = SFInterfaceBase.updateSlmDefintion(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createORUpdateEntities(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>(), resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String loginID = "", query = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    query = SFInterfaceBase.generateQuery(parameters, endpointName, request.getParameter(ACCOUNT_SK), user);
                    resultMap = SFInterfaceBase.returnQueryresult(query, endpointName, user);
                    if (resultMap.size() == NUM_ZERO) {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                    } else if (resultMap.size() == 1
                            &&
                            resultMap
                                    .parallelStream()
                                    .anyMatch(stringObjectMap -> stringObjectMap.containsKey(ERROR))) {
                        errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(0).get(ERROR));
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }

                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static ResponseEntity createORUpdateEntitiess(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>(), resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String loginID = "", query = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                loginID = user.getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    resultMap = createORUpdateEntitiess(parameters, endpointName, refLabJDBCTemplate, request, user);
                    if (resultMap.size() == NUM_ZERO) {
                        errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                    } else if (resultMap.size() == 1
                            &&
                            resultMap
                                    .parallelStream()
                                    .anyMatch(stringObjectMap -> stringObjectMap.containsKey(ERROR))) {
                        errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(0).get(ERROR));
                        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }

    public static List<Map<String, Object>> createORUpdateEntitiess(
            Map<String, Object> parameters, String endpointName
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        String query = "";
        SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
        query = SFInterfaceBase.generateQueryEntity(parameters, endpointName, request.getParameter(ACCOUNT_SK));
        resultMap = SFInterfaceBase.returnQueryresult(query, endpointName, user);
        if (resultMap.size() > NUM_ZERO) {
            updateEntitiesIndexer(resultMap, endpointName, user);
        }
        return resultMap;
    }

    public static ResponseEntity fetchEntityLinks(String methodType, String aToken, String refreshToken, String
            accessToken
            , Map<String, Object> ticketFields, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>(), resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String loginID = "", query = "";
        EndpointValidationModel endpointValidationModel = SFInterfaceBase.validateEndpoint(endpointName, ticketFields, request.getParameterMap());

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, endpointValidationModel.getRequestparametermap().toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    endpointName = fetchValuefromTableMappingStringresult(endpointName, "fetchendpointname").getAsString();
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModel(endpointValidationModel.getRequestparametermap(), Boolean.TRUE
                                , endPointDomain.get(ENDPOINT_SK).toString(), user);
                        resultMap = SFInterfaceBase.fetchWhooshRecords(endPointDomain.get(INDEX_NAME).toString()
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

                        if (resultMap.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.size() == 1
                                &&
                                resultMap
                                        .parallelStream()
                                        .anyMatch(stringObjectMap -> stringObjectMap.containsKey(ERROR))) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(0).get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return returnResponsewithValidation(endpointName, resultMap, endpointValidationModel.getRequestparametermap(), endpointValidationModel.isEmptyParams_Whitelisted(), user);
    }
    /* public static ResponseEntity fetchEntityLinks(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, String[]> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>(), resultMap = new ArrayList<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        String loginID = "", query = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    endpointName = fetchValuefromTableMappingStringresult(endpointName, "fetchendpointname").getAsString();
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType,user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        CustomWhooshModel customWhooshModel = SFInterfaceBase.generateWhooshModelForSP(parameters);

                        query = getDBQuery(endPointDomain.get(QUERY).toString(), customWhooshModel.getParams());
                        SFInterfaceLoggerBase.log(LOGGER, Constants.FETCH, null);
                        arrayList = SFInterfaceBase.runjdbcQueryWithPagination(jdbcTemplate
                                , query
                                , customWhooshModel.getRequiredColumns()
                                , customWhooshModel.getSortfields()
                                , customWhooshModel.getSortorder()
                                , customWhooshModel.getDistinct()
                                , customWhooshModel.getDistinctfield()
                                , customWhooshModel.getPageno()
                                , customWhooshModel.getCount()
                                , Boolean.TRUE);
                        if (resultMap.size() == NUM_ZERO) {
                            errorMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.size() == 1
                                &&
                                resultMap
                                        .parallelStream()
                                        .anyMatch(stringObjectMap -> stringObjectMap.containsKey(ERROR))) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(0).get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    }
                } else {
                    errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                errorMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return returnResponsewithValidation(endpointName, arrayList, parameters, false);
    }*/

    public static ResponseEntity createDmartAPI(String methodType, String aToken, String refreshToken, String
            accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
        String loginID = "", groupList = "", spName = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(CREATE_DMART_ACCOUNT)) {
                            resultMap = SFInterfaceBase.createDmartAccount(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_FDN_PEOPLE_INFO)) {
                            resultMap = SFInterfaceBase.createFdnPeopleInfo(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_GROUP)) {
                            resultMap = SFInterfaceBase.createDmartGroup(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_USER)) {
                            userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup1((String) parameters.get(LOGIN_NAME), null, user);

                            if (!userAccountNGroup.getUserGroups().isEmpty()) {
                                for (Map<String, Object> userGroup : userAccountNGroup.getUserGroups()) {
                                    if (groupList.isEmpty()) {
                                        groupList += SEMI_COLON + userGroup.get(GROUP_CODE) + SEMI_COLON;
                                    } else {
                                        groupList += userGroup.get(GROUP_CODE) + SEMI_COLON;
                                    }
                                }
                            }
                            parameters.put(GROUP_LIST, groupList);
                            resultMap = SFInterfaceBase.createDMartUserDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(CREATE_DMART_GRP_OPTIONS)) {
                            resultMap = SFInterfaceBase.createDmartGrpOptionsDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity updateDmartAPI(String methodType, String aToken, String refreshToken, String
            accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
        String loginID = "", spName = "", groupList = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_DMART_ACCOUNT)) {
                            resultMap = SFInterfaceBase.updateDmartAccount(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_FDN_PEOPLE_INFO)) {
                            resultMap = SFInterfaceBase.updateFdnPeopleInfo(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_GROUP)) {
                            resultMap = SFInterfaceBase.updateDmartGroup(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_USER)) {
                            userAccountNGroup = SFInterfaceBase.fetchUserAccountNGroup1((String) parameters.get(LOGIN_NAME), null, user);

                            if (!userAccountNGroup.getUserGroups().isEmpty()) {
                                for (Map<String, Object> userGroup : userAccountNGroup.getUserGroups()) {
                                    if (groupList.isEmpty()) {
                                        groupList += SEMI_COLON + userGroup.get(GROUP_CODE) + SEMI_COLON;
                                    } else {
                                        groupList += userGroup.get(GROUP_CODE) + SEMI_COLON;
                                    }
                                }
                            }
                            parameters.put(GROUP_LIST, groupList);
                            resultMap = SFInterfaceBase.updateDMartUserDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_GRP_OPTIONS)) {
                            resultMap = SFInterfaceBase.updateDmartGrpOptionsDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs3(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_SLM_GOALTYPE)) {
                            resultMap = SFInterfaceBase.updateSlmGoalType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RDY_ATTACHMENTS)) {
                            resultMap = SFInterfaceBase.updateRdyAttachments(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RDY_MAPPING_SLA)) {
                            resultMap = SFInterfaceBase.updateRdyMappingSLA(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RDY_MAPPING)) {
                            resultMap = SFInterfaceBase.updateRdyMapping(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ENTITY)) {
                            resultMap = SFInterfaceBase.updateEntity(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_ARTICLE_TYPE)) {
                            resultMap = SFInterfaceBase.updateKBArticleType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_CONTENT_TYPE)) {
                            resultMap = SFInterfaceBase.updateKBContentType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_DOC_CONTENT_TYPE)) {
                            resultMap = SFInterfaceBase.updateKBDocContentType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_KB_DOCUMENT_TYPE)) {
                            resultMap = SFInterfaceBase.updateKBDocumentType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_OBJECT_PERMISSIONS)) {
                            resultMap = SFInterfaceBase.updateObjectPermissions(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(Create_Update_RDYKBPhraseDetails)) {
                            resultMap = SFInterfaceBase.UpdateRDYKBPhraseDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATETITLE)) {
                            resultMap = SFInterfaceBase.UpdateTitle(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATESkillSET)) {
                            resultMap = SFInterfaceBase.UpdateSkillSet(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs4(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_RDY_QUERIES)) {
                            resultMap = SFInterfaceBase.updateRDYQueriesDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RDY_ROOTCAUSE)) {
                            resultMap = SFInterfaceBase.updateRDYRootCauseDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity CreateDMartCMNCategorization(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(CREATE_CMN_CATEGORIZATION)) {
                            resultMap = SFInterfaceBase.CreateDMartCMNCategorization(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity UpdateDMartCMNCategorization(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_CMN_CATEGORIZATION)) {
                            resultMap = SFInterfaceBase.UpdateDMartCMNCategorization(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity CreateUpdateRDYKBPhrases(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(Create_Update_RDYKB_Phrases)) {
                            resultMap = SFInterfaceBase.CreateUpdateRDYKBPhrases(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity CreateUpdateFDNBackUpApprovers(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(Create_Update_FDNBackUp_Approvers)) {
                            resultMap = SFInterfaceBase.CreateUpdateFDNBackUpApprovers(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity CreateUpdateFDN_Controls(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(Create_Update_FDN_Controls)) {
                            resultMap = SFInterfaceBase.CreateUpdateFDN_Controls(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity CreateUpdateFDNSupportUsers(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(Create_Update_FDN_Support_Users)) {
                            resultMap = SFInterfaceBase.CreateUpdateFDNSupportUsers(refLabJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs5(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_IAM_APPLICATION)) {
                            resultMap = SFInterfaceBase.updateIAMApplication(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_WORKNOTES_TYPE)) {
                            resultMap = SFInterfaceBase.updateWorknoteType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_EMAIL_OPTIONS)) {
                            resultMap = SFInterfaceBase.updateEmailOptions(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_CASE_TYPE)) {
                            resultMap = SFInterfaceBase.updateCaseType(refLabJDBCTemplate, spName, parameters, endpointName,user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_BOOKMARKS)) {
                            resultMap = SFInterfaceBase.updateBookmarks(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_APPROVAL_CRITERIA)) {
                            resultMap = SFInterfaceBase.updateApprovalCriteria(refLabJDBCTemplate, spName, parameters, endpointName, user);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_IMPACT)) {
                            resultMap = SFInterfaceBase.updateImpact(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs6(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_SUPPORT1)) {
                            resultMap = SFInterfaceBase.updateSupport1(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SUPPORT_ATTACHMENTS)) {
                            resultMap = SFInterfaceBase.updateSupportAttachments(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_TASK_DETAILS)) {
                            resultMap = SFInterfaceBase.updateTaskDetails(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_USER_POSITION)) {
                            resultMap = SFInterfaceBase.updateUserPosition(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_RDY_WIZARD)) {
                            resultMap = SFInterfaceBase.updateRDYWizard(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ROUND_ROBIN_RULE)) {
                            resultMap = SFInterfaceBase.updateRoundRobinRule(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_AUTHENTICATION_TYPE)) {
                            resultMap = SFInterfaceBase.updateAuthenticationType(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateDmartChangeRequest(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate dmartJDBCTemplate, HttpServletRequest request, JdbcTemplate refLabJDBCTemplate, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
        String loginID = "", groupList = "", spName = "";
        List<Map<String, Object>> crResult = new ArrayList();
        String ticketSK = "", businessFunction_sk = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        spName = endPointDomain.get(QUERY).toString();
                        if (parameters.containsKey(CR_NUMBER) && StringUtils.isNotBlank((String) parameters.get(CR_NUMBER))) {
                            String ticketNumber = (String) parameters.get(CR_NUMBER);
                            endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CHANGE_REQUEST1, V1, GET_METHOD, user);
                            if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
                                Map<String, String[]> params = new HashMap();
                                params.put(TICKET, new String[]{ticketNumber});
                                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                                crResult = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, new String[]{TICKET_SK, BUSINESS_FUNCTION_SK});
                            }
                            if (crResult.size() > NUM_ZERO && crResult.get(0).containsKey(TICKET_SK)) {
                                ticketSK = (String) crResult.get(0).get(TICKET_SK);
                                businessFunction_sk = (String) crResult.get(0).get(BUSINESS_FUNCTION_SK);
                                parameters.put(WORK_LOG, crWorknotesConcatenation(ticketNumber, refLabJDBCTemplate, user));
                                Map<String, Object> crLocations = new HashMap<>();
                                crLocations = crLocationConcatenation(ticketNumber, refLabJDBCTemplate, user);
                                if (crLocations.containsKey(LOCATION_OF_CHANGE)) {
                                    parameters.put(LOCATION_OF_CHANGE, crLocations.get(LOCATION_OF_CHANGE));
                                }
                                if (crLocations.containsKey(EFFECTED_LOCATION)) {
                                    parameters.put(EFFECTED_LOCATION, crLocations.get(EFFECTED_LOCATION));
                                }
                                parameters.put(CLIENT_USER_IMPACT, crComResponseConcatenation(ticketSK, businessFunction_sk, refLabJDBCTemplate, user));
                                parameters.put(REFERENCE, crComReferenceConcatenation(ticketNumber, refLabJDBCTemplate, user));
                                parameters.put(EMAIL_SUBJECT, crDiscussionBoardConcatenation(ticketNumber, refLabJDBCTemplate, user));

                                if (endpointName.equalsIgnoreCase(CREATE_DMART_CHANGE_REQUEST)) {
                                    resultMap = SFInterfaceBase.createDmartChangeRequest(dmartJDBCTemplate, spName, parameters, endpointName);
                                } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_CHANGE_REQUEST)) {
                                    resultMap = SFInterfaceBase.updateDmartChangeRequest(dmartJDBCTemplate, spName, parameters, endpointName);
                                }
                                if (resultMap.size() == NUM_ZERO) {
                                    resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                                } else if (resultMap.containsKey(ERROR)) {
                                    errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                                    return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                                }
                            } else {
                                resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.INVALIDCRNUMBER", null, endpointName);
                                return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                            }

                        } else {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTYCRNUMBER", null, endpointName);
                            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateDmartCRTask(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate dmartJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
        String loginID = "", groupList = "", spName = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(CREATE_DMART_CRTASK)) {
                            resultMap = SFInterfaceBase.createDmartCRTask(dmartJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_CRTASK)) {
                            resultMap = SFInterfaceBase.updateDmartCRTask(dmartJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs7(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_MASTER_TYPE)) {
                            resultMap = SFInterfaceBase.updateMasterType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ALERT_STATUS)) {
                            resultMap = SFInterfaceBase.updateAlertStatus(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_USER_TYPE)) {
                            resultMap = SFInterfaceBase.updateUserType(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SEVERITY)) {
                            resultMap = SFInterfaceBase.updateSeverity(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_TASK_ASSIGNMENT_GROUP)) {
                            resultMap = SFInterfaceBase.updateTaskAssignmentGroup(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_TITLE)) {
                            resultMap = SFInterfaceBase.updateTitle(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_USER_HIERARCHY)) {
                            resultMap = SFInterfaceBase.updateUserHierarchy(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_USER_NOTES_VISIBILITY)) {
                            resultMap = SFInterfaceBase.updateUserNotesVisibility(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs8(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_USER_SKILL_SETS)) {
                            resultMap = SFInterfaceBase.updateUserSkillSets(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_INSTRUCTIONS)) {
                            resultMap = SFInterfaceBase.updateInstructions(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_EMAIL_DOMAINS)) {
                            resultMap = SFInterfaceBase.updateEmailDomains(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_SKILL_SET)) {
                            resultMap = SFInterfaceBase.updateSkillSet(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_AUTOMATION_SERVICES)) {
                            resultMap = SFInterfaceBase.updateAutomationServices(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_ACCOUNT_TYPE)) {
                            resultMap = SFInterfaceBase.updateAccountType(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static String crWorknotesConcatenation(String ticketNumber, JdbcTemplate refLabJDBCTemplate, User user) throws IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        List<Map<String, Object>> workLogs = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        String workLogString = "";

        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_WORKNOTES, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET, new String[]{ticketNumber});
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
            workLogs = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
            if (workLogs.size() > NUM_ZERO) {
                for (Map<String, Object> workLog : workLogs) {
                    if (StringUtils.isBlank(workLogString)) {
                        workLogString = workLog.get(CREATED_ON) + "\u0004" + workLog.get(WORKNOTES)
                                + "\u0004" + workLog.get(CREATED_BY_LOGINID);
                    } else {
                        workLogString += "\u0003" + workLog.get(CREATED_ON) + "\u0004" + workLog.get(WORK_LOG)
                                + "\u0004" + workLog.get(CREATED_BY_LOGINID);
                    }
                }
            }
        }
        return workLogString;
    }

    public static Map<String, Object> crLocationConcatenation(String ticketNumber, JdbcTemplate refLabJDBCTemplate, User user) throws IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        Map<String, Object> crLocation = new HashMap<>();
        Map<String, String[]> params = new HashMap<>();
        List<Map<String, Object>> resultMap;


        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_LOCATIONS1, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET, new String[]{ticketNumber});
            params.put(STATUS, new String[]{ONE});

            List<String> locationType = new ArrayList<>();
            locationType.add("C");
            locationType.add("A");

            for (String type : locationType) {
                resultMap = new ArrayList<>();
                String locationString = "";
                params.put(TYPE, new String[]{type});
                params.put(STATUS, new String[]{ONE});

                String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                resultMap = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
                if (resultMap.size() > NUM_ZERO) {
                    for (Map<String, Object> result : resultMap) {
                        if (StringUtils.isBlank(locationString)) {
                            locationString = (String) result.get(CR_LOCATION);
                        } else {
                            locationString += "\u0003" + (String) result.get(CR_LOCATION);
                        }
                    }
                }
                if (type.equalsIgnoreCase("C")) {
                    crLocation.put(LOCATION_OF_CHANGE, locationString);
                } else if (type.equalsIgnoreCase("A")) {
                    crLocation.put(EFFECTED_LOCATION, locationString);
                }
            }
        }
        return crLocation;
    }

    public static String crComResponseConcatenation(String ticketSk, String businessFunctionSk, JdbcTemplate refLabJDBCTemplate, User user) throws IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        String responseString = "";

        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_RESPONSES, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET_SK, new String[]{ticketSk});
            params.put(BUSINESS_FUNCTION_SK, new String[]{businessFunctionSk});
            params.put(STATUS, new String[]{ONE});
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
            result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
            if (result.size() > NUM_ZERO) {
                for (Map<String, Object> comResponse : result) {
                    if (StringUtils.isBlank(responseString)) {
                        responseString = (String) comResponse.get(RESPONSE);
                    } else {
                        responseString += "\u0003" + (String) comResponse.get(RESPONSE);
                    }
                }
            }
        }
        return responseString;
    }

    public static String crComReferenceConcatenation(String ticketNumber, JdbcTemplate refLabJDBCTemplate, User user) throws IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        String responseString = "";

        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_COM_REFERENCES, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET, new String[]{ticketNumber});
            params.put(STATUS, new String[]{ONE});
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
            result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
            if (result.size() > NUM_ZERO) {
                for (Map<String, Object> comResponse : result) {
                    if (StringUtils.isBlank(responseString)) {
                        responseString = (String) comResponse.get(REFERENCE_TICKET_NUMBER);
                    } else {
                        responseString += "\u0003" + (String) comResponse.get(REFERENCE_TICKET_NUMBER);
                    }
                }
            }
        }
        return responseString;
    }

    public static String crDiscussionBoardConcatenation(String ticketNumber, JdbcTemplate refLabJDBCTemplate, User user) throws IOException {
        Map<String, Object> endPointDomain = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String[]> params = new HashMap<>();
        String responseString = "";

        endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_CR_DISCUSSION_BOARD_SP, V1, GET_METHOD, user);
        if (endPointDomain.size() > NUM_ZERO && !(endPointDomain.containsKey(ERROR))) {
            params.put(TICKET, new String[]{ticketNumber});
            params.put(STATUS, new String[]{ACTIVE});
            String refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
            result = SFInterfaceBase.runjdbcQueryWithSelectedColumns(refLabJDBCTemplate, refdbquery, null);
            if (result.size() > NUM_ZERO) {
                for (Map<String, Object> comResponse : result) {
                    if (StringUtils.isBlank(responseString)) {
                        responseString = (String) comResponse.get(NOTES);
                    } else {
                        responseString += "\u0003" + (String) comResponse.get(NOTES);
                    }
                }
            }
        }
        return responseString;
    }

    public static ResponseEntity createOrUpdateDmartCRConsolidated(String methodType, String aToken, String refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate dmartJDBCTemplate, HttpServletRequest request, JdbcTemplate refLabJDBCTemplate, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        UserAccountNGroup userAccountNGroup = new UserAccountNGroup();
        String loginID = "", groupList = "", spName = "";
        List<Map<String, Object>> crResult = new ArrayList();
        String ticketSK = "", businessFunction_sk = "";

        try {
            SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        spName = endPointDomain.get(QUERY).toString();
                        if (endpointName.equalsIgnoreCase(CREATE_DMART_CR_CONSOLIDATED)) {
                            resultMap = SFInterfaceBase.createDmartCRConsolidated(dmartJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_DMART_CR_CONSOLIDATED)) {
                            resultMap = SFInterfaceBase.updateDmartCRConsolidated(dmartJDBCTemplate, spName, parameters, endpointName);
                        }
                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SFInterfaceLoggerBase.response(LOGGER, endpointName, NA, NA, resultMap.toString());
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static ResponseEntity createOrUpdateAdminAPIs9(String methodType, String aToken, String
            refreshToken, String accessToken
            , Map<String, Object> parameters, String endpointName, String version
            , JdbcTemplate refLabJDBCTemplate, HttpServletRequest request, User user) throws Exception {

        List<Map<String, Object>> arrayList = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>(), errorMap = new LinkedHashMap<>();
        String loginID = "";

        try {
            if (isRequestContainToken(aToken, refreshToken, accessToken)) {
                loginID = SFInterfaceConnectionBase.fetchRequestInfo(request).getAuthUserName();
                if (StringUtils.isNotBlank(loginID)) {
                    SFInterfaceLoggerBase.log(LOGGER, endpointName, parameters.toString());
                    Map<String, Object> endPointDomain = SFInterfaceBase.fetchEndpointDetails(endpointName, version, methodType, user);
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName = endPointDomain.get(QUERY).toString();

                        if (endpointName.equalsIgnoreCase(UPDATE_ESCLATIONS)) {
                            resultMap = SFInterfaceBase.updateEscalations(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_UNITS)) {
                            resultMap = SFInterfaceBase.updateUnits(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_NOTIFICATIONS)) {
                            resultMap = SFInterfaceBase.updateNotifications(refLabJDBCTemplate, spName, parameters, endpointName);
                        } else if (endpointName.equalsIgnoreCase(UPDATE_NOTIFICATION_MAPPING)) {
                            resultMap = SFInterfaceBase.updateNotificationMapping(refLabJDBCTemplate, spName, parameters, endpointName);
                        }

                        if (resultMap.size() == NUM_ZERO) {
                            resultMap = SFInterfaceServices.getInfoMap("INFO.MESSAGE.EMPTY", null, endpointName);
                        } else if (resultMap.containsKey(ERROR)) {
                            errorMap = SFInterfaceServices.getDefaultErrorMap("ERROR.MESSAGE.SPERROR", null, endpointName, (String) resultMap.get(ERROR));
                            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.ENDPOINTNOTFOUND", null, endpointName);
                        return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
                    }
                } else {
                    resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.INVALIDAUTHENTICATION", null, endpointName);
                    return new ResponseEntity<>(resultMap, HttpStatus.UNAUTHORIZED);
                }
            } else {
                resultMap = SFInterfaceServices.getErrorMap("ERROR.MESSAGE.MISSINGTOKEN", null, endpointName);
                return new ResponseEntity<>(resultMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            SFInterfaceLoggerBase.exception(LOGGER, endpointName, e);
            return new ResponseEntity<>(SFInterfaceServices.ISPExceptionHandler(e, null, endpointName), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(SFInterfaceServices.convertTimestamptoStringFromMap(resultMap), HttpStatus.OK);
    }

    public static void insertOrUpdateKBArticleInReporting(Map<String, Object> kbArticle, String dmartEndpoint, String version, User user) throws IOException {
        List<Map<String, Object>> arrayList = new ArrayList<>();
        String loginID = "", refdbquery = "";
        Map<String, String[]> params = new HashMap<>();
        Map<String, Object> endPointDomain = new HashMap<>();
        if (kbArticle.containsKey(KB_ARTICLE_ID) && kbArticle.get(KB_ARTICLE_ID) != null) {
            List<Map<String, Object>> articleList = new ArrayList<>();
            Map<String, Object> resultMap1 = new HashMap<>();
            endPointDomain = SFInterfaceBase.fetchEndpointDetails(INDEXERNAME_FETCH_KB_ARTICLEDETAILS, version, GET_METHOD, user);
            if (!endPointDomain.containsKey(ERRORCODE)) {
                params.put(KB_ARTICLE_ID, new String[]{kbArticle.get(KB_ARTICLE_ID).toString()});
                refdbquery = getDBQuery(endPointDomain.get(QUERY).toString(), params);
                articleList = SFInterfaceBase.runjdbcQueryWithPagination(SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName())
                        , refdbquery
                        , null, null, null, null, null, 0, 0
                        , Boolean.TRUE);
                if (articleList.size() > NUM_ZERO) {
                    endPointDomain = SFInterfaceBase.fetchEndpointDetails(dmartEndpoint, version, POST_METHOD, user);
                    Map<String, Object> kbArticleDetails = new HashMap<>();
                    if (!endPointDomain.containsKey(ERRORCODE)) {
                        String spName1 = endPointDomain.get(QUERY).toString();
                        String typeAheadPhrase = "";
                        String typeAheadPhraseArr[] = new String[]{};
                        kbArticleDetails = articleList.get(NUM_ZERO);
                        typeAheadPhrase = kbArticleDetails.containsKey(TYPE_AHEAD_PHRASES) ? (String) kbArticleDetails.get(TYPE_AHEAD_PHRASES) : null;
                        String kbPhrases = "";
                        if (StringUtils.isNotBlank(typeAheadPhrase)) {
                            typeAheadPhraseArr = typeAheadPhrase.split(COMMA);
                            for (String phrase : typeAheadPhraseArr) {
                                kbPhrases += StringUtils.isNotBlank(kbPhrases) ? COMMA + phrase.split(":")[2] : phrase.split(":")[2];
                            }
                        }
                        kbArticleDetails.put(TYPE_AHEAD_PHRASES, kbPhrases);

                        SFInterfaceLoggerBase.log(LOGGER, dmartEndpoint, " Request:" + kbArticleDetails);
                        if (dmartEndpoint.equalsIgnoreCase(CREATE_DMART_KB_ARTICLE_METADATA)) {
                            resultMap1 = SFInterfaceBase.createDmartKBArticle(
                                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName())
                                    , spName1, kbArticleDetails, dmartEndpoint);
                        } else if (dmartEndpoint.equalsIgnoreCase(UPDATE_DMART_KB_ARTICLE_METADATA)) {
                            resultMap1 = SFInterfaceBase.updateDmartKBArticle(
                                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbReportingConnectionName())
                                    , spName1, kbArticleDetails, dmartEndpoint);
                        }

                        SFInterfaceLoggerBase.log(LOGGER, dmartEndpoint, " Response:" + resultMap1);
                    }
                }
            }
        }
    }
}

