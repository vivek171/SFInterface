package com.htc.remedy.controller;


import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EntryValueList;
import com.bmc.arsys.api.SQLResult;
import com.google.gson.JsonArray;
import com.htc.remedy.base.ITSMBase;
import com.htc.remedy.base.LoggerBase;
import com.htc.remedy.base.RemedyBase;
import com.htc.remedy.base.SSOBase;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.repo.EndPointRepo;
import com.htc.remedy.repo.FieldRepo;
import com.htc.remedy.repo.QualificationRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping(path = "/itsm")
public class ITSMController {

    @Value("${remedy.host}")
    String serverName;
    @Value("${remedy.port}")
    Integer port;
    @Value("${ctsspi.username}")
    String dusername;

    @Value("${ctsspi.password}")
    String dpassword;

    @Value("${ctsspi.hash}")
    String atoken1;

    @Value("${support.groups.id}")
    String supportgroupids;

    @Value("${ctsspi.fileattachment}")
    String fileattachmentpath;

    private final
    EndPointRepo endPointRepo;

    final
    QualificationRepo qualificationRepo;

    final
    FieldRepo fieldRepo;


    private final
    ARServerUser adminuser;


    public ITSMController(EndPointRepo endPointRepo, QualificationRepo qualificationRepo, FieldRepo fieldRepo, ARServerUser adminuser) {
        this.endPointRepo = endPointRepo;
        this.qualificationRepo = qualificationRepo;
        this.fieldRepo = fieldRepo;
        this.adminuser = adminuser;
    }


    @RequestMapping(path = "/createformentrybyid1", method = RequestMethod.POST)
    public ResponseEntity createFormEntryByIsdfD(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "formName", required = false) String formName,
            HttpServletRequest ticketFields,
            org.springframework.web.multipart.MultipartHttpServletRequest request) {
        Map<String, String> srResult = new HashMap<>();
        Map<String, String> inputmap = new HashMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        Entry entryData = new Entry();
        String loginid = "", emailformName = "CMN:Email Correspondence";
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Auth Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            for (Map.Entry<String, String[]> stringEntry : ticketFields.getParameterMap().entrySet()) {
                if (stringEntry.getKey().equalsIgnoreCase("formName")) {
                    formName = stringEntry.getValue()[0];
                } else {
                    inputmap.put(stringEntry.getKey(), stringEntry.getValue()[0]);
                }
            }
            if (formName.isEmpty() || formName == null) {
                throw new Exception("formName is required!");
            }
            String entityId = "";
            if (formName.equalsIgnoreCase(emailformName)) {
                Map<String, Object> resultset = new HashMap<>();
                String sourceguid = "";
                entryData = ITSMBase.mapinsertrecordsforcorrespondence(ticketFields, inputmap, loggedinuser, emailformName, request);
                entityId = ITSMBase.createTicket(loggedinuser, emailformName, entryData);
                resultset = RemedyBase.getEntry(adminuser, formName, entityId);
                sourceguid = resultset.get("GUID").toString();
                ITSMBase.insertattachmentforemailcorrespondence(ticketFields, request, loggedinuser, sourceguid);
            } else {
                entryData = ITSMBase.mapinsertrecords(ticketFields, inputmap, loggedinuser, formName, request);
                entityId = ITSMBase.createTicket(loggedinuser, formName, entryData);
            }
            Map<String, Object> iId = RemedyBase.getSingleEntryWithFields(adminuser, formName, entityId, new int[]{179});
            srResult.put("EntityId", entityId);
            if (iId != null && iId.containsKey("instanceId")) {
                srResult.put("InstanceID", iId.get("instanceId").toString());
            }
            LoggerBase.loguserrecords(loggedinuser, entityId, entityId, formName, Constants.INSERT, request);
        } catch (Exception e) {
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
            return new ResponseEntity(srResult, HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(srResult, HttpStatus.OK);
    }


    @RequestMapping(path = "/createformentrybyid", method = RequestMethod.POST)
    public ResponseEntity createFormEntryByID(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam("formName") String formName,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestBody Map<String, Object> ticketFields, HttpServletRequest request) {
        Map<String, Object> srResult = new HashMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            if (formName.equalsIgnoreCase("CMN:PrimarySupportSchedule")) {
                ticketFields = ITSMBase.validatesupportscheduleinsert(adminuser, ticketFields, loginid, group);
            }

            Entry entryData = ITSMBase.parseentryvalueforinsert(ticketFields);

            String entityId = ITSMBase.createTicket(loggedinuser, formName, entryData);

            srResult.put("EntityId", entityId);

            if (formName.equalsIgnoreCase("CMN:TrackPatientInfoViewers")) {
                srResult.put("audit_trial", ITSMBase.audittrialforsecurepatientinfo(entryData.get(600000002).toString(), adminuser, request));
            }
            LoggerBase.loguserrecords(loggedinuser, entityId, ITSMBase.gsonstojson(ticketFields), formName, Constants.INSERT, request);
        } catch (Exception e) {
            srResult.put("EntityId", "");
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
            return new ResponseEntity(srResult, HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(srResult, HttpStatus.OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/customupdateticketbyid")
    public ResponseEntity updateticketduplicatebyid(
            HttpServletRequest request,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "groupid", required = false, defaultValue = "") String groupid,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestBody Map<String, String> updateParam) {

        String formName = null, ticketId = null;
        ARServerUser loggedinuser = new ARServerUser();

        Map<String, Object> response = new HashMap<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            formName = ITSMBase.validateformnameexist(updateParam);
            ticketId = ITSMBase.validateticketidexist(updateParam);

            updateParam = ITSMBase.removeformnameticketidparams(updateParam);

            if (formName.equalsIgnoreCase("CMN:PrimarySupportSchedule")) {//condition for primarysupportschedule
                updateParam = ITSMBase.validatesupportscheduleupdate(adminuser, updateParam, loginid, group, groupid);
                response = ITSMBase.customupdateTicketbyid(loggedinuser, updateParam, formName, ticketId, request);
            } else if ((formName.equalsIgnoreCase("sr:servicerequest") || formName.equalsIgnoreCase("pt:problemticket"))) {
                Map<String, String> newupdateparams = new HashMap<>();
                newupdateparams.putAll(updateParam);
                if (updateParam.containsKey("tickettype")) updateParam.remove("tickettype");
                if (updateParam.containsKey("queue")) updateParam.remove("queue");
                if (updateParam.containsKey("typeofupdate_articleid")) updateParam.remove("typeofupdate_articleid");
                if (updateParam.containsKey("link_used")) updateParam.remove("link_used");
                if (updateParam.containsKey("no_Published_article")) updateParam.remove("no_Published_article");
                response = ITSMBase.customupdateTicketbyid(loggedinuser, updateParam, formName, ticketId, request);

                if (newupdateparams.containsKey("tickettype")) {
                    response.put("typeofupdate", ITSMBase.srptticketupdate(formName, ticketId, newupdateparams, loggedinuser, request));
                }
                if (updateParam.containsKey("700001552")) {//secure patientinfo
                    response.put("securepatientinfo", ITSMBase.securepatientinforecords(ticketId, adminuser, request));
                }
                if (updateParam.containsKey("700003719")) {
                    response.put("pt_casehistory", ITSMBase.pt_casehistory(ticketId, adminuser));
                }
                ITSMBase.smefetchrecords(formName, ticketId, loggedinuser, request);//sme needs to be triggered
            } else {
                response = ITSMBase.customupdateTicketbyid(loggedinuser, updateParam, formName, ticketId, request);
            }
            LoggerBase.loguserrecords(loggedinuser, ticketId, ITSMBase.gsonstojson(response), formName, Constants.UPDATE, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/customupdateticketbyidwithattachment")
    public ResponseEntity customupdateticketbyidwithattachment(
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            HttpServletRequest ticketFields,
            org.springframework.web.multipart.MultipartHttpServletRequest request) {

        String formName = null, ticketId = null, loginid = "";
        ARServerUser loggedinuser = new ARServerUser();
        Map<String, String> inputmap = new HashMap<>();
        Map<String, File[]> attach = new TreeMap<>();
        int[] reqfield;
        int j = 0;

        Map<String, Object> response = new HashMap<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            Entry entryData = new Entry();

            if (ticketFields.getContentType() != null && ticketFields.getContentType().startsWith("multipart/form-data")) {

                MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();
                for (Map.Entry<String, List<MultipartFile>> stringListEntry : attachments.entrySet()) {
                    File[] files = new File[stringListEntry.getValue().size()];
                    int i = 0;
                    for (MultipartFile multipartFile : stringListEntry.getValue()) {
                        files[i] = ITSMBase.convertMultiPartToFile(multipartFile);
                        i++;
                    }
                    attach.put(stringListEntry.getKey(), files);
                }
                entryData = RemedyBase.putAttachmentswithitsm(loggedinuser, formName, attach);
            }

            formName = ITSMBase.validateformnameexist(inputmap);
            ticketId = ITSMBase.validateticketidexist(inputmap);
            reqfield = new int[inputmap.size()];
            if (formName != null && ticketId != null) {
                for (Map.Entry<String, String> entry : inputmap.entrySet()) {
                    if (entry.getValue() == null || entry.getValue().toString() == null || entry.getValue().toString().equalsIgnoreCase("null") || entry.getValue().toString().equalsIgnoreCase("NO_VALUE") || entry.getValue().toString().equalsIgnoreCase("NOVALUE") || entry.getValue().toString().isEmpty()) {
                        entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value());
                        reqfield[j] = Integer.parseInt(entry.getKey());
                        j++;
                    } else {
                        reqfield[j] = Integer.parseInt(entry.getKey());
                        j++;
                        entryData.put(Integer.parseInt(entry.getKey()), new com.bmc.arsys.api.Value(entry.getValue().toString()));
                    }
                }

                Entry updatedentry = loggedinuser.setGetEntry(formName, ticketId, entryData, null, 0, reqfield);

                List<Entry> results = new ArrayList<>();
                results.add(updatedentry);
                List<Map<String, Object>> resultset = ITSMBase.remedyresultsetforitsm(loggedinuser, formName, results, request, reqfield);

                resultset.forEach(stringObjectMap -> {
                    stringObjectMap.forEach((s, o) -> {
                        response.put(s, o);
                    });
                });

                response.put("ticketId", ticketId);

                LoggerBase.loguserrecords(loggedinuser, ticketId, ITSMBase.gsonstojson(response), formName, Constants.UPDATE, request);
            } else {
                throw new Exception("formname or ticketid does not exist");
            }
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/GetOnCallArchivalClient")
    public ResponseEntity GetOnCallArchivalClient1(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "") String client) {

        String formName = "CMN:OC+Group+GRP:OPT-Join";
        List<EntryValueList> entries = new ArrayList<>();
        List<EntryValueList> newentries = new ArrayList<>();
        List<EntryValueList> resultentries = new ArrayList<>();
        List<Entry> cmnclientinfo = new ArrayList<>();
        int[] cmnclientinforeq = new int[]{536870913, 536871013};
        List response = new ArrayList();
        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(loggedinuser, loginid);

            String query = "'Master Client' != $NULL$ AND 'Status' < \"Remove\"";

            if (client.isEmpty()) {

                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formName, new int[]{536871028}, query, 0, true, new int[]{536871028}, new int[]{536871028});

                response = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formName, entries, request, new int[]{536871028});
            } else {
                query = "'Master Client' != $NULL$ AND 'Master Client' = \"" + client + "\" AND 'Status' < \"Remove\"";
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formName, new int[]{105}, query, 0, true, new int[]{105}, new int[]{105});
                resultentries = entries;
                response = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formName, resultentries, request, new int[]{105});
            }

            LoggerBase.loguserrecords("GetOnCallArchivalClient", loggedinuser, formName, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/primarysupportclientandgroups")
    public ResponseEntity primarysupportclientandgroups(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "") String client) {

        String formName = "CMN:PSS+Group+GRP:OPT-Join";
        List<EntryValueList> entries = new ArrayList<>();
        List<EntryValueList> newentries = new ArrayList<>();
        List<EntryValueList> resultentries = new ArrayList<>();
        List<Entry> cmnclientinfo = new ArrayList<>();
        int[] cmnclientinforeq = new int[]{536870913, 536871013};
        ARServerUser loggedinuser = new ARServerUser();
        List response = new ArrayList();
        try {
            if (atoken != null && atoken.equals(atoken1)) {

            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            String query = "'Master Client' != $NULL$ AND 'Status' < \"Remove\"";

            if (client.isEmpty()) {
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formName, new int[]{536871028}, query, 0, true, new int[]{536871028}, new int[]{536871028});
                response = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formName, entries, request, new int[]{536871028});
            } else {
                query = "'Master Client' != $NULL$ AND 'Master Client' = \"" + client + "\" AND 'Status' < \"Remove\"";
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formName, new int[]{536871034}, query, 0, true, new int[]{536871034}, new int[]{536871034});
                resultentries = entries;
                response = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formName, resultentries, request, new int[]{536871034});
            }
            LoggerBase.loguserrecords("primarysupportclientandgroups", loggedinuser, formName, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/get_support_persons")
    public ResponseEntity get_support_persons(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid) {

        List<Map<String, Object>> entriess = new ArrayList<>();

        ARServerUser user;
        try {

            loginid = ITSMBase.escapesql(loginid);
            if (atoken != null && atoken.equals(atoken1)) {
                user = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            int[] reqfield = new int[]{
                    536870916,
                    536870917,
                    536871013
            };
            String clients = ITSMBase.fetchuserclientsforoncall(loginid, user);
            SQLResult sqlResult;
            if (clients.isEmpty())
                clients += "'CTS'";
            else
                clients += ",'CTS'";
            sqlResult = user.getListSQL("select client,login_name,user_full_name from User_CMN_PEO_Join with (nolock) where client in (select client from cmn_clientinfo where master_client in (" + clients + ") ) and support_person_=1 order by client,user_full_name", 0, true);

            for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                Map<String, Object> newmap = new HashMap<>();
                newmap.put("client", content.get(0).toString());
                newmap.put("loginname", content.get(1).toString());
                newmap.put("userfullname", content.get(2).toString());
                entriess.add(newmap);
            }

        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(entriess, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/vacationtable")
    public ResponseEntity vacationtable(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "status", required = false) String status) {

        List<Map<String, Object>> entriesss = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:BusyVacation+";
        try {
            if (loginid != null && !loginid.isEmpty()) {
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Auth Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            String query = "";
            SQLResult sqlResult;

            int[] req = new int[]{

                    1,//	Request ID
                    2,//	Submitter
                    3,//	Create Date
                    4,//	Assigned To
                    5,//	Last Modified By
                    6,//	Modified Date
                    7,//	Status
                    8,//	Short Description
                    15,//	Status History
                    112,//	zAssigned Group
                    200000012,//	Client
                    536871095,//	zD_NameChange
                    536871354,//	Panel Holder
                    700001039,//	Busy/Vacation Person
                    704000001,//	Reason
                    704000003,//	Full Name
                    704000004,//	Begin Date/Time
                    704000005,//	End Date/Time
            };

            List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, req, "'Status'=\"" + status + "\"", 0);
            entriesss = ITSMBase.remedyresultsetforitsm(adminuser, formname, entries, request, req);
            LoggerBase.loguserrecords("vacationtable", loggedinuser, formname, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(entriesss, HttpStatus.OK);

    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/getUsersByAccountCode")
    public ResponseEntity getUsersByAccountCode(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "") String client) {

        List<Map<String, Object>> entriess = new ArrayList<>();
        List<Map<String, Object>> entriess1 = new ArrayList<>();
        String formname = "User+CMN:PEO-Join";

        ARServerUser loggedinuser = new ARServerUser();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                if (loginid.isEmpty()) {
                    loginid = adminuser.getUser();
                }
            } else {
                throw new Exception("Authorization error");
            }
            String query = "", clientquery = "";//'Support Person?'="Yes" AND 'Status'="Active"
            if (!client.isEmpty()) {
                clientquery = "  'Client'=\"" + client + "\"";
                query += clientquery;
            }

            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            int[] reqfield = new int[]{
                    200000012,//	Client
                    8,//	User Full Name
                    101,//	Login Name
            };
            List<EntryValueList> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0, true, reqfield, reqfield);
            entriess = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formname, entries, request, reqfield);

           /* if (ITSMBase.adminuserornot(loginid, user)) {
                query = "select login_name,user_full_name,client from user_cmn_peo_join  where status=2 and client='" + client + "'  ORDER BY CLient,user_full_name";
            } else {
                query = "select login_name,user_full_name,client from user_cmn_peo_join  where status=2";

                if (!client.isEmpty()) {
                    query += " and client = '" + client + "' ";
                }
                query += " and login_name in (\n" +
                        "\tselect login_id from CMN_Notification_Assignments where group_name in (\n" +
                        "\tselect group_name from CMN_Notification_Assignments with (nolock) where (Functional_Type = 1) AND (Status = 2) AND login_id ='" + loginid + "' " +
                        "\t) ) ORDER BY CLient,user_full_name";
            }*/

            for (Map<String, Object> stringObjectMap : entriess) {
                Map<String, Object> newmap = new HashMap<>();
                newmap.put("login_name", stringObjectMap.get("Login_Name").toString());
                newmap.put("user_full_name", stringObjectMap.get("User_Full_Name").toString());
                newmap.put("client", stringObjectMap.get("Client").toString());
                entriess1.add(newmap);
            }

        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(entriess1, HttpStatus.OK);
    }

    //4
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/getvacationsupportusers")
    public ResponseEntity getvacationsupportusers(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "") String client) {

        List<Map<String, Object>> entriess = new ArrayList<>();

        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                if (loginid.isEmpty()) {
                    loginid = adminuser.getUser();
                }
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            int[] reqfield = new int[]{
                    4,//	Login ID
                    200000012,//	Client
                    700001013,//	Full Name
            };
            int[] notreqfield = new int[]{
                    536870913,//	Group Name
            };
            String query = "", notificaitonquery = "'Functional Type'=\"Support\" AND 'Login ID'=\"" + loginid + "\"", groupquery = "";
            String groupformname = "Group+GRP:OPT+CMN:NOT-Join";
            String notificationformname = "CMN:Notification-Assignments";
            List<Entry> notentries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, notificationformname, notreqfield, notificaitonquery, 0);

            for (Entry notentry : notentries) {
                if (groupquery.isEmpty()) {
                    groupquery += " 'Group Name'=\"" + notentry.get(536870913).toString() + "\" ";
                } else {
                    groupquery += " OR 'Group Name'=\"" + notentry.get(536870913).toString() + "\" ";
                }
            }
            if (!groupquery.isEmpty()) {
                query += " ( " + groupquery + " )";
            }

            List<EntryValueList> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, groupformname, reqfield, query, com.bmc.arsys.api.Constants.AR_NO_MAX_LIST_RETRIEVE, true, reqfield, reqfield);
            List<Map<String, Object>> response = ITSMBase.remedyresultsetforitsmentrylist(adminuser, groupformname, entries, request, reqfield);
           /* query = "select distinct login_id,full_name,client from Group_GRP_OPT_CMN_NOT_Join where group_id in (SELECT value FROM STRING_SPLIT(( SELECT Group_List FROM User_x WITH (NOLOCK) WHERE Login_Name='" + loginid + "'),';')) order by client,full_name";
            sqlResult = loggedinuser.getListSQL(query, 0, true);*/

            for (Map<String, Object> stringObjectMap : response) {
                Map<String, Object> newmap = new HashMap<>();
                newmap.put("login_name", stringObjectMap.get("Login_ID").toString());
                newmap.put("user_full_name", stringObjectMap.get("Full_Name").toString());
                newmap.put("client", stringObjectMap.get("Client").toString());
                entriess.add(newmap);
            }
            LoggerBase.loguserrecords("getvacationsupportusers", loggedinuser, groupformname, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(entriess, HttpStatus.OK);
    }


    //5
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/getOnCallClients")
    public ResponseEntity getOnCallClients(
            HttpServletRequest request,
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid) {
        List<Map<String, Object>> entriess = new ArrayList<>();

        ARServerUser loggedinuser = new ARServerUser();
        try {
            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                if (loginid.isEmpty()) {
                    loginid = adminuser.getUser();
                }
            } else {
                throw new Exception("Authorization error");
            }
            String formname = "CMN:MasterClients";
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            int[] reqfield = new int[]{
                    536870916,//	Client
                    536870917,//	Client Code Name
                    536871013,//	Master Client Code Name
            };

            List<Entry> results = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, "CMN:MasterClients", reqfield, "'Client Status'=\"Active\" OR 'Client Status'=\"Container\" OR 'Client Status'=\"Onboarding\"", 0);

            results.forEach(entry -> {
                Map<String, Object> newmap = new HashMap<>();
                newmap.put("client", entry.get(536870916).toString());
                newmap.put("clientcodename", entry.get(536870917).toString());
                newmap.put("masterclientcodename", entry.get(536871013).toString());
                entriess.add(newmap);
            });
            LoggerBase.loguserrecords("getOnCallClients", loggedinuser, formname, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(RemedyBase.returnError(e.getMessage()), HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(entriess, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/viewuserbasedclients")
    public ResponseEntity viewuserbasedclients1(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "client", required = false, defaultValue = "empty") String client,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        List<Map<String, Object>> results = new ArrayList<>();
        String query = "";
        String sortfield;
        int[] groupreq;
        try {
            if (atoken != null && atoken.equals(atoken1)) {
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            String formname = "Group+GRP:OPT+CMN:MSTR-Join";
            if (client.equalsIgnoreCase("empty")) {
                sortfield = "clientcodename";
                groupreq = new int[]{536871013, 105};
                query = "'Functional Type'=\"Support\" AND 'Master Client Code Name' != $NULL$ AND 'Status__c'=\"Active\"";
            } else {
                sortfield = "group";
                groupreq = new int[]{105, 536871013};
                query = "'Master Client Code Name' = \"" + client + "\" AND 'Functional Type'=\"Support\" AND 'Status__c'=\"Active\"";
            }
            List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, groupreq, query, com.bmc.arsys.api.Constants.AR_NO_MAX_LIST_RETRIEVE);

            results = ITSMBase.remedyresultsetforclient(entries);
            results = ITSMBase.distinctresultset(results, sortfield, "true", "");
            LoggerBase.loguserrecords("viewuserbasedclients", loggedinuser, formname, Constants.FETCH, request);

        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnErrorobject(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(path = "/endpointurl/{endpointname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity endpointurl(Model model, @PathVariable("endpointname") String endpointName,
                                      @RequestHeader(value = "atoken", required = false) String atoken,
                                      @RequestHeader(value = "access_token", required = false) String accesstoken,
                                      @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
                                      @RequestParam(value = "custom", required = false, defaultValue = "false") String custom,
                                      @RequestParam(value = "sortfield", required = false) String sortfield,
                                      @RequestParam(value = "toLowerCase", required = false, defaultValue = "false") String toLowerCase,
                                      @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                                      @RequestParam(value = "noofrecords", required = false, defaultValue = "3000") Integer noofrecords,
                                      @RequestParam(value = "sortorder", required = false, defaultValue = "asc") String sortorder,
                                      @RequestParam(value = "errormessage", required = false, defaultValue = "") String errormessage,
                                      @RequestParam(value = "client", required = false, defaultValue = "") String client,
                                      HttpServletRequest request) {

        JsonArray tArray = new JsonArray();
        List<Map<String, Object>> result = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String loginid = "";
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }

            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            result = ITSMBase.endpointurlfunctionality(endPointRepo, fieldRepo, endpointName, toLowerCase, request, loggedinuser, noofrecords, custom, sortfield, sortorder, distinct);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("error", e.getMessage());
            result.add(error);
            return new ResponseEntity(result, HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/primarysupportothergroup")
    public ResponseEntity primarysupportothergroup(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "group", required = false, defaultValue = "") String group,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:PrimarySupportSchedule", query = "";
        List<Map<String, ? extends Object>> results = new ArrayList<>();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            if (group.isEmpty()) {
                query = "(('Status' = \"Current\"))";
            } else {
                query = "((('Status' = \"Current\") OR ('Status' = \"Scheduled\")) AND ('Group' = \"" + group + "\"))";
            }
            loggedinuser = ITSMBase.impersonateuser(loggedinuser, loginid);
            int[] reqfield = new int[]{1,//	Request ID
                    2,//	Submitter
                    3,//	Create-date
                    4,//	Primary Name
                    5,//	Last-modified-by
                    6,//	Modified-date
                    7,//	Status
                    8,//	Summary
                    15,//	Status-History
                    112,//	zAssigned Group
                    200000012,//	Client
                    536870909,//	Secondary Profile
                    536870910,//	Primary Profile
                    536870914,//	Secondary Name
                    536870915,//	Primary Login ID
                    536870916,//	Dup ID
                    536870917,//	Secondary Login ID
                    536870918,//	Line5
                    536870919,//	Start Date/Time
                    536870920,//	End Date/Time
                    536870924,//	Group ID
                    536871034,//	Group
            };

            List<Entry> entries = RemedyBase.queryEntrysByQual(loggedinuser, formname, reqfield, query);
            results = ITSMBase.remedyresultsetforitsm(adminuser, formname, entries, request, reqfield);
            LoggerBase.loguserrecords("primarysupportothergroup", loggedinuser, formname, Constants.FETCH, request);
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.OK);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/ticketinfowithhistory")
    public ResponseEntity ticketInfowithhistory(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId, HttpServletRequest request) {

        Map<String, Object> resultMap = new HashMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        String loginid = "";
        try {

            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Auth Error");
            }

            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            LoggerBase.loguserrecords("ticketinfowithhistory", loggedinuser, ticketId, formName, Constants.FETCH, request);

            return new ResponseEntity(ITSMBase.getTicketEntrywithfileattachmentandhistory(loggedinuser, formName, ticketId, request), HttpStatus.OK);
        } catch (Exception e) {
            resultMap.put("error", e.getMessage());
        }
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/ticketinfo")
    public Map<String, Object> ticketInfo(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId) {

        Map<String, Object> resultMap = new HashMap<>();
        ARServerUser user = new ARServerUser();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                user = adminuser;
            } else if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
                user = RemedyBase.loginUser(serverName, port, username, password);
            } else {
                throw new Exception("Authorization error");
            }
            return ITSMBase.getTicketEntry(user, formName, ticketId);
        } catch (Exception e) {
            resultMap.put("error", e.getMessage());
        }
        return resultMap;
    }

    //6
    @RequestMapping(method = RequestMethod.POST, path = "/viewuserbasedclientsbusyvacation")
    public ResponseEntity viewuserbasedclientsbusyvacation(
            @RequestHeader(value = "username", required = false) String username,
            @RequestHeader(value = "password", required = false) String password,
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        String sortfield = "master_client";
        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Map<String, String>> tempresults = new HashMap<>();
        try {

            if (username == null && password == null && atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
                if (loginid.isEmpty()) {
                    loginid = adminuser.getUser();
                }
            } else {
                throw new Exception("Authorization error");
            }

            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            List<Entry> entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, "User+CMN:PEO-Join", new int[]{200000012}, "'Support Person?'=\"Yes\"", 0);//'Status'="Active" AND

            String clientcodenamequery = "select client,client_code_name,master_client from cmn_clientinfo order by master_client,client";

            SQLResult clientcodenameresults = adminuser.getListSQL(clientcodenamequery, 0, true);

            for (List<com.bmc.arsys.api.Value> content : clientcodenameresults.getContents()) {
                Map<String, String> tempmap = new HashMap<>();
                tempmap.put("client_code_name", content.get(1).toString());
                tempmap.put("master_client", content.get(2).toString());
                tempresults.put(content.get(0).toString(), tempmap);
            }

            for (Entry entry : entries) {
                Boolean flag = false;
                for (Map<String, Object> stringStringMap : results) {
                    if (stringStringMap.get("client").toString().equalsIgnoreCase(entry.get(200000012).toString())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    Map<String, Object> tempresult = new HashMap<>();
                    tempresult.put("clientcode", tempresults.get(entry.get(200000012).toString()).get("client_code_name"));
                    tempresult.put("masterclientcode", tempresults.get(tempresults.get(entry.get(200000012).toString()).get("master_client")).get("client_code_name"));
                    tempresult.put("client", entry.get(200000012).toString());
                    results.add(tempresult);
                }
            }
            results = ITSMBase.distinctresultset(results, "masterclientcode", "false", "");

            return new ResponseEntity(results, HttpStatus.OK);
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnErrorobject(e.getMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
    }

    //remedyapi9.1
    @RequestMapping(method = RequestMethod.POST, path = "/cmnmasterclients")
    public ResponseEntity cmnmasterclients(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:MasterClients", query = "'Client Status' < \"Bundle\"";
        List<Map<String, String>> results = new ArrayList<>();
        List<EntryValueList> entries = new ArrayList<>();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            int[] reqfield = new int[]{536871013, 536870913};

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0, true, reqfield, reqfield);

            results = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formname, entries, request, reqfield);

        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/securepatientinfo")
    public ResponseEntity securepatientinfo(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "ticketid", required = false) String ticketid,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        List<Map<String, String>> results = new ArrayList<>();
        String loginid = "";
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
                loginid = dusername;
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            results = ITSMBase.securepatientinfo(ticketid, loggedinuser, request);
            LoggerBase.loguserrecords("securepatientinfo", loggedinuser, "CMN:PatientInfo", Constants.FETCH, request);
        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/cmnformsdata")
    public ResponseEntity cmnformsdata(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            @RequestParam(value = "client", required = false) String client,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:Forms", query = "'Status' = \"Active\" and 'Client'=\"" + client + "\"";
        List<Map<String, String>> results = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();

        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);
            int[] reqfield = new int[]{
                    4,    //Form Name
                    200000012,//	Client
                    8,//	Description of Form
                    7,//	Status
                    1,// request_id
                    700001591,//Attachment Field 1
            };

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0);
            results = ITSMBase.remedyresultsetforitsm(adminuser, formname, entries, request, reqfield);

            LoggerBase.loguserrecords("cmnformsdata", loggedinuser, formname, Constants.FETCH, request);

        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/assignedgroup")
    public ResponseEntity assignedgroup(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            HttpServletRequest request
    ) {
        Map<String, List> resultmap = new TreeMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "Group+GRP:OPT+CMN:MSTR-Join", query = "'Functional Type'=\"Support\" AND 'Status__c'=\"Active\" AND 'Client Code Name' != $NULL$ ";
        List<Map<String, String>> results = new ArrayList<>();
        List<EntryValueList> entries = new ArrayList<>();
        try {
            if (atoken != null && atoken.equals(atoken1)) {
                loggedinuser = adminuser;
            } else {
                throw new Exception("Authorization error");
            }
            loggedinuser = ITSMBase.impersonateuser(loggedinuser, loginid);
            int[] reqfield = new int[]{536871013, 105};

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0, true, reqfield, reqfield);

            results = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formname, entries, request, reqfield);


            results.stream().forEach(stringStringMap -> {
                resultmap.put(stringStringMap.get("Master_Client_Code_Name").toString(), new ArrayList());
            });

            results.stream().forEach(stringStringMap -> {
                List<String> value = resultmap.get(stringStringMap.get("Master_Client_Code_Name"));
                value.add(stringStringMap.get("Group_Name"));
                resultmap.put(stringStringMap.get("Master_Client_Code_Name"), value);
            });
            LoggerBase.loguserrecords("assignedgroup", loggedinuser, formname, Constants.FETCH, request);

        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(resultmap, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/srptclient")
    public ResponseEntity srptclient(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "loginid", required = false) String loginid,
            HttpServletRequest request
    ) {
        Map<String, List> resultmap = new TreeMap<>();
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:ClientInfo", query = "'Status'=\"Active\"";
        List<Map<String, String>> results = new ArrayList<>();
        Map<String, Map<String, List>> response = new TreeMap<>();
        List<EntryValueList> entries = new ArrayList<>();

        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            int[] reqfield = new int[]{
                    8,//	Queue
                    536871039,//	SubType
                    536871013,//	Client Code Name
                    536870913,//	Client
            };

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0, true, reqfield, reqfield);

            results = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formname, entries, request, reqfield);

            for (Map<String, String> stringStringMap : results) {
                response.put(stringStringMap.get("Queue").toString(), new TreeMap<>());
            }

            for (Map<String, String> result : results) {
                Map<String, List> tempmap = new TreeMap<>();
                tempmap = response.get(result.get("Queue").toString());
                Boolean found = false;
                for (Map.Entry<String, List> stringListEntry : tempmap.entrySet()) {
                    if (stringListEntry.getKey().equalsIgnoreCase(result.get("SubType").toString())) {
                        found = true;
                    }
                }
                if (!found) {
                    tempmap.put(result.get("SubType").toString(), new ArrayList());
                }
                response.put(result.get("Queue").toString(), tempmap);
            }

            for (Map<String, String> result : results) {
                List<Map<String, String>> tempresult = new ArrayList<>();
                Map<String, String> resultset = new TreeMap<>();
                resultset.put("client", result.get("Client"));
                resultset.put("clientcodename", result.get("Client_Code_Name"));
                for (Map.Entry<String, Map<String, List>> stringMapEntry : response.entrySet()) {
                    for (String s : stringMapEntry.getValue().keySet()) {
                        if (result.get("SubType").equalsIgnoreCase(s)) {
                            tempresult = stringMapEntry.getValue().get(s);
                        }
                    }
                }
                tempresult.add(resultset);
                response.get(result.get("Queue")).put(result.get("SubType"), tempresult);
            }
            LoggerBase.loguserrecords("srptclient", loggedinuser, formname, Constants.FETCH, request);

        } catch (Exception e) {
            results = new ArrayList<>();
            results.add(RemedyBase.returnError(e.getLocalizedMessage()));
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/srptclientmasterchildclient")
    public ResponseEntity srptclientmasterchildclient(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "queue", required = false) String queue,
            HttpServletRequest request
    ) {
        String loginid = "";
        Map<String, List> resultmap = new TreeMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:ClientInfo", query = "'Status'=\"Active\" AND 'Queue'=\"" + queue + "\"";
        List<EntryValueList> entries = new ArrayList<>();

        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            int[] reqfield = new int[]{
                    8,//	Queue
                    536871039,//	SubType
                    536871013,//	Client Code Name
                    536870913,//	Client
                    536870914,//master client
            };

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 0, true, reqfield, reqfield);

            results = ITSMBase.remedyresultsetforitsmentrylist(adminuser, formname, entries, request, reqfield);

            for (Map<String, Object> stringStringMap : results) {
                resultmap.put(stringStringMap.get("SubType").toString(), new ArrayList());
            }
            List<Map> subclients = new ArrayList<>();
            for (Map<String, Object> result : results) {
                Map<String, String> resultclient = new HashMap<>();
                subclients = resultmap.get(result.get("SubType").toString());
                resultclient.put("client", result.get("Client").toString());
                resultclient.put("clientcodename", result.get("Client_Code_Name").toString());
                resultclient.put("master_client", result.get("Master_Client").toString());

                subclients.add(resultclient);
                resultmap.put(result.get("SubType").toString(), subclients);
            }
        } catch (Exception e) {
            return new ResponseEntity(resultmap, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(resultmap, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/filterpeople")
    public ResponseEntity filterpeople(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "loginid", required = false, defaultValue = "") String loginid,
            @RequestParam(value = "firstname", required = false, defaultValue = "") String firstname,
            @RequestParam(value = "lastname", required = false, defaultValue = "") String lastname,
            @RequestParam(value = "client", required = false, defaultValue = "") String client,
            @RequestParam(value = "emailaddress", required = false, defaultValue = "") String emailaddress,
            @RequestParam(value = "networklogin", required = false, defaultValue = "") String networklogin,
            HttpServletRequest request
    ) {
        ARServerUser loggedinuser = new ARServerUser();
        String formname = "CMN:PEO+CMN:CNFO-Join", query = "";
        List<Map<String, Object>> response = new ArrayList();
        List<Map<String, Object>> responseforclient = new ArrayList();
        Map<String, Map<String, String>> clientmapping = new HashMap<>();
        List<Entry> entries = new ArrayList<>();
        String loginid1 = "";
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid1 = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid1 = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid1);

            int[] reqfield = new int[]{
                    8,//	Full Name
                    200000006,//	Department
                    200000007,//	Building
                    200000012,//	Client
                    536870914,//	Master Client
                    536870915,//	CST
                    536870949,//	Network Login
                    536871016,//	Type
                    620000180,//	Title
                    700001012,//	Email Address
                    700001022,//	VIP
                    700001039,//	Login ID
                    700001082,//	Last Name
                    700001083,//	Middle Init
                    700001084,//	First Name
                    704000051,//	Support Person?
                    910000104,//	Phone-Work
                    910000105,//	Phone Ext
                    910000110,//	Phone-Cell
                    910000111,//	Phone-Home
                    910000105,//	Phone Ext
                    910000100,//	Floor
                    910000102,//	Office
                    620000897,//	Client Note
            };

            if (!loginid.isEmpty()) {
                query += "'Login ID' like \"" + loginid + "%\"";
            }
            if (!firstname.isEmpty()) {
                if (query.isEmpty()) {
                    query += "'First Name' like \"" + firstname + "%\"";
                } else {
                    query += " AND 'First Name' like \"" + firstname + "%\"";
                }
            }
            if (!lastname.isEmpty()) {
                if (query.isEmpty()) {
                    query += "'Last Name' like \"" + lastname + "%\"";
                } else {
                    query += " AND 'Last Name' like \"" + lastname + "%\"";
                }
            }
            if (!client.isEmpty()) {
                if (query.isEmpty()) {
                    query += "'Master Client' like \"" + client + "%\"";
                } else {
                    query += " AND 'Master Client' like \"" + client + "%\"";
                }
            }
            if (!emailaddress.isEmpty()) {
                if (query.isEmpty()) {
                    query += "'Email Address' like \"" + emailaddress + "%\"";
                } else {
                    query += " AND 'Email Address' like \"" + emailaddress + "%\"";
                }
            }
            if (!networklogin.isEmpty()) {
                if (query.isEmpty()) {
                    query += "'Network Login' like \"" + networklogin + "%\"";
                } else {
                    query += " AND 'Network Login' like \"" + networklogin + "%\"";
                }
            }
            query += " AND ('Status' =\"Active\" OR 'Status' =\"Inactive\" OR 'Status' =\"Pending Removal\")";

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, formname, reqfield, query, 3000);
            response = ITSMBase.remedyresultsetforitsm(adminuser, formname, entries, request, reqfield);
            reqfield = new int[]{
                    8,//	Queue
                    536871039,//	SubType
                    536870913,//	Client
                    536871013,//	Client Code Name
            };
            String clientformname = "CMN:ClientInfo";
            query = "'Status'=\"Active\"";
            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, clientformname, reqfield, query, 3000);
            responseforclient = ITSMBase.remedyresultsetforitsm(adminuser, clientformname, entries, request, reqfield);
            for (Map<String, Object> stringObjectMap : responseforclient) {
                Map<String, String> temp = new HashMap<>();
                temp.put("Queue", stringObjectMap.get("Queue").toString());
                temp.put("Client_Code_Name", stringObjectMap.get("Client_Code_Name").toString());
                clientmapping.put(stringObjectMap.get("Client").toString(), temp);
            }

            for (Map<String, Object> stringObjectMap : response) {
                if (clientmapping.get(stringObjectMap.get("Client")) != null) {
                    stringObjectMap.put("Queue", clientmapping.get(stringObjectMap.get("Client")).get("Queue"));
                    stringObjectMap.put("Client_Name", clientmapping.get(stringObjectMap.get("Client")).get("Client_Code_Name"));
                    stringObjectMap.put("Client_Code_Name", clientmapping.get(stringObjectMap.get("Client")).get("Client_Code_Name"));
                } else {
                    stringObjectMap.put("Queue", null);
                    stringObjectMap.put("Client_Name", null);
                    stringObjectMap.put("Client_Code_Name", null);
                }
            }
            LoggerBase.loguserrecords("filterpeople", loggedinuser, formname, Constants.FETCH, request);
        } catch (Exception e) {
            response.add(RemedyBase.returnErrorobject(e.getLocalizedMessage()));
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/srptpickupbutton")
    public ResponseEntity srptpickupbutton(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "loginid") String loginid,
            @RequestParam(value = "groupcount", required = false, defaultValue = "2") String groupcount,
            HttpServletRequest request
    ) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> response = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String userformname = "User", notificationformname = "CMN:Notification-Assignments",
                notificationquery = "('Login ID' =\"" + loginid + "\") AND ('Functional Type' = \"Support\")",
                userquery = "('Login Name' =\"" + loginid + "\")";
        List<Entry> entries = new ArrayList<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            int[] reqfielduser = new int[]{
                    8,//	Full Name
                    101,//Login Name
            };
            int[] reqfieldnotification = new int[]{
                    536870913,//	Group Name
                    536870914,//	Group ID
                    536870915,//	zUnusedChar03
                    536870916,//	Full Name
                    4,//	Login ID
            };
            if (!groupcount.equalsIgnoreCase("1")) {
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, userformname, reqfielduser, userquery, 0);
                results = ITSMBase.remedyresultsetforitsm(adminuser, userformname, entries, request, reqfielduser);

                results.forEach(stringObjectMap -> {
                    Map<String, Object> tempobj = new HashMap<>();
                    tempobj.put("Group_Name", "");
                    tempobj.put("Full_Name", stringObjectMap.get("Full_Name"));
                    tempobj.put("Login_ID", stringObjectMap.get("Login_Name"));
                    response.add(tempobj);
                });

            } else {
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, notificationformname, reqfieldnotification, notificationquery, 0);
                results = ITSMBase.remedyresultsetforitsm(adminuser, notificationformname, entries, request, reqfieldnotification);
                results.forEach(stringObjectMap -> {
                    Map<String, Object> tempobj = new HashMap<>();
                    tempobj.put("Group_Name", stringObjectMap.get("Group_Name"));
                    tempobj.put("Full_Name", stringObjectMap.get("Full_Name"));
                    tempobj.put("Login_ID", stringObjectMap.get("Login_ID"));
                    response.add(tempobj);
                });
            }
            LoggerBase.loguserrecords("pickupbutton", loggedinuser, notificationformname, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/usersupportcountandgroups")
    public ResponseEntity usersupportcountandgroups(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "loginid") String loginid,
            HttpServletRequest request
    ) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> response = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String notificationformname = "CMN:Notification-Assignments",
                notificationquery = "('Login ID' =\"" + loginid + "\") AND ('Functional Type' = \"Support\") AND ('Status'=\"Active\")";

        List<Entry> entries = new ArrayList<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            int[] reqfieldnotification = new int[]{
                    536870913,//	Group Name
                    536870914,//	Group ID
                    536870915,//	zUnusedChar03
                    536870916,//	Full Name
            };

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, notificationformname, reqfieldnotification, notificationquery, 0);
            results = ITSMBase.remedyresultsetforitsm(adminuser, notificationformname, entries, request, reqfieldnotification);
            List<String> groupname = new ArrayList<>();
            results.forEach(stringObjectMap -> {
                groupname.add(stringObjectMap.get("Group_Name").toString());
            });
            Map<String, Object> tempobj = new HashMap<>();
            tempobj.put("Group_Name", groupname);
            tempobj.put("supportcount", groupname.size());
            response.add(tempobj);
            LoggerBase.loguserrecords("usersupportcountandgroups", loggedinuser, notificationformname, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fetchemaildetails")
    public ResponseEntity fetchemaildetails(
            @RequestHeader(value = "atoken", required = false) String atoken,
            @RequestHeader(value = "refresh_token", required = false) String refreshtoken,
            @RequestHeader(value = "access_token", required = false) String accesstoken,
            @RequestParam(value = "ticketnumber") String ticketnumber,
            HttpServletRequest request
    ) {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> results1 = new ArrayList<>();
        List<Map<String, Object>> response = new ArrayList<>();
        ARServerUser loggedinuser = new ARServerUser();
        String emailform = "CMN:Email Correspondence", attachmentform = "CMN:Attachments", loginid = "",
                emailquery = "'From Case #' = \"" + ticketnumber + "\"", attachmentquery = "";

        List<Entry> entries = new ArrayList<>();
        try {
            if (refreshtoken != null && !refreshtoken.isEmpty()) {
                loginid = SSOBase.ssovalidate(accesstoken, refreshtoken);
            } else if (atoken != null && atoken.equals(atoken1)) {
                loginid = dusername;
            } else {
                throw new Exception("Authorization Error");
            }
            loggedinuser = ITSMBase.impersonateuser(adminuser, loginid);

            int[] emailfields = new int[]{
                    1,//	Request ID
                    3,//	Create Date
                    4,//	Assigned To
                    7,//	Source
                    8,//	Short Description
                    15,//	Status History
                    179,//	GUID
                    18022,//	Error Message
                    18099,//	Status
                    18134,//	Source ID
                    536870919,//	Mailbox Name
                    536870920,//	From Case #
                    536870921,//	From Name
                    536870922,//	To Names
                    536870923,//	CC Names
                    536870925,//	Subject
                    536870926,//	Text Body
                    536870949,//	From Address
                    536871069,//	Message Type
                    536871070,//	BCC Names
                    536871072,//	Attachments?
                    536880176,//	Reply To Address
            };

            int[] reqattachmentsfields = new int[]{
                    555550110,//	Application
                    750556106,//	Attachment Field
                    750554802,//	Attachment Pool
                    750556077,//	Attachment Type
                    750554801,//	Case ID
                    750551200,//	Filename
            };

            entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, emailform, emailfields, emailquery, 0);
            results = ITSMBase.remedyresultsetforitsm(adminuser, emailform, entries, request, emailfields);

            for (Map<String, Object> result : results) {
                ticketnumber = result.get("GUID").toString();
                attachmentquery = "'Source GUID'=\"" + ticketnumber + "\"";
                entries = ITSMBase.queryEntrysByQualwithmaxrecords(loggedinuser, attachmentform, reqattachmentsfields, attachmentquery, 0);
                results1 = ITSMBase.remedyresultsetforitsm(adminuser, attachmentform, entries, request, reqattachmentsfields);
                result.put("Attachments", results1);
            }
            LoggerBase.loguserrecords("fetchemaildetails", loggedinuser, emailform, Constants.FETCH, request);
        } catch (Exception e) {
            return new ResponseEntity(results, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            ITSMBase.revokeuserandlogout(loggedinuser);
        }
        return new ResponseEntity(results, HttpStatus.OK);
    }
}
