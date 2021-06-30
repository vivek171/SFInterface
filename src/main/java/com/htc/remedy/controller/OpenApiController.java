package com.htc.remedy.controller;

import com.bmc.arsys.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.htc.remedy.base.*;
import com.htc.remedy.domain.EndPointDomain;
import com.htc.remedy.domain.FieldsDomain;
import com.htc.remedy.domain.LDAPEndPoints;
import com.htc.remedy.model.*;
import com.htc.remedy.repo.EndPointRepo;
import com.htc.remedy.repo.FieldRepo;
import com.htc.remedy.repo.LDAPAccountsRepo;
import com.htc.remedy.repo.LDAPEndpointRepo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by kvivek on 10/30/2017.
 */

@RestController
@CrossOrigin
public class OpenApiController {

    @Value("${remedy.host}")
    String serverName;
    @Value("${remedy.port}")
    Integer port;

    @Value("${spring.datasource.urlref}")
    String datasource;

    @Value("${spring.datasource.usernameref}")
    String databaseusername;

    @Value("${spring.datasource.passwordref}")
    String databasepassword;


    @Value("${ctsspi.username}")
    String dusername;

    @Value("${ctsspi.fileattachment}")
    String fileattachmentpath;

    @Value("${ctsspi.password}")
    String dpassword;

    @Value("${ctsspi.hash}")
    String atoken1;

    @Value("${ctsspi.encryptkey}")
    String key1;


    private final
    EndPointRepo endPointRepo;


    private final
    LDAPAccountsRepo ldapAccountsRepo;

    private final
    LDAPEndpointRepo ldapEndpointRepo;

    private final
    ARServerUser arServerUser1;

   /* private final
    Map<String, List<LdapTemplate>> ldapTemplateMap;*/

    @Autowired
    public OpenApiController(EndPointRepo endPointRepo, LDAPAccountsRepo ldapAccountsRepo, LDAPEndpointRepo ldapEndpointRepo, ARServerUser arServerUser1/*, Map<String, List<LdapTemplate>> ldapTemplateMap*/) {
        this.endPointRepo = endPointRepo;

        this.ldapAccountsRepo = ldapAccountsRepo;
        this.ldapEndpointRepo = ldapEndpointRepo;
        this.arServerUser1 = arServerUser1;
        /*this.ldapTemplateMap = ldapTemplateMap;*/
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/customupdateticket")
    @ResponseBody
    public Map<String, String> updateticketduplicate(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String formName = null, ticketId = null;
        boolean formstatus = false, ticketIdstatus = false;
        Map<String, String> response = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (String s : updateParam.keySet()) {
                if (s.equals("formName")) {
                    formName = updateParam.get(s);
                    formstatus = true;
                }
                if (s.equals("ticketId")) {
                    ticketId = updateParam.get(s);
                    ticketIdstatus = true;
                }
            }
            if (formstatus && ticketIdstatus) {
                response = RemedyBase.customupdateTicket(user, updateParam, formName, ticketId);
                LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(response), formName, com.htc.remedy.constants.Constants.UPDATE, request);
                return response;
            } else {
                throw new Exception("Formname or Ticket id not available");
            }
        } catch (Exception e) {
            return RemedyBase.returnError(e.getMessage());
        }
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/customupdateticketbyid")
    @ResponseBody
    public Map<String, String> updateticketduplicatebyid(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String formName = null, ticketId = null;
        boolean formstatus = false, ticketIdstatus = false;
        Map<String, String> response = new HashMap<>();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (String s : updateParam.keySet()) {
                if (s.equals("formName")) {
                    formName = updateParam.get(s);
                    formstatus = true;
                }
                if (s.equals("ticketId")) {
                    ticketId = updateParam.get(s);
                    ticketIdstatus = true;
                }
            }

            if (formstatus && ticketIdstatus) {
                updateParam.remove("formName");
                updateParam.remove("ticketId");
                response = RemedyBase.customupdateTicketbyid(user, updateParam, formName, ticketId);
                LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(response), formName, com.htc.remedy.constants.Constants.UPDATE, request);
                return response;
            } else {
                throw new Exception("Formname or Ticket id not available");
            }

        } catch (Exception e) {
            return RemedyBase.returnError(e.getMessage());
        }
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/customupdateprofilebyloginid")
    @ResponseBody
    public ResponseEntity customupdateprofilebyloginid(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String formName = "CMN:People Information", ticketId = null;
        boolean formstatus = false, ticketIdstatus = false;

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (String s : updateParam.keySet()) {
                if (s.equals("ticketId")) {
                    ticketId = updateParam.get(s);
                    ticketIdstatus = true;
                }
            }
            List<Entry> entries = new ArrayList<>();
            if (ticketIdstatus) {
                entries = RemedyBase.queryEntrysByQual(user, formName, null, "'Login ID'=\"" + ticketId + "\"");

                if (entries.isEmpty()) {
                    throw new Exception("Record Not Found");
                } else {
                    updateParam.remove("ticketId");
                    return new ResponseEntity(RemedyBase.customupdateTicketbyid(user, updateParam, formName, entries.get(0).getEntryId()), HttpStatus.OK);
                }
            } else {
                throw new Exception("Ticket id not available");
            }
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/updatenotificationassignmentbyloginid")
    @ResponseBody
    public ResponseEntity updatenotificationassignmentbyloginid(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String formName = "CMN:Notification-Assignments", ticketId = null;
        boolean formstatus = false, ticketIdstatus = false;

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (String s : updateParam.keySet()) {
                if (s.equals("ticketId")) {
                    ticketId = updateParam.get(s);
                    ticketIdstatus = true;
                }
            }

            List<Entry> entries = new ArrayList<>();
            if (ticketIdstatus) {
                entries = RemedyBase.queryEntrysByQual(user, formName, null, "'Login ID'=\"" + ticketId + "\" AND 'Status'=\"Inactive\"");

                if (entries.isEmpty()) {
                    throw new Exception("Record Not Found");
                } else {
                    updateParam.remove("ticketId");
                    return new ResponseEntity(RemedyBase.customupdateTicketbyid(user, updateParam, formName, entries.get(0).getEntryId()), HttpStatus.OK);
                }
            } else {
                throw new Exception("Ticket id not available");
            }

        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, produces = MediaType.APPLICATION_JSON_VALUE, path = "/openapi/cancelvacation")
    @ResponseBody
    public ResponseEntity cancelvacation(
            HttpServletRequest request,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String notificationformName = "CMN:Notification-Assignments", peopleinfoform = "CMN:People Information", busyvacationform = "CMN:BusyVacation+", ticketId = null;
        boolean formstatus = false, ticketIdstatus = false;
        String loginid = "";
        Map<String, String> updateparams = new HashMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization error");
            }

            loggedinuser = ITSMBase.impersonateuser(arServerUser1, loginid);

            for (String s : updateParam.keySet()) {
                if (s.equals("loginid")) {
                    ticketId = updateParam.get(s);
                    ticketIdstatus = true;
                }
            }

            List<Entry> entries = new ArrayList<>();
            if (ticketIdstatus) {
                entries = RemedyBase.queryEntrysByQual(arServerUser1, peopleinfoform, null, "'Login ID'=\"" + ticketId + "\"");
                if (entries.isEmpty()) {
                    throw new Exception("User not in Vacation or Found!");
                } else {
                    updateparams.put("7", "Active");
                    updateparams.put("536870928", "SKIP");
                    updateparams.put("704000016", "NOVALUE");
                    RemedyBase.customupdateTicketbyid(loggedinuser, updateparams, peopleinfoform, entries.get(0).getEntryId());

                    entries = RemedyBase.queryEntrysByQual(arServerUser1, notificationformName, null, "'Login ID'=\"" + ticketId + "\" AND 'Status'=\"Inactive\"");

                    if (!entries.isEmpty()) {
                        updateparams.clear();
                        updateparams.put("7", "Active");
                        RemedyBase.customupdateTicketbyid(loggedinuser, updateparams, notificationformName, entries.get(0).getEntryId());
                    }
                    entries = RemedyBase.queryEntrysByQual(arServerUser1, busyvacationform, null, "'Busy/Vacation Person'=\"" + ticketId + "\" AND ('Status'=\"Scheduled\" OR 'Status'=\"Current\")");
                    updateparams.clear();

                    if (!entries.isEmpty()) {
                        for (Entry entry : entries) {
                            updateparams.put("7", "Remove");
                            RemedyBase.customupdateTicketbyid(loggedinuser, updateparams, busyvacationform, entry.getEntryId());
                        }
                    }
                }
            } else {
                throw new Exception("Login Id not available!");
            }
            LoggerBase.loguserrecords("cancelvacation", loggedinuser, "", ticketId, busyvacationform, com.htc.remedy.constants.Constants.DELTE, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnSuccess(ticketId + " " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }

        return new ResponseEntity(RemedyBase.returnSuccess(ticketId + " vacation has been ended!"), HttpStatus.OK);
    }


    DataSource dataSource() {
        return
                DataSourceBuilder
                        .create()
                        .url(datasource)
                        .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                        .username(databaseusername)
                        .password(databasepassword)
                        .build();
    }

    JdbcTemplate jdbc() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/syncprogram")
    @ResponseBody
    public void syncprogram() {
        ARServerUser arServerUser = new ARServerUser();
        arServerUser.setServer("10.165.135.97");
        arServerUser.setPort(5000);
        arServerUser.setUser("CTSSP-WEBUSER");
        arServerUser.setPassword("N2v9k!pt4r9rM$Xln");
        try {
            arServerUser.login();
        } catch (ARException e) {
            e.printStackTrace();
        }

        int[] buildingref = new int[]{
                1,//	Request ID
                200000012,//	Client
                200000007,//	Building
                7,//	Status
        };


        int[] departmentref = new int[]{
                200000006,//	Department
                200000007,//	Building
                200000012,//	Client
                7,//	Status
                1,//	Request ID

        };

        int[] floorref = new int[]{
                1,//	Request ID
                200000007,//	Building
                200000012,//	Client
                536870913,//	Floor
        };


        int[] suiteref = new int[]{
                1,//	Request ID
                200000007,//	Building
                200000012,//	Client
                536870913,//	Floor
                536871039,//	Suite
        };

        int[] ctsaccountref = new int[]{
                536870913,//	Client
                536870914,//	Master Client
                536870918,//	Client Name
                536871032,//	URLKey
        };

        String ctsaccountform = "CMN:ClientInfo", floorformname = "CMN:BuildingFloorSuite+", locationformname = "CMN:Location", buildingformname = "CMN:BuildingAddress+";
/*
        List<Entry> accountdatas = RemedyBase.queryEntrysByQual(arServerUser, ctsaccountform, ctsaccountref, "");
        accountdatas.forEach(entry -> {
            String client = entry.get(536870913).toString();
            String master_client = entry.get(536870914).toString();
            String clientname = entry.get(536870918).toString();
            String urlkey = entry.get(536871032).toString();
            int masterornot = 0;
            if (client.equalsIgnoreCase(master_client)) {

                if (client != null && master_client != null ) {
                    jdbc().execute("EXEC USP_CTSSP_InsertCTSSpAccount \"" + client + "\",\"" + clientname + "\",\"" + master_client + "\",\""+urlkey+"\"");
                }//
            }

        });

        accountdatas.forEach(entry -> {
            String client = entry.get(536870913).toString();
            String master_client = entry.get(536870914).toString();
            String clientname = entry.get(536870918).toString();
            String urlkey = entry.get(536871032).toString();
            int masterornot = 0;
            if (!client.equalsIgnoreCase(master_client)) {

                if (client != null && master_client != null ) {
                    jdbc().execute("EXEC USP_CTSSP_InsertCTSSpAccount \"" + client + "\",\"" + clientname + "\",\"" + master_client + "\",\""+urlkey+"\"");
                }//
            }

        });*/


        List<Entry> buildingdatas = RemedyBase.queryEntrysByQual(arServerUser, buildingformname, buildingref, "'Status'=\"Active\"");


        buildingdatas.forEach(entry -> {

            if (entry.get(1).toString() != null && entry.get(200000012).toString() != null && entry.get(200000007).toString() != null) {
                jdbc().execute("EXEC USP_CTSSP_InsertCTSSpBuilding \"" + entry.get(1).toString() + "\",\"" + entry.get(200000012).toString() + "\",\"" + entry.get(200000007).toString() + "\"");
            }//requestid,client,buildingname
        });

        List<Entry> departmentdatas = RemedyBase.queryEntrysByQual(arServerUser, locationformname, departmentref, "'Status'=\"Active\"");
        departmentdatas.forEach(entry -> {
            if (entry.get(1).toString() != null && entry.get(200000012).toString() != null && entry.get(200000007).toString() != null && entry.get(200000006).toString() != null) {//requestid,client,building,departmentname
                jdbc().execute("EXEC USP_CTSSP_InsertCTSSPDepartment \"" + entry.get(1).toString() + "\",\"" + entry.get(200000012).toString() + "\",\"" + entry.get(200000007).toString() + "\",\"" + entry.get(200000006).toString() + "\"");
            }
        });

        List<Entry> floordatas = RemedyBase.queryEntrysByQual(arServerUser, floorformname, floorref, "'Status'=\"Current\"");
        floordatas.forEach(entry -> {
            if (entry.get(1).toString() != null && entry.get(200000012).toString() != null && entry.get(200000007).toString() != null && entry.get(536870913).toString() != null) {//requestid,client,building,floor
                jdbc().execute("EXEC USP_CTSSP_InsertCTSSPFloor \"" + entry.get(1).toString() + "\",\"" + entry.get(200000012).toString() + "\",\"" + entry.get(200000007).toString() + "\",\"" + entry.get(536870913).toString() + "\"");
            }
        });

        List<Entry> suitedatas = RemedyBase.queryEntrysByQual(arServerUser, floorformname, suiteref, "'Status'=\"Current\"");
        suitedatas.forEach(entry -> {
            if (entry.get(1).toString() != null && entry.get(536870913).toString() != null && entry.get(536871039).toString() != null) {//requestid,floor,suite
                jdbc().execute("EXEC USP_CTSSP_InsertCTSSPSuite \"" + entry.get(1).toString() + "\",\"" + entry.get(536870913).toString() + "\",\"" + entry.get(536871039).toString() + "\"");
            }
        });


    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/customcreateformentry")
    @ResponseBody
    public Map<String, String> customcreateformentry(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateParam) {

        String formName = null;
        boolean formstatus = false;

        Map<String, String> srResult = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (String s : updateParam.keySet()) {
                if (s.equals("formName")) {
                    formName = updateParam.get(s);
                    formstatus = true;
                }
            }
            if (formstatus) {
                updateParam.remove("formName");
            }

            Entry entryData = new Entry();
            for (Map.Entry<String, String> entry : updateParam.entrySet()) {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }
            if (formstatus) {
                String ticketId = RemedyBase.createTicket(user, formName, entryData);
                srResult.put("entryid", ticketId);
                LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(updateParam), formName, com.htc.remedy.constants.Constants.INSERT, request);
                return srResult;
            } else {
                throw new Exception("Formname  not available");
            }
        } catch (Exception e) {
            return RemedyBase.returnError(e.getMessage());
        }
    }

   /* @ResponseBody
    @RequestMapping(path = "/openapi/ldapquery/{epname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String ldapQuery(@RequestHeader(value = "username", required = false) String username,
                            @RequestHeader(value = "password", required = false) String password,
                            @RequestHeader(value = "atoken", required = false) String atoken,
                            @PathVariable("epname") String endpointName,
                            @RequestParam(value = "sortfield", required = false) String sortfield,
                            @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                            HttpServletRequest request) {
        JsonArray jsonElements = new JsonArray();

        try {
            LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue(endpointName);
            if (ldapEndPoints == null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("error", "Endpoint not available with : " + endpointName);
                return jsonObject.toString();
            } else {
                String qstr = ldapEndPoints.getQuery();

                Map<String, String[]> params = request.getParameterMap();
                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }

                if (sortfield == null || sortfield.isEmpty()) {
                    jsonElements = LDAPBase.runQuery(
                            ldapEndPoints.getLdapAccounts(),
                            ldapEndPoints.getBase(),
                            rValue,
                            ldapEndPoints.getRequiredFields()
                    );

                } else {
                    jsonElements = LDAPBase.runQuery1(ldapEndPoints.getLdapAccounts(),
                            ldapEndPoints.getBase(),
                            rValue,
                            ldapEndPoints.getRequiredFields(), sortfield, distinct);

                }
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }
        return jsonElements.toString();

    }*/

    @ResponseBody
    @RequestMapping(path = "/openapi/ldapquery/{epname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> ldapQuery1(@RequestHeader(value = "username", required = false) String username,
                                                @RequestHeader(value = "password", required = false) String password,
                                                @RequestHeader(value = "atoken", required = false) String atoken,
                                                @PathVariable("epname") String endpointName,
                                                @RequestParam(value = "sortfield", required = false) String sortfield,
                                                @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                                                @RequestParam(value = "message", required = false, defaultValue = "") String message,
                                                HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue(endpointName);
            if (ldapEndPoints == null) {
                Map<String, Object> errorobject = new HashMap<>();
                errorobject.put("error", "Endpoint not available with : " + endpointName);
                result.add(errorobject);
                return result;
            } else {
                String qstr = ldapEndPoints.getQuery();

                Map<String, String[]> params = request.getParameterMap();
                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }

                if (sortfield == null || sortfield.isEmpty()) {
                    result = LDAPBase.runldapQuery(
                            ldapEndPoints.getLdapAccounts(),
                            ldapEndPoints.getBase(),
                            rValue,
                            ldapEndPoints.getRequiredFields()
                    );

                } else {
                    result = LDAPBase.runldapQuerywithsort(ldapEndPoints.getLdapAccounts(),
                            ldapEndPoints.getBase(),
                            rValue,
                            ldapEndPoints.getRequiredFields(), sortfield, distinct);
                }
            }
        } catch (Exception e) {
            Map<String, Object> errorobject = new HashMap<>();
            errorobject.put("error", e.getMessage());
            result.add(errorobject);
        }
        if (result.isEmpty() && !message.isEmpty()) {
            Map<String, Object> errorobject = new HashMap<>();
            errorobject.put("error", message);
            result.add(errorobject);
        }
        return result;

    }

    @ResponseBody
    @RequestMapping(path = "/openapi/ldapquery/authorizingmanager", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> authorizingmanager(@RequestParam(value = "sortfield", required = false) String sortfield,
                                                        @RequestParam(value = "distinct", required = false, defaultValue = "true") String distinct,
                                                        @RequestParam(value = "message", required = false, defaultValue = "") String message,
                                                        HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue("cvntldapusers-groupmembers");
            if (ldapEndPoints == null) {
                Map<String, Object> errorobject = new HashMap<>();
                errorobject.put("error", "Endpoint not available with : " + "cvntldapusers-groupmembers");
                result.add(errorobject);
                return result;
            } else {
                String qstr = ldapEndPoints.getQuery();

                Map<String, String[]> params = request.getParameterMap();
                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }
                result = LDAPBase.runldapQuerywithsortauthorizingmanager(ldapEndPoints.getLdapAccounts(),
                        ldapEndPoints.getBase(),
                        rValue,
                        ldapEndPoints.getRequiredFields(), sortfield, distinct);

            }
        } catch (Exception e) {
            Map<String, Object> errorobject = new HashMap<>();
            errorobject.put("error", e.getMessage());
            result.add(errorobject);
        }
        if (result.isEmpty() && !message.isEmpty()) {
            Map<String, Object> errorobject = new HashMap<>();
            errorobject.put("error", message);
            result.add(errorobject);
        }
        return result;

    }


    @ResponseBody
    @RequestMapping(path = "/openapi/authenticate", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> authenticate(@RequestParam(value = "username") String username,
                                                  @RequestParam(value = "password") String password) {
        List<Map<String, String>> result = new ArrayList<>();
        int[] loginfieldid = new int[]{700001039};
        int[] rf = new int[]{
                8, //fullname 0
                200000006, //dept 1
                200000007, //building 2
                700001012, //email 3
                700001038, //empno 4
                910000100, //floor 5
                910000101, //suite 6
                910000102, //office 7
                910000103, //busorg 8
                910000104, //ph work 9
                700001084, //fname 10
                700001082, //lname 11
                700001039, //loginid 12
                910000105,//	Phone Ext 13
                179,//	GUID 14
                700001132,//	Designation 15
                910000106,//	Fax 16
                700001022,//	VIP 17
                620000180,//	Title 18
                536870949,//	Network Login 19
                910000350,//	Queue 20
                700001132,//	Designation 21
                200000012,//	Client 22
                700001083,//middle Initial 23
                910000416,//cost code
                910000415,//Role
                910000414,//Role Prefix
                704000051//support person
        };
        try {
            Map<String, String> userdetails = new HashMap<>();
            List<Entry> login_id1 = RemedyBase.queryEntrysByQual
                    (arServerUser1, "User", new int[]{101, 117}, "('Login Name' =\"" + username + "\")");
            if ((login_id1.size() > 0) && (login_id1 != null)) {
                for (Entry entry : login_id1) {
                    username = entry.get(101).toString();   //getting first matching remedy login id
                }
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            List<Entry> peopleEntry = RemedyBase.queryEntrysByQual
                    (arServerUser1, "CMN:People Information", rf, "('Login ID' = \"" + username + "\")");

            if (peopleEntry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser1, "CMN:People Information");

                for (Entry entry1 : peopleEntry) {
                    entry1.keySet().parallelStream().forEach(keyId -> {
                        userdetails.put(userMap.get(keyId), entry1.get(keyId).toString());
                    });
                }
            }

            result.add(userdetails);

        } catch (Exception e) {
            result.clear();
            Map<String, String> errorobject = new HashMap<>();
            errorobject.put("error", "Authentication failed.");
            result.add(errorobject);
        }
        return result;

    }


    @ResponseBody
    @RequestMapping(path = "/openapi/ctsspi/{endpointname}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String endpointurl(Model model,
                              @RequestHeader(value = "username", required = false) String username,
                              @RequestHeader(value = "password", required = false) String password,
                              @RequestHeader(value = "atoken", required = false) String atoken,
                              @PathVariable("endpointname") String endpointName,
                              HttpServletRequest request) {

        JsonArray tArray = new JsonArray();

        try {

            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }


            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            EndPointDomain endPointDomain = endPointRepo.findByEndPointName(endpointName);

            if (endPointDomain != null) {
                String formName = endPointDomain.getFormName();
                Set<FieldsDomain> selectedFields = endPointDomain.getSelectedFields();

                Map<Integer, String> sMap = new HashMap<>();
                int[] rf = new int[selectedFields.size()];
                int c = 0;


                for (FieldsDomain s : selectedFields) {
                    sMap.put(s.getFieldId().intValue(), s.getFieldName());
                    rf[c++] = s.getFieldId().intValue();
                }


                String qstr = endPointDomain.getQualificationString();

                Map<String, String[]> params = request.getParameterMap();

                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                    System.out.println((qstr.replace("{{" + s + "}}", params.get(s)[0])));
                }


                List<Entry> entries = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        formName,
                        rf,
                        rValue
                );

                for (Entry entry : entries) {
                    JsonObject jsonObject = new JsonObject();
                    for (Integer integer : sMap.keySet()) {
                        if (entry.containsKey(integer)) {
                            jsonObject.addProperty(sMap.get(integer), entry.get(integer).toString().trim());
                        }
                    }
                    tArray.add(jsonObject);
                }
            } else {
                JsonObject error = new JsonObject();
                error.addProperty("error", "No data found for the given criteria");
                tArray.add(error);
            }

        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("error", e.getMessage());
            tArray.add(error);
        }
        return tArray.toString();
    }


    @ResponseBody
    @RequestMapping(path = "/openapi/dashboardlistservicefocus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DBValue> dashboardlistservicefocus(Model model,
                                                   @RequestHeader(value = "username", required = false) String username,
                                                   @RequestHeader(value = "password", required = false) String password,
                                                   @RequestHeader(value = "atoken", required = false) String atoken,
                                                   @RequestParam(value = "client", required = false, defaultValue = "CTS") String client,
                                                   @RequestParam(value = "fromdate", required = false, defaultValue = "") String fromdate,
                                                   @RequestParam(value = "todate", required = false, defaultValue = "") String todate,
                                                   HttpServletRequest request) {

        List<DBValue> dbValues = new ArrayList<>();
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        if (fromdate.isEmpty()) {
            fromdate = date.toString();
        }
        if (todate.isEmpty()) {
            todate = date.toString();
        }
        try {

            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            dbValues = jdbc().query("EXEC CW_Dashboard_Report \"" + fromdate + "\",\"" + todate + "\",\"" + client + "\"", new RowMapper<DBValue>() {
                @Override
                public DBValue mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DBValue dbValue = new DBValue();
                    dbValue.put("requesttype", rs.getString(1));
                    dbValue.put("srcreated", rs.getString(2));
                    dbValue.put("requestwaitingforapproval", rs.getString(3));
                    dbValue.put("totalrequestsubmitted", rs.getString(4));
                    dbValue.put("averageapprovaltime", rs.getString(5));
                    dbValue.put("rejected", rs.getString(6));
                    dbValue.put("resubmit", rs.getString(7));
                    dbValue.put("void", rs.getString(8));
                    return dbValue;
                }
            });


        } catch (Exception e) {
            dbValues = new ArrayList<>();

        }
        if (dbValues.isEmpty()) {
            DBValue dbValue = new DBValue();
            dbValue.put("requesttype", "");
            dbValue.put("srcreated", "");
            dbValue.put("requestwaitingforapproval", "");
            dbValue.put("totalrequestsubmitted", "");
            dbValue.put("averageapprovaltime", "");
            dbValue.put("rejected", "");
            dbValue.put("resubmit", "");
            dbValue.put("void", "");
            dbValues.add(dbValue);
        }

        return dbValues;
    }

    class DBValue extends HashMap<String, String> {

    }

    @ResponseBody
    @RequestMapping(path = "/openapi/srandptlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> srandptlist(Model model,
                                                 @RequestHeader(value = "username", required = false) String username,
                                                 @RequestHeader(value = "password", required = false) String password,
                                                 @RequestHeader(value = "atoken", required = false) String atoken,
                                                 @RequestParam(value = "client", required = true) String client,
                                                 @RequestParam(value = "fromdate", required = false, defaultValue = "") String fromdate,
                                                 @RequestParam(value = "todate", required = false, defaultValue = "") String todate,
                                                 HttpServletRequest request) {

        List<Map<String, Object>> result = new ArrayList<>();
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Map<String, String> fieldfieldname = new HashMap<>();

        if (fromdate.isEmpty()) {
            fromdate = date.toString() + " 00:00:00";
        } else {
            fromdate += " 00:00:00";
        }
        if (todate.isEmpty()) {
            todate = date.toString() + " 23:59:59";
        } else {
            todate += " 23:59:59";
        }
        try {

            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            int[] clientfieldid = new int[]{536870913, 536870918, 536870914};

            List<Entry> entrie = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:ClientInfo",
                    clientfieldid,
                    "('Master Client' = \"" + client + "\" OR 'Client'= \"" + client + "\")"
            );
            String masterclient = new String();
            masterclient = "";

            StringBuilder peopleQuery = new StringBuilder("(");
            int i = 0;
            if (!(entrie.size() == 0 || entrie == null)) {
                for (Entry entry : entrie) {

                    if (i == 0) {
                        peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    } else {
                        peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    }
                    ++i;
                }
            } else {
                peopleQuery.append("('Client' = \"" + client + "\")");
            }
            peopleQuery.append(")");


            ARServerUser arServerUser = null;

            arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            int[] reqfield = new int[]{
                    1,//	Request #+
                    3,//	Date/Time Created
                    700001047,//	Name+
                    7,//	Status
                    700001528,//	Priority
                    8,//	Summary
                    700001052,//	Assigned Group
                    700001059,//	Assigned Individual
            };

            fieldfieldname.put("1", "ticket");
            fieldfieldname.put("3", "createddate");
            fieldfieldname.put("700001047", "name");
            fieldfieldname.put("7", "status");
            fieldfieldname.put("700001528", "priority");
            fieldfieldname.put("8", "summary");
            fieldfieldname.put("700001052", "assignedgroup");
            fieldfieldname.put("700001059", "assignedindividual");

            List<Field> fields = arServerUser.getListFieldObjects("SR:ServiceRequest");
            Field status = fields.get(6);
            Field datetime = fields.get(2);
            // Field prirority = fields.get(419);
            List<Entry> entryList = new ArrayList<>();
            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser1, "SR:ServiceRequest", reqfield, "('Date/Time Created' >= \"" + fromdate + "\" AND 'Date/Time Created' <= \"" + todate + "\") AND " + peopleQuery.toString() + " AND 'Summary' Like \"%System Access Request%\"");
            //  List<Entry> entries1 = RemedyBase.queryEntrysByQual(arServerUser1, "PT:ProblemTicket", reqfield, "('Date/Time Created' >= \"" + fromdate + "\" AND 'Date/Time Created' <= \"" + todate + "\") AND " + peopleQuery.toString()+"'Summary'=\"\"");
            //  entries.addAll(entries1);

            for (Entry entry : entries) {
                Map<String, Object> tempentry = new HashMap<>();
                tempentry.put(fieldfieldname.get("1"), entry.get(1) != null ? entry.get(1).toString() : "");
                tempentry.put(fieldfieldname.get("3"), entry.get(3) != null ? RemedyBase.dateTimefieldvalue(datetime, entry.get(3)) : "");
                tempentry.put(fieldfieldname.get("700001047"), entry.get(700001047) != null ? entry.get(700001047).toString() : "");
                tempentry.put(fieldfieldname.get("7"), entry.get(7) != null ? RemedyBase.selectionfieldvalue(status, entry.get(7).toString()) : "");
                //  tempentry.put(fieldfieldname.get("700001528"), entry.get(700001528) != null ? RemedyBase.selectionfieldvalue(prirority, entry.get(700001528).toString()) : "");
                tempentry.put(fieldfieldname.get("8"), entry.get(8) != null ? entry.get(8).toString() : "");
                tempentry.put(fieldfieldname.get("700001052"), entry.get(700001052) != null ? entry.get(700001052).toString() : "");
                tempentry.put(fieldfieldname.get("700001059"), entry.get(700001059) != null ? entry.get(700001059).toString() : "");
                result.add(tempentry);
            }

        } catch (Exception e) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            Map<String, Object> tempentry = new HashMap<>();
            tempentry.put(fieldfieldname.get("1"), "");
            tempentry.put(fieldfieldname.get("3"), "");
            tempentry.put(fieldfieldname.get("700001047"), "");
            tempentry.put(fieldfieldname.get("7"), "");
            tempentry.put(fieldfieldname.get("700001528"), "");
            tempentry.put(fieldfieldname.get("8"), "");
            tempentry.put(fieldfieldname.get("700001052"), "");
            tempentry.put(fieldfieldname.get("700001059"), "");
            result.add(tempentry);
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(path = "/openapi/srandptlistwithassignedgroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> srandptlistwithassignedgroup(Model model,
                                                                  @RequestHeader(value = "username", required = false) String username,
                                                                  @RequestHeader(value = "password", required = false) String password,
                                                                  @RequestHeader(value = "atoken", required = false) String atoken,
                                                                  @RequestParam(value = "client", required = true) String client,
                                                                  @RequestParam(value = "fromdate", required = false, defaultValue = "") String fromdate,
                                                                  @RequestParam(value = "todate", required = false, defaultValue = "") String todate,
                                                                  @RequestParam(value = "assignedgroup", required = false, defaultValue = "CTS-SELF SERVICE") String assignedgroup,
                                                                  HttpServletRequest request) {

        List<Map<String, Object>> result = new ArrayList<>();
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Map<String, String> fieldfieldname = new HashMap<>();

        if (fromdate.isEmpty()) {
            fromdate = date.toString() + " 00:00:00";
        } else {
            fromdate += " 00:00:00";
        }
        if (todate.isEmpty()) {
            todate = date.toString() + " 23:59:59";
        } else {
            todate += " 23:59:59";
        }
        try {

            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            int[] clientfieldid = new int[]{536870913, 536870918, 536870914};

            List<Entry> entrie = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:ClientInfo",
                    clientfieldid,
                    "('Master Client' = \"" + client + "\" OR 'Client'= \"" + client + "\")"
            );
            String masterclient = new String();
            masterclient = "";

            StringBuilder peopleQuery = new StringBuilder("(");
            int i = 0;
            if (!(entrie.size() == 0 || entrie == null)) {
                for (Entry entry : entrie) {

                    if (i == 0) {
                        peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    } else {
                        peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    }
                    ++i;
                }
            } else {
                peopleQuery.append("('Client' = \"" + client + "\")");
            }
            peopleQuery.append(")");


            ARServerUser arServerUser = null;

            arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            int[] reqfield = new int[]{
                    1,//	Request #+
                    3,//	Date/Time Created
                    700001047,//	Name+
                    7,//	Status
                    700001528,//	Priority
                    8,//	Summary
                    700001052,//	Assigned Group
                    700001059,//	Assigned Individual
            };

            fieldfieldname.put("1", "ticket");
            fieldfieldname.put("3", "createddate");
            fieldfieldname.put("700001047", "name");
            fieldfieldname.put("7", "status");
            fieldfieldname.put("700001528", "priority");
            fieldfieldname.put("8", "summary");
            fieldfieldname.put("700001052", "assignedgroup");
            fieldfieldname.put("700001059", "assignedindividual");
            List<Field> fields = arServerUser.getListFieldObjects("SR:ServiceRequest");
            Field status = fields.get(6);
            Field datetime = fields.get(2);
            Field prirority = fields.get(421);

            List<Entry> entryList = new ArrayList<>();
            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser1, "SR:ServiceRequest", reqfield, "('Assigned Group'=\"" + assignedgroup + "\") AND ('Date/Time Created' >= \"" + fromdate + "\" AND 'Date/Time Created' <= \"" + todate + "\") AND " + peopleQuery.toString());
            List<Entry> entries1 = RemedyBase.queryEntrysByQual(arServerUser1, "PT:ProblemTicket", reqfield, "('Assigned Group'=\"" + assignedgroup + "\") AND ('Date/Time Created' >= \"" + fromdate + "\" AND 'Date/Time Created' <= \"" + todate + "\") AND " + peopleQuery.toString());
            entries.addAll(entries1);
            for (Entry entry : entries) {
                Map<String, Object> tempentry = new HashMap<>();
                tempentry.put(fieldfieldname.get("1"), entry.get(1) != null ? entry.get(1).toString() : "");
                tempentry.put(fieldfieldname.get("3"), entry.get(3) != null ? RemedyBase.dateTimefieldvalue(datetime, entry.get(3)) : "");
                tempentry.put(fieldfieldname.get("700001047"), entry.get(700001047) != null ? entry.get(700001047).toString() : "");
                tempentry.put(fieldfieldname.get("7"), entry.get(7) != null ? RemedyBase.selectionfieldvalue(status, entry.get(7).toString()) : "");
                //  tempentry.put(fieldfieldname.get("700001528"), entry.get(700001528) != null ? RemedyBase.selectionfieldvalue(prirority, entry.get(700001528).toString()) : "");
                tempentry.put(fieldfieldname.get("8"), entry.get(8) != null ? entry.get(8).toString() : "");
                tempentry.put(fieldfieldname.get("700001052"), entry.get(700001052) != null ? entry.get(700001052).toString() : "");
                tempentry.put(fieldfieldname.get("700001059"), entry.get(700001059) != null ? entry.get(700001059).toString() : "");
                result.add(tempentry);
            }

        } catch (Exception e) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            Map<String, Object> tempentry = new HashMap<>();
            tempentry.put(fieldfieldname.get("1"), "");
            tempentry.put(fieldfieldname.get("3"), "");
            tempentry.put(fieldfieldname.get("700001047"), "");
            tempentry.put(fieldfieldname.get("7"), "");
            tempentry.put(fieldfieldname.get("700001528"), "");
            tempentry.put(fieldfieldname.get("8"), "");
            tempentry.put(fieldfieldname.get("700001052"), "");
            tempentry.put(fieldfieldname.get("700001059"), "");
            result.add(tempentry);
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(path = "/openapi/srwithassignedgroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> srwithassignedgroup(Model model,
                                                         @RequestHeader(value = "username", required = false) String username,
                                                         @RequestHeader(value = "password", required = false) String password,
                                                         @RequestHeader(value = "atoken", required = false) String atoken,
                                                         @RequestParam(value = "client", required = true) String client,
                                                         @RequestParam(value = "fromdate", required = false, defaultValue = "") String fromdate,
                                                         @RequestParam(value = "todate", required = false, defaultValue = "") String todate,
                                                         @RequestParam(value = "assignedgroup", required = false, defaultValue = "CTS-SELF SERVICE") String assignedgroup,
                                                         HttpServletRequest request) {

        List<Map<String, Object>> result = new ArrayList<>();
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        Map<String, String> fieldfieldname = new HashMap<>();

        if (fromdate.isEmpty()) {
            fromdate = date.toString() + " 00:00:00";
        } else {
            fromdate += " 00:00:00";
        }
        if (todate.isEmpty()) {
            todate = date.toString() + " 23:59:59";
        } else {
            todate += " 23:59:59";
        }
        try {

            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            int[] clientfieldid = new int[]{536870913, 536870918, 536870914};

            List<Entry> entrie = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:ClientInfo",
                    clientfieldid,
                    "('Master Client' = \"" + client + "\" OR 'Client'= \"" + client + "\")"
            );
            String masterclient = new String();
            masterclient = "";

            StringBuilder peopleQuery = new StringBuilder("(");
            int i = 0;
            if (!(entrie.size() == 0 || entrie == null)) {
                for (Entry entry : entrie) {

                    if (i == 0) {
                        peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    } else {
                        peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    }
                    ++i;
                }
            } else {
                peopleQuery.append("('Client' = \"" + client + "\")");
            }
            peopleQuery.append(")");


            ARServerUser arServerUser = null;

            arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );
            int[] reqfield = new int[]{
                    1,//	Request #+
                    3,//	Date/Time Created
                    700001047,//	Name+
                    7,//	Status
                    700001528,//	Priority
                    8,//	Summary
                    700001052,//	Assigned Group
                    700001059,//	Assigned Individual
                    200000012,//	Client
                    536871059,//	.Create Date/Time
                    700001053,//	Description
                    700001260,//	Resolution Details
                    2,//	Submitted By
                    620000180,//	Title
                    700001406,//	Date/Time Resolved
            };

            fieldfieldname.put("1", "ticket");
            fieldfieldname.put("3", "createddate");
            fieldfieldname.put("700001047", "name");
            fieldfieldname.put("7", "status");
            fieldfieldname.put("700001528", "priority");
            fieldfieldname.put("8", "summary");
            fieldfieldname.put("700001052", "assignedgroup");
            fieldfieldname.put("700001059", "assignedindividual");
            fieldfieldname.put("200000012", "Client");
            fieldfieldname.put("536871059", "createddatetime");
            fieldfieldname.put("700001053", "description");
            fieldfieldname.put("700001260", "resolutiondetails");
            fieldfieldname.put("2", "submittedby");
            fieldfieldname.put("620000180", "title");
            fieldfieldname.put("700001406", "resolvedate");
            List<Field> fields = arServerUser.getListFieldObjects("SR:ServiceRequest");
            Field status = fields.get(6);
            Field datetime = fields.get(2);
            Field prirority = fields.get(421);

            StringBuilder statusbuilder = new StringBuilder("AND ('Status'=\"Resolved\" OR 'Status' = \"Closed\")");

            statusbuilder = new StringBuilder("AND ('Status'=\"Resolved\")");


            List<Entry> entryList = new ArrayList<>();
            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser1, "SR:ServiceRequest", reqfield, "('Assigned Group'=\"" + assignedgroup + "\")  AND ('Date/Time Resolved' >= \"" + fromdate + "\" AND 'Date/Time Resolved' <= \"" + todate + "\") AND " + peopleQuery.toString() + statusbuilder.toString());


            if (entries.isEmpty()) {
                entries = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(arServerUser1, "SR:ServiceRequest", reqfield, "('Assigned Group'=\"" + assignedgroup + "\")  AND " + peopleQuery.toString() + statusbuilder.toString(), 1, 700001406);
            }

            for (Entry entry : entries) {
                Map<String, Object> tempentry = new HashMap<>();
                tempentry.put(fieldfieldname.get("1"), entry.get(1) != null ? entry.get(1).toString() : "");
                tempentry.put(fieldfieldname.get("3"), entry.get(3) != null ? RemedyBase.dateTimefieldvalue(datetime, entry.get(3)) : "");
                tempentry.put(fieldfieldname.get("700001047"), entry.get(700001047) != null ? entry.get(700001047).toString() : "");
                tempentry.put(fieldfieldname.get("7"), entry.get(7) != null ? RemedyBase.selectionfieldvalue(status, entry.get(7).toString()) : "");
                //  tempentry.put(fieldfieldname.get("700001528"), entry.get(700001528) != null ? RemedyBase.selectionfieldvalue(prirority, entry.get(700001528).toString()) : "");
                tempentry.put(fieldfieldname.get("8"), entry.get(8) != null ? entry.get(8).toString() : "");
                tempentry.put(fieldfieldname.get("700001052"), entry.get(700001052) != null ? entry.get(700001052).toString() : "");
                tempentry.put(fieldfieldname.get("700001059"), entry.get(700001059) != null ? entry.get(700001059).toString() : "");
                tempentry.put(fieldfieldname.get("200000012"), entry.get(200000012) != null ? entry.get(200000012).toString() : "");
                tempentry.put(fieldfieldname.get("536871059"), entry.get(536871059) != null ? RemedyBase.dateTimefieldvalue(datetime, entry.get(536871059)) : "");
                tempentry.put(fieldfieldname.get("700001053"), entry.get(700001053) != null ? entry.get(700001053).toString() : "");
                tempentry.put(fieldfieldname.get("700001260"), entry.get(700001260) != null ? entry.get(700001260).toString() : "");
                tempentry.put(fieldfieldname.get("2"), entry.get(2) != null ? entry.get(2).toString() : "");
                tempentry.put(fieldfieldname.get("620000180"), entry.get(620000180) != null ? entry.get(620000180).toString() : "");
                tempentry.put(fieldfieldname.get("700001406"), entry.get(700001406) != null ? RemedyBase.dateTimefieldvalue(datetime, entry.get(700001406)) : "");
                result.add(tempentry);
            }

        } catch (Exception e) {
            result = new ArrayList<>();
        }
        if (result.isEmpty()) {
            Map<String, Object> tempentry = new HashMap<>();
            tempentry.put(fieldfieldname.get("1"), "");
            tempentry.put(fieldfieldname.get("3"), "");
            tempentry.put(fieldfieldname.get("700001047"), "");
            tempentry.put(fieldfieldname.get("7"), "");
            tempentry.put(fieldfieldname.get("700001528"), "");
            tempentry.put(fieldfieldname.get("8"), "");
            tempentry.put(fieldfieldname.get("700001052"), "");
            tempentry.put(fieldfieldname.get("700001059"), "");
            tempentry.put(fieldfieldname.get("200000012"), "");
            tempentry.put(fieldfieldname.get("536871059"), "");
            tempentry.put(fieldfieldname.get("700001053"), "");
            tempentry.put(fieldfieldname.get("700001260"), "");
            tempentry.put(fieldfieldname.get("2"), "");
            tempentry.put(fieldfieldname.get("620000180"), "");
            tempentry.put(fieldfieldname.get("700001406"), "");

            result.add(tempentry);
        }
        return result;
    }

    @RequestMapping(path = "/openapi/usersearch", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> userSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            //creteria.replace("'","\\'");

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            LoggerBase.loguserrecords("usersearch", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            return RemedyBase.userSearchsqlquery(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }


    @RequestMapping(path = "/openapi/usersearchwithgrouplist", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<Object, Object>> userSearchwithgrouplist(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            HttpServletRequest request
    ) {
        List<Map<Object, Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            //creteria.replace("'","\\'");

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("usersearchwithgrouplist", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            return RemedyBase.userSearchsqlquerywithgrouplist(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );
        } catch (Exception e) {
            resultMap.add(RemedyBase.returnErrorobjectobject(e.getLocalizedMessage()));
        }
        return resultMap;
    }


    @RequestMapping(path = "/openapi/usersearchwithcssweb", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> userSearchforctssarf(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            //creteria.replace("'","\\'");

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("usersearchwithcssweb", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            return RemedyBase.userSearchsqlquerywithcssweb(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }

    @RequestMapping(path = "/openapi/barnbackupapproverwithloginid", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> barnapprovalsearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            int reqfd[] = {
                    910000325,//	Backup Approver ID
            };
            String pattern = "MM-dd-yyyy";
            String truee = "true";
            String falsee = "false";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            String todaysdate = simpleDateFormat.format(new Date());

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser1, "CMN:People Information", reqfd, "'Status'=\"Inactive\" AND 'Return On:' != $NULL$ AND 'Return On:' >= \"" + todaysdate + "\" AND 'Login ID' = \"" + creteria + "\"");

            if (entries != null && !entries.isEmpty()) {
                creteria = entries.get(0).get(910000325).toString();
            }

            return RemedyBase.userSearchsqlquery(
                    arServerUser,
                    client,
                    creteria,
                    falsee,
                    falsee,
                    falsee,
                    falsee,
                    falsee,
                    falsee,
                    truee,
                    falsee
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }


    @RequestMapping(path = "/openapi/usersearchwithstatus", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> userSearchwithstatus(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("usersearchwithstatus", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);

            return RemedyBase.userSearchwithstatussqlquery(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber,
                    status
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }


    @RequestMapping(path = "/openapi/ctcausersearch", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> ctcauserSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname, HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("ctcausersearch", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            return RemedyBase.ctcauserSearch(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }

    @RequestMapping(path = "/openapi/hfmusersearch", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> hfmuserSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        List<Map<String, ? extends Object>> resultMap1 = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("hfmusersearch", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            resultMap = RemedyBase.userSearchsqlquery(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );

            int[] reqfield = new int[]{536870915//	Type
            };
            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser, "CTSSP:DynamicLists", reqfield, "'Dynamic List'=\"HFM SARF APPROVERS\" AND 'Client'=\"HFM\" AND 'Status'=\"Active\"");


            resultMap.forEach(stringMap -> {
                for (Entry entry : entries) {
                    if (entry.get(536870915).toString() != null && stringMap.get("full_name") != null) {
                        if (entry.get(536870915).toString().equalsIgnoreCase(stringMap.get("full_name").toString() == null ? "" : stringMap.get("full_name").toString())) {
                            resultMap1.add(stringMap);
                        }
                    }
                }
            });


        } catch (Exception e) {
            resultMap1.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap1;
    }

    @RequestMapping(path = "/openapi/jmhsusersearch1", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> userSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "true") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "true") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "true") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            @RequestParam(value = "condition", required = false, defaultValue = "true") String condition,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        List<Map<String, ? extends Object>> resultMap1 = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("jmhsusersearch1", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            resultMap = RemedyBase.userSearchsqlquery(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid, empnumber
            );

            resultMap.forEach(stringMap -> {
                if (stringMap.get("Network_Login") != null) {
                    if (stringMap.get("Network_Login").toString().toLowerCase().startsWith(condition.toLowerCase())) {
                        resultMap1.add(stringMap);
                    }

                }

            });


        } catch (Exception e) {
            resultMap1.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap1;
    }


  /*  @RequestMapping(path = "/openapi/procurement", method = {RequestMethod.GET, RequestMethod.POST})
    public void procurement(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String currentClient
    ) {
        int refprocurementitems[] = new int[]{
                4,//Assigned To
                7,//Status
                8,//Category
                15,//Status History
                536870913,//Panel Holder
                536870917,//Equipment
                536870918,//Description
                536870919,//Price
                536870923,//Sort Order
                536870924,//Note
                536870925,//Support User Text
                536870932,//Client
                536870916,//Set Price From Details
                179,//	Unique Identifier

        };
        int refprocurementrelation[] = new int[]{
                4,//Assigned To
                5,//Last Modified By
                6,//Modified Date
                7,//Status
                8,//Short Description
                15,//Status History
                536870916,//Child Unique ID
                536870919,//Procurement Unique Identifier
                536870923,//Attachment
                536870926,//Sort Order
        };

        String FileAtt="NONE";
        String output = "";
        String accessoriesList = "";
        int i=0;

        List<Entry> procurementresult=RemedyBase.queryEntrysByQual(arServerUser1,"CTSSP:Procurement_Items",refprocurementitems,"'Client'=\""+currentClient+"\" AND 'Status'=\"0\"");

        for (Entry entry : procurementresult) {

            accessoriesList="";
            if (i==0) {
                output = "{\"aaData\":[";
            }


            String uniqueid=entry.get(179).toString();

            String[] accessoryTYpe=new String[2];

            accessoryTYpe[0] = "requiredAccessories";
            accessoryTYpe[1] = "optionalAccessories";
            int[] status;
            status = new int[2];
            status[0] = 20; // Required
            status[1] = 10; // Optional

for(int j=0;i<accessoryTYpe.length;i++){
    String currentTypeList="";

*//*RemedyBase.queryEntrysByQual(arServerUser1,"CTSSP:Procurement_Relations",)*//*
}
        }
        *//*return resultMap;*//*
    }


*/

    @Autowired
    FieldRepo fieldRepo;

    @Value("${ctsspi.username}")
    String username;

    @Value("${ctsspi.password}")
    String password;


    @RequestMapping(path = "/openapi/bulkquery", method = {RequestMethod.POST})
    public Map<String, List<Map<String, Object>>> bulkQuery(@RequestBody List<Map<String, String>> requestBody) throws
            Exception {
        List<String> apiEndpoints = new ArrayList<>();

        for (Map<String, String> sp : requestBody) {
            if (sp.get("API").contains("$")) {
                apiEndpoints.add(sp.get("API").substring(sp.get("API").indexOf("$") + 1, sp.get("API").length()));
            } else {
                apiEndpoints.add(sp.get("API"));
            }
        }

        ARServerUser arServerUser = RemedyBase.loginUser(
                serverName,
                port,
                username,
                password
        );
        int i = 0;
        Map<String, List<Map<String, Object>>> res = new HashMap<>();
        for (String endPointDomaini : apiEndpoints) {

            EndPointDomain endPointDomain = endPointRepo.findByEndPointName(endPointDomaini);

            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, String> eParamMap = requestBody.get(i);


            String custom = eParamMap.getOrDefault("custom", "false");
            String sortfield = eParamMap.getOrDefault("sortfield", "");
            String toLowerCase = eParamMap.containsKey("custom") ? eParamMap.get("toLowerCase") : "false";
            String distinct = eParamMap.getOrDefault("distinct", "false");
            Integer noofrecords = eParamMap.containsKey("noofrecords") ? Integer.parseInt(eParamMap.get("noofrecords")) : 0;
            String sortorder = eParamMap.getOrDefault("sortorder", "asc");
            String errormessage = eParamMap.getOrDefault("errormessage", "");


            if (endPointDomain != null) {
                String formName = endPointDomain.getFormName();
                Set<FieldsDomain> selectedFields = fieldRepo.findByFieldsEndpoint(endPointDomain);

                Map<Integer, String> sMap = new HashMap<>();
                int[] rf = new int[selectedFields.size()];
                int c = 0;

                if (toLowerCase.equalsIgnoreCase("true")) {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName().toLowerCase());
                        rf[c++] = s.getFieldId().intValue();
                    }
                } else {
                    for (FieldsDomain s : selectedFields) {
                        sMap.put(s.getFieldId().intValue(), s.getFieldName());
                        rf[c++] = s.getFieldId().intValue();
                    }
                }

                String qstr = endPointDomain.getQualificationString();

                Map<String, String> params = eParamMap;

                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s));
                }
                rValue = StringUtils.replace(rValue, "\"$NULL$\"", "$NULL$");
                List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        rf,
                        rValue, noofrecords
                );

                if (custom.equalsIgnoreCase("true")) {
                    List<Field> fields = arServerUser.getListFieldObjects(formName);
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();
                        for (Integer integer : sMap.keySet()) {
                            Boolean selectionfield = false, datetimefield = false, diaryfield = false;
                            Field field1 = null;
                            if (entry.containsKey(integer)) {
                                sMap.put(integer, sMap.get(integer).replaceAll(" ", "_"));

                                for (Field field : fields) {
                                    if (field.getFieldID() == integer) {
                                        if (field instanceof SelectionField) {
                                            selectionfield = true;
                                            field1 = field;
                                        } else if (field instanceof DateTimeField) {
                                            datetimefield = true;
                                            field1 = field;
                                        } else if (field instanceof DiaryField) {
                                            diaryfield = true;
                                            field1 = field;
                                        }

                                    }
                                }
                                if (entry.get(integer).toString() == null) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), "");
                                } else if (entry.get(integer).toString() != null && selectionfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.selectionfieldvalue(field1, entry.get(integer).toString()));
                                } else if (entry.get(integer).toString() != null && datetimefield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.dateTimefieldvalue(field1, entry.get(integer)));
                                } else if (entry.get(integer).toString() != null && diaryfield == true) {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.diaryfieldValue(field1, entry.get(integer)));
                                }
                               /* else if (entry.get(integer) != null && entry.get(integer).toString() != null && entry.get(integer).toString().startsWith("[Timestamp=")) {
                                            eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.formatEpocDate(entry.get(integer).toString().substring(entry.get(integer).toString().indexOf("=") + 1, entry.get(integer).toString().indexOf("]"))).toString());
                                        }
                                else if (sMap.get(integer).equalsIgnoreCase("Work_Log")) {
                                    String worklog = entry.get(integer).toString();
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), RemedyBase.parseWorkLog1(worklog));
                                }*/
                                else {
                                    eachobject.put(sMap.get(integer).replaceAll("[^a-zA-Z0-9]", "_"), entry.get(integer).toString());
                                }
                            }
                        }
                        result.add(eachobject);
                    }
                } else {
                    for (Entry entry : entries) {
                        Map<String, Object> eachobject = new HashMap<>();

                        for (Integer integer : sMap.keySet()) {
                            if (entry.containsKey(integer)) {
                                eachobject.put(sMap.get(integer), entry.get(integer).toString());
                            }
                        }

                        result.add(eachobject);
                    }
                }
                List<Map<String, Object>> distinctresult = new ArrayList<>();
                if (sortfield != null && !sortfield.isEmpty()) {
                    Collections.sort(result, new Comparator<Map<String, Object>>() {
                        public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                            if (o1.get(sortfield) instanceof String && o2.get(sortfield) instanceof String)
                                return String.class.cast(o1.get(sortfield)).compareTo(String.class.cast(o2.get(sortfield)));
                            else
                                return 0;
                        }
                    });

                    if (distinct.equalsIgnoreCase("true")) {
                        result.stream().forEach(stringStringMap -> {
                            List result1 = distinctresult.stream()
                                    .filter(stringStringMap1 ->
                                            stringStringMap1.get(sortfield).toString().equalsIgnoreCase(stringStringMap.get(sortfield).toString())
                                    )
                                    .collect(Collectors.toList());
                            if (result1.isEmpty() || result1 == null) {
                                distinctresult.add(stringStringMap);
                            }
                        });
                        result.clear();
                        result = new ArrayList<>(distinctresult);
                    }
                    if (sortorder.equalsIgnoreCase("desc")) {
                        Collections.reverse(result);
                    }
                }

            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Endpoint Not found");
                result.add(error);
            }
            if (!errormessage.isEmpty() && result.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                result.clear();
                error.put("error", errormessage);
                result.add(error);
            }

            res.put(requestBody.get(i).get("API"), result);
            i++;
        }

        return res;
    }


    @RequestMapping(path = "/openapi/jmhsusersearch", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> jmhsuserSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "client", defaultValue = "JMHS") String client,
            @RequestParam(value = "criteria", defaultValue = "") String creteria,
            @RequestParam(value = "mailid", required = false, defaultValue = "false") String mailid,
            @RequestParam(value = "networklogin", required = false, defaultValue = "true") String networklogin,
            @RequestParam(value = "fullname", required = false, defaultValue = "true") String fullname,
            @RequestParam(value = "empnumber", required = false, defaultValue = "false") String empnumber,
            @RequestParam(value = "loginid", required = false, defaultValue = "false") String loginid,
            @RequestParam(value = "partialname", required = false, defaultValue = "true") String partialname,
            @RequestParam(value = "firstname", required = false, defaultValue = "true") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "true") String lastname,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (creteria.isEmpty()) {
                throw new Exception("No data found for the given criteria");
            }
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            LoggerBase.loguserrecords("jmhsusersearch", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);
            return RemedyBase.jmhsuserSearch(
                    arServerUser,
                    client,
                    creteria,
                    mailid,
                    networklogin,
                    fullname,
                    partialname,
                    firstname,
                    lastname,
                    loginid,
                    empnumber
            );

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return resultMap;
    }


    @RequestMapping(path = "/openapi/customusersearch", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, ? extends Object>> customuserSearch(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("client") String client,
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "processcode") String processcode,
            @RequestParam(value = "usertype") String usertype,
            @RequestParam(value = "criteria") String creteria,
            HttpServletRequest request
    ) {

        String generalerrormessage = "";
        List<Map<String, ? extends Object>> resultMap = new ArrayList<>();
        try {
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            int[] reqfield = new int[]{
                    536870929,//Email
                    536870930,//Phone
                    536870931,//Partial Name
                    536870932,//Client
                    536870934,//Network Login ID
                    536870935,//Employee Number
                    536870941,//Name
                    536870972,//Last Name
                    536870982,//	General Error
            };
            StringBuilder userlookupquery = new StringBuilder("");
            userlookupquery.append(client == null ? "" : "('Client'=\"" + client + "\")");
            userlookupquery.append(account == null ? "" : "AND ('Name__c'=\"" + account + "\")");
            userlookupquery.append(usertype == null ? "" : "AND ('User Type'=\"" + usertype + "\")");
            userlookupquery.append(processcode == null ? "" : "AND ('Process Code'=\"" + processcode + "\")");

            String mailid = "true", networklogin = "true", fullname = "true", partialname = "true", firstname = "true", lastname = "true", loginid = "true", empnumber = "true";

            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser, "CTSSP:People_Lookups", reqfield, userlookupquery.toString());

            if (entries.size() > 0 && entries != null) {
                for (Entry entry : entries) {
                    mailid = entry.get(536870929).toString() == null ? "false" : "true";
                    networklogin = entry.get(536870934).toString() == null ? "false" : "true";
                    fullname = entry.get(536870941).toString() == null ? "false" : "true";
                    partialname = entry.get(536870931).toString() == null ? "false" : "true";
                    firstname = entry.get(536870941).toString() == null ? "false" : "true";
                    lastname = entry.get(536870972).toString() == null ? "false" : "true";
                    empnumber = entry.get(536870935).toString() == null ? "false" : "true";
                    generalerrormessage = entry.get(536870982).toString() == null ? "" : entry.get(536870982).toString();
                    /*       loginid=entry.get(536870929).toString()==null ? "false" : "true";*/
                    break;
                }

                resultMap = RemedyBase.userSearch(
                        arServerUser,
                        client,
                        creteria,
                        mailid,
                        networklogin,
                        fullname,
                        partialname,
                        firstname,
                        lastname,
                        loginid,
                        empnumber
                );

                if (resultMap.size() == 1) {
                    if (resultMap.get(0).containsKey("error") && !generalerrormessage.isEmpty()) {
                        resultMap.clear();
                        resultMap.add(RemedyBase.returnError(creteria + " " + generalerrormessage));
                    }
                }
                LoggerBase.loguserrecords("customusersearch", arServerUser, "CMN:People Information", com.htc.remedy.constants.Constants.FETCH, request);

            } else {
                resultMap.add(RemedyBase.returnError("No user lookup criteria set for this process"));
            }

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError("No data found for the given criteria"));
        }

        return resultMap;
    }


    @RequestMapping(path = "/openapi/surveyquery", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, String>> surveyquery(@RequestHeader(value = "username", required = false) String
                                                         username,
                                                 @RequestHeader(value = "password", required = false) String password,
                                                 @RequestHeader(value = "atoken", required = false) String atoken,
                                                 @RequestParam("formName") String formName,
                                                 @RequestParam("query") String query) {
        List<Map<String, String>> resultMap = new ArrayList<>();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            int[] ref = new int[]{


                    700000830, // public string FormId { get; set; } //

                    700001059,//  public string AssignedTo { get; set; } //

                    7,//  public string StatusId { get; set; } //

                    2,//   public int CreatedBy { get; set; } //2

                    3,//public DateTime? CreatedOn { get; set; } //3

                    5,//public int? ModifiedBy { get; set; } //5

                    6,//public DateTime? ModifiedOn { get; set; } //6

                    700001534, //public string RequestedFullName { get; set; } //700001534

                    700001012,  //public string RequestedEmail { get; set; } //700001012

                    700001039,// public string RequestedLoginId { get; set; } //700001039

                    536870938,//    public string ApproverFullName { get; set; } //536870938

                    536870922,//    public string ApproverLoginId { get; set; } //536870922

                    1, //  public string TicketNumber { get; set; } //1
                    700007501,//formbuilderid
                    //  public string FBTicketNumber { get; set; }
                    // ProcessSubmissionData_Sk { get; set; }
                    //    public string ApproverEmail { get; set; }
                    //public string RequesterFullName { get; set; }

                    //public string RequesterEmail { get; set; }

                    //public string RequesterLoginId { get; set; }
            };

            List<Entry> enty = RemedyBase.queryEntrysByQual(arServerUser, formName, ref, query);

            for (Entry entry : enty) {
                HashMap<String, String> entryObject = new HashMap<>();
                entryObject.put("FormId", entry.get(ref[0]).toString());
                entryObject.put("AssignedTo", entry.get(ref[1]).toString());
                entryObject.put("StatusId", entry.get(ref[2]).toString());
                entryObject.put("CreatedBy", entry.get(ref[3]).toString());
                entryObject.put("CreatedOn", entry.get(ref[4]).toString());
                entryObject.put("ModifiedBy", entry.get(ref[5]).toString());
                entryObject.put("ModifiedOn", entry.get(ref[6]).toString());
                entryObject.put("RequestedFullName", entry.get(ref[7]).toString());
                entryObject.put("RequestedEmail", entry.get(ref[8]).toString());
                entryObject.put("RequestedLoginId", entry.get(ref[9]).toString());
                entryObject.put("ApproverFullName", entry.get(ref[10]).toString());
                entryObject.put("ApproverLoginId", entry.get(ref[11]).toString());
                entryObject.put("TicketNumber", entry.get(ref[12]).toString());
                entryObject.put("ProcessId", entry.get(ref[13]).toString());
                // entryObject.put("TicketNumber", entry.get(ref[12]).toString());
                // entryObject.put("TicketNumber", entry.get(ref[12]).toString());
                resultMap.add(entryObject);
            }

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }


        return resultMap;
    }

    class QB {
        String formName;
        String query;
        Integer noofrecords;
        List<Integer> fields;

//        public QB() {
//            this.noofrecords = 0;
//        }

        public QB(String formName, String query, Integer noofrecords, List<Integer> fields) {
            this.formName = formName;
            this.query = query;
            this.noofrecords = noofrecords;
            this.fields = fields;
        }

        public String getFormName() {
            return formName;
        }

        public void setFormName(String formName) {
            this.formName = formName;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public Integer getNoofrecords() {
            return noofrecords;
        }

        public void setNoofrecords(Integer noofrecords) {
            this.noofrecords = noofrecords;
        }

        public List<Integer> getFields() {
            return fields;
        }

        public void setFields(List<Integer> fields) {
            this.fields = fields;
        }
    }

    @RequestMapping(value = "/openapi/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String queryTickets(@RequestHeader(value = "username", required = false) String username,
                               @RequestHeader(value = "password", required = false) String password,
                               @RequestHeader(value = "atoken", required = false) String atoken,
                               //@RequestParam Map<String,Object> qb
                               @RequestParam(value = "formName") String formName,
                               @RequestParam(value = "query") String query,
                               @RequestParam(value = "fields", required = false) List<Integer> fields,
                               @RequestParam(value = "noofrecords", required = false, defaultValue = "0") Integer noofrecords
    ) {

        JsonArray resultMap = new JsonArray();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            List<Entry> enty = new ArrayList<>();
            if (fields != null && fields.size() > 0) {

                int[] f = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    f[i] = fields.get(i);
                }
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        f,
                        query, noofrecords
                );
            } else {
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        null,
                        query, noofrecords
                );
            }


            Map<Integer, String> formFields = RemedyBase.getFormFields2(arServerUser, formName);

            for (Entry e : enty) {
                JsonObject entryObject = new JsonObject();

                for (Integer keys : e.keySet()) {
                    entryObject.addProperty(formFields.get(keys), e.get(keys).toString());
                }
                resultMap.add(entryObject);
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }

        return resultMap.toString();
    }


    @RequestMapping(value = "/openapi/srwithstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String queryTickets(@RequestHeader(value = "username", required = false) String username) {
        String a = "";

        List<Entry> entry = RemedyBase.queryEntrysByQual(arServerUser1, "SR:ServiceRequest", null, "('Status'=\"Acknowledged\" AND 'Login ID'=" + username + ")");


        return entry.toString();
    }

    @RequestMapping(value = "/openapi/calendarquery", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String queryCalendarTickets(@RequestHeader(value = "username", required = false) String username,
                                       @RequestHeader(value = "password", required = false) String password,
                                       @RequestHeader(value = "atoken", required = false) String atoken,
                                       //@RequestParam Map<String,Object> qb
                                       @RequestParam(value = "formName") String formName,
                                       @RequestParam(value = "query") String query,
                                       @RequestParam(value = "fields", required = false) String stringFields,
                                       @RequestParam(value = "noofrecords", required = false, defaultValue = "0") Integer noofrecords
    ) {
        JsonArray resultMap = new JsonArray();
        int x = 0, y = 0;
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            List<Integer> fields = null;
            if (stringFields != null && !stringFields.isEmpty()) {
                fields = new ArrayList<>();
                for (String s : stringFields.split(",")) {
                    fields.add(Integer.parseInt(s));
                }
            }
            List<Entry> enty = new ArrayList<>();
            if (fields != null && fields.size() > 0) {

                int[] f = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    f[i] = fields.get(i);
                }
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        f,
                        query, noofrecords
                );
            } else {
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        null,
                        query, noofrecords
                );
            }


            Map<Integer, Field> formFields = RemedyBase.getFormFieldsObject(arServerUser, formName);

            for (Entry e : enty) {
                JsonObject entryObject = new JsonObject();
                x++;


                for (Integer keys : e.keySet()) {
                    y = keys;
                    if (formFields.get(keys) != null && formFields.get(keys) instanceof SelectionField) {
                        String selectionFieldValue = "";
                        if (e.get(keys).getValue() != null) {
                            selectionFieldValue = RemedyBase.selectionfieldvalue(formFields.get(keys), e.get(keys).getValue().toString());
                        }
                        entryObject.addProperty(formFields.get(keys).getName(), selectionFieldValue);
                    } else if (formFields.get(keys) instanceof DateTimeField) {
                        String timestamp = "";
                        if (e.get(keys).getValue() != null) {
                            timestamp = ((Timestamp) e.get(keys).getValue()).toDate().toString();
                        }
                        entryObject.addProperty(formFields.get(keys).getName(), timestamp);
                    } else {
                        entryObject.addProperty(formFields.get(keys).getName(), e.get(keys).toString());
                    }
                }
                resultMap.add(entryObject);
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }
        return resultMap.toString();
    }


    @RequestMapping(value = "/openapi/calendarquerywithstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String queryCalendarTicketswithstatus(@RequestHeader(value = "username", required = false) String
                                                         username,
                                                 @RequestHeader(value = "password", required = false) String password,
                                                 @RequestHeader(value = "atoken", required = false) String atoken,
                                                 //@RequestParam Map<String,Object> qb
                                                 @RequestParam(value = "formName") String formName,
                                                 @RequestParam(value = "query") String query,
                                                 @RequestParam(value = "fields", required = false) String stringFields,
                                                 @RequestParam(value = "noofrecords", required = false, defaultValue = "0") Integer noofrecords
    ) {

        JsonArray resultMap = new JsonArray();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            List<Integer> fields = null;
            if (stringFields != null && !stringFields.isEmpty()) {
                fields = new ArrayList<>();
                for (String s : stringFields.split(",")) {
                    fields.add(Integer.parseInt(s));
                }
            }


            StringBuilder stringbuilder = new StringBuilder("");
            stringbuilder.append(query);
            stringbuilder.append(" AND ('Status of Change' > \"Incomplete\" AND 'Status of Change' < \"Complete\")");


            List<Entry> enty = new ArrayList<>();
            if (fields != null && fields.size() > 0) {

                int[] f = new int[fields.size()];
                for (int i = 0; i < fields.size(); i++) {
                    f[i] = fields.get(i);
                }
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        f,
                        stringbuilder.toString(), noofrecords
                );
            } else {
                enty = RemedyBase.queryEntrysByQualWithNoOfRecords(
                        arServerUser,
                        formName,
                        null,
                        stringbuilder.toString(), noofrecords
                );
            }


            Map<Integer, Field> formFields = RemedyBase.getFormFieldsObject(arServerUser, formName);

            for (Entry e : enty) {
                JsonObject entryObject = new JsonObject();

                for (Integer keys : e.keySet()) {

                    if (formFields.get(keys) instanceof DateTimeField) {
                        String timestamp = "";
                        if (e.get(keys).getValue() != null) {
                            timestamp = ((Timestamp) e.get(keys).getValue()).toDate().toString();
                        }
                        entryObject.addProperty(formFields.get(keys).getName(), timestamp);
                    } else {
                        entryObject.addProperty(formFields.get(keys).getName(), e.get(keys).toString());
                    }
                }
                resultMap.add(entryObject);
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }

        return resultMap.toString();
    }


    @RequestMapping(value = "/openapi/querylookup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String querylookup(@RequestHeader(value = "username", required = false) String username,
                              @RequestHeader(value = "password", required = false) String password,
                              @RequestHeader(value = "atoken", required = false) String atoken,
                              @RequestParam(value = "query") String query
    ) {

        JsonArray resultMap = new JsonArray();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            int[] reqfield = new int[]{
                    1,//Request ID
                    2,//Submitter
                    3,//Create Date
                    4,//Assigned To
                    5,//Last Modified By
                    6,//Modified Date
                    7,//Status
                    8,//Short Description
                    15,//Status History
                    536870913,//Mapping
                    536870914,//Allow Edit
                    536870915,//Lookup Button
                    536870916,//Process_Pannel
                    536870917,//CTSSP Process
                    536870918,//Multi Must be in Remedy
                    536870922,//Lookup Button Text
                    536870923,//Lookup Button Icon
                    536870929,//Email
                    536870930,//Phone
                    536870931,//Partial Name
                    536870932,//Client
                    536870933,//Name__c
                    536870934,//Network Login ID
                    536870935,//Employee Number
                    536870936,//Text
                    536870937,//Box4
                    536870938,//Employee Number Error
                    536870939,//Process Code
                    536870940,//Process Name
                    536870941,//Name
                    536870942,//Name Error
                    536870943,//Partial Name Error
                    536870944,//temp_accID
                    536870945,//temp ProcessName
                    536870946,//Callback
                    536870947,//Validate
                    536870948,//Must be in Remedy
                    536870949,//New User Button
                    536870950,//New User Button Icon
                    536870951,//New User Button Text
                    536870952,//Widget Header
                    536870953,//Same As Button
                    536870954,//Same As Button Icon
                    536870955,//Same As Button Text
                    536870956,//Multi User Button
                    536870957,//Multi User Button Icon
                    536870958,//Multi User Button Text
                    536870959,//Existing User Button
                    536870960,//Existing User Button Icon
                    536870961,//Existing User Button Text
                    536870962,//New User Callback
                    536870963,//Same As Callback
                    536870964,//Multi User Callback
                    536870965,//Existing User Callback
                    536870966,//Undo Callback
                    536870967,//Requested User Type
                    536870968,//Label
                    536870969,//Box
                    536870970,//Text2
                    536870971,//Box2
                    536870972,//Last Name
                    536870973,//Last Name Error
                    536870974,//Custom
                    536870975,//Custom Error
                    536870976,//Custom Parameters
                    536870977,//Sort New User
                    536870978,//Sort Same As
                    536870979,//Sort Multi User
                    536870980,//Sort Existing
                    536870981,//Default Requested
                    536870982,//General Error
                    536870983,//Email Error
                    536870985,//Network Login ID Error
                    536870987,//Phone Error

            };
            List<Entry> enty = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    "CTSSP:People_Lookups",
                    null,
                    query
            );

            Map<Integer, String> formFields = RemedyBase.getFormFields2(arServerUser, "CTSSP:People_Lookups");

            for (Entry e : enty) {
                JsonObject entryObject = new JsonObject();

                for (Integer keys : e.keySet()) {
                    entryObject.addProperty(formFields.get(keys), e.get(keys).toString());
                }
                resultMap.add(entryObject);
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }

        return resultMap.toString();
    }

    /*remedy webservices*/

    @RequestMapping(
            path = "/openapi/formfields", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getFields(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName) {
        List<Map<String, Object>> fieldResult = new ArrayList<>();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            return RemedyBase.getFormFields1(arServerUser, formName);
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("error", e.getLocalizedMessage());
            fieldResult.add(errorMap);
        }

        return fieldResult;

    }


    @RequestMapping(value = "/openapi/forms", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> allGroups(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName
    ) {


        // SecurityContextHolder.getContext().getAuthentication()
        List<Map<String, Object>> remedyGroup = new ArrayList<>();

        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            OutputInteger nMatches = new OutputInteger();
            List<Entry> entryList = arServerUser.getListEntryObjects(
                    formName, null, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    null, null, true, nMatches);

            if (nMatches.intValue() > 0) {
                Map<Integer, String> groupMap = RemedyBase.getFormFields2(arServerUser, formName);
                for (Entry entry1 : entryList) {
                    if (entry1 != null) {
                        Map<String, Object> groupMapSingle = new HashMap<>();
                        for (Integer keyId : entry1.keySet()) {

                            groupMapSingle.put(groupMap.get(keyId), entry1.get(keyId).toString());
                        }
                        remedyGroup.add(groupMapSingle);
                    }
                }

            }
        } catch (Exception e) {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("error", e.getLocalizedMessage());
            remedyGroup.add(errorMap);
        }

        return remedyGroup;
    }


    @RequestMapping(path = "/openapi/pwd/decrypt", method = {RequestMethod.POST, RequestMethod.GET})
    private List<Map<String, String>> decrypt_data(@RequestParam(value = "password") String encData)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String key = key1;
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] original = cipher
                .doFinal(Base64.getDecoder().decode(encData.getBytes()));

        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("password", new String(original).trim());

        List<Map<String, String>> mapList = new ArrayList<>();
        mapList.add(returnMap);
        return mapList;
    }

    @RequestMapping(path = "/openapi/pwd/encrypt", method = {RequestMethod.POST, RequestMethod.GET})
    private List<Map<String, String>> encrypt_data(@RequestParam(value = "password") String data)
            throws Exception {
        String key = key1;
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] original = Base64.getEncoder().encode(cipher.doFinal(data.getBytes()));

        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("password", new String(original));

        List<Map<String, String>> mapList = new ArrayList<>();
        mapList.add(returnMap);
        return mapList;
    }


   /* @RequestMapping(path = "/openapi/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> login(
            @RequestHeader(value = "username") String username,
            @RequestHeader(value = "password") String password,
            @RequestHeader(value = "client") String client

         *//*   HttpServletResponse httpServletResponse,
            HttpStatus httpStatus*//*
    ) {
        Map<String, String> userdetails = new HashMap<>();
        try {

            String identifyingAttribute = "samaccountname";
            List<LdapTemplate> ldapTemplate = ldapTemplateMap.get(client);
            int[] rf = new int[]{
                    8, //fullname 0
                    200000006, //dept 1
                    200000007, //building 2
                    700001012, //email 3
                    700001038, //empno 4
                    910000100, //floor 5
                    910000101, //suite 6
                    910000102, //office 7
                    910000103, //busorg 8
                    910000104, //ph work 9
                    700001084, //fname 10
                    700001082, //lname 11
                    700001039, //loginid 12
                    910000105,//	Phone Ext 13
                    179,//	GUID 14
                    700001132,//	Designation 15
                    910000106,//	Fax 16
                    700001022,//	VIP 17
                    620000180,//	Title 18
                    536870949,//	Network Login 19
                    910000350,//	Queue 20
                    700001132,//	Designation 21
                    200000012,//	Client 22
                    700001083,//middle Initial 23
                    910000416,//cost code
                    910000415,//Role
                    910000414,//Role Prefix
                    704000051//support person
            };
            String searchFilter = "(&(!(userAccountControl:1.2.840.113556.1.4.803:=2))(" + identifyingAttribute + "=" + username + "))";


            int isAuthenticated = 0;
            for (LdapTemplate template : ldapTemplate) {
                if (template.authenticate("", searchFilter, password)) {

                    List<Entry> entry = RemedyBase.queryEntrysByQual
                            (arServerUser1, "CMN:People Information", rf, "('Client'=\"" + client + "\") AND ('Network Login'=\"" + username + "\") AND ('Status'= \"Active\")");


                    if (entry != null && !entry.isEmpty()) {
                        Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser1, "CMN:People Information");

                        for (Entry entry1 : entry) {
                            entry1.keySet().stream().forEach(keyId -> {
                                userdetails.put(userMap.get(keyId), entry1.get(keyId).toString());
                            });
                            break;
                        }
                    } else {
                        isAuthenticated = 3;
                        break;

                    }
                } else {
                    isAuthenticated = 2;
                    break;

                }
            }

            if (isAuthenticated == 2) {
                throw new BadCredentialsException("Authentication Failed.");
            }
            if (isAuthenticated == 3) {
                throw new BadCredentialsException("You have been successfully authenticated but a matching profile was not found. Please call the Service Desk to either have your network login added to your existing profile or have a new profile created for you.");
            }


        } catch (Exception e) {
            userdetails.put("Exception", e.getMessage());
        }

        return userdetails;
    }*/


    @RequestMapping(path = "/openapi/createticket", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createServiceRequest(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody Map<String, String> ticketFields,
            HttpServletRequest request
         /*   HttpServletResponse httpServletResponse,
            HttpStatus httpStatus*/
    ) {
        Map<String, String> srResult = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        /*        httpServletResponse=RemedyBase.successresponse();*/

        try {
            Map<String, String> inputMap = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            Map<String, Object> fields = RemedyBase.getFormFields(arServerUser, formName);
            Entry entryData = new Entry();
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                Integer FieldID = getFieldID(fields, entry.getKey());
                if (FieldID != null && entry.getValue().isEmpty() == false) {
                    entryData.put(FieldID, new com.bmc.arsys.api.Value(entry.getValue()));
                }
            }

            String entityId = RemedyBase.createTicket(arServerUser, formName, entryData);
            srResult.put("EntityId", entityId);
            LoggerBase.loguserrecords(arServerUser, entityId, new Gson().toJson(ticketFields), formName, com.htc.remedy.constants.Constants.INSERT, request);
        } catch (Exception e) {
            //e.printStackTrace();
            /*      httpServletResponse=RemedyBase.errorresponse();*/
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
        }

        return srResult;
    }

    @RequestMapping(path = "/openapi/createformentry", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createFormEntry(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody Map<String, String> ticketFields, HttpServletRequest request) {
        Map<String, String> srResult = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, String> inputMap = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            Map<String, Object> fields = RemedyBase.getFormFields(arServerUser, formName);
            Entry entryData = new Entry();
            for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                Integer FieldID = (Integer) fields.get(entry.getKey().toLowerCase());
                if (FieldID != null && entry.getValue().isEmpty() == false) {
                    entryData.put(FieldID, new com.bmc.arsys.api.Value(entry.getValue()));
                }
            }

            String entityId = RemedyBase.createTicket(arServerUser, formName, entryData);

            Map<String, Object> iId = RemedyBase.getSingleEntryWithFields(arServerUser, formName, entityId, new int[]{179});
            srResult.put("EntityId", entityId);
            if (iId != null && iId.containsKey("instanceId")) {
                srResult.put("InstanceID", iId.get("instanceId").toString());
            }
            LoggerBase.loguserrecords(arServerUser, entityId, new Gson().toJson(ticketFields), formName, com.htc.remedy.constants.Constants.INSERT, request);
        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
        }
        return srResult;
    }


    @RequestMapping(path = "/openapi/mygroupticket", method = RequestMethod.POST)
    @ResponseBody
    public List mygrouppt(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("loginid") String loginid,
            @RequestParam("formname") String formname,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords, HttpServletRequest request
    ) {
        List<Map<String, Object>> resultset = new ArrayList<>();
        Map<String, Object> srResult = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            String assignedgroup = "'Assigned Group'= ";
            StringBuilder builder = new StringBuilder("(");


            List<GroupInfo> groupInfo = new ArrayList<>();
            groupInfo = arServerUser.getListGroup(loginid, null);
            for (int i = 0; i < groupInfo.size(); i++) {
                if (i == groupInfo.size() - 1)
                    builder.append(assignedgroup + "\"" + groupInfo.get(i).getName() + "\"");
                else
                    builder.append(assignedgroup + "\"" + groupInfo.get(i).getName() + "\" OR ");
            }

            builder.append(")");

            int[] reqfield = new int[]{1, 8, 3, 7};
            List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecords(
                    arServerUser,
                    formname,
                    reqfield,
                    builder.toString() + " AND ('Status' =\"Assigned\" OR 'Status' =\"Acknowledged\" OR 'Status' =\"Pending\" ) ", noofrecords
            );

            return RemedyBase.remedyresultset(arServerUser, formname, entries, request);


        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
            resultset.add(srResult);
        }
        return resultset;
    }


    @RequestMapping(path = "/openapi/ticketsummary1", method = RequestMethod.POST)
    @ResponseBody
    public Map ticketsummary(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("loginid") String loginid
    ) {
        Map<String, Map> resultset = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            String assignedgroup = "'Assigned Group'= ";
            StringBuilder builder = new StringBuilder("");

            List<GroupInfo> groupInfo = new ArrayList<>();
            try {
                groupInfo = arServerUser.getListGroup(loginid, null);
            } catch (Exception e) {
                groupInfo = new ArrayList<>();
            }
            for (int i = 0; i < groupInfo.size(); i++) {
                if (i == groupInfo.size() - 1)
                    builder.append(assignedgroup + "\"" + groupInfo.get(i).getName() + "\" )");
                else if (i == 0)
                    builder.append("(");
                else
                    builder.append(assignedgroup + "\"" + groupInfo.get(i).getName() + "\" OR ");
            }


            String[] srptticketstatus = {"Assigned", "Acknowledged", "Pending"};
            String[] crticketstatus = {"Requested", "Reviewing", "Approved", "Scheduled"};

            Map<String, String> forms = new HashMap<>();
            forms.put("SR:ServiceRequest", "srs");
            forms.put("PT:ProblemTicket", "pts");

            Map<String, String> crformnames = new HashMap<>();
            crformnames.put("CM-ChangeRequest", "crs");


            for (Map.Entry<String, String> formkeyvalue : forms.entrySet()) {
                Map<String, Map> tempresultset = new HashMap<>();
                Map<String, String> result = new HashMap<>();
                String query = "";
                String loginquery = "\"" + loginid + "\"";
                for (String s : srptticketstatus) {
                    query = "'Status'=\"" + s + "\" AND 'zAssignLogin'=" + loginquery;
                    result.put(s, RemedyBase.resultcount(arServerUser, formkeyvalue.getKey(), query));
                }
                tempresultset.put("my_" + formkeyvalue.getValue(), result);
                result = new HashMap<>();
                for (String s : srptticketstatus) {
                    query = "'Status'=\"" + s + "\" AND 'Submitter ID'=" + loginquery;
                    result.put(s, RemedyBase.resultcount(arServerUser, formkeyvalue.getKey(), query));
                }
                tempresultset.put(formkeyvalue.getValue() + "_raised_by_me", result);

                if (!builder.toString().isEmpty()) {
                    result = new HashMap<>();
                    for (String s : srptticketstatus) {
                        query = "'Status'=\"" + s + "\"";
                        result.put(s, RemedyBase.resultcount(arServerUser, formkeyvalue.getKey(), builder.toString() + " AND " + query));
                    }
                    tempresultset.put("my_group_" + formkeyvalue.getValue(), result);
                } else {
                    result = new HashMap<>();
                    for (String s : srptticketstatus) {
                        result.put(s, "0");
                    }
                    tempresultset.put("my_group_" + formkeyvalue.getValue(), result);
                }
                resultset.put(formkeyvalue.getValue(), tempresultset);
            }

            for (Map.Entry<String, String> formkeyvalue : crformnames.entrySet()) {
                String query = "";
                Map<String, Map> tempresultset = new HashMap<>();
                Map<String, String> result = new HashMap<>();
                String loginquery = "\"" + loginid + "\"";
                for (String s : crticketstatus) {
                    query = "'Status of Change'=\"" + s + "\" AND 'Submitted By'=" + loginquery;
                    result.put(s, RemedyBase.resultcount(arServerUser, formkeyvalue.getKey(), query));
                }
                tempresultset.put("my_" + formkeyvalue.getValue(), result);
                resultset.put(formkeyvalue.getValue(), tempresultset);
            }

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
        }
        return resultset;
    }


    @RequestMapping(path = "/openapi/createformentrybyid", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createFormEntryByID(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody Map<String, Object> ticketFields, HttpServletRequest request) {
        Map<String, String> srResult = new HashMap<>();


        try {
            Map<String, Object> inputMap = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            Entry entryData = new Entry();
            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }

            String entityId = RemedyBase.createTicket(arServerUser, formName, entryData);

            Map<String, Object> iId = RemedyBase.getSingleEntryWithFields(arServerUser, formName, entityId, new int[]{179});
            srResult.put("EntityId", entityId);
            if (iId != null && iId.containsKey("instanceId")) {
                srResult.put("InstanceID", iId.get("instanceId").toString());
            }
            LoggerBase.loguserrecords(arServerUser, entityId, new Gson().toJson(ticketFields), formName, com.htc.remedy.constants.Constants.INSERT, request);
        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
        }
        return srResult;
    }

    @RequestMapping(path = "/openapi/bulkcreateformentrybyid", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Map<String, String>>> customcreateformentry(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody List<Map<String, Map<String, String>>> updateParams) {

        List<Map<String, Map<String, String>>> response = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            for (Map<String, Map<String, String>> eachupdateParam : updateParams) {
                Map<String, Map<String, String>> eachresult = new HashMap<>();
                Map<String, String> srResult = new HashMap<>();
                try {
                    for (Map.Entry<String, Map<String, String>> updateParam : eachupdateParam.entrySet()) {
                        String formName = null;
                        boolean formstatus = false;
                        try {
                            for (String s : updateParam.getValue().keySet()) {
                                if (s.equals("formName")) {
                                    formName = updateParam.getValue().get(s);
                                    formstatus = true;
                                }
                            }
                            if (formstatus) {
                                updateParam.getValue().remove("formName");
                            }
                            Entry entryData = new Entry();
                            for (Map.Entry<String, String> entry : updateParam.getValue().entrySet()) {
                                if (entry.getValue() != null)
                                    entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
                            }
                            if (formstatus) {
                                String ticketid = RemedyBase.createTicket(user, formName, entryData);
                                srResult.putAll(updateParam.getValue());
                                srResult.put("ticketId", ticketid);
                                eachresult.put(updateParam.getKey(), srResult);
                                response.add(eachresult);
                            } else {
                                throw new Exception("Formname  not available");
                            }
                        } catch (Exception e) {
                            srResult.putAll(updateParam.getValue());
                            srResult.put("ticketId", "");
                            srResult.put("Exception", e.getMessage());
                            eachresult.put(updateParam.getKey(), srResult);
                            response.add(eachresult);
                        }
                    }
                } catch (Exception e) {
                    srResult.put("ticketId", "");
                    srResult.put("Exception", e.getMessage());
                    eachresult.put("Exception", srResult);
                    response.add(eachresult);
                }
            }
        } catch (Exception e) {
            Map<String, Map<String, String>> eachresult = new HashMap<>();
            Map<String, String> srResult = new HashMap<>();
            srResult.put("Exception", e.getMessage());
            srResult.put("ticketId", "");
            eachresult.put("Exception", srResult);
            response.add(eachresult);
        }
        return response;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/bulkupdateticketbyid")
    @ResponseBody
    public List<Map<String, Map<String, String>>> bulkupdateticketbyid(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody List<Map<String, Map<String, String>>> updateParams) {
        List<Map<String, Map<String, String>>> response = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (Map<String, Map<String, String>> eachupdateParam : updateParams) {
                Map<String, Map<String, String>> eachresult = new HashMap<>();
                Map<String, String> srResult = new HashMap<>();

                try {
                    for (Map.Entry<String, Map<String, String>> updateParam : eachupdateParam.entrySet()) {

                        String formName = null, ticketId = null;
                        boolean formstatus = false, ticketIdstatus = false;
                        try {
                            for (String s : updateParam.getValue().keySet()) {
                                if (s.equals("formName")) {
                                    formName = updateParam.getValue().get(s);
                                    formstatus = true;
                                }
                                if (s.equals("ticketId")) {
                                    ticketId = updateParam.getValue().get(s);
                                    ticketIdstatus = true;
                                }
                            }
                            if (formstatus && ticketIdstatus) {
                                updateParam.getValue().remove("formName");
                                updateParam.getValue().remove("ticketId");
                                Map<String, String> params = new HashMap<>();
                                params = updateParam.getValue().entrySet().stream().filter(stringStringEntry -> stringStringEntry.getValue() != null).collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()));
                                srResult.putAll(RemedyBase.customupdateTicketbyid(user, updateParam.getValue(), formName, ticketId));
                                eachresult.put(updateParam.getKey(), srResult);
                                response.add(eachresult);
                            } else {
                                throw new Exception("Formname or Ticket id not available");
                            }
                        } catch (Exception e) {
                            srResult.putAll(updateParam.getValue());
                            srResult.put("Exception", e.getMessage());
                            eachresult.put(updateParam.getKey(), srResult);
                            response.add(eachresult);
                        }
                    }
                } catch (Exception e) {
                    srResult.put("Exception", e.getMessage());
                    response.add(eachresult);
                }
            }
        } catch (Exception e) {
            Map<String, Map<String, String>> eachresult = new HashMap<>();
            Map<String, String> srResult = new HashMap<>();
            srResult.put("Exception", e.getMessage());
            eachresult.put("Exception", srResult);
            response.add(eachresult);
        }
        return response;
    }


    @RequestMapping(path = "/openapi/createkbarticleentry", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createFormEntrybyidkbarticle(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "formName", required = false, defaultValue = "RKM:ReferenceTemplate") String
                    formName,
            @RequestBody Map<String, Object> ticketFields) {
        Map<String, String> srResult = new HashMap<>();


        try {
            Map<String, Object> inputMap = ticketFields;
            Map<String, Object> inputMap1 = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            Entry entryDataforreferncetemplate = new Entry();


            Map<String, Object> formFields = RemedyBase.getFormFields(arServerUser, formName);
            for (String s : ticketFields.keySet()) {

                if (!s.equalsIgnoreCase("status"))
                    if (formFields.containsValue(Integer.parseInt(s))) {
                        entryDataforreferncetemplate.put(Integer.parseInt(s), new com.bmc.arsys.api.Value(ticketFields.get(s).toString()));
                    }
            }
            String entityId = RemedyBase.createTicket(arServerUser, formName, entryDataforreferncetemplate);
            Map<String, Object> iId = RemedyBase.getSingleEntryWithFields(arServerUser, formName, entityId, new int[]{179});




           /* for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                entryDataforreferncetemplate.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
            }
*/
            String knowledgeformName = "RKM:KnowledgeArticleManager";
            formFields = RemedyBase.getFormFields(arServerUser, knowledgeformName);
            List<Entry> entryList = RemedyBase.queryEntrysByQual(arServerUser, knowledgeformName, new int[]{302300507}, "'FK_GUID'=\"" + iId.get("GUID").toString() + "\"");

            entryDataforreferncetemplate = new Entry();
            for (String s : ticketFields.keySet()) {
                if (String.valueOf(302301262).equalsIgnoreCase(s) || String.valueOf(302311142).equalsIgnoreCase(s)) {
                    entryDataforreferncetemplate.put(Integer.parseInt(s), new com.bmc.arsys.api.Value(ticketFields.get(s).toString()));
                } else if (s.equalsIgnoreCase("status")) {
                    entryDataforreferncetemplate.put(7, new com.bmc.arsys.api.Value(ticketFields.get(s).toString()));
                }
            }

            if (!entryDataforreferncetemplate.isEmpty()) {
                entryDataforreferncetemplate.put(302300503, new com.bmc.arsys.api.Value(formName));
                arServerUser.setEntry(
                        knowledgeformName,
                        entryList.get(0).getEntryId(),
                        entryDataforreferncetemplate,
                        null,
                        0
                );
            }

            entryList = RemedyBase.queryEntrysByQual(arServerUser, knowledgeformName, new int[]{302300507, 1, 302301020}, "'FK_GUID'=\"" + iId.get("GUID").toString() + "\"");
            srResult.put("entityid", entryList.get(0).get(302300507).toString());
            srResult.put("requestid", entryList.get(0).get(1).toString());
            srResult.put("fkguid", entryList.get(0).get(302301020).toString());
           /* if (iId != null && iId.containsKey("instanceId")) {
                srResult.put("InstanceID", iId.get("instanceId").toString());
            }*/
        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("entityid", "");
            srResult.put("exception", e.getMessage());
            srResult.put("source", e.getLocalizedMessage());
        }
        return srResult;
    }


    /* @ResponseBody
     @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, produces = {MediaType.APPLICATION_JSON_VALUE}, path = "/openapi/updateticketbyidwithnovalue")
     public Map<String, String> updateticketbyidwithnovalue(
             @RequestHeader(value = "username", required = false) String username,
             @RequestHeader(value = "password", required = false) String password,
             @RequestHeader(value = "atoken", required = false) String atoken,
             @RequestParam("formName") String formName,
             @RequestParam("ticketId") String ticketId,
             @RequestBody Map<String, Object> updateParam) {
         Map<String, String> srResult = new HashMap<>();
         try {
             if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                 username = dusername;
                 password = dpassword;
             } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                 username = username;
                 password = password;
             } else {
                 throw new Exception("Authorization error");
             }
             int reqfields[] = {
                     1,//	Request ID
                     7,//	Status
                     200000012,//	Client
                     200000006,//	Department
                     200000007,//	Building
                     910000100,//	Floor
                     536870949,//	Network Login
                     700001039,//	Login ID
                     700001082,//	Last Name
                     700001084,//	First Name
                     8,//	Full Name
                     700001012,//	Email Address
                     700001038,//	Employee Number
                     910000414,//	Role Prefix
                     910000415,//	Role
                     910000350,//	Queue
                     910000104,//	Phone-Work
             };

             ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
             RemedyBase.updateTicketbyidwithnovalue(user, updateParam, formName, ticketId);
             List<Entry> entries = RemedyBase.queryEntrysByQual(user, formName, reqfields, "'Request ID'=\"" + ticketId + "\"");

             entries.forEach(entry -> {
                 srResult.put("EntityId", ticketId);
                 srResult.put("status", entry.get(7).toString());
                 srResult.put("client", entry.get(200000012).toString());
                 srResult.put("department", entry.get(200000006).toString());
                 srResult.put("building", entry.get(200000007).toString());
                 srResult.put("floor", entry.get(910000100).toString());
                 srResult.put("networklogin", entry.get(536870949).toString());
                 srResult.put("loginid", entry.get(700001039).toString());
                 srResult.put("firstname", entry.get(700001082).toString());
                 srResult.put("lastname", entry.get(700001084).toString());
                 srResult.put("fullname", entry.get(8).toString());
                 srResult.put("employeeaddress", entry.get(700001012).toString());
                 srResult.put("employeenumber", entry.get(700001038).toString());
                 srResult.put("roleprefix", entry.get(910000414).toString());
                 srResult.put("role", entry.get(910000415).toString());
                 srResult.put("queue", entry.get(910000350).toString());
                 srResult.put("phonework", entry.get(910000104).toString());
             });
         } catch (Exception e) {
             Map<String, String> srResult1 = new HashMap<>();
             srResult1.put("EntityId", e.getMessage());
             srResult1.put("status", "");
             srResult1.put("client", "");
             srResult1.put("department", "");
             srResult1.put("building", "");
             srResult1.put("floor", "");
             srResult1.put("networklogin", "");
             srResult1.put("loginid", "");
             srResult1.put("firstname","");
             srResult1.put("lastname", "");
             srResult1.put("fullname", "");
             srResult1.put("employeeaddress", "");
             srResult1.put("employeenumber", "");
             srResult1.put("roleprefix", "");
             srResult1.put("role", "");
             srResult1.put("queue", "");
             srResult1.put("phonework", "");
             return srResult1;
         }
         return srResult;

     }
     */


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, produces = {MediaType.APPLICATION_JSON_VALUE}, path = "/openapi/updateticketbyidwithnovalue")
    public Map<String, String> updateticketbyidwithnovalue(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestBody Map<String, Object> updateParam, HttpServletRequest request) {
        Map<String, String> srResult = new HashMap<>();
        ARServerUser user = new ARServerUser();
        String loginid = "";
        List<Entry> entries1 = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            int reqfields[] = {
                    1,//	Request ID
                    7,//	Status
                    200000012,//	Client
                    200000006,//	Department
                    200000007,//	Building
                    910000100,//	Floor
                    536870949,//	Network Login
                    700001039,//	Login ID
                    700001082,//	Last Name
                    700001084,//	First Name
                    8,//	Full Name
                    700001012,//	Email Address
                    700001038,//	Employee Number
                    910000414,//	Role Prefix
                    910000415,//	Role
                    910000350,//	Queue
                    910000104,//	Phone-Work
            };

            user = RemedyBase.loginUser(serverName, port, username, password);
            entries1 = RemedyBase.queryEntrysByQual(user, formName, reqfields, "'Request ID'=\"" + ticketId + "\"");
            loginid = entries1.get(0).get(700001039).toString();
            RemedyBase.updateTicketbyidwithnovalue(user, updateParam, formName, ticketId);
            List<Entry> entries = RemedyBase.queryEntrysByQual(user, formName, reqfields, "'Request ID'=\"" + ticketId + "\"");
            loginid = entries1.get(0).get(700001039).toString();
            srResult.put("loginid", loginid);
            srResult.put("EntityId", ticketId);
            srResult.put("networklogin", entries.get(0).get(536870949).toString());
            srResult.put("firstname", entries.get(0).get(700001084).toString());
            srResult.put("lastname", entries.get(0).get(700001082).toString());

            LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(srResult), formName, com.htc.remedy.constants.Constants.UPDATE, request);
            RemedyBase.logout(user);
        } catch (Exception e) {
            srResult = new HashMap<>();
            srResult.put("loginid", loginid);
            srResult.put("EntityId", ticketId + " " + e.getMessage());
            srResult.put("networklogin", entries1.get(536870949).toString());
            srResult.put("firstname", entries1.get(700001084).toString());
            srResult.put("lastname", entries1.get(700001082).toString());
        }
        return srResult;

    }

    @RequestMapping(path = "/openapi/createformentrybyidwithnovalue", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createFormEntryByIDWithNoValue(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody Map<String, Object> ticketFields) {
        Map<String, String> srResult = new HashMap<>();
        String loginid = "";
        String networklogin = "";
        String firstname = "";
        String lastname = "";
        try {
            Map<String, Object> inputMap = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }

            int reqfields[] = {
                    1,//	Request ID
                    7,//	Status
                    200000012,//	Client
                    200000006,//	Department
                    200000007,//	Building
                    910000100,//	Floor
                    536870949,//	Network Login
                    700001039,//	Login ID
                    700001082,//	Last Name
                    700001084,//	First Name
                    8,//	Full Name
                    700001012,//	Email Address
                    700001038,//	Employee Number
                    910000414,//	Role Prefix
                    910000415,//	Role
                    910000350,//	Queue
                    910000104,//	Phone-Work
            };
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            Entry entryData = new Entry();
            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().toString().equalsIgnoreCase("NO_VALUE")) {
                    //no value
                } else {
                    entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
                    if (Integer.parseInt(entry.getKey()) == 536870949) {
                        networklogin = entry.getValue().toString();
                    } else if (Integer.parseInt(entry.getKey()) == 700001084) {
                        firstname = entry.getValue().toString();
                    } else if (Integer.parseInt(entry.getKey()) == 700001082) {
                        lastname = entry.getValue().toString();
                    }
                }
            }
            loginid = entryData.get(700001039).toString();
            String entityId = RemedyBase.createTicket(arServerUser, formName, entryData);
            // List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser, formName, reqfields, "'Request ID'=\"" + entityId + "\"");
            srResult.put("loginid", loginid);
            srResult.put("EntityId", entityId);
            srResult.put("networklogin", networklogin);
            srResult.put("firstname", firstname);
            srResult.put("lastname", lastname);
            RemedyBase.logout(arServerUser);
        } catch (Exception e) {
            srResult = new HashMap<>();
            srResult.put("loginid", loginid);
            srResult.put("EntityId", e.getMessage());
            srResult.put("networklogin", networklogin);
            srResult.put("firstname", firstname);
            srResult.put("lastname", lastname);
        }
        return srResult;
    }

/*
 @RequestMapping(path = "/openapi/createformentrybyidwithnovalue", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
    @ApiOperation(
            value = "Creates ticket to the requested form and input value",
            notes = "Retuens ticket id ")
    @ResponseBody
    public Map<String, String> createFormEntryByIDWithNoValue(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody Map<String, Object> ticketFields) {
        Map<String, String> srResult = new HashMap<>();
        try {
            Map<String, Object> inputMap = ticketFields;

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Auth Error");
            }

            int reqfields[] = {
                    1,//	Request ID
                    7,//	Status
                    200000012,//	Client
                    200000006,//	Department
                    200000007,//	Building
                    910000100,//	Floor
                    536870949,//	Network Login
                    700001039,//	Login ID
                    700001082,//	Last Name
                    700001084,//	First Name
                    8,//	Full Name
                    700001012,//	Email Address
                    700001038,//	Employee Number
                    910000414,//	Role Prefix
                    910000415,//	Role
                    910000350,//	Queue
                    910000104,//	Phone-Work
            };
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            Entry entryData = new Entry();
            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().toString().equalsIgnoreCase("NO_VALUE")) {
                    //no value
                } else {
                    entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
                }
            }

            String entityId = RemedyBase.createTicket(arServerUser, formName, entryData);

            List<Entry> entries = RemedyBase.queryEntrysByQual(arServerUser, formName, reqfields, "'Request ID'=\"" + entityId + "\"");

            entries.forEach(entry -> {
                srResult.put("EntityId", entityId);
                srResult.put("status", entry.get(7).toString());
                srResult.put("client", entry.get(200000012).toString());
                srResult.put("department", entry.get(200000006).toString());
                srResult.put("building", entry.get(200000007).toString());
                srResult.put("floor", entry.get(910000100).toString());
                srResult.put("networklogin", entry.get(536870949).toString());
                srResult.put("loginid", entry.get(700001039).toString());
                srResult.put("firstname", entry.get(700001082).toString());
                srResult.put("lastname", entry.get(700001084).toString());
                srResult.put("fullname", entry.get(8).toString());
                srResult.put("employeeaddress", entry.get(700001012).toString());
                srResult.put("employeenumber", entry.get(700001038).toString());
                srResult.put("roleprefix", entry.get(910000414).toString());
                srResult.put("role", entry.get(910000415).toString());
                srResult.put("queue", entry.get(910000350).toString());
                srResult.put("phonework", entry.get(910000104).toString());
            });
            srResult.put("EntityId", entityId);

        } catch (Exception e) {
            //e.printStackTrace();
            Map<String, String> srResult1 = new HashMap<>();
            srResult1.put("EntityId", e.getMessage());
            srResult1.put("status", "");
            srResult1.put("client", "");
            srResult1.put("department", "");
            srResult1.put("building", "");
            srResult1.put("floor", "");
            srResult1.put("networklogin", "");
            srResult1.put("loginid", "");
            srResult1.put("firstname","");
            srResult1.put("lastname", "");
            srResult1.put("fullname", "");
            srResult1.put("employeeaddress", "");
            srResult1.put("employeenumber", "");
            srResult1.put("roleprefix", "");
            srResult1.put("role", "");
            srResult1.put("queue", "");
            srResult1.put("phonework", "");
            return srResult1;
        }
        return srResult;
    }

*/


    @RequestMapping(path = "/openapi/createformmultipleentry", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, String>> createFormMultipleEntry(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestBody List<Map<String, String>> ticketFields) {

        List<Map<String, String>> te = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> srResult = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            Map<String, Object> fields = RemedyBase.getFormFields(arServerUser, formName);
            final ARServerUser finalArServerUser = arServerUser;
            ticketFields.forEach(ticket -> {
                /* Map<String, String> srResult = new HashMap<>();*/
                Map<String, String> inputMap = ticket;

                Entry entryData = new Entry();
                for (Map.Entry<String, String> entry : inputMap.entrySet()) {
                    Integer FieldID = (Integer) fields.get(entry.getKey().toLowerCase());
                    if (FieldID != null && entry.getValue().isEmpty() == false) {
                        entryData.put(FieldID, new com.bmc.arsys.api.Value(entry.getValue()));
                    }
                }

                String entityId = null;
                try {
                    entityId = RemedyBase.createTicket(finalArServerUser, formName, entryData);
                } catch (Exception e) {
                    srResult.put("Result", "");
                    srResult.put("Exception", e.getMessage());
                    srResult.put("Source", e.getLocalizedMessage());
                    te.add(srResult);
                }

                Map<String, Object> iId = RemedyBase.getSingleEntryWithFields(finalArServerUser, formName, entityId, new int[]{179});
                srResult.put("Result", entityId);
                if (iId != null && iId.containsKey("instanceId")) {
                    srResult.put("InstanceID", iId.get("instanceId").toString());
                }

                te.add(srResult);
            });


        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("Result", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
            te.add(srResult);
        }
        return te;
    }


    public Integer getFieldID(Map<String, Object> fields, String fieldName) {
        Integer fieldID = (Integer) fields.get(fieldName.replace("_", " ").trim());
        if (fieldID == null) {
            fieldID = (Integer) fields.get(fieldName.replace("_", "-").trim());
        }

        return fieldID;
    }


    @RequestMapping(value = "/openapi/arprocessexecute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> arProcessExecute(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("command") String command
    ) {

        Map<String, Object> fieldResult = new HashMap<>();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            fieldResult.put("output", arServerUser.executeProcess(command).getOutput());
            fieldResult.put("status", arServerUser.executeProcess(command).getStatus());
        } catch (Exception e) {
            //e.printStackTrace();
            fieldResult.put("message", e.getLocalizedMessage());
        }

        return fieldResult;
    }


    @RequestMapping(path = "/openapi/loginDetails", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public Map<String, Object> loginDetails(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "password", required = false) String password
    ) {
        Map<String, Object> loginResults = new HashMap<String, Object>();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            loginResults.put("authenticated", "true");
            loginResults.put("user", arServerUser.getUser());
            //loginResults.put("token", "1234567897");


            Entry entry = queryEntrysByQual
                    (arServerUser, "User", "('Login Name' = \"" + arServerUser.getUser() + "\")");

            if (entry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "User");

                for (Integer keyId : entry.keySet()) {
                    loginResults.put(userMap.get(keyId), entry.get(keyId).toString());

                }
            }


            Entry peopleEntry = queryEntrysByQual
                    (arServerUser, "CMN:People Information", "('Login ID' = \"" + arServerUser.getUser() + "\")");

            if (peopleEntry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:People Information");

                for (Integer keyId : peopleEntry.keySet()) {
                    loginResults.put(userMap.get(keyId), peopleEntry.get(keyId).toString());

                }
            }

            List<GroupInfo> groupInfo = arServerUser.getListGroup(arServerUser.getUser(), arServerUser.getPassword());

            loginResults.put("userGroups", groupInfo);
        } catch (Exception e) {
            loginResults.put("authenticated", "false");
            loginResults.put("server", serverName);
            loginResults.put("reason", e.getMessage());

        }

        return loginResults;
    }


    Entry queryEntrysByQual(ARServerUser server, String formName, String qualStr) {
        Entry userEntry = null;
        try {
            // Retrieve the detail info of all fields from the form.
            List<Field> fields =
                    server.getListFieldObjects(formName);
            // Create the search qualifier.
            QualifierInfo qual = server.parseQualification(qualStr,
                    fields, null, Constants.AR_QUALCONTEXT_DEFAULT);

            int[] fieldIds = {8, 101, 103};
            OutputInteger nMatches = new OutputInteger();
            List<SortInfo> sortOrder = new ArrayList<SortInfo>();
            sortOrder.add(new SortInfo(2,
                    Constants.AR_SORT_DESCENDING));
            // Retrieve entries from the form using the given
            // qualification.
            List<Entry> entryList = server.getListEntryObjects(
                    formName, qual, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    sortOrder, null, true, nMatches);

            System.out.println("Query returned " + nMatches +
                    " matches.");
            if (nMatches.intValue() == 1) {
                userEntry = entryList.get(0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return userEntry;
    }


    @RequestMapping(value = "/openapi/allForms", method = RequestMethod.GET)
    @ResponseBody
    public List<FormModel> allForms(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "password", required = false) String password
    ) {


        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);


            return RemedyBase.getAllForms(arServerUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/openapi/ping", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> callSomething() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "pong");


        return map;
    }


    @RequestMapping(value = "/openapi/formq/{form}")
    public BaseModel queryObject(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @PathVariable(name = "form") String formName) {

        BaseModel baseModel = null;

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            OutputInteger nMatches = new OutputInteger();
            List<Entry> entryList = arServerUser.getListEntryObjects(
                    formName, null, 0,
                    Constants.AR_NO_MAX_LIST_RETRIEVE,
                    null, null, true, nMatches);

            if (nMatches.intValue() > 0) {
                Map<Integer, String> groupMap = RemedyBase.getFormFields2(arServerUser, formName);
                for (Entry entry1 : entryList) {
                    if (entry1 != null) {
                        Map<String, Object> groupMapSingle = new HashMap<>();
                        for (Integer keyId : entry1.keySet()) {

                            groupMapSingle.put(groupMap.get(keyId), entry1.get(keyId).toString());
                        }
                        // remedyGroup.add(groupMapSingle);
                    }
                }
            }


            JsonElement jsonElement = new JsonArray();

        } catch (Exception e) {
            baseModel = new ErrorModel(e.getMessage());
        }

        return baseModel;

    }


    @RequestMapping(path = "/openapi/myticket", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getTicketsForUser(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("login_name") String user
    ) {


        Map<String, Object> loginResults = new HashMap<String, Object>();

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            int[] fields = null;

            List<Entry> myTickets = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    "SR:ServiceRequest",
                    fields,
                    "('Network Login' = \"" + user + "\")"
            );


            loginResults.put("authenticated", "true");
            loginResults.put("user", arServerUser.getUser());
            //loginResults.put("token", "1234567897");


            Entry entry = queryEntrysByQual
                    (arServerUser, "User", "('Login Name' = \"" + arServerUser.getUser() + "\")");

            if (entry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "User");

                for (Integer keyId : entry.keySet()) {
                    loginResults.put(userMap.get(keyId), entry.get(keyId).toString());

                }
            }


            Entry peopleEntry = queryEntrysByQual
                    (arServerUser, "CMN:People Information", "('Login ID' = \"" + arServerUser.getUser() + "\")");

            if (peopleEntry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:People Information");

                for (Integer keyId : peopleEntry.keySet()) {
                    loginResults.put(userMap.get(keyId), peopleEntry.get(keyId).toString());

                }
            }

            List<GroupInfo> groupInfo = arServerUser.getListGroup(arServerUser.getUser(), arServerUser.getPassword());

            loginResults.put("userGroups", groupInfo);
        } catch (Exception e) {
            loginResults.put("authenticated", "false");
            loginResults.put("server", serverName);
            loginResults.put("reason", e.getMessage());

        }


        return loginResults;
    }


    @RequestMapping(value = "/openapi/filterticket", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> filterTickets(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam Map<String, String> allRequestParams
    ) {

        List<BaseModel> baseModels = new ArrayList<>();
        String ticketid = "";

        if (request.getMethod().equalsIgnoreCase("options")) {
            baseModels.add(new ErrorModel("ok"));
            return baseModels;
        }
        try {
            int[] requiredFields = {1, 7, 8, 3};
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            String qual = "";
            boolean run = false, ticket = false;

            if (allRequestParams.containsKey("email")) {
                run = true;

                qual = "('Email Address' LIKE \"%" + allRequestParams.get("email") + "%\")";
                //search by email
            } else if (allRequestParams.containsKey("submitter")) {
                //search by name
                run = true;

                qual = "('Login ID' LIKE \"%" + allRequestParams.get("submitter") + "%\")";
            } else if (allRequestParams.containsKey("department")) {
                //search by name
                run = true;

                qual = "('Department' LIKE \"%" + allRequestParams.get("department") + "%\")";
            } else if (allRequestParams.containsKey("building")) {
                run = true;

                qual = "('Building' LIKE \"%" + allRequestParams.get("building") + "%\")";
            } else if (allRequestParams.containsKey("name")) {
                run = true;
                qual = "('First Name' LIKE \"%" + allRequestParams.get("name") + "%\") OR ('Last Name' LIKE \"%" + allRequestParams.get("name") + "%\")";
            } else if (allRequestParams.containsKey("ticketid")) {
                run = true;
                ticket = true;
                ticketid = allRequestParams.get("ticketid");
                if (ticketid.startsWith("SR"))
                    qual = "('Request #+' LIKE \"%" + allRequestParams.get("ticketid") + "%\")";
                else if (ticketid.startsWith("PT"))
                    qual = "('Case #+' LIKE \"%" + allRequestParams.get("ticketid") + "%\")";
            } else {
                run = false;
            }

            if (run) {
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if (ticket) {

                    if (ticketid.startsWith("SR")) {
                        List<Entry> sr = RemedyBase.queryEntrysByQual(
                                arServerUser,
                                "SR:ServiceRequest",
                                requiredFields,
                                qual
                        );
                        if (sr.size() > 0) {
                            for (Entry entry1 : sr) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ServiceRequest"
                                ));
                            }
                        }
                    } else if (ticketid.startsWith("PT")) {
                        List<Entry> pt = RemedyBase.queryEntrysByQual(
                                arServerUser,
                                "PT:ProblemTicket",
                                requiredFields,
                                qual
                        );


                        if (pt.size() > 0) {
                            for (Entry entry1 : pt) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ProblemTicket"
                                ));
                            }
                        }

                    }
                }

                if (ticket == false) {
                    List<Entry> sr = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "SR:ServiceRequest",
                            requiredFields,
                            qual
                    );
                    if (sr.size() > 0) {
                        for (Entry entry1 : sr) {
                            String date = entry1.get(requiredFields[3]).toString();
                            baseModels.add(new MiniTicketModel(
                                    entry1.get(requiredFields[0]).toString(),
                                    entry1.get(requiredFields[1]).toString(),
                                    entry1.get(requiredFields[2]).toString(),
                                    RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                    "ServiceRequest"
                            ));
                        }
                    }


                    List<Entry> pt = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "PT:ProblemTicket",
                            requiredFields,
                            qual
                    );


                    if (pt.size() > 0) {
                        for (Entry entry1 : pt) {
                            String date = entry1.get(requiredFields[3]).toString();
                            baseModels.add(new MiniTicketModel(
                                    entry1.get(requiredFields[0]).toString(),
                                    entry1.get(requiredFields[1]).toString(),
                                    entry1.get(requiredFields[2]).toString(),
                                    RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                    "ProblemTicket"
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            baseModels.add(new ErrorModel(e.getLocalizedMessage()));
        }

        return baseModels;
    }


    @RequestMapping(value = "/openapi/ishardwareorsoftware", method = {RequestMethod.GET, RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public String ishardwareorsoftware(@RequestParam("client") String currentClient,
                                       @RequestHeader(value = "username", required = false) String username,
                                       @RequestHeader(value = "password", required = false) String password,
                                       @RequestHeader(value = "atoken", required = false) String atoken
    ) {
        int refprocurementitems[] = new int[]{
                7,//Status
                8,//Category
                536870917,//Equipment
                536870918,//Description
                536870919,//Price
                536870925,//Support User Text
                536870932,//Client
                536870916,//Set Price From Details
                179,//    Unique Identifier
                536870921,//Attachment
                536870927,//	Workflow
        };
        int refprocurementitemrelation[] = new int[]{
                1,//Request ID
                7,//Status
                8,//Category
                536870913,//Join Request ID
                536870914,//Items Request ID
                536870915,//Set Price From Details
                536870916,//Child Unique ID
                536870917,//Equipment
                536870918,//Description
                536870919,//Procurement Unique Identifier
                536870920,//Price
                536870921,//Status2
                536870925,//Support User Text
                536870927,//Workflow
                536870932,//Client

        };
        String output = "";
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            String itemprependdata = "<div class='itemDescriptionText'><strong>";
            String itemmiddledata = "</strong><br />";
            String itempostenddata = "</div>";
            String accesoryimagefilestart = "<img style='float:left;width:75px;margin-right:5px;' src='Clients/images/procurement/";
            String accesoryimagefileend = "'/>";
            String procitemsformname = "CTSSP:Procurement_Items", procuitemsrelation = "CTSSP:Procurement_Items+Relations";

            List<Entry> procurementitems = RemedyBase.queryEntrysByQual(arServerUser1, procitemsformname, refprocurementitems, "'Client'=\"" + currentClient + "\" AND 'Status'=\"Active\"");

            List<Entry> procurementitemrelations = RemedyBase.queryEntrysByQual(arServerUser1, procuitemsrelation, refprocurementitemrelation, "'Client'=\"" + currentClient + "\" AND 'Status2'=\"Active\"");


            String accessoriesList = "";
            String FileAtt = "";
            int procurementcount = 0;

            for (Entry procurementitem : procurementitems) {

                if (procurementcount == 0) {
                    output = "{\"aaData\":[";
                }
                accessoriesList = "";
                String[] accessoryType;
                accessoryType = new String[2];
                accessoryType[0] = "requiredAccessories";
                accessoryType[1] = "optionalAccessories";
                int[] status;
                status = new int[2];
                status[0] = 20; // Required
                status[1] = 10; // Optional

                String unique_identifier = procurementitem.get(179).toString();

                for (int i = 0; i < accessoryType.length; i++) {

                    String currentTypeList = "";

                    int count = 0;

                    List<Entry> procurementitemrelation = new ArrayList<>();
                    for (Entry entry : procurementitemrelations) {
                        if (entry.get(536870919).toString().equalsIgnoreCase(unique_identifier) && Integer.parseInt(entry.get(7).toString()) == status[i]) {
                            procurementitemrelation.add(entry);
                        }
                    }

                    for (Entry entry : procurementitemrelation) {
                        String currentAccessory = "";

                        // If this is the first time through the resultset then start the JSON string properly
                        if (count == 0) {
                            currentAccessory = ",\"" + accessoryType[i] + "\":[\n";
                        }
                        count++;


                        String accessoryUID = entry.get(536870916).toString();//child_unique_id

                        /* List<Entry> procuremenitems = RemedyBase.queryEntrysByQual(arServerUser1, procitemsformname, refprocurementitems, "'Unique Identifier'=\"" + accessoryUID + "\"");*/

                        List<Entry> procuremenitems = new ArrayList<>();
                        procurementitems.forEach(entry1 -> {
                            if (entry1.get(179).toString().equalsIgnoreCase(accessoryUID)) {
                                procuremenitems.add(entry1);
                            }
                        });

                        Entry procuremenitem = procuremenitems.get(0);

                        String entryid = entry.getEntryId();

                        if (procuremenitem.get(536870921).toString() != null) {

                            String attachmentinfo = procuremenitem.get(536870921).toString();
                            String[] sa = attachmentinfo.split(";");

                            String[] AFileName = sa[2].split("[\\\\]");

                            String[] FileExt = AFileName[AFileName.length - 1].split("[.]");

                            String filePath = RemedyBase.escape(AFileName[AFileName.length - 1]);

                            FileAtt = AFileName[AFileName.length - 1];

                        }

                        String accessoryCategory = entry.get(8).toString();
                        String accessoryEquipment = entry.get(536870917).toString();

                        // Accessory question value pair
                        String accessoryWorkflow = "";
                        if (entry.get(536870927).toString() != null) {//workflow
                            accessoryWorkflow = entry.get(536870927).toString();
                        }

                        // Set accessory description
                        String accessoryDescription = "";
                        if (entry.get(536870918).toString() != null) {//description
                            accessoryDescription = entry.get(536870918).toString() + "<br />";
                        }

                        // Set accessory support user text
                        String accessorySupportUserText = "";
                        if (entry.get(536870925).toString() != null) {//Support_User_Text
                            accessorySupportUserText = entry.get(536870925).toString();
                        }

                        String accessoryImageFile;
                        String accessoryPrice = "";

                        if (FileAtt.isEmpty()) {
                            accessoryImageFile = "";
                        } else {
                            accessoryImageFile = "<img style='float:left;width:75px;margin-right:5px;' src='Clients/images/procurement/" + FileAtt + "'/>";
                        }

                        if (entry.get(536870920).toString() != null) {//price
                            accessoryPrice = entry.get(536870920).toString();
                        }

                        currentAccessory += "\n{\"item\": \"" + accessoryImageFile + "<div class='itemDescriptionText'><strong>" + accessoryEquipment + "</strong><br />" + RemedyBase.escapeQuotes(accessoryDescription) + accessoryPrice + "</div>\",\"equipmentUID\":\"" + accessoryUID + "\",\"equipment\":\"" + accessoryEquipment + "\",\"price\":\"" + accessoryPrice + "\",\"category\":\"" + accessoryCategory + "\",\"supporttext\":\"" + accessorySupportUserText + "\",\"workflow\":\"" + accessoryWorkflow + "\"\n}";


                        if (procurementitemrelation.size() == count) {
                            currentAccessory += "\n]\n";
                        } else {
                            currentAccessory += ",";
                        }

                        // Add this current accessory on to the current type list
                        currentTypeList += currentAccessory;
                    }

                    // Add the current type list onto the overall accessory list for the main item we are working on
                    accessoriesList += currentTypeList;

                    // Close the resultset for the accessories

                }

                String entryid = procurementitem.getEntryId();

                if (procurementitem.get(536870921).toString() != null) {//attachment
                    // Create FieldID object adding the fieldID of the attachment field in the constructor
                    String s;
                    String[] sa;
                    String[] AFileName;
                    String[] FileExt;
                    s = procurementitem.get(536870921).toString();
                    sa = s.split(";");
                    AFileName = sa[2].split("[\\\\]");
                    FileExt = AFileName[AFileName.length - 1].split("[.]");

                    String filePath = RemedyBase.escape(AFileName[AFileName.length - 1]);
                    FileAtt = AFileName[AFileName.length - 1];
                }

                // Prepare strings to build JSON
                String category = procurementitem.get(8).toString();//category
                String equipment = procurementitem.get(536870917).toString();//equipment

                // Question value pair
                String workflow = "";
                if (procurementitem.get(536870927).toString() != null) {
                    workflow = procurementitem.get(536870927).toString();
                }

                // Set item description
                String description = "";
                if (procurementitem.get(536870918).toString() != null) {
                    description = procurementitem.get(536870918).toString() + "<br />";
                }
                //String description = "";

                // Set item support user text
                String supportUserText = "";
                if (procurementitem.get(536870925).toString() != null) {
                    supportUserText = procurementitem.get(536870925).toString();
                }

                // Set image attachment string if one exists
                String itemImageFile = "";
                if (FileAtt.equals("")) {
                    itemImageFile = "";
                } else {
                    itemImageFile = "<img style='float:left;width:75px;margin-right:5px;' src='Clients/images/procurement/" + FileAtt + "'/>";
                }

                // Set price if one exists
                String price = "";
                if (procurementitem.get(536870919).toString() != null) {
                    price = procurementitem.get(536870919).toString();
                }

                // Check the Set Price from Details Checkbox
                String priceCheckbox = "";
                if (procurementitem.get(536870916).toString() != null && procurementitem.get(536870916).toString() != "0") {
                    priceCheckbox = procurementitem.get(536870916).toString();
                } else {
                    priceCheckbox = "1";
                }

                output += "\n{\"item\": \"" + itemImageFile + "<div class='itemDescriptionText'><strong>" + equipment + "</strong><br />" + RemedyBase.escapeQuotes(description) + price + "</div>\",\"equipmentUID\":\"" + unique_identifier + "\",\"equipment\":\"" + equipment + "\",\"price\":\"" + price + "\",\"pricecheckbox\":\"" + priceCheckbox + "\",\"category\":\"" + category + "\",\"supporttext\":\"" + supportUserText + "\",\"workflow\":\"" + workflow + "\"" + accessoriesList + "\n}";
                if (procurementcount + 1 == procurementitems.size()) {
                    output += "]}";
                } else {
                    output += ",";
                }
                procurementcount++;
            }
        } catch (Exception e) {
            output = "";
            output += "error:" + e.getMessage();
        }

        return output.toString();
    }


    @RequestMapping(value = "/openapi/customfilterticket", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> customfilterTickets(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "ticketid", required = false, defaultValue = "$NULL$") String ticketid,
            @RequestParam(value = "submitter", required = false, defaultValue = "$NULL$") String submitter,
            @RequestParam(value = "email", required = false, defaultValue = "$NULL$") String email,
            @RequestParam(value = "name", required = false, defaultValue = "$NULL$") String name,
            @RequestParam(value = "building", required = false, defaultValue = "$NULL$") String building,
            @RequestParam(value = "client") String client,
            @RequestParam(value = "department", required = false, defaultValue = "$NULL$") String department,
            @RequestParam(value = "custom", required = false, defaultValue = "all") String custom,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") Integer noofrecords
    ) {

        List<BaseModel> baseModels = new ArrayList<>();
        String errormessage = "";
        try {
            ticketid = ticketid.trim();
            submitter = submitter.trim();
            email = email.trim();
            name = name.trim();
            building = building.trim();
            client = client.trim();
            department = department.trim();
            int[] requiredFields = {1, 7, 8, 536871059};

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            StringBuilder qual = new StringBuilder("");
            boolean run = false, ticket = false;


            if (!email.equalsIgnoreCase("$NULL$")) {
                qual.append("('Email Address' LIKE \"%" + email + "%\")");        //search by email
            }
            if (!submitter.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" OR ('Login ID' = \"" + submitter + "\")");//search by name
                } else {
                    qual.append("('Login ID' = \"" + submitter + "\")");         //search by name
                }
            }
            if (!building.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" OR ('Building' LIKE \"%" + building + "%\")");
                } else {
                    qual.append("('Building' LIKE \"%" + building + "%\")");
                }


            }
            if (!department.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" AND ('Department' LIKE \"%" + department + "%\")");
                } else {
                    qual.append(" ('Department' LIKE \"%" + department + "%\")");
                }
            }

            if (!name.equalsIgnoreCase("$NULL$")) {
                String[] splittedcriteria = name.split(" ");
                int splittedcriterialength = splittedcriteria.length;
                if (qual.length() > 1) {
                    /*qual.append(" OR ('First Name' LIKE \"" + name + "%\") OR ('Last Name' LIKE \"" + name + "%\")");*/
                    if (splittedcriteria.length >= 2) {
                        qual.append(" OR ((('First Name' LIKE \"" + splittedcriteria[0] + "%\") AND ('Last Name' = \"" + splittedcriteria[1] + "\" OR 'Last Name' = \"" + splittedcriteria[splittedcriterialength - 1] + "\")) OR ('Name+' = \"" + String.join(" ", splittedcriteria) + "\"))");
                    } else {
                        qual.append(" OR (('First Name' = $NULL$ ) AND ('Last Name' = $NULL$ ))");
                    }
                } else {
                    /*qual.append("('First Name' LIKE \"" + name + "%\") OR ('Last Name' LIKE \"" + name + "%\")");*/
                    if (splittedcriteria.length >= 2) {
                        qual.append("((('First Name' LIKE \"" + splittedcriteria[0] + "%\") AND ('Last Name' = \"" + splittedcriteria[1] + "\" OR 'Last Name' = \"" + splittedcriteria[splittedcriterialength - 1] + "\")) OR ('Name+' = \"" + String.join(" ", splittedcriteria) + "\"))");
                    } else {
                        qual.append("(('First Name' = $NULL$ ) AND ('Last Name' = $NULL$ ))");
                    }
                }
            }
            StringBuilder srstringbuilder = new StringBuilder("");
            StringBuilder ptstringbuilder = new StringBuilder("");

            srstringbuilder.append(qual);
            ptstringbuilder.append(qual);
            if (!ticketid.equalsIgnoreCase("$NULL$")) {
                ticket = true;

                if (ticketid.length() > 4) {

                    if (custom.equalsIgnoreCase("all")) {
                        if (qual.length() > 1) {
                            srstringbuilder.append(" OR ('Request #+' LIKE \"%" + ticketid + "%\")");
                        } else {
                            srstringbuilder.append("('Request #+' LIKE \"%" + ticketid + "%\")");
                        }
                        if (qual.length() > 1) {
                            ptstringbuilder.append(" OR ('Case #+' LIKE \"%" + ticketid + "%\")");
                        } else {
                            ptstringbuilder.append("('Case #+' LIKE \"%" + ticketid + "%\")");
                        }
                    } else if (custom.equalsIgnoreCase("sr")) {
                        if (srstringbuilder.length() > 1) {
                            srstringbuilder.append(" OR ('Request #+' LIKE \"%" + ticketid + "%\")");
                        } else {
                            srstringbuilder.append("('Request #+' LIKE \"%" + ticketid + "%\")");
                        }
                    } else if (custom.equalsIgnoreCase("PT")) {
                        if (ptstringbuilder.length() > 1) {
                            ptstringbuilder.append(" OR ('Case #+' LIKE \"%" + ticketid + "%\")");
                        } else {
                            ptstringbuilder.append("('Case #+' LIKE \"%" + ticketid + "%\")");
                        }
                    }
                } else {
                    //throw new Exception("Please enter a ticket number with at least 5 digits.");
                }
            }
            StringBuilder clientinfoquery = new StringBuilder("");
            if (!client.equalsIgnoreCase("$NULL$")) {
                int[] clientfieldid = new int[]{536870913};
                if (ptstringbuilder.length() > 1 || srstringbuilder.length() > 1) {

                    List<Entry> entrie = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "CMN:ClientInfo",
                            clientfieldid,
                            "('Master Client' = \"" + client + "\")"
                    );

                    clientinfoquery.append(" AND (");
                    if (!(entrie.size() == 0 || entrie == null)) {
                        int i = 0;
                        for (Entry entry : entrie) {

                            if (i == 0) {
                                clientinfoquery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            } else {
                                clientinfoquery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            }
                            ++i;
                        }
                    } else {
                        clientinfoquery.append("('Client' = \"" + client + "\")");
                    }

                    clientinfoquery.append(")");

                } else {

                    List<Entry> entrie = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "CMN:ClientInfo",
                            clientfieldid,
                            "('Master Client' = \"" + client + "\")"
                    );

                    clientinfoquery.append("(");
                    if (!(entrie.size() == 0 || entrie == null)) {
                        int i = 0;
                        for (Entry entry : entrie) {

                            if (i == 0) {
                                clientinfoquery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            } else {
                                clientinfoquery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            }
                            ++i;
                        }
                    } else {
                        clientinfoquery.append("('Client' = \"" + client + "\")");
                    }
                    clientinfoquery.append(")");
                }
            }
            Boolean srquit = false, ptquit = false;
            if (srstringbuilder.length() != 0) {
                srquit = true;
                srstringbuilder.append(clientinfoquery);

            }
            if (ptstringbuilder.length() != 0) {
                ptquit = true;
                ptstringbuilder.append(clientinfoquery);
            }

            if (true) {
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if (ticket) {
                    if ((custom.equalsIgnoreCase("sr") || custom.equalsIgnoreCase("all")) && srquit) {
                        List<Entry> sr = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(
                                arServerUser,
                                "SR:ServiceRequest",
                                requiredFields,
                                srstringbuilder.toString(), noofrecords, requiredFields[3]
                        );
                        if (sr.size() > 0) {
                            for (Entry entry1 : sr) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ServiceRequest"
                                ));
                            }
                        }
                    }
                    if ((custom.equalsIgnoreCase("PT") || custom.equalsIgnoreCase("all")) && ptquit) {
                        List<Entry> pt = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(
                                arServerUser,
                                "PT:ProblemTicket",
                                requiredFields,
                                ptstringbuilder.toString(), noofrecords, requiredFields[3]
                        );

                        if (pt.size() > 0) {
                            for (Entry entry1 : pt) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ProblemTicket"
                                ));
                            }
                        }
                    }
                }

                if (ticket == false) {
                    if ((custom.equalsIgnoreCase("sr") || custom.equalsIgnoreCase("all")) && srquit) {
                        List<Entry> sr = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(
                                arServerUser,
                                "SR:ServiceRequest",
                                requiredFields,
                                srstringbuilder.toString(), noofrecords, requiredFields[3]
                        );
                        if (sr.size() > 0) {
                            for (Entry entry1 : sr) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ServiceRequest"
                                ));
                            }
                        }
                    }
                    if ((custom.equalsIgnoreCase("pt") || custom.equalsIgnoreCase("all")) && ptquit) {
                        List<Entry> pt = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(
                                arServerUser,
                                "PT:ProblemTicket",
                                requiredFields,
                                ptstringbuilder.toString(), noofrecords, requiredFields[3]
                        );

                        if (pt.size() > 0) {
                            for (Entry entry1 : pt) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ProblemTicket"
                                ));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            baseModels.add(new ErrorModel(e.getLocalizedMessage()));
        }
        return baseModels;
    }
/*
@RequestMapping(value = "/openapi/customfilterticket", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> customfilterTickets(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "ticketid", required = false, defaultValue = "$NULL$") String ticketid,
            @RequestParam(value = "submitter", required = false, defaultValue = "$NULL$") String submitter,
            @RequestParam(value = "email", required = false, defaultValue = "$NULL$") String email,
            @RequestParam(value = "name", required = false, defaultValue = "$NULL$") String name,
            @RequestParam(value = "building", required = false, defaultValue = "$NULL$") String building,
            @RequestParam(value = "client") String client,
            @RequestParam(value = "department", required = false, defaultValue = "$NULL$") String department,
            @RequestParam(value = "custom", required = false, defaultValue = "all") String custom,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") Integer noofrecords
    ) {

        List<BaseModel> baseModels = new ArrayList<>();

        try {
            ticketid = ticketid.trim();
            submitter = submitter.trim();
            email = email.trim();
            name = name.trim();
            building = building.trim();
            client = client.trim();
            department = department.trim();
            int[] requiredFields = {1, 7, 8, 3};

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            StringBuilder qual = new StringBuilder("");
            boolean run = false, ticket = false;


            if (!email.equalsIgnoreCase("$NULL$")) {
                qual.append("('Email Address' LIKE \"%" + email + "%\")");        //search by email
            }
            if (!submitter.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" OR ('Login ID' = \"" + submitter + "\")");//search by name
                } else {
                    qual.append("('Login ID' = \"" + submitter + "\")");         //search by name
                }
            }
            if (!building.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" OR ('Building' LIKE \"%" + building + "%\")");
                } else {
                    qual.append("('Building' LIKE \"%" + building + "%\")");
                }
            }
            if (!department.equalsIgnoreCase("$NULL$")) {
                if (qual.length() > 1) {
                    qual.append(" AND ('Department' LIKE \"%" + department + "%\")");
                } else {
                    qual.append(" ('Department' LIKE \"%" + department + "%\")");
                }
            }

            if (!name.equalsIgnoreCase("$NULL$")) {
                String[] splittedcriteria = name.split(" ");
                if (qual.length() > 1) {
                    */
    /*qual.append(" OR ('First Name' LIKE \"" + name + "%\") OR ('Last Name' LIKE \"" + name + "%\")");*//*

                    if (splittedcriteria.length >= 2) {
                        qual.append(" OR (('First Name' LIKE \"" + splittedcriteria[0] + "%\") AND ('Last Name' = \"" + splittedcriteria[1] + "\"))");
                    } else {
                        qual.append(" OR (('First Name' = $NULL$ ) AND ('Last Name' = $NULL$ ))");
                    }
                } else {
                    */
    /*qual.append("('First Name' LIKE \"" + name + "%\") OR ('Last Name' LIKE \"" + name + "%\")");*//*

                    if (splittedcriteria.length >= 2) {
                        qual.append("(('First Name' LIKE \"" + splittedcriteria[0] + "%\") AND ('Last Name' = \"" + splittedcriteria[1] + "\"))");
                    } else {
                        qual.append("(('First Name' = $NULL$ ) AND ('Last Name' = $NULL$ ))");
                    }
                }
            }

            if (!ticketid.equalsIgnoreCase("$NULL$")) {
                ticket = true;
                if (ticketid.startsWith("SR")) {
                    if (qual.length() > 1) {
                        qual.append(" OR ('Request #+' LIKE \"%" + ticketid + "%\")");
                    } else {
                        qual.append("('Request #+' LIKE \"%" + ticketid + "%\")");
                    }
                } else if (ticketid.startsWith("PT")) {
                    if (qual.length() > 1) {
                        qual.append(" OR ('Case #+' LIKE \"%" + ticketid + "%\")");
                    } else {
                        qual.append("('Case #+' LIKE \"%" + ticketid + "%\")");
                    }
                }
            }

            if (!client.equalsIgnoreCase("$NULL$")) {
                int[] clientfieldid = new int[]{536870913};
                if (qual.length() > 1) {

                    List<Entry> entrie = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "CMN:ClientInfo",
                            clientfieldid,
                            "('Master Client' = \"" + client + "\")"
                    );

                    qual.append(" AND (");
                    if (!(entrie.size() == 0 || entrie == null)) {
                        int i = 0;
                        for (Entry entry : entrie) {

                            if (i == 0) {
                                qual.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            } else {
                                qual.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            }
                            ++i;
                        }
                    } else {
                        qual.append("('Client' = \"" + client + "\")");
                    }

                    qual.append(")");

                } else {

                    List<Entry> entrie = RemedyBase.queryEntrysByQual(
                            arServerUser,
                            "CMN:ClientInfo",
                            clientfieldid,
                            "('Master Client' = \"" + client + "\")"
                    );

                    qual.append("(");
                    if (!(entrie.size() == 0 || entrie == null)) {
                        int i = 0;
                        for (Entry entry : entrie) {

                            if (i == 0) {
                                qual.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            } else {
                                qual.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                            }
                            ++i;
                        }
                    } else {
                        qual.append("('Client' = \"" + client + "\")");
                    }
                    qual.append(")");
                }
            }

            if (true) {
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                if (ticket) {
                    if (custom.equalsIgnoreCase("sr")) {

                        if (ticketid.startsWith("SR")) {
                            List<Entry> sr = RemedyBase.queryEntrysByQualWithNoOfRecords(
                                    arServerUser,
                                    "SR:ServiceRequest",
                                    requiredFields,
                                    qual.toString(), noofrecords
                            );
                            if (sr.size() > 0) {
                                for (Entry entry1 : sr) {
                                    String date = entry1.get(requiredFields[3]).toString();
                                    baseModels.add(new MiniTicketModel(
                                            entry1.get(requiredFields[0]).toString(),
                                            entry1.get(requiredFields[1]).toString(),
                                            entry1.get(requiredFields[2]).toString(),
                                            RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                            "ServiceRequest"
                                    ));
                                }
                            }
                        }
                    } else if (ticketid.startsWith("PT")) {
                        List<Entry> pt = RemedyBase.queryEntrysByQualWithNoOfRecords(
                                arServerUser,
                                "PT:ProblemTicket",
                                requiredFields,
                                qual.toString(), noofrecords
                        );


                        if (pt.size() > 0) {
                            for (Entry entry1 : pt) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ProblemTicket"
                                ));
                            }
                        }

                    }
                }

                if (ticket == false) {
                    if (custom.equalsIgnoreCase("sr") || custom.equalsIgnoreCase("all")) {
                        List<Entry> sr = RemedyBase.queryEntrysByQualWithNoOfRecords(
                                arServerUser,
                                "SR:ServiceRequest",
                                requiredFields,
                                qual.toString(), noofrecords
                        );
                        if (sr.size() > 0) {
                            for (Entry entry1 : sr) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ServiceRequest"
                                ));
                            }
                        }
                    } else if (custom.equalsIgnoreCase("pt") || custom.equalsIgnoreCase("all")) {
                        List<Entry> pt = RemedyBase.queryEntrysByQualWithNoOfRecords(
                                arServerUser,
                                "PT:ProblemTicket",
                                requiredFields,
                                qual.toString(), noofrecords
                        );


                        if (pt.size() > 0) {
                            for (Entry entry1 : pt) {
                                String date = entry1.get(requiredFields[3]).toString();
                                baseModels.add(new MiniTicketModel(
                                        entry1.get(requiredFields[0]).toString(),
                                        entry1.get(requiredFields[1]).toString(),
                                        entry1.get(requiredFields[2]).toString(),
                                        RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                                        "ProblemTicket"
                                ));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            baseModels.add(new ErrorModel(e.getLocalizedMessage()));
        }


        return baseModels;
    }
*/

    @RequestMapping(value = "/openapi/clientBuildingDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseModel clientBuildingDetails(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("clientname") String client) {


        ClientBuildingDetailsModel clientBuildingDetailsModel = new ClientBuildingDetailsModel();
        try {
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            int[] orgFields = {8, 200000012};
            int[] buildingFields = {1, 200000007};
            int[] departmentFields = {1, 200000006};
            int[] floorSuiteFields = {1, 536870913, 536871039};
            List<Entry> clientForms = RemedyBase.queryEntrysByQual(
                    user,
                    "CMN:BusOrg",
                    orgFields,
                    "('Client' = \"" + client + "\")"
            );

            List<BaseModel> orgModel = new ArrayList<>();
            List<BaseModel> buildingModel = new ArrayList<>();
            List<BaseModel> departmentModel = new ArrayList<>();
            List<BaseModel> floorModel = new ArrayList<>();
            List<BaseModel> suiteModel = new ArrayList<>();
            clientForms.forEach(f -> {
                orgModel.add(new OrganisationModel(f.get(orgFields[0]).toString(), f.get(orgFields[1]).toString()));
                String mClient = f.get(orgFields[1]).toString();

                List<Entry> buildingEntry = RemedyBase.queryEntrysByQual(
                        user,
                        "CMN:BuildingAddress+",
                        buildingFields,
                        "('Client' = \"" + mClient + "\")"
                );
                buildingEntry.forEach(building -> {
                    buildingModel.add(new BuildingModel(
                            building.get(buildingFields[0]).toString(),
                            building.get(buildingFields[1]).toString(),
                            mClient
                    ));
                    List<Entry> departmentEntry = RemedyBase.queryEntrysByQual(
                            user,
                            "CMN:Location",
                            departmentFields,
                            "('Building' = \"" + building.get(buildingFields[1]).toString() + "\")"
                    );
                    departmentEntry.forEach(department -> {
                        departmentModel.add(new DepartmentModel(
                                department.get(departmentFields[0]).toString(),
                                department.get(departmentFields[1]).toString(),
                                building.get(buildingFields[0]).toString()
                        ));
                    });
                    List<Entry> floorSuiteEntry = RemedyBase.queryEntrysByQual(
                            user,
                            "CMN:BuildingFloorSuite+",
                            floorSuiteFields,
                            "('Building' = \"" + building.get(buildingFields[1]).toString() + "\")"
                    );
                    floorSuiteEntry.forEach(floorSuite -> {
                        floorModel.add(new FloorModel(
                                floorSuite.get(floorSuiteFields[0]).toString(),
                                floorSuite.get(floorSuiteFields[1]).toString(),
                                building.get(buildingFields[0]).toString()
                        ));
                        suiteModel.add(new SuiteModel(
                                floorSuite.get(floorSuiteFields[0]).toString(),
                                building.get(buildingFields[0]).toString(),
                                floorSuite.get(floorSuiteFields[2]).toString(),
                                floorSuite.get(floorSuiteFields[0]).toString()
                        ));
                    });
                });
                clientBuildingDetailsModel.setBuildingModels(buildingModel);
                clientBuildingDetailsModel.setDepartmentModels(departmentModel);
                clientBuildingDetailsModel.setFloorModels(floorModel);
                clientBuildingDetailsModel.setOrganisationModelList(orgModel);
                clientBuildingDetailsModel.setSuiteModels(suiteModel);
            });
        } catch (Exception e) {
            return new ErrorModel(e.getMessage());
        }
        return clientBuildingDetailsModel;
    }


    @RequestMapping(value = "/openapi/departmentinfo", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> departmentinfo(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam Map<String, String> allRequestParams) {

        List<BaseModel> baseModels = new ArrayList<>();
        String ticketid = "";

        try {

            int[] requiredFields = {200000012,  //client
                    200000007,                  //Building
                    200000006,                  //Department
                    2,                          //Submitter
                    7,                          //Status
                    536870981                   //Business Organisation
            };

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);
            String qual = "";
            boolean run = false, ticket = false;

            if (allRequestParams.containsKey("client")) {
                run = true;

                qual = "('Client' LIKE \"%" + allRequestParams.get("client") + "%\")";
                //search by email

            } else if (allRequestParams.containsKey("building")) {
                run = true;

                qual = "('Building' LIKE \"%" + allRequestParams.get("building") + "%\")";
            } else {
                run = false;
            }

            if (run) {

                List<Entry> location = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        "CMN:Location",
                        requiredFields,
                        qual
                );
                if (location.size() > 0) {
                    for (Entry entry1 : location) {
                        String date = entry1.get(requiredFields[3]).toString();
                        baseModels.add(new LocationDetailModel(
                                entry1.get(requiredFields[0]).toString(),
                                entry1.get(requiredFields[1]).toString(),
                                entry1.get(requiredFields[2]).toString(),
                                entry1.get(requiredFields[3]).toString(),
                                entry1.get(requiredFields[4]).toString(),
                                entry1.get(requiredFields[5]).toString()
                        ));
                    }
                }


            }
        } catch (Exception e) {
            baseModels.add(new ErrorModel(e.getLocalizedMessage()));
        }

        return baseModels;
    }


    @RequestMapping(value = "/openapi/clientBuildingDetails1", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseModel clientBuildingDetails1(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("clientname") String client) {
        ClientBuildingDetailsModel clientBuildingDetailsModel = new ClientBuildingDetailsModel();
        try {
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            int[] orgFields = {8, 200000012};
            int[] buildingFields = {1, 200000007, 200000012};
            int[] departmentFields = {1, 200000006, 200000007};
            int[] floorSuiteFields = {1, 536870913, 536871039, 200000007};
            List<Entry> clientForms = RemedyBase.queryEntrysByQual(
                    user,
                    "CMN:BusOrg",
                    orgFields,
                    "('Client' = \"" + client + "\")"
            );

            List<Entry> buildingEntry = RemedyBase.queryEntrysByQual(
                    user,
                    "CMN:BuildingAddress+",
                    buildingFields,
                    null
            );

            List<Entry> departmentEntry = RemedyBase.queryEntrysByQual(
                    user,
                    "CMN:Location",
                    departmentFields,
                    null
            );

            List<Entry> floorSuiteEntry = RemedyBase.queryEntrysByQual(
                    user,
                    "CMN:BuildingFloorSuite+",
                    floorSuiteFields,
                    null
            );

            List<BaseModel> orgModel = new ArrayList<>();
            List<BaseModel> buildingModel = new ArrayList<>();
            List<BaseModel> departmentModel = new ArrayList<>();
            List<BaseModel> floorModel = new ArrayList<>();
            List<BaseModel> suiteModel = new ArrayList<>();
            System.out.println(buildingEntry.size());
            System.out.println(departmentEntry.size());
            System.out.println(floorSuiteEntry.size());

            clientForms.forEach(f -> {
                orgModel.add(new OrganisationModel(f.get(orgFields[0]).toString(), f.get(orgFields[1]).toString()));
                String mClient = f.get(orgFields[1]).toString();

                buildingEntry
                        .parallelStream()
                        .filter(entry1 -> entry1.get(buildingFields[2]).toString() != null)
                        .filter(entry -> entry.get(buildingFields[2]).toString().equalsIgnoreCase(mClient))
                        .map(m -> {
                            BaseModel baseModel = new BuildingModel(m.get(buildingFields[0]).toString(), m.get(buildingFields[1]).toString(), mClient);
                            buildingModel.add(baseModel);
                            return baseModel;
                        })
                        .collect(Collectors.toList());

                buildingModel.forEach(b -> {
                    BuildingModel buildingModel1 = (BuildingModel) b;
                    departmentEntry
                            .parallelStream()
                            .filter(entry -> entry.get(departmentFields[2]).toString() != null)
                            .filter(entry -> entry.get(departmentFields[2]).toString().equals(buildingModel1.getBuilding()))
                            .map(m -> {
                                BaseModel baseModel = new DepartmentModel(
                                        m.get(departmentFields[0]).toString(),
                                        m.get(departmentFields[1]).toString(),
                                        buildingModel1.getBuilding_Id()
                                );
                                departmentModel.add(baseModel);
                                return baseModel;
                            }).collect(Collectors.toList());


                    floorSuiteEntry
                            .stream()
                            .filter(entry -> entry.get(floorSuiteFields[3]).toString() != null)
                            .filter(entry -> entry.get(floorSuiteFields[3]).toString().equals(buildingModel1.getBuilding()))
                            .map(m -> {
                                floorModel.add(new FloorModel(
                                        m.get(floorSuiteFields[0]).toString(),
                                        m.get(floorSuiteFields[1]).toString(),
                                        buildingModel1.getBuilding_Id()
                                ));

                                suiteModel.add(new SuiteModel(
                                        m.get(floorSuiteFields[0]).toString(),
                                        buildingModel1.getBuilding_Id(),
                                        m.get(floorSuiteFields[2]).toString(),
                                        m.get(floorSuiteFields[0]).toString()
                                ));

                                return true;
                            }).collect(Collectors.toList());


                });

                System.out.println(buildingModel.size());
                System.out.println(departmentModel.size());
                System.out.println(floorModel.size());
                System.out.println(orgModel.size());
                System.out.println(suiteModel.size());

                clientBuildingDetailsModel.setBuildingModels(buildingModel);
                clientBuildingDetailsModel.setDepartmentModels(departmentModel);
                clientBuildingDetailsModel.setFloorModels(floorModel);
                clientBuildingDetailsModel.setOrganisationModelList(orgModel);
                clientBuildingDetailsModel.setSuiteModels(suiteModel);
            });
        } catch (Exception e) {
            return new ErrorModel(e.getMessage());
        }

        return clientBuildingDetailsModel;
    }


    @RequestMapping(value = "/openapi/buildingdetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Map<String, Object>> buildingdetails(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "client") String client,
            @RequestParam(value = "status", required = false, defaultValue = "Active") String status) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> distinctresult = new ArrayList<>();
        try {
            int[] clientfieldid = new int[]{536870913};
            int[] masterclientfieldid = new int[]{536870914};

            List<Entry> entrie = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:ClientInfo",
                    clientfieldid,
                    "('Master Client' = \"" + client + "\")"
            );
            StringBuilder peopleQuery = new StringBuilder("(");
            int i = 0;
            if (!(entrie.size() == 0 || entrie == null)) {
                for (Entry entry : entrie) {

                    if (i == 0) {
                        peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    } else {
                        peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    }
                    ++i;
                }
            } else {
                peopleQuery.append("('Client' = \"" + client + "\")");
            }

            peopleQuery.append(")");
            peopleQuery.append(" AND ('Status'=\"" + status + "\")");
            int[] buildingFields = {200000007, 200000012};//building client
            int[] departmentFields = {1, 200000006, 200000007};//200000006	Department ,200000007 building


            List<Entry> buildingEntry1 = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:BuildingAddress+",
                    buildingFields,
                    peopleQuery.toString()
            );
            for (Entry entry : buildingEntry1) {
                Map<String, Object> buildingEntry = new HashMap<>();
                buildingEntry.put("building", entry.get(200000007).toString());
                buildingEntry.put("client", entry.get(200000012).toString());
                result.add(buildingEntry);
            }

            Collections.sort(result, new Comparator<Map<String, Object>>() {
                public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                    if (o1.get("building") instanceof String && o2.get("building") instanceof String)
                        return String.class.cast(o1.get("building")).compareTo(String.class.cast(o2.get("building")));
                    else
                        return 0;
                }
            });
            result.stream().forEach(stringStringMap -> {
                List result1 = distinctresult.stream()
                        .filter(stringStringMap1 ->
                                stringStringMap1.get("building").toString().equalsIgnoreCase(stringStringMap.get("building").toString())
                        )
                        .collect(Collectors.toList());
                if (result1.isEmpty() || result1 == null) {
                    distinctresult.add(stringStringMap);
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
        return distinctresult;
    }


    @RequestMapping(value = "/openapi/departmentdetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public List<Map<String, Object>> departmentdetails(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "client") String client,
            @RequestParam(value = "building") String building,
            @RequestParam(value = "status") String status) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> distinctresult = new ArrayList<>();
        try {

            int[] departmentFields = {1, 200000006, 200000007};//200000006	Department ,200000007 building

            int[] clientfieldid = new int[]{536870913};
            int[] masterclientfieldid = new int[]{536870914};

            List<Entry> entrie = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:ClientInfo",
                    clientfieldid,
                    "('Master Client' = \"" + client + "\")"
            );
            StringBuilder peopleQuery = new StringBuilder("(");
            int i = 0;
            if (!(entrie.size() == 0 || entrie == null)) {
                for (Entry entry : entrie) {

                    if (i == 0) {
                        peopleQuery.append("('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    } else {
                        peopleQuery.append("OR ('Client' = \"" + entry.get(clientfieldid[0]).toString() + "\")");
                    }
                    ++i;
                }
            } else {
                peopleQuery.append("('Client' = \"" + client + "\")");
            }
            peopleQuery.append(")");
            peopleQuery.append(" AND ('Building'=\"" + building + "\")");
            peopleQuery.append(" AND ('Status'=\"" + status + "\")");

            List<Entry> departmentEntry1 = RemedyBase.queryEntrysByQual(
                    arServerUser1,
                    "CMN:Location",
                    departmentFields,
                    peopleQuery.toString()
            );

            for (Entry entry : departmentEntry1) {
                Map<String, Object> departmentEntry = new HashMap<>();
                departmentEntry.put("building", entry.get(200000007).toString());
                departmentEntry.put("department", entry.get(200000006).toString());
                result.add(departmentEntry);
            }


            Collections.sort(result, new Comparator<Map<String, Object>>() {
                public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                    if (o1.get("department") instanceof String && o2.get("department") instanceof String)
                        return String.class.cast(o1.get("department")).compareTo(String.class.cast(o2.get("department")));
                    else
                        return 0;
                }
            });
            result.stream().forEach(stringStringMap -> {
                List result1 = distinctresult.stream()
                        .filter(stringStringMap1 ->
                                stringStringMap1.get("department").toString().equalsIgnoreCase(stringStringMap.get("department").toString())
                        )
                        .collect(Collectors.toList());
                if (result1.isEmpty() || result1 == null) {
                    distinctresult.add(stringStringMap);
                }
            });


        } catch (Exception e) {
            e.getMessage();
        }
        return distinctresult;
    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/ticketinfo")
    public Map<String, Object> ticketInfo(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId) {

        Map<String, Object> resultMap = new HashMap<>();

        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            return RemedyBase.getTicketEntry(user, formName, ticketId);
        } catch (Exception e) {
            resultMap.put("error", e.getMessage());
        }

        return resultMap;
    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/ticketinfowithhistory")
    public Map<String, Object> ticketInfowithhistory(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId, HttpServletRequest request) {

        Map<String, Object> resultMap = new HashMap<>();

        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            return RemedyBase.getTicketEntrywithfileattachmentandhistory(user, formName, ticketId, request);
        } catch (Exception e) {
            resultMap.put("error", e.getMessage());
        }

        return resultMap;
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/downloadattachment")
    public FileSystemResource downloadattachment(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestParam("fieldId") String fieldid, HttpServletRequest request, HttpServletResponse
                    response, HttpSession session) {
        try {
            Map<String, Object> res = RemedyBase.getdownloadattachment(arServerUser1, formName, ticketId, fieldid);
            if (res != null) {
                response.setContentType("application/force-download");
                response.setHeader("Content-Disposition", "attachment; filename=" + res.get("name"));
                return new FileSystemResource((File) res.get("file"));
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/ticketstatus")
    public Map<String, String> ticketstatus(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam(value = "ticketfieldid", required = false, defaultValue = "1") String ticketfieldid,
            @RequestParam(value = "statusfieldid", required = false, defaultValue = "7") String statusfieldid,
            @RequestBody List<String> tickets) {

        Map<String, String> resultMap = new HashMap<>();

        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            return RemedyBase.getticketstatus(user, formName, ticketfieldid, statusfieldid, tickets);
        } catch (Exception e) {
            resultMap.put("error", e.getMessage());
        }

        return resultMap;
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/updateticket")
    public Map<String, String> updateticket(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestBody Map<String, String> updateParam, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");

            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            response = RemedyBase.updateTicket(user, updateParam, formName, ticketId);
            LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(response), formName, com.htc.remedy.constants.Constants.UPDATE, request);
        } catch (Exception e) {
            return RemedyBase.returnError(e.getMessage());
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/calquery")
    public Object calquery(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam(value = "fieldid", defaultValue = "0") String fieldID
    ) {
        List<fields> fieldsList = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            List<Field> fields = new ArrayList<>();
            try {
                if (fieldID.equalsIgnoreCase("0")) {
                    fields = user.getListFieldObjects(formName);
                } else {
                    Map<Integer, Field> f1 = RemedyBase.getFormFieldsObject(user, formName);
                    for (String s : fieldID.split(",")) {
                        fields.add(f1.get(Integer.parseInt(s)));
                    }

                }

            } catch (ARException e) {
                e.printStackTrace();
            }
            Boolean selectionfield = false, datetimefield = false, diaryfield = false;
            Field field1 = null;
            List<fields> fields1 = new ArrayList<>();
            if (fields.size() == 1) {
                return selectionfieldvalue(fields.get(0));
            } else {

                for (Field field : fields) {
                    selectionfield = false;
                    datetimefield = false;
                    diaryfield = false;
                    if (field.getFieldID() >= 0) {
                        if (field instanceof SelectionField) {
                            selectionfield = true;

                        } else if (field instanceof DateTimeField) {
                            datetimefield = true;

                        } else if (field instanceof DiaryField) {
                            diaryfield = true;

                        }
                        field1 = field;
                    }
                    fields fied = new fields();
                    if (field1 != null) {
                        if (!selectionfield) {
                            //System.out.println(field1.getFieldID() + " " + field1.getName());
                            fied = new fields(String.valueOf(field1.getFieldID()), field1.getName());
                        } else {

                            fied = new fields(String.valueOf(field1.getFieldID()), field1.getName(), selectionfieldvalue(field1));
                            //System.out.println(field1.getFieldID() + " " + field1.getName());
                            //System.out.println(selectionfieldvalue(field1));
                        }
                    }
                    fieldsList.add(fied);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldsList;
    }


    static class fields {
        String fieldid;
        String fieldname;
        List<Map<String, String>> values = new ArrayList<>();

        public fields(String fieldid, String fieldname) {
            this.fieldid = fieldid;
            this.fieldname = fieldname;
        }

        public fields(String fieldid, String fieldname, List<Map<String, String>> values) {
            this.fieldid = fieldid;
            this.fieldname = fieldname;
            this.values = values;
        }

        public List<Map<String, String>> getValues() {
            return values;
        }

        public void setValues(List<Map<String, String>> values) {
            this.values = values;
        }

        public fields() {
        }

        public String getFieldid() {
            return fieldid;
        }

        public void setFieldid(String fieldid) {
            this.fieldid = fieldid;
        }

        public String getFieldname() {
            return fieldname;
        }

        public void setFieldname(String fieldname) {
            this.fieldname = fieldname;
        }


    }

    public static List<Map<String, String>> selectionfieldvalue(Field field) {
        List<Map<String, String>> listmap = new ArrayList<>();

        SelectionFieldLimit sFieldLimit = (SelectionFieldLimit) field.getFieldLimit();
        if (sFieldLimit != null) {
            List<EnumItem> eItemList = sFieldLimit.getValues();
            for (EnumItem eItem : eItemList) {
                Map<String, String> selectionfieldvalue = new HashMap<>();
                if (eItem.getEnumItemNumber() > -1) {
                    selectionfieldvalue.put("id", String.valueOf(eItem.getEnumItemNumber()));
                    selectionfieldvalue.put("name", eItem.getEnumItemName());

                }
                listmap.add(selectionfieldvalue);
            }
        }
        return listmap;
    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/updateticketbyid")
    public Map<String, Object> updateticketbyid(

            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestBody Map<String, Object> updateParam, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            response = RemedyBase.updateTicketbyid(user, updateParam, formName, ticketId);
            LoggerBase.loguserrecords(user, ticketId, new Gson().toJson(response), formName, com.htc.remedy.constants.Constants.UPDATE, request);
        } catch (Exception e) {
            return RemedyBase.returnErrorobject(e.getMessage());
        }
        return response;

    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/openapi/cvntroletask")
    public List<Map<String, String>> cvntroletask(

            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "roleprefix", required = false) String roleprefix,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("costcode") String costcode,
            @RequestParam("companycode") String companycode) {
        List<Map<String, String>> result = new ArrayList<>();
        int[] reqfield = {
                536870915,//	Task__c
                704000115,//	Task Sequence__c
                536870914,//	Task ID__c
                704000098,//	Task Details__c
                704000085,//	Request Type
                536870918,//	Details After__c
                704000159,//	Approval__c
                700001059,//	Assigned Individual__c
                700001052,//	Assigned Group__c
                200000012,//	Client__c
                536870931,//	IAM__c
                536870967,//	IAM Client__c
                536870933,//	IAM Application__c
                702170968,//	IAM Application Login ID__c
                536877932,//	Remove PDT__c
                536870919,//	Role Task Status
        };

        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            List<Entry> entries = RemedyBase.queryEntrysByQual(user, "CTSSP:Roles", new int[]{179}, "'Role'=\"" + role + "\" AND 'Role Prefix__c'=\"" + roleprefix + "\" AND 'Cost Code__c'=\"" + costcode + "\" AND 'Company Code'=\"" + companycode + "\" ");
            String roleuniqueidentifier = entries.get(0).get(179).toString();
            List<Entry> rolestaskentries = RemedyBase.queryEntrysByQual(user, "CTSSP:Role_Tasks+SR:PDT-Tasks", reqfield, "'Role Unique Identifier__c'=\"" + roleuniqueidentifier + "\"");
            rolestaskentries.forEach(entry1 -> {
                Map<String, String> newresponse = new HashMap<>();
                newresponse.put("task", entry1.get(536870915).toString());
                newresponse.put("tasksequence", entry1.get(704000115).toString());
                newresponse.put("taskid", entry1.get(536870914).toString());
                newresponse.put("taskdetails", entry1.get(704000098).toString());
                newresponse.put("requesttype", entry1.get(704000085).toString());
                newresponse.put("detailsafter", entry1.get(536870918).toString());
                newresponse.put("approval", entry1.get(704000159).toString());
                newresponse.put("assignedinidividual", entry1.get(700001059).toString());
                newresponse.put("assignedgroup", entry1.get(700001052).toString());
                newresponse.put("client", entry1.get(200000012).toString());
                newresponse.put("iam", entry1.get(536870931).toString());
                newresponse.put("iam_client", entry1.get(536870967).toString());
                newresponse.put("iam_application", entry1.get(536870933).toString());
                newresponse.put("iam_application_loginid", entry1.get(702170968).toString());
                newresponse.put("removepdt", entry1.get(536877932).toString());
                newresponse.put("roletaskstatus", entry1.get(536870919).toString());
                result.add(newresponse);
            });
        } catch (Exception e) {
            e.getMessage();
        }
        if (result.isEmpty()) {
            Map<String, String> newresponse = new HashMap<>();
            newresponse.put("task", "");
            newresponse.put("tasksequence", "");
            newresponse.put("taskid", "");
            newresponse.put("taskdetails", "");
            newresponse.put("requesttype", "");
            newresponse.put("detailsafter", "");
            newresponse.put("approval", "");
            newresponse.put("assignedinidividual", "");
            newresponse.put("assignedgroup", "");
            newresponse.put("client", "");
            newresponse.put("iam", "");
            newresponse.put("iam_client", "");
            newresponse.put("iam_application", "");
            newresponse.put("iam_application_loginid", "");
            newresponse.put("removepdt", "");
            newresponse.put("roletaskstatus", "");
            result.clear();
            result.add(newresponse);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            path = "/openapi/iquery/{formName}"
    )
    public List<Map<String, String>> iQuery(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(name = "atoken") String atoken,
            @PathVariable(name = "formName") String formName,
            HttpServletRequest request
    ) {

        List<Map<String, String>> resultMap = new ArrayList<>();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            Map<String, String[]> rb = request.getParameterMap();
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            //rb.remove("atoken");
            StringBuilder query = new StringBuilder("(");
            for (String s : rb.keySet()) {
                if (!s.equalsIgnoreCase("atoken")) {
                    query.append("'").append(s.replace("^", " ")).append("' = \"").append(rb.get(s)[0]).append("\" ");
                    query.append("AND");
                }
            }

            String queryString = query.toString().substring(0, query.length() - 3);
            queryString += ")";

            List<Entry> enty = RemedyBase.queryEntrysByQual(arServerUser, formName,
                    RemedyBase.getAllFieldsInt(arServerUser, formName),
                    queryString);

            Map<Integer, String> formFields = RemedyBase.getFormFields2(arServerUser, formName);

            for (Entry entry : enty) {
                HashMap<String, String> entryObject = new HashMap<>();

                for (Integer keys : entry.keySet()) {
                    entryObject.put(formFields.get(keys).toString(), entry.get(keys).toString());
                }
                resultMap.add(entryObject);
            }

        } catch (Exception e) {
            resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return resultMap;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/openapi/getAttachement")
    public byte[] getAttachement(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestParam("fieldId") String fieldId
    ) {
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }


            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            return RemedyBase.getAttachement(user, formName, ticketId, fieldId);

        } catch (Exception e) {
            return new byte[]{};
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/putAttachement")
    public Map<String, String> putAttachement(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            @RequestParam("fieldId") String fieldId,
            @RequestParam("attachment1") MultipartFile file
    ) {
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }


            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            File tFile = multipartToFile(file);
            Entry entry = RemedyBase.putAttachement(user, formName, ticketId, fieldId, tFile);

            if (entry != null) {
                return RemedyBase.returnSuccess();
            }
        } catch (Exception e) {
            return RemedyBase.returnError(e.getLocalizedMessage());
        }

        return RemedyBase.returnError("Error");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/putAttachements")
    public Map<String, String> putAttachement1(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            org.springframework.web.multipart.MultipartHttpServletRequest request
    ) {
        Map<String, File> attach = new TreeMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            Map<String, MultipartFile> attachments = request.getFileMap();
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            for (Map.Entry<String, MultipartFile> entry : attachments.entrySet()) {
                attach.put(entry.getKey(), multipartToFile(entry.getValue()));
            }
            Entry entry = RemedyBase.putAttachement1(user, formName, ticketId, attach);
            if (entry != null) {
                return RemedyBase.returnSuccess();
            }
        } catch (Exception e) {
            return RemedyBase.returnError(e.getLocalizedMessage());
        }
        return RemedyBase.returnError("Error");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/updateAttachments")
    public Map<String, Object> updateattachments(
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,
            HttpServletRequest httpServletRequest,
            org.springframework.web.multipart.MultipartHttpServletRequest request
    ) {
        Map<String, File> attach = new TreeMap<>();
        String loginid = "";
        ARServerUser loggedinuser = new ARServerUser();

        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Auth Error");
            }
            loggedinuser = ITSMBase.impersonateuser(arServerUser1, loginid);
            Map<String, String> updateParam = new HashMap<>();
            System.out.println(refreshtoken + ":" + accesstoken + ":" + atoken + ":" + loggedinuser.getImpersonatedUser() + ":" + loggedinuser.getUser() + ":" + ticketId);
            httpServletRequest.getParameterMap().forEach((s, strings) -> {
                if (strings != null) {
                    Arrays.stream(strings).forEach(s1 -> {
                        if (s1 == null || s1.equalsIgnoreCase("null") || s1.equalsIgnoreCase("NO_VALUE") || s1.equalsIgnoreCase("NO_VALUE") || s1.equalsIgnoreCase("NOVALUE"))
                            updateParam.put(s, "NO_VALUE");
                    });
                } else {
                    updateParam.put(s, "NO_VALUE");
                }
            });

            Map<String, MultipartFile> attachments = request.getFileMap();
            for (Map.Entry<String, MultipartFile> entry : attachments.entrySet()) {
                attach.put(entry.getKey(), convertMultiPartToFile(entry.getValue()));
            }
            Entry entry = ITSMBase.putAttachmentwithnovalue(loggedinuser, formName, ticketId, attach, updateParam);
            if (entry != null) {
                Map<Integer, String> integerStringMap = RemedyBase.getFormFields2(arServerUser1, formName);
                List<Field> fields = arServerUser1.getListFieldObjects(formName);
                Entry resultform = arServerUser1.getEntry(formName, ticketId, null);
                for (Field field : fields) {
                    if (field instanceof AttachmentField && resultform.get(field.getFieldID()) != null && resultform.get(field.getFieldID()).toString() != null) {
                        try {
                            resultMap.put(integerStringMap.get(field.getFieldID()), RemedyBase.createattatchmenturi(arServerUser1, formName, ticketId, field, resultform.get(field.getFieldID()), request, resultform));
                        } catch (Exception e) {
                            resultMap.put(integerStringMap.get(field.getFieldID()), null);
                        }
                    }
                }
            }
            LoggerBase.loguserrecords(loggedinuser, ticketId, new Gson().toJson(resultMap), formName, com.htc.remedy.constants.Constants.UPDATEATTACHMENTS, request);
        } catch (Exception e) {
            resultMap.clear();
            resultMap.put("error", e.getMessage());
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        resultMap.put("success", "ok");
        return resultMap;
    }

    public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        String fileName = multipart.getOriginalFilename();
        String[] dStirng = fileName.split(Pattern.quote("."));
        File convFile = File.createTempFile(fileattachmentpath + String.join(".", ArrayUtils.remove(dStirng, dStirng.length - 1)), "." + dStirng[dStirng.length - 1]);
        multipart.transferTo(convFile);
        return convFile;
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(fileattachmentpath + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/griddata")
    public List<Map<String, ? extends Object>> griddata(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("requesttype") String requesttype,
            @RequestParam("roleuniqueidentifier") String roleuniqueidentifier
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            return RemedyBase.griddata(
                    user,
                    requesttype,
                    roleuniqueidentifier
            );
        } catch (Exception e) {
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return results;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/userbasedclientandgroups")
    public ResponseEntity userbasedclientandgroups(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("loginid") String loginid
    ) {
        Map<String, List> results = new TreeMap<>();
        try {
            loginid = ITSMBase.escapesql(loginid);
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            Boolean adminuser = false;
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            List<GroupInfo> groupInfo = new ArrayList<>();
            try {
                groupInfo = user.getListGroup(loginid, null);
                adminuser = groupInfo.stream().anyMatch(groupInfo1 -> groupInfo1.getId() == 1);//Admin or super support  removed for oncall groups   || groupInfo1.getId() == 55555555
                RemedyBase.fetchusergroups(loginid, user);
            } catch (ARException e) {
                throw new Exception(loginid +
                        " Not a support user!");
            }
            String query = "";
            if (adminuser) {
                query += "SELECT  distinct master_client_code_name as client,group_name as group_name\n" +
                        "                    FROM Group_GRP_OPT_CMN_MSTR_Join WITH (NOLOCK)\n" +
                        "                    WHERE Functional_Type IN (1) AND  Status__C=0 and client_code_name is not null order by client,group_name ";
            } else {
                query += "SELECT  distinct master_client_code_name as client,group_name as group_name\n" +
                        "                                           FROM Group_GRP_OPT_CMN_MSTR_Join WITH (NOLOCK)\n" +
                        "                                          WHERE Functional_Type IN (1) AND  Status__C=0 and client_code_name is not null and group_name IN( ";

                query += "select group_name from CMN_Notification_Assignments WITH (NOLOCK) where (Functional_Type = 1) AND (Status = 2) AND login_id =\'" + loginid + "\' )";
                query += " order by client,group_name";
            }

            StringBuilder builder = new StringBuilder("");

            SQLResult sqlResult;

            sqlResult = user.getListSQL(query, 0, true);


            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                results.put(content.get(0).toString(), new ArrayList());
            }

            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                List<String> appEntry = results.get(content.get(0).toString());
                appEntry.add(content.get(1).toString());
                results.put(content.get(0).toString(), appEntry);
            }
        } catch (Exception e) {
            results.clear();
            List errorresponse = new ArrayList();
            errorresponse.add(e.getLocalizedMessage());
            results.put("error", errorresponse);
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/userbasedclientandgroupss")
    public ResponseEntity userbasedclientandgroupss(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("loginid") String loginid,
            HttpServletRequest request
    ) {
        Map<String, List> results = new TreeMap<>();
        List<Map<String, Object>> response=new ArrayList<>();
        try {
            loginid = ITSMBase.escapesql(loginid);
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            Boolean adminuser = false;
            ARServerUser loggedinuser = ITSMBase.impersonateuser(arServerUser1, loginid);
            List<GroupInfo> groupInfo = new ArrayList<>();
            int[] ref = new int[]{
                    536871013,//	Master Client Code Name
                    105,//	Group Name
            };

            String query = "'Functional Type'=\"Support\" AND ('Status__c'=\"Active\" OR  'Status__c'=\"Hidden\") AND 'Client Code Name'!=$NULL$ ", formname = "Group+GRP:OPT+CMN:MSTR-Join";

            List<EntryValueList> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, ref, query, Constants.AR_NO_MAX_LIST_RETRIEVE, true, ref, ref);
            response=ITSMBase.remedyresultsetforitsmentrylist(arServerUser1, formname, entries, request, ref);

            for (Map<String, Object> stringObjectMap : response) {
                results.put(stringObjectMap.get("Master_Client_Code_Name").toString(), new ArrayList());
            }
            for (Map<String, Object> stringObjectMap : response) {
                List<String> appEntry =  results.get(stringObjectMap.get("Master_Client_Code_Name").toString());
                appEntry.add(stringObjectMap.get("Group_Name").toString());
                results.put(stringObjectMap.get("Master_Client_Code_Name").toString(), appEntry);
            }
        } catch (Exception e) {
            results.clear();
            List errorresponse = new ArrayList();
            errorresponse.add(e.getLocalizedMessage());
            results.put("error", errorresponse);
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/deleteentry")
    public ResponseEntity deleteentry(
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "ticketId", required = false) String ticketId,
            @RequestParam(value = "formName", required = false) String formName,
            @RequestBody Map<String, String> updateparam, HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        String loginid = "";
        Map<String, String> individualresult = new HashMap<>();
        Boolean formstatus = false, ticketIdstatus = false;
        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(arServerUser1, loginid);
            for (String s : updateparam.keySet()) {
                if (s.equals("formName")) {
                    formName = updateparam.get(s);
                    formstatus = true;
                }
                if (s.equals("ticketId")) {
                    ticketId = updateparam.get(s);
                    ticketIdstatus = true;
                }
            }
            if (formstatus && ticketIdstatus) {
                loggedinuser.deleteEntry(formName, ticketId, 0);
                individualresult.put("ticketId", ticketId);
                individualresult.put("message", "Entry deleted");
                results.add(individualresult);
            }
            LoggerBase.loguserrecords(loggedinuser, ticketId, formName, com.htc.remedy.constants.Constants.DELTE, request);
        } catch (Exception e) {
            individualresult.put("ticketId", ticketId);
            individualresult.put("message", e.getMessage());
            results.add(individualresult);
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/watchlistdelete")
    public ResponseEntity watchlistdelete(
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestBody Map<String, String> updateparam, HttpServletRequest request
    ) {
        String formName = "CMN:WatchList", ticketId = "", loginid = "";
        ARServerUser loggedinuser = new ARServerUser();
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        Map<String, String> individualresult = new HashMap<>();
        Boolean loginidstatus = false, ticketIdstatus = false;
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(arServerUser1, loginid);

            for (String s : updateparam.keySet()) {
                if (s.equals("ticketId")) {
                    ticketId = updateparam.get(s);
                    ticketIdstatus = true;
                }
                if (s.equals("loginid")) {
                    loginid = updateparam.get(s);
                    loginidstatus = true;
                }
            }
            if (ticketIdstatus && loginidstatus) {
                entries = RemedyBase.queryEntrysByQual(loggedinuser, formName, null, "'Assigned To' =\"" + loginid + "\" AND 'Case ID' =\"" + ticketId + "\"");
                if (!entries.isEmpty()) {
                    for (Entry entry : entries) {
                        try {
                            loggedinuser.deleteEntry(formName, entry.getEntryId(), 0);
                        } catch (ARException e) {
                            e.printStackTrace();
                        }
                    }
                    individualresult.put("ticketId", ticketId);
                    individualresult.put("message", "Entry deleted");
                    results.add(individualresult);
                } else {
                    throw new Exception(ticketId + " Ticket ID not added as watchlist for " + loginid);
                }
            } else {
                throw new Exception("Login ID or Ticket ID not Found for the request!");
            }
            LoggerBase.loguserrecords(loggedinuser, ticketId, formName, com.htc.remedy.constants.Constants.DELTE, request);
        } catch (Exception e) {
            individualresult.put("ticketId", ticketId);
            individualresult.put("message", e.getMessage());
            results.add(individualresult);
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/oncallsupport")
    public List<Map<String, ? extends Object>> oncallsupport(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "groupname", required = false, defaultValue = "") String groupname,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            user = ITSMBase.impersonateuser(user, loginid);

            String query = "";

            if (groupname == null || groupname.isEmpty()) {
                query = "(('Status' = \"Current\"))";
            } else {
                query = "((('Status' = \"Scheduled\") OR ('Status' = \"Current\"))AND ('Group' = \"" + groupname + "\"))";
            }
            int[] reqfield = new int[]{
                    1,//	Request ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	Primary Name
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Short Description
                    15,//	Status History
                    112,//	zAssigned Group
                    200000012,//	Client
                    536870905,//	btn_End Early
                    536870906,//	btn_Close
                    536870908,//	btn_Save
                    536870909,//	btn_Profile Secondary
                    536870910,//	btn_Profile Primary
                    536870913,//	Line4
                    536870914,//	Secondary Name
                    536870915,//	Primary Login ID
                    536870916,//	Dupe ID
                    536870917,//	Secondary Login ID
                    536870918,//	Line5
                    536870919,//	Start Date/Time
                    536870920,//	End Date/Time
                    536870921,//	Line6
                    536870922,//	txt_End Date
                    536870923,//	Audit Log
                    536870924,//	Group ID
                    536870925,//	txt_Client
                    536870926,//	Calendar End Date/Time
                    536870927,//	Attachment Pool
                    536870928,//	OnCall Listing
                    536870929,//	zD_Temp01
                    536870930,//	zD_Member of Group
                    536870933,//	zD_Group Client
                    536870939,//	tbl_Audit
                    536870945,//	zD_FilterWorkflow
                    536871028,//	Master Client
                    536871032,//	Line
                    536871033,//	Line2
                    536871034,//	Group
                    536871040,//	Line3
                    536871041,//	txt_Pick_Group
                    536871042,//	txt_On-Call Scheduling
                    555550109,//	zD_Global
                    586871156,//	zD_LoginID
                    586871157,//	zD_PEO_Check
                    586871158,//	zD_ProfileType
                    600000037,//	tbl_Audit_col_Date
                    600000038,//	tbl_Audit_col_User
                    600000039,//	tbl_Audit_col_Changes
                    600000178,//	zUnused01
                    600000185,//	Page Holder
                    600000186,//	pnl_Main
                    600000187,//	pnl_Hidden
                    600000188,//	pnl_Audit
                    600000190,//	btn_Refresh
                    600000191,//	Notes
                    700001003,//	Category
                    700001004,//	Type
                    734870931,//	Set OnCall Person
            };

            List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(user, "CMN:OnCall", reqfield, query, noofrecords, 536871034);
            return ITSMBase.remedyresultsetforitsm(arServerUser1, "CMN:OnCall", entries, request, reqfield);
        } catch (Exception e) {
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return results;
    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/consolidatedticketsassignedtome")
    public List<Map<String, ? extends Object>> consolidatedticketsassignedtome(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "maindisplayfilter", required = false, defaultValue = "") String maindisplayfilter,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            String query = "('Assigned To' =\"" + loginid + "\") AND ('Status Integer' = 1) ";

            if (maindisplayfilter == null || maindisplayfilter.isEmpty()) {
                query += "";
            } else {
                query += "AND";
            }
            if (maindisplayfilter.equalsIgnoreCase("Urgent/High")) {
                query += " ('Application' < \"Service Request Tasks\" AND 'Priority' > \"Medium\" AND 'Application' <> \"Root Cause Analyse\")";
            } else if (maindisplayfilter.equalsIgnoreCase("VIP/CST")) {
                query += " ('VIP' = \"Yes\" OR 'CST' = \"Yes\")";
            } else if (maindisplayfilter.equalsIgnoreCase("Nearing/Past SLA")) {
                query += " ('Application' < \"Service Request Tasks\" AND 'Alert Status' > \"VIP\" AND 'Alert Status' < \"CST\" AND 'Application' <> \"Root Cause Analyse\")";
            }
            int[] reqfield = new int[]{
                    1,//	Request ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	Assigned To
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Summary
                    15,//	Status History
                    112,//	zAssigned Group
                    61900001,//	Alt Login ID
                    88888888,//	zD_Workspace
                    200000003,//	Category
                    200000004,//	Type
                    200000005,//	Item
                    200000006,//	Department
                    200000007,//	Building
                    200000012,//	Client
                    230000009,//	Last Update User
                    240000006,//	Assigned To Group+
                    260000000,//	Status Display
                    260000001,//	SLA Paused
                    260000005,//	Record Entry ID
                    260000009,//	Status Integer
                    260000126,//	Priority
                    260000127,//	Dup ID
                    536870910,//	Btn_View Source
                    536870913,//	Record Created
                    536870914,//	CST
                    536870915,//	zUnused02
                    536870916,//	zD_CascadeType
                    536870917,//	zUnused01
                    536870918,//	IM Flag
                    536870919,//	Group ID
                    536870922,//	View Other Group
                    536870951,//	Long Term Change
                    536871027,//	zD_FilterWorkflow
                    536871033,//	zSubmitter Group
                    536871038,//	Alert Status
                    536871060,//	Reassign Count__c
                    536871092,//	Box2
                    536871093,//	Box3
                    536871094,//	Box4
                    536871095,//	Box5
                    536871096,//	Assignee Name
                    536871104,//	Pending Time
                    536873620,//	Task Count
                    536873621,//	zD_New Parent ID
                    536873623,//	Priority-SLA Status
                    536873624,//	Last Update Date
                    536880941,//	Next zzEscalation
                    555550110,//	Application
                    620000056,//	zD_Application
                    620000073,//	Converted From
                    700001039,//	Req. Login ID
                    700001141,//	Auto-close Time
                    700001249,//	Source
                    700001261,//	VIP
                    700001544,//	Pending Reason
                    700003736,//	Hide Online
                    704000085,//	Request Type
                    704000089,//	_Current IM
                    704000090,//	_HD Mgmt Email
                    704000091,//	External Client
                    734873622,//	Ticket Aging
                    910000103,//	Business Organization
            };

            List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(user, "CMN:ConsolidatedList", reqfield, query, noofrecords, 3);
            return RemedyBase.remedyresultset(user, "CMN:ConsolidatedList", entries, request);
        } catch (Exception e) {
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return results;
    }


 /*   @RequestMapping(method = RequestMethod.POST, path = "/openapi/viewuserbasedclients")
    public ResponseEntity viewuserbasedclients(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            HttpServletRequest request
    ) {
        ARServerUser user;
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                user = arServerUser1;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }

            StringBuilder builder = new StringBuilder("");
            String query = RemedyBase.useraccessiblemasterclientcode(loginid, user);

            SQLResult
                    sqlResult = user.getListSQL(query, 0, true);
            if (sqlResult.getContents().size() > 0) {

                for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                    Map<String, String> appEntry = new HashMap<>();
                    appEntry.put("clientcodename", content.get(0).toString());
                    results.add(appEntry);
                }
            } else {
                throw new Exception(loginid + " is not a support user");
            }
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }*/


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/viewuserbasedclients")
    public ResponseEntity viewuserbasedclients1(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "empty") String client,
            HttpServletRequest request
    ) {
        ARServerUser user = new ARServerUser();
        String query = "";
        String sortfield;
        int[] groupreq;
        if (client.equalsIgnoreCase("empty")) {
            sortfield = "clientcodename";
            groupreq = new int[]{536871013, 105};
            query = "'Functional Type'=\"Support\" AND 'Master Client Code Name' != $NULL$ AND 'Status__c'=\"Active\"";
        } else {
            sortfield = "group";
            groupreq = new int[]{105, 536871013};
            query = "'Master Client Code Name' = \"" + client + "\" AND 'Functional Type'=\"Support\" AND 'Status__c'=\"Active\"";
        }

        List<Map<String, Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                user = arServerUser1;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }
            user = ITSMBase.impersonateuser(user, loginid);
            String formname = "Group+GRP:OPT+CMN:MSTR-Join";

            Map<String, List<String>> masterclient = ITSMBase.fetchclientmasterclient(arServerUser1);

            List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(user, formname, groupreq, query, Constants.AR_NO_MAX_LIST_RETRIEVE);

            results = ITSMBase.remedyresultsetforclient(entries);
            results = ITSMBase.distinctresultset(results, sortfield, "true", "");

            for (Map<String, Object> result : results) {
                result.put("subclients", masterclient.get(result.get("clientcodename")));
            }

            return new ResponseEntity(results, HttpStatus.OK);
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnErrorobject(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(user);
        }
    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/consolidatedticketsassignedtogroup")
    public ResponseEntity<List<Map<String, ? extends Object>>> consolidatedticketstoothergroups(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "maindisplayfilter", required = false, defaultValue = "") String maindisplayfilter,
            @RequestParam(value = "viewothergroup", required = false, defaultValue = "") String viewothergroup,
            @RequestParam(value = "tickettype", required = false, defaultValue = "") String application,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords,
            @RequestBody Map<String, Object> updateParam,
            HttpServletRequest request
    ) {
        ARServerUser user;
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            loginid = ITSMBase.escapesql(loginid);
            //   viewothergroup = ITSMBase.escapesql(viewothergroup);
            //   application = ITSMBase.escapesql(application);

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                user = arServerUser1;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }
            String formname = "CMN:ConsolidatedList";
            String filtercondition = "";
            String status = "";
            List<String> addstatuslist = new ArrayList<>();

            loginid = updateParam.get("loginid") != null ? updateParam.get("loginid").toString() : loginid;
            maindisplayfilter = updateParam.get("maindisplayfilter") != null ? updateParam.get("maindisplayfilter").toString() : maindisplayfilter;
            viewothergroup = updateParam.get("viewothergroup") != null ? updateParam.get("viewothergroup").toString() : viewothergroup;
            application = updateParam.get("tickettype") != null ? updateParam.get("tickettype").toString() : application;
            filtercondition = updateParam.get("filter_query") != null ? updateParam.get("filter_query").toString() : "";
            status = updateParam.get("status_display") != null ? updateParam.get("status_display").toString() : "";

            if (!status.isEmpty()) {
                addstatuslist = Arrays.asList(status.split(","));
            }
            String usergroups = usergroups(loginid, user);
            String userclients = RemedyBase.useraccessibleclient(loginid, user);

            String query = "Select \n" +
                    "\t\tCL.Create_Date, \n" +
                    "\t\tCL.Record_Entry_ID,\n" +
                    "\t\tCL.Summary,\n" +
                    "\t\tCL.Status_Display,\n" +
                    "\t\tCL.Assigned_To_Group_,\n" +
                    "\t\tCASE CL.Alert_Status\n" +
                    "\t\t\t WHEN 0 THEN 'Standard' \n" +
                    "\t\t\t WHEN 1 THEN 'Medium' \n" +
                    "\t\t\t WHEN 2 THEN 'High' \n" +
                    "\t\t\t WHEN 3 THEN 'Urgent' \n" +
                    "\t\t\t WHEN 4 THEN 'VIP' \n" +
                    "\t\t\t WHEN 5 THEN 'SLA Alert' \n" +
                    "\t\t\t WHEN 6 THEN 'SLA Missed' \n" +
                    "\t\t\t WHEN 7 THEN 'CST' END [Alert Status],\n" +
                    "\t\tCL.Client,\t\t\n" +
                    "\t\tCL.VIP,\n" +
                    "\t\tCL.Req__Login_ID,\n" +
                    "\t\tCASE CL.Priority WHEN 0 THEN 'Standard' WHEN 1 THEN 'Medium' WHEN 2 THEN 'High' WHEN 3 THEN 'Urgent' END Priority,\n" +
                    "\t\tCL.CST,\n" +
                    "\t\tCASE CL.Application\n" +
                    "\t\t\t WHEN 0 THEN 'Problem Tickets' \n" +
                    "\t\t\t WHEN 1 THEN 'Service Requests' \n" +
                    "\t\t\t WHEN 2 THEN 'Service Request Tasks' \n" +
                    "\t\t\t WHEN 3 THEN 'Asset Build Sheets' \n" +
                    "\t\t\t WHEN 4 THEN 'Production Change' \n" +
                    "\t\t\t WHEN 5 THEN 'Production Change Install Tasks' \n" +
                    "\t\t\t WHEN 6 THEN 'Production Change Approval Requests' \n" +
                    "\t\t\t WHEN 8 THEN 'Repair Ticket' \n" +
                    "\t\t\t WHEN 9 THEN 'Cablecare Approval'\n" +
                    "\t\t\t WHEN 10 THEN 'Production Change Backout Tasks'\n" +
                    "\t\t\t WHEN 11 THEN 'Root Cause Analyse'\n" +
                    "\t\t\t END  Application,\n" +
                    "\t\tCL.Assigned_To,\n" +
                    "\t\tCASE CL.Application WHEN 0 THEN  PT.FLR_Achieved WHEN 1 THEN SR.FLR_Achieved  END [FLR Acheived],\n" +
                    "\t\tCASE CL.Application WHEN 0 THEN PT.FLR_Possible WHEN 1 THEN SR.FLR_Possible END [FLR Possible],\n" +
                    "\t\tCASE CL.Application WHEN 0 THEN PTWL.Status WHEN 1 THEN SRWL.Status END  WatchListStatus,\n" +
                    "\t\tCASE WHEN (CL.req__Login_ID=\'" + loginid + "\' AND  Assigned_To_Group_  NOT IN (" + usergroups + ")) THEN 1 ELSE 0 END SelfFlag,\n" +
                    "CASE CL.Application WHEN 0 THEN  PT.Last_Name WHEN 1 THEN SR.Last_Name  END Last_Name,\n" +
                    "CASE CL.Application WHEN 0 THEN  PT.First_Name WHEN 1 THEN SR.First_Name  END First_Name,\n" +
                    "CASE CL.Application WHEN 0 THEN  PT.Network_Login WHEN 1 THEN SR.Network_Login  END Network_Login,\n" +
                    "CASE CL.Application WHEN 0 THEN  PT.Email_Address WHEN 1 THEN SR.Email_Address  END Email_Address,\n" +
                    "CL.Building," +
                    "CL.Department," +
                    "CL.Category," +
                    "CL.Type," +
                    "CL.ITEM " +
                    "FROM  CMN_ConsolidatedList CL WITH (NOLOCK)\n" +
                    "LEFT OUTER  JOIN SR_ServiceRequest SR WITH (NOLOCK)\n" +
                    "ON CL.Record_Entry_ID=SR.Request___\n" +
                    "LEFT OUTER  JOIN  PT_ProblemTicket PT WITH (NOLOCK)\n" +
                    "ON CL.Record_Entry_ID=PT.Case___\n" +
                    "LEFT OUTER  JOIN  CMN_WTC_PT_PT_Join PTWL WITH (NOLOCK)\n" +
                    "ON CL.Record_Entry_ID=PTWL.Case_ID AND PTWL.assigned_to=\'" + loginid + "\'" +
                    "LEFT OUTER  JOIN  CMN_WTC_SR_SR_Join SRWL WITH (NOLOCK)\n" +
                    "ON CL.Record_Entry_ID=SRWL.Case_ID  AND SRWL.assigned_to=\'" + loginid + "\' \n";

            String mdgqualification = "";
            if (viewothergroup == null || viewothergroup.isEmpty()) {
                StringBuilder builder = new StringBuilder("(CL.Assigned_To_Group_ IN (");

                if (usergroups != null && !usergroups.isEmpty()) {
                    builder.append(usergroups);
                    builder.append(") OR (CL.Req__Login_ID=\'" + loginid + "\')) ");
                } else {
                    throw new Exception(loginid + " not a Support User");
                }
                mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                mdgqualification += builder.toString();
            } else {
                mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                mdgqualification += "((CL.Assigned_To_Group_ = \'" + viewothergroup + "\' ) AND CL.Status_Integer = 1 )\n";
            }

            if (maindisplayfilter.equalsIgnoreCase("Urgent/High")) {
                mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                mdgqualification += " (CL.Application < 2 AND CL.Priority > 1 AND CL.Application <> 11 )";
            } else if (maindisplayfilter.equalsIgnoreCase("VIP/CST")) {
                mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                mdgqualification += " (CL.VIP = 1 OR CL.CST = 1)";
            } else if (maindisplayfilter.equalsIgnoreCase("Nearing/Past SLA")) {
                mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                mdgqualification += " (CL.Application < 2 AND CL.Alert_Status > 4 AND CL.Alert_Status < 7 AND CL.Application <> 11 )\n";
            } else {
                if ((maindisplayfilter == null || maindisplayfilter.isEmpty()) && viewothergroup != null && !viewothergroup.isEmpty()) {
                    if (!application.isEmpty()) {
                        mdgqualification = RemedyBase.validatequery(mdgqualification, addstatuslist, application);
                        mdgqualification += "(CL.Application IN (" + application + "))\n";
                    }
                }
            }
            SQLResult sqlResult;
            if (mdgqualification.isEmpty()) {
            } else {
                query += mdgqualification;
                if (!userclients.isEmpty()) {
                    query += " AND CL.Client IN (" + userclients + ")\n";
                }

                if (!filtercondition.isEmpty()) {
                    query += " AND " + filtercondition;
                }
                query += " ORDER BY CL.Create_Date DESC";
                sqlResult = user.getListSQL(query, 0, true);
                for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                    Map<String, String> appEntry = new HashMap<>();
                    appEntry.put("created_date", content.get(0).toString());
                    appEntry.put("record_entry_id", content.get(1).toString());
                    appEntry.put("summary", content.get(2).toString());
                    appEntry.put("status_display", content.get(3).toString());
                    appEntry.put("assigned_to_group_", content.get(4).toString());
                    appEntry.put("alert_status", content.get(5).toString());
                    appEntry.put("client", content.get(6).toString());
                    appEntry.put("vip", content.get(7).toString());
                    appEntry.put("req_login_id", content.get(8).toString());
                    appEntry.put("priority", content.get(9).toString());
                    appEntry.put("cst", content.get(10).toString());
                    appEntry.put("application", content.get(11).toString());
                    appEntry.put("assigned_to", content.get(12).toString());
                    appEntry.put("flrachieved", content.get(13).toString());
                    appEntry.put("flrpossible", content.get(14).toString());
                    appEntry.put("watchliststatus", content.get(15).toString());
                    appEntry.put("selfflag", content.get(16).toString());
                    appEntry.put("lastname", content.get(17).toString());
                    appEntry.put("firstname", content.get(18).toString());
                    appEntry.put("networklogin", content.get(19).toString());
                    appEntry.put("emailaddress", content.get(20).toString());
                    appEntry.put("building", content.get(21).toString());
                    appEntry.put("department", content.get(22).toString());
                    appEntry.put("category", content.get(23).toString());
                    appEntry.put("type", content.get(24).toString());
                    appEntry.put("item", content.get(25).toString());
                    results.add(appEntry);
                }
            }
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    public String usergroups(List<GroupInfo> groupInfo, String loginid) throws Exception {
        StringBuilder builder = new StringBuilder("");
        if (groupInfo != null) {
            for (int i = 0; i < groupInfo.size(); i++) {
                if (i == groupInfo.size() - 1)
                    builder.append("\'" + groupInfo.get(i).getName() + "\'");
                else
                    builder.append("\'" + groupInfo.get(i).getName() + "\',");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        return builder.toString();
    }

    public String usergroups(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select group_name from CMN_Notification_Assignments where login_id =\'" + loginid + "\' ";
        String clientquery = "select client from cmn_people_information where (Status = 2) AND login_id =\'" + loginid + "\' and support_person_=1";

        SQLResult sqlResult = user.getListSQL(query, 0, true);
        SQLResult sqlResultsupportperson = user.getListSQL(clientquery, 0, true);

        if (sqlResult.getContents().size() > 0 && sqlResultsupportperson.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("\'" + content.get(0).toString() + "\',");
            }
        } else if (sqlResultsupportperson.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("\'" + content.get(0).toString() + "\',");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }

    public String useraccountgroups(String loginid, ARServerUser user) throws Exception {
        StringBuilder builder = new StringBuilder("");

        String query = "select group_name from CMN_Notification_Assignments where (Functional_Type = 1) AND (Status = 2) AND login_id =\'" + loginid + "\' ";

        SQLResult sqlResult = user.getListSQL(query, 0, true);

        if (sqlResult.getContents().size() > 0) {
            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                builder.append("\'" + content.get(0).toString() + "\',");
            }
        } else {
            throw new Exception(loginid + " not a Support User");
        }
        builder.deleteCharAt(builder.toString().length() - 1);
        return builder.toString();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/ticketsummary")
    public ResponseEntity consolidatedticketstoothergroupsticketsummary(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid") String loginid,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords,
            @RequestParam(value = "group", required = false, defaultValue = "") String group,
            HttpServletRequest request
    ) {
        Map<String, Object> resultset = new HashMap<>();
        String[] srptticketstatus = {"Assigned", "Acknowledged", "Pending"};
        String[] crticketstatus = {"Requested", "Reviewing", "Scheduled"};
        ARServerUser user;

        group = (group != null ? group : "");
        String groupquery = "";

        if (group != null && !group.isEmpty()) {
            groupquery = " AND (CL.Assigned_To_Group_= '" + group + "') ";
        }
        Map<String, String> forms = new HashMap<>();
        forms.put("1", "srs");
        forms.put("0", "pts");
        //   forms.put("4", "crs");


        Map<String, String> typeofstatus = new HashMap<>();
        typeofstatus.put("My", "my_");
        typeofstatus.put("MyGroup", "my_group_");
        typeofstatus.put("RaisedByMe", "_raised_by_me");


        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                user = arServerUser1;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }
            String formname = "CMN:ConsolidatedList";

            String query = "Select 'MyGroup' typeofStatus,APPLICATION,status_display,COUNT(1) cnt\t\t\n" +
                    "FROM  CMN_ConsolidatedList CL WITH (NOLOCK)\n" +
                    "WHERE APPLICATION IN (0,1,4) AND \n" +
                    "\t\tstatus_display IN  ('Assigned','Pending','Acknowledged','Requested','Scheduled','Approved','Reviewing') AND (CL.Assigned_To_Group_ IS NOT NULL ) AND ";
            StringBuilder builder = new StringBuilder("(CL.Assigned_To_Group_ IN (");

            String usergroups = usergroups(loginid, user);
            builder.append(usergroups);
            String userclients = RemedyBase.useraccessibleclient(loginid, user);

            builder.append("))");
            builder.append(" AND (assigned_to!=\'" + loginid + "\' OR assigned_to IS NULL) \n");

            SQLResult sqlResult;
            if (usergroups == null || usergroups.isEmpty()) {
                throw new Exception(loginid + " not a support user");

            } else {
                query += builder.toString();
                if (!userclients.isEmpty()) {
                    query += " AND CL.Client IN ( " + userclients + " ) \n";
                }
                query += groupquery;
                query += " GROUP BY APPLICATION,status_display\n";

                query += " UNION ALL\n" +
                        "Select 'My' typeofStatus,APPLICATION,status_display,COUNT(1)\t\t\n" +
                        "FROM  CMN_ConsolidatedList CL WITH (NOLOCK)\n" +
                        "WHERE APPLICATION IN (0,1,4) AND \n" +
                        "\t\tstatus_display IN  ('Assigned','Pending','Acknowledged','Requested','Scheduled','Approved','Reviewing') AND (CL.Assigned_To_Group_ IS NOT NULL ) AND \n"
                        + "assigned_to=\'" + loginid + "\'   \n";

                if (!userclients.isEmpty()) {
                    query += " AND CL.Client IN ( " + userclients + " ) \n";
                }

                query += groupquery;
                query += " GROUP BY APPLICATION,status_display ";

                query += "UNION ALL\n" +
                        "Select 'RaisedByMe' typeofStatus,APPLICATION,status_display,COUNT(1)\t\t\n" +
                        "FROM  CMN_ConsolidatedList CL WITH (NOLOCK)\n" +
                        "WHERE APPLICATION IN (0,1,4) AND \n" +
                        "\t\tstatus_display IN  ('Assigned','Pending','Acknowledged','Requested','Scheduled','Approved','Reviewing') AND (CL.Assigned_To_Group_ IS NOT NULL ) "
                        + "AND Req__Login_Id=\'" + loginid + "\'  \n ";
                if (!userclients.isEmpty()) {
                    query += " AND CL.Client IN ( " + userclients + " ) \n";
                }
                query += groupquery;
                query += " GROUP BY APPLICATION,status_display \n ORDER BY typeofStatus,APPLICATION,status_display ";


                sqlResult = user.getListSQL(query, 0, true);

                for (Map.Entry<String, String> stringStringEntry : forms.entrySet()) {
                    Map<String, Map> mainappEntry = new HashMap<>();


                    for (Map.Entry<String, String> stringEntry : typeofstatus.entrySet()) {
                        Map<String, String> appEntry = new HashMap<>();
                        for (String s : srptticketstatus) {
                            Boolean ticketstatus = false;
                            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                                if (stringStringEntry.getKey().equalsIgnoreCase(content.get(1).toString()) && s.equalsIgnoreCase(content.get(2).toString()) && stringEntry.getKey().equalsIgnoreCase(content.get(0).toString())) {
                                    appEntry.put(s, content.get(3).toString());
                                    ticketstatus = true;
                                }
                            }
                            if (!ticketstatus) {
                                appEntry.put(s, "0");
                            }
                        }
                        String key = stringEntry.getValue().startsWith("_") ? stringStringEntry.getValue() + stringEntry.getValue() : stringEntry.getValue() + stringStringEntry.getValue();
                        mainappEntry.put(key, appEntry);
                    }
                    resultset.put(stringStringEntry.getValue(), mainappEntry);
                }

                forms.clear();
                forms.put("4", "crs");
                typeofstatus.clear();
                typeofstatus.put("My", "my_");

                for (Map.Entry<String, String> stringStringEntry : forms.entrySet()) {
                    Map<String, Map> mainappEntry = new HashMap<>();


                    for (Map.Entry<String, String> stringEntry : typeofstatus.entrySet()) {
                        Map<String, String> appEntry = new HashMap<>();
                        for (String s : crticketstatus) {
                            Boolean ticketstatus = false;
                            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                                if (stringStringEntry.getKey().equalsIgnoreCase(content.get(1).toString()) && s.equalsIgnoreCase(content.get(2).toString()) && stringEntry.getKey().equalsIgnoreCase(content.get(0).toString())) {
                                    appEntry.put(s, content.get(3).toString());
                                    ticketstatus = true;
                                }
                            }
                            if (!ticketstatus) {
                                appEntry.put(s, "0");
                            }
                        }
                        String key = stringEntry.getValue().startsWith("_") ? stringStringEntry.getValue() + stringEntry.getValue() : stringEntry.getValue() + stringStringEntry.getValue();
                        mainappEntry.put(key, appEntry);
                    }
                    resultset.put(stringStringEntry.getValue(), mainappEntry);
                }
            }
        } catch (Exception e) {
            resultset = new HashMap<>();
            Map<String, Object> appEntry = new HashMap<>();
            appEntry.put("error", e.getLocalizedMessage());
            resultset.put("Exception", appEntry);
            return new ResponseEntity(resultset, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(resultset, HttpStatus.OK);
    }
/*
   @RequestMapping(method = RequestMethod.POST, path = "/openapi/consolidatedticketsassignedtogroup")
    public List<Map<String, ? extends Object>> consolidatedticketstoothergroups(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "maindisplayfilter", required = false, defaultValue = "") String maindisplayfilter,
            @RequestParam(value = "viewothergroup", required = false, defaultValue = "") String viewothergroup,
            @RequestParam(value = "application", required = false, defaultValue = "") String application,
            @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") int noofrecords,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }
            String formname = "CMN:ConsolidatedList";
            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);
            String query = "(('Assigned To' !=\"" + loginid + "\") OR ('Assigned To' = $NULL$)) AND ";

            int[] reqfield = new int[]{
                    1,//	Request ID
                    4,//	Assigned To
                    8,//	Summary
                    112,//	zAssigned Group
                    88888888,//	zD_Workspace
                    200000003,//	Category
                    200000004,//	Type
                    200000005,//	Item
                    200000006,//	Department
                    200000007,//	Building
                    200000012,//	Client
                    230000009,//	Last Update User
                    240000006,//	Assigned To Group+
                    260000000,//	Status Display
                    260000005,//	Record Entry ID
                    260000009,//	Status Integer
                    260000126,//	Priority
                    260000127,//	Dup ID
                    536870913,//	Record Created
                    536870918,//	IM Flag
                    536870919,//	Group ID
                    536870922,//	View Other Group
                    536870951,//	Long Term Change
                    536871033,//	zSubmitter Group
                    536871038,//	Alert Status
                    536871096,//	Assignee Name
                    536871104,//	Pending Time
                    536873620,//	Task Count
                    536873623,//	Priority-SLA Status
                    536873624,//	Last Update Date
                    536880941,//	Next zzEscalation
                    555550110,//	Application
                    700001039,//	Req. Login ID
                    700001141,//	Auto-close Time
                    700001261,//	VIP
                    700001544,//	Pending Reason
                    700003736,//	Hide Online
                    704000085,//	Request Type
                    704000091,//	External Client
                    734873622,//	Ticket Aging
                    910000103,//	Business Organization
            };
            String mdgqualification = "";
            if (maindisplayfilter == null || maindisplayfilter.isEmpty() && viewothergroup != null && !viewothergroup.isEmpty()) {
                String[] applications = application.split(",");
                List<String> apps = Arrays.asList(applications);

                for (int i = 0; i < apps.size(); i++) {
                    if (i == 0) {
                        mdgqualification += " AND (('Application' = \"" + apps.get(i) + "\") ";
                    } else
                        mdgqualification += " OR ('Application' = \"" + apps.get(i) + "\")";
                    if (i == apps.size() - 1)
                        mdgqualification += " OR ('Application' = \"" + apps.get(i) + "\") )";
                }
            }

            if (viewothergroup == null || viewothergroup.isEmpty()) {
                formname = "CMN:NOT+CMN:CL-IJ";
                mdgqualification = " 'Login ID'=\"" + loginid + "\"";
            } else {
                mdgqualification = "(('Assigned To Group+' = \"" + viewothergroup + "\" ) AND 'Status Integer' = 1 )";
            }
            query += mdgqualification;

            if (maindisplayfilter == null || maindisplayfilter.isEmpty()) {
                query += "";
            } else {
                query += " AND ";
            }
            if (maindisplayfilter.equalsIgnoreCase("Urgent/High")) {
                query += " ('Application' < \"Service Request Tasks\" AND 'Priority' > \"Medium\" AND 'Application' <> \"Root Cause Analyse\")";
            } else if (maindisplayfilter.equalsIgnoreCase("VIP/CST")) {
                query += " ('VIP' = \"Yes\" OR 'CST' = \"Yes\")";
            } else if (maindisplayfilter.equalsIgnoreCase("Nearing/Past SLA")) {
                query += " ('Application' < \"Service Request Tasks\" AND 'Alert Status' > \"VIP\" AND 'Alert Status' < \"CST\" AND 'Application' <> \"Root Cause Analyse\")";
            }

            List<Entry> entries = RemedyBase.queryEntrysByQualWithNoOfRecordswithsort(user, formname, reqfield, query, noofrecords, 536870913);
            return RemedyBase.remedyresultset(user, formname, entries, request);
        } catch (Exception e) {
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return results;
    }
*/

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, path = "/openapi/iamstaging")
    public List<Map<String, ? extends Object>> iamstaging(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("USER_GUID") String userguid,
            @RequestParam(value = "client", required = false, defaultValue = "") String client
    ) {
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser user = RemedyBase.loginUser(serverName, port, username, password);

            return RemedyBase.iamstaging(
                    user,
                    userguid, client
            );

        } catch (Exception e) {
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return results;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/rolematrix")
    public List<Map<String, ? extends Object>> rolematrix(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("role") String role,
            @RequestParam("roleprefix") String roleprefix,
            @RequestParam(value = "costcode", required = false, defaultValue = "$NULL$") String costcode,
            @RequestParam("templateinstanceid") String templateinstanceid
    ) {
        List<Map<String, ? extends Object>> rolematrixs = new ArrayList<>();

        int[] rolesrequiredfield = {179    //Unique Identifier

        };
        int[] roledetailrequiredfield = {8,//	Role Unique Identifier
                536870916,//	Template instance ID
                536870920,//	Question instance ID
                536870932//	Change To
        };
        int[] question = {179,//instanceId
                700000850,    //SurveyInstanceID
                700060050,    //CSSStyleClass
                700000003,    //Question
                700060003,    //QuestionLabel
                700000008,    //Choice_Type
                700073060    //Display_Style
        };
        int[] questionchoice = {700000820,//	Label
                700000850,//	SurveyInstanceID
                700000800//	Choice_Type
        };
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            if (!costcode.equals("$NULL$")) {
                costcode = "\"" + costcode + "\"";
            }

            String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + ") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            //String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + "\") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            List<Entry> roles = null;
            roles = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Roles", rolesrequiredfield, rolequery
            );

            if (!roles.isEmpty()) {
                List<Entry> roledetails = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Role_Details", roledetailrequiredfield,
                        " (('Template instance ID'=\"" + templateinstanceid + "\") AND ('Role Unique Identifier' = \"" + roles.get(0).get((rolesrequiredfield[0])).toString() + "\"))"
                );

                StringBuilder instanceid = new StringBuilder("(");
                for (Entry roledet : roledetails) {
                    if (roledet.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(roledetails.get(0).get(roledetailrequiredfield[2]).toString()))
                        instanceid.append("('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");

                    else
                        instanceid.append(" OR ('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");
                }

                instanceid.append(")");
                instanceid.append("AND ('Display_Style' =$NULL$) AND ('SurveyInstanceID' =\"" + templateinstanceid + "\")");

                List<Entry> surveyquestions = RemedyBase.queryEntrysByQual(arServerUser, "KS_SRV_SurveyQuestion", question, instanceid.toString()
                );

                StringBuilder choicetype = new StringBuilder("(");
                for (Entry survey : surveyquestions) {
                    if (survey.get(question[5]).toString().equalsIgnoreCase(surveyquestions.get(0).get(question[5]).toString()))
                        choicetype.append("('Choice_Type' = \"" + survey.get(question[5]).toString() + "\")");
                    else
                        choicetype.append(" OR ('Choice_Type' = \"" + survey.get(question[5]).toString() + "\")");
                }
                choicetype.append(")");
                choicetype.append("AND ('SurveyInstanceID' =\"" + templateinstanceid + "\")");
                List<Entry> surveyquestionchoices = RemedyBase.queryEntrysByQual(arServerUser, "KS_SRV_SurveyQuestionChoice", questionchoice,
                        choicetype.toString()
                );

                if (surveyquestionchoices != null && surveyquestionchoices.size() > 0) {

                    for (Entry surveyquestionchoice : surveyquestionchoices) {
                        Map<String, String> rolematrix = new HashMap<>();

                        rolematrix.put("label", surveyquestionchoice.get(questionchoice[0]).toString());

                        for (Entry surveyquetion : surveyquestions) {

                            if (surveyquetion.get(question[5]).toString().equalsIgnoreCase(surveyquestionchoice.get(questionchoice[2]).toString())) {
                                rolematrix.put("cssstyleclass", surveyquetion.get(question[2]).toString());
                                rolematrix.put("question", surveyquetion.get(question[3]).toString());
                                rolematrix.put("questionlabel", surveyquetion.get(question[4]).toString());
                                rolematrix.put("displaystyle", surveyquetion.get(question[6]).toString());

                                for (Entry roledetail : roledetails) {
                                    if (roledetail.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(surveyquetion.get(question[0]).toString())) {
                                        rolematrix.put("questioninstanceid", roledetail.get(roledetailrequiredfield[2]).toString());
                                        rolematrix.put("changeto", roledetail.get(roledetailrequiredfield[3]).toString());
                                    }
                                }
                            }
                        }
                        rolematrixs.add(rolematrix);
                    }
                }
            } else {
                throw new Exception("Role does not exist");
            }
        } catch (Exception e) {
            rolematrixs.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }
        return rolematrixs;
    }


   /* @RequestMapping(method = RequestMethod.POST, path = "/openapi/rolematrix")
    public List<Map<String, ? extends Object>> rolex(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("role") String role,
            @RequestParam("roleprefix") String roleprefix,
            @RequestParam(value = "costcode", required = false, defaultValue = "$NULL$") String costcode,
            @RequestParam("templateinstanceid") String templateinstanceid,
            @Value("${app.excelpath}") String excel
    ) {
        List<Map<String, ? extends Object>> rolematrixs = new ArrayList<>();

        int[] rolesrequiredfield = {179    //Unique Identifier

        };
        int[] roledetailrequiredfield = {8,//	Role Unique Identifier
                536870916,//	Template instance ID
                536870920,//	Question instance ID
                536870932//	Change To


        };
        int[] question = {179,//instanceId
                700000850,    //SurveyInstanceID
                700060050,    //CSSStyleClass
                700000003,    //Question
                700060003,    //QuestionLabel
                700000008,    //Choice_Type
                700073060    //Display_Style


        };
        int[] questionchoice = {700000820,//	Label
                700000850,//	SurveyInstanceID
                700000800//	Choice_Type
        };
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            String fileLocation = "controlidmapping.xls";
            File serverFile = new File(excel);

            Map<String, String> questionmap = RemedyBase.excelread(serverFile);

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            if (!costcode.equals("$NULL$")) {
                costcode = "\"" + costcode + "\"";
            }

            String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + ") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            //String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + "\") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            List<Entry> roles = null;
            roles = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Roles", rolesrequiredfield, rolequery
            );

            if (!roles.isEmpty()) {
                List<Entry> roledetails = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Role_Details", roledetailrequiredfield,
                        " (('Template instance ID'=\"" + templateinstanceid + "\") AND ('Role Unique Identifier' = \"" + roles.get(0).get((rolesrequiredfield[0])).toString() + "\"))"
                );

                StringBuilder instanceid = new StringBuilder("(");
                for (Entry roledet : roledetails) {
                    if (roledet.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(roledetails.get(0).get(roledetailrequiredfield[2]).toString()))
                        instanceid.append("('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");

                    else
                        instanceid.append(" OR ('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");
                }

                instanceid.append(")");
                instanceid.append("AND ('Display_Style' =$NULL$) AND ('SurveyInstanceID' =\"" + templateinstanceid + "\")");

                List<Entry> surveyquestions = RemedyBase.queryEntrysByQual(arServerUser, "KS_SRV_SurveyQuestion", question, instanceid.toString()
                );

                StringBuilder choicetype = new StringBuilder("(");
                for (Entry survey : surveyquestions) {
                    if (survey.get(question[5]).toString().equalsIgnoreCase(surveyquestions.get(0).get(question[5]).toString()))
                        choicetype.append("('Choice_Type' = \"" + survey.get(question[5]).toString() + "\")");
                    else
                        choicetype.append(" OR ('Choice_Type' = \"" + survey.get(question[5]).toString() + "\")");
                }
                choicetype.append(")");
                choicetype.append("AND ('SurveyInstanceID' =\"" + templateinstanceid + "\")");
                List<Entry> surveyquestionchoices = RemedyBase.queryEntrysByQual(arServerUser, "KS_SRV_SurveyQuestionChoice", questionchoice,
                        choicetype.toString()
                );


                if (surveyquestionchoices != null && surveyquestionchoices.size() > 0) {

                    for (Entry surveyquestionchoice : surveyquestionchoices) {
                        Map<String, String> rolematrix = new HashMap<>();

                        rolematrix.put("label", surveyquestionchoice.get(questionchoice[0]).toString());

                        for (Entry surveyquetion : surveyquestions) {

                            if (surveyquetion.get(question[5]).toString().equalsIgnoreCase(surveyquestionchoice.get(questionchoice[2]).toString())) {
                                rolematrix.put("cssstyleclass", surveyquetion.get(question[2]).toString());
                                rolematrix.put("question", surveyquetion.get(question[3]).toString());
                                rolematrix.put("questionlabel", surveyquetion.get(question[4]).toString());
                                rolematrix.put("displaystyle", surveyquetion.get(question[6]).toString());

                                for (Entry roledetail : roledetails) {
                                    if (roledetail.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(surveyquetion.get(question[0]).toString())) {
                                        rolematrix.put("questioninstanceid", roledetail.get(roledetailrequiredfield[2]).toString());
                                        rolematrix.put("changeto", roledetail.get(roledetailrequiredfield[3]).toString());

                                    }
                                }
                            }
                        }
                        rolematrixs.add(rolematrix);
                    }
                }
            } else {
                throw new Exception("Role does not exist");
            }
        } catch (Exception e) {
            rolematrixs.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return rolematrixs;


    }
*/

    @RequestMapping(method = RequestMethod.POST, path = "/openapi/roles")
    public List<Map<String, ? extends Object>> roles(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "uniqueidentifier", required = false, defaultValue = "$NULL$") String
                    uniqueidentifier,
            @Value("${app.excelpath}") String excel

    ) {
        List<Map<String, ? extends Object>> rolematrixs = new ArrayList<>();

        int[] rolesrequiredfield = {179,//Unique Identifier
                536870916//template instance id
        };
        int[] roledetailrequiredfield = {8,//	Role Unique Identifier
                536870916,//	Template instance ID
                536870920,//	Question instance ID
                536870932,//	Change To
                536870933,//Execution Order
                536870930,//syntheticChangeEvent
                536870913,//Read Only


        };
        int[] question = {179,//instanceId
                700000850,    //SurveyInstanceID
                700060050,    //CSSStyleClass
                700000003,    //Question
                700060003,    //QuestionLabel
                700000008,    //Choice_Type
                700073060    //Display_Style


        };
        int[] questionchoice = {700000820,//	Label
                700000850,//	SurveyInstanceID
                700000800//	Choice_Type
        };
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            String fileLocation = "controlidmapping.xls";
            File serverFile = new File(excel);

            Map<String, String> questionmap = RemedyBase.excelread(serverFile);


            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

           /* if (!costcode.equals("$NULL$")) {
                costcode = "\"" + costcode + "\"";
            }*/

            // String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + ") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            String rolequeryduplicate = "('Unique Identifier'=\"" + uniqueidentifier + "\")";
            //String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + "\") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            List<Entry> roles = null;
            roles = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Roles", rolesrequiredfield, rolequeryduplicate
            );
            String templateinstanceid = null;

            if (!roles.isEmpty()) {
                templateinstanceid = roles.get(0).get((rolesrequiredfield[1])).toString();
                List<Entry> roledetails = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Role_Details", roledetailrequiredfield,
                        " (('Template instance ID'=\"" + templateinstanceid + "\") AND ('Role Unique Identifier' = \"" + roles.get(0).get((rolesrequiredfield[0])).toString() + "\"))"
                );

                StringBuilder instanceid = new StringBuilder("(");
                for (Entry roledet : roledetails) {
                    if (roledet.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(roledetails.get(0).get(roledetailrequiredfield[2]).toString()))
                        instanceid.append("('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");

                    else
                        instanceid.append(" OR ('instanceId' = \"" + roledet.get(roledetailrequiredfield[2]).toString() + "\")");
                }

                instanceid.append(")");

                List<Entry> surveyquestions = RemedyBase.queryEntrysByQual(arServerUser, "KS_SRV_SurveyQuestion", question, instanceid.toString()
                );


                for (Entry surveyquetion : surveyquestions) {
                    Map<String, String> rolematrix = new HashMap<>();

                    rolematrix.put("question", surveyquetion.get(question[3]).toString());
                    rolematrix.put("questionlabel", surveyquetion.get(question[4]).toString());
                    rolematrix.put("displaystyle", surveyquetion.get(question[6]).toString());
                    rolematrix.put("choicetype", surveyquetion.get(question[5]).toString());

                    for (Entry roledetail : roledetails) {
                        if (roledetail.get(roledetailrequiredfield[2]).toString().equalsIgnoreCase(surveyquetion.get(question[0]).toString())) {
                            rolematrix.put("questioninstanceid", roledetail.get(roledetailrequiredfield[2]).toString());
                            rolematrix.put("roleuniqueidentifier", roledetail.get(roledetailrequiredfield[0]).toString());
                            rolematrix.put("changeto", roledetail.get(roledetailrequiredfield[3]).toString());
                            rolematrix.put("readonly", roledetail.get(roledetailrequiredfield[6]).toString());
                            rolematrix.put("syntheticchange", roledetail.get(roledetailrequiredfield[5]).toString());
                            rolematrix.put("executionorder", roledetail.get(roledetailrequiredfield[4]).toString());
                            if (questionmap.containsKey(roledetail.get(536870920).toString())) {
                                rolematrix.put("controlid", questionmap.get(roledetail.get(536870920).toString()));
                            } else {
                                rolematrix.put("controlid", "");
                            }

                        }

                    }
                    rolematrixs.add(rolematrix);
                }

            } else {
                throw new Exception("Role does not exist");
            }
        } catch (Exception e) {
            rolematrixs.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return rolematrixs;


    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/rolematrixcustomized")
    public List<Map<String, ? extends Object>> rolematrixcustom(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("uniqueidentifier") String roleuniqueidentifier,
            @Value("${app.excelpath}") String excel
           /* @RequestParam("role") String role,
            @RequestParam("roleprefix") String roleprefix,
            @RequestParam(value = "costcode", required = false, defaultValue = "$NULL$") String costcode,
            @RequestParam("templateinstanceid") String templateinstanceid*/
    ) {
        List<Map<String, ? extends Object>> rolematrixs = new ArrayList<>();

        int[] rolesrequiredfield = {179    //Unique Identifier

        };
        int[] roledetailrequiredfield = {8,//	Role Unique Identifier
                536870916,//	Template instance ID
                536870920,//	Question instance ID
                536870932//	Change To


        };
        int[] question = {179,//instanceId
                700000850,    //SurveyInstanceID
                700060050,    //CSSStyleClass
                700000003,    //Question
                700060003,    //QuestionLabel
                700000008,    //Choice_Type
                700073060    //Display_Style


        };
        int[] questionchoice = {700000820,//	Label
                700000850,//	SurveyInstanceID
                700000800//	Choice_Type
        };
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }


            String fileLocation = "controlidmapping.xls";
            File serverFile = new File(excel);

            Map<String, String> questionmap = RemedyBase.excelread(serverFile);

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

/*            if (!costcode.equals("$NULL$")) {
                costcode = "\"" + costcode + "\"";
            }

            String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + ") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            //String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + "\") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            List<Entry> roles = null;
            roles = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Roles", rolesrequiredfield, rolequery
            );*/


            List<Entry> roledetails = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Role_Details", roledetailrequiredfield,
                    "('Role Unique Identifier' = \"" + roleuniqueidentifier + "\")"
            );

            for (Entry roledetail : roledetails) {
                Map<String, String> rolematrix = new HashMap<>();
                rolematrix.put("Question", roledetail.get(536870920).toString());
                rolematrix.put("ChangeTo", roledetail.get(536870932).toString());
                // rolematrix.put("ControlId", roledetail.get(536870920).toString());
                if (questionmap.containsKey(roledetail.get(536870920).toString())) {
                    rolematrix.put("controlid", questionmap.get(roledetail.get(536870920).toString()));
                } else {
                    rolematrix.put("controlid", "");
                }

                rolematrixs.add(rolematrix);
            }


        } catch (Exception e) {
            e.printStackTrace();
            //rolematrixs.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return rolematrixs;


    }

/*
    @RequestMapping(method = RequestMethod.POST, path = "/openapi/rolematrixs")
    public Map<String, ? extends Object> rolematrixcustoms(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("uniqueidentifier") String roleuniqueidentifier,
            @Value("${app.excelpath}") String excel
           *//* @RequestParam("role") String role,
            @RequestParam("roleprefix") String roleprefix,
            @RequestParam(value = "costcode", required = false, defaultValue = "$NULL$") String costcode,
            @RequestParam("templateinstanceid") String templateinstanceid*//*
    ) {
        List<Map<String, ? extends Object>> rolematrixs = new ArrayList<>();

        int[] rolesrequiredfield = {179    //Unique Identifier

        };
        int[] roledetailrequiredfield = {8,//	Role Unique Identifier
                536870916,//	Template instance ID
                536870920,//	Question instance ID
                536870932//	Change To


        };
        int[] question = {179,//instanceId
                700000850,    //SurveyInstanceID
                700060050,    //CSSStyleClass
                700000003,    //Question
                700060003,    //QuestionLabel
                700000008,    //Choice_Type
                700073060    //Display_Style


        };
        int[] questionchoice = {700000820,//	Label
                700000850,//	SurveyInstanceID
                700000800//	Choice_Type
        };
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }


            String fileLocation = "controlidmapping.xls";
            File serverFile = new File(excel);

            Map<String, String> questionmap = RemedyBase.excelread(serverFile);

            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

*//*            if (!costcode.equals("$NULL$")) {
                costcode = "\"" + costcode + "\"";
            }

            String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + ") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            //String rolequery = "(('Role Prefix' = \"" + roleprefix + "\") AND ('Cost Code' = " + costcode + "\") AND ('Role' =\"" + role + "\") AND ('Template instance ID'=\"" + templateinstanceid + "\"))";
            List<Entry> roles = null;
            roles = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Roles", rolesrequiredfield, rolequery
            );*//*


            List<Entry> roledetails = RemedyBase.queryEntrysByQual(arServerUser, "CTS:KS_RQT_Role_Details", roledetailrequiredfield,
                    "('Role Unique Identifier' = \"" + roleuniqueidentifier + "\")"
            );

            for (Entry roledetail : roledetails) {
                Map<String, String> rolematrix = new HashMap<>();
                rolematrix.put("Question", roledetail.get(536870920).toString());
                rolematrix.put("ChangeTo", roledetail.get(536870932).toString());
                // rolematrix.put("ControlId", roledetail.get(536870920).toString());
                if (questionmap.containsKey(roledetail.get(536870920).toString())) {
                    rolematrix.put("controlid", questionmap.get(roledetail.get(536870920).toString()));
                } else {
                    rolematrix.put("controlid", "");
                }

                rolematrixs.add(rolematrix);
            }

            Map<String,String> formbuilder =new HashMap<>();
            if(!rolematrixs.isEmpty()&&rolematrixs.size()>0){
            for (Map<String, ? extends Object> rolematrix : rolematrixs) {
                if (formbuilder.get(rolematrix.get("controlid")) == null) {
                    StringBuilder builder=new StringBuilder("");
                    for (Map<String, ? extends Object> stringMap : rolematrixs) {
builder.append(rolematrix.get())
                    }
                }

            }
        }
        }
        catch (Exception e) {
            e.printStackTrace();
            //rolematrixs.add(RemedyBase.returnError(e.getLocalizedMessage()));
        }

        return rolematrixs;


    }*/


    class groups {

        String groupid;
        String groupname;
        String client;

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/openapi/getsrtgroups", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ticketInfowithhistory
            (@RequestHeader(value = "username", required = false) String username,
             @RequestHeader(value = "password", required = false) String password,
             @RequestHeader(value = "atoken", required = false) String atoken) {
        Map<String, Object> response = new TreeMap<>();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                username = dusername;
                password = dpassword;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                username = username;
                password = password;
            } else {
                throw new Exception("Authorization error");
            }

            int[] jointablefields = {
                    105,//	Group Name
                    106,//	Group ID
                    200000012,//	Client
                    536870917,//	Group Type
                    536871028,//	Master Client
            };

            int[] clientfields = {
                    536870913,//	Client
                    536871013,//	Client Code Name
            };
            String groupformname = "Group+GRP:OPT-Join", clientformname = "CMN:ClientInfo";
            ARServerUser arServerUser = RemedyBase.loginUser(serverName, port, username, password);

            List<Entry> groupentries = RemedyBase.queryEntrysByQualwithmaxrecords(arServerUser, groupformname, jointablefields, "('Functional Type' = \"Support\") AND ('Status' = \"Active\")");
            List<Entry> cliententries = RemedyBase.queryEntrysByQualwithmaxrecords(arServerUser, clientformname, clientfields, "('Status' = \"Container\") OR ('Status' = \"Active\")");

            cliententries.forEach(client -> {

                List<groups> clientgroupentry = new ArrayList<>();
                groupentries.forEach(group -> {
                    if (group.get(200000012).toString().equalsIgnoreCase(client.get(536870913).toString())) {
                        groups tempgroup = new groups();
                        tempgroup.setClient(group.get(200000012).toString());
                        tempgroup.setGroupid(group.get(106).toString());
                        tempgroup.setGroupname(group.get(105).toString());
                        clientgroupentry.add(tempgroup);
                    }
                });
                if (!clientgroupentry.isEmpty())
                    response.put(client.get(536871013).toString(), clientgroupentry);

            });

        } catch (Exception e) {
            e.getMessage();
        }


        return response;
    }


    @RequestMapping(method = RequestMethod.POST, path = "/openapi/jhhsrequesttype")
    public ResponseEntity jhhsrequesttype(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("TemplateInstanceID") String processcode
    ) {
        ARServerUser user = new ARServerUser();
        List response = new ArrayList();
        Map<String, String> results = new HashMap<>();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }
            String query = "SELECT DISTINCT Criteria_1 [Short Description]\n" +
                    "FROM CTSSP_CTI cti\n" +
                    "INNER JOIN  CMN_ClientInfo Cl_If\n" +
                    "ON cti.Criteria_1=Cl_If.Client\n" +
                    "WHERE cti.Status=0\n" +
                    "AND Process_Code='" + processcode + "'\n" +
                    " AND Cl_If.Status=0\n";

            SQLResult sqlResult;

            sqlResult = user.getListSQL(query, 0, true);


            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                results = new HashMap<>();
                results.put("Short_Description", content.get(0).toString());
                response.add(results);
            }

        } catch (Exception e) {
            response.clear();
            results.put("error", e.getMessage());
            response.add(results);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }


}











