package com.htc.remedy.controller;

import com.bmc.arsys.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.htc.remedy.base.BaseModel;
import com.htc.remedy.base.LDAPBase;
import com.htc.remedy.base.RemedyBase;
import com.htc.remedy.domain.EndPointDomain;
import com.htc.remedy.domain.FieldsDomain;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.domain.LDAPEndPoints;
import com.htc.remedy.model.*;
import com.htc.remedy.repo.EndPointRepo;
import com.htc.remedy.repo.LDAPAccountsRepo;
import com.htc.remedy.repo.LDAPEndpointRepo;
import com.htc.remedy.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by poovarasanv on 10/4/17.
 */

@RestController
@CrossOrigin

public class RemedyWebServices {

    @Value("${remedy.host}")
    String serverName;

    @Value("${remedy.port}")
    Integer port;

    @Value("${ctsspi.username}")
    String dusername;

    @Value("${ctsspi.password}")
    String dpassword;

    private final
    UserRepo userRepo;

    private final
    ARServerUser arServerUser1;
    //888Master

  /*  @Autowired
    public Map<String, List<LdapTemplate>> ldapTemplateMap;
*/

    private final
    TokenStore tokenStore;

    private final
    EndPointRepo endPointRepo;

    private final
    LDAPAccountsRepo ldapAccountsRepo;

    private final
    LDAPEndpointRepo ldapEndpointRepo;


    @Autowired
    public RemedyWebServices(UserRepo userRepo, ARServerUser arServerUser1, TokenStore tokenStore, EndPointRepo endPointRepo, LDAPAccountsRepo ldapAccountsRepo, LDAPEndpointRepo ldapEndpointRepo) {
        this.userRepo = userRepo;
        this.arServerUser1 = arServerUser1;
        this.tokenStore = tokenStore;
        this.endPointRepo = endPointRepo;
        this.ldapAccountsRepo = ldapAccountsRepo;
        this.ldapEndpointRepo = ldapEndpointRepo;

    }


//    @RequestMapping( value = "/**", method = RequestMethod.OPTIONS ) public ResponseEntity handle() {
//        return new ResponseEntity(HttpStatus.OK);
//    }


    @RequestMapping(path = "/api/ldapquery/{epname}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> ldapQuery(@PathVariable("epname") String endpointName,
                                               HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue(endpointName);
            if (ldapEndPoints == null) {
                throw new Exception("Endpoint not available with : " + endpointName + "");
            } else {
                String qstr = ldapEndPoints.getQuery();

                Map<String, String[]> params = request.getParameterMap();
                String rValue = qstr;
                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", params.get(s)[0]);
                }


                result = LDAPBase.runldapQuery(
                        ldapEndPoints.getLdapAccounts(),
                        ldapEndPoints.getBase(),
                        rValue,
                        ldapEndPoints.getRequiredFields()
                );
            }
        } catch (Exception e) {
            if (e instanceof NamingException) {
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", e.getMessage());
                result.clear();
                result.add(error);
            }
        }
        return result;

    }


    @RequestMapping(path = "/api/ctsspi/{endpointname}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String endpointurl(Model model,
                              @PathVariable("endpointname") String endpointName,
                              HttpServletRequest request) {

        JsonArray tArray = new JsonArray();

        try {


            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1
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
                            jsonObject.addProperty(sMap.get(integer), entry.get(integer).toString());
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

    @RequestMapping(
            path = "/api/formfields", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getFields(
            @RequestParam String formName) {
        List<Map<String, Object>> fieldResult = new ArrayList<>();

        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1
            );

            return RemedyBase.getFormFields1(arServerUser, formName);
        } catch (ARException e) {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("error", e.getLocalizedMessage());
            fieldResult.add(errorMap);
        }

        return fieldResult;

    }

    @RequestMapping(value = "/api/logout", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> logout(HttpServletRequest request) {
        Map<String, String> logout = new HashMap<>();

        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                String tokenValue = authHeader.replace("Bearer", "").trim();
                OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
                System.out.println(accessToken.toString());
                tokenStore.removeAccessToken(accessToken);
                // System.out.println(accessToken.toString());
                request.getSession().invalidate();
                SecurityContextHolder.getContext().setAuthentication(null);
                arServerUser.logout();
                logout.put("result", "success");
            }
        } catch (ARException e) {
            e.printStackTrace();
        }

        return logout;
    }

    @RequestMapping(value = "/api/forms", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> allGroups(
            @RequestParam("formName") String formName
    ) {


        // SecurityContextHolder.getContext().getAuthentication()
        List<Map<String, Object>> remedyGroup = new ArrayList<>();

        try {

            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

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
        } catch (ARException e) {
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("error", e.getLocalizedMessage());
            remedyGroup.add(errorMap);
        }

        return remedyGroup;
    }


    @RequestMapping(path = "/api/createticket", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createServiceRequest(
            @RequestParam String formName,
            @RequestBody Map<String, String> ticketFields) {
        Map<String, String> srResult = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();

        ARServerUser arServerUser = new ARServerUser();

        try {
            Map<String, String> inputMap = ticketFields;

            arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);


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
        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
        }

        return srResult;
    }

    @RequestMapping(path = "/api/createformentry", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> createFormEntry(
            @RequestParam String formName,
            @RequestBody Map<String, String> ticketFields) {
        Map<String, String> srResult = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();

        ARServerUser arServerUser = new ARServerUser();

        try {
            Map<String, String> inputMap = ticketFields;

            arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);


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
        } catch (Exception e) {
            //e.printStackTrace();
            srResult.put("Exception", e.getMessage());
            srResult.put("Source", e.getLocalizedMessage());
        }
        return srResult;
    }


    @RequestMapping(path = "/api/createformmultipleentry", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, String>> createFormMultipleEntry(
            @RequestParam String formName,
            @RequestBody List<Map<String, String>> ticketFields) {

        List<Map<String, String>> te = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        ARServerUser arServerUser = new ARServerUser();

        try {
            arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
            Map<String, Object> fields = RemedyBase.getFormFields(arServerUser, formName);
            final ARServerUser finalArServerUser = arServerUser;
            ticketFields.forEach(ticket -> {
                Map<String, String> srResult = new HashMap<>();
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
            Map<String, String> srResult = new HashMap<>();
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


    @RequestMapping(value = "/api/arprocessexecute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> arProcessExecute(
            @RequestParam("command") String command
    ) {

        Map<String, Object> fieldResult = new HashMap<>();

        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

            fieldResult.put("output", arServerUser.executeProcess(command).getOutput());
            fieldResult.put("status", arServerUser.executeProcess(command).getStatus());
        } catch (ARException e) {
            //e.printStackTrace();
            fieldResult.put("message", e.getLocalizedMessage());
        }

        return fieldResult;
    }

    public class TempGroupInfo extends GroupInfo {
        int groupId;

        public TempGroupInfo(int i, int i1, List<String> list, int i2, int i3, int i4) {
            super(i, i1, list, i2, i3, i4);
            groupId = i;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            TempGroupInfo that = (TempGroupInfo) o;

            return groupId == that.groupId;

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + groupId;
            return result;
        }
    }


    public class TempGroupInfo1 {
        String name;
        String id;
        String groupType;
        List<String> groupnames;
        String category;
        String groupParent;
        String groupOverLay;
        String groupcategory;

        public TempGroupInfo1(String name, String id, String groupType, List<String> groupnames, String category, String groupParent, String groupOverLay, String groupcategory) {
            this.name = name;
            this.id = id;
            this.groupType = groupType;
            this.groupnames = groupnames;
            this.category = category;
            this.groupParent = groupParent;
            this.groupOverLay = groupOverLay;
            this.groupcategory = groupcategory;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public List<String> getGroupnames() {
            return groupnames;
        }

        public void setGroupnames(List<String> groupnames) {
            this.groupnames = groupnames;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getGroupParent() {
            return groupParent;
        }

        public void setGroupParent(String groupParent) {
            this.groupParent = groupParent;
        }

        public String getGroupOverLay() {
            return groupOverLay;
        }

        public void setGroupOverLay(String groupOverLay) {
            this.groupOverLay = groupOverLay;
        }

        public String getGroupcategory() {
            return groupcategory;
        }

        public void setGroupcategory(String groupcategory) {
            this.groupcategory = groupcategory;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    @RequestMapping(path = "/api/login", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> login(
            @RequestParam String username, @RequestParam String password) {


        Map<String, Object> loginResults = new HashMap<String, Object>();

        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            loginResults.put("authenticated", "true");
            loginResults.put("user", arServerUser.getUser());
            //loginResults.put("token", "1234567897");


            Entry entry = queryEntrysByQual
                    (arServerUser, "User", "('Login Name' = \"" + username + "\")");

            if (entry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "User");

                for (Integer keyId : entry.keySet()) {
                    loginResults.put(userMap.get(keyId), entry.get(keyId).toString());

                }
            }


            Entry peopleEntry = queryEntrysByQual
                    (arServerUser, "CMN:People Information", "('Login ID' = \"" + username + "\")");

            if (peopleEntry != null) {
                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:People Information");

                for (Integer keyId : peopleEntry.keySet()) {
                    loginResults.put(userMap.get(keyId), peopleEntry.get(keyId).toString());

                }
            }

            List<GroupInfo> groupInfo = arServerUser.getListGroup(username, password);

            loginResults.put("userGroups", groupInfo);
        } catch (ARException e) {
            loginResults.put("authenticated", "false");
            loginResults.put("server", serverName);
            loginResults.put("reason", e.getMessage());
        }
        return loginResults;
    }


/*
    @RequestMapping(path = "/api/loginDetails", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    @ApiOperation(
            value = "User Login APi",
            notes = "Retuens user detail along with groups associated with that user ")
    public Map<String, Object> loginDetails(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
                704000051,//support person
                112,//zAssigned Group
        };
        int reqgroup[] = new int[]{
                134, //Parent Group
                107,//Group Type
                105, //Group Name
                106,//Group ID
                120,//Group Category
                136,//Real Overlay Group
                137,//DisplayOverlayGroup
        };

        Map<String, Object> loginResults = new HashMap<String, Object>();


        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                loginResults.put("authenticated", "true");
                loginResults.put("user", auth.getName());
                //loginResults.put("token", "1234567897");

                Entry entry = queryEntrysByQual
                        (arServerUser, "User", "('Login Name' = \"" + auth.getName() + "\")");

                if (entry != null) {
                    Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "User");

                    entry.keySet().forEach(keyId -> {
                        loginResults.put(userMap.get(keyId), entry.get(keyId).toString());
                    });

                }

                List<Entry> peopleEntry = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:People Information", rf, "('Login ID' = \"" + auth.getName() + "\")");

                String subclient = null, networklogin = null;
                String group = "";
                String masterclient1 = null;
                if (peopleEntry != null) {
                    Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:People Information");


                    for (Entry entry1 : peopleEntry) {
                        subclient = entry1.get(200000012).toString();       //client
                        networklogin = entry1.get(536870949).toString();//networklogin
                        if (entry1.get(112).toString() != null) {
                            group = entry1.get(112).toString();
                        }
                        group = group.replaceAll(";", "");
                        for (Integer keyId : entry1.keySet()) {
                            loginResults.put(userMap.get(keyId), entry1.get(keyId).toString());
                        }
                        break;
                    }
                }

                List<Entry> masterclient = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:ClientInfo", new int[]{536870914}, "('Client' = \"" + subclient + "\")");

                for (Entry entry1 : masterclient) {
                    masterclient1 = entry1.get(536870914).toString();
                }


                List<Entry> masterclientinfo = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:ClientInfo", new int[]{536870913,
                                536870914,
                                536870929,
                                112,
                                536871013,
                                536870918,
                                536871121,
                                536871120}, "('Master Client' = \"" + masterclient1 + "\")");

                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:ClientInfo");

                List subEntry = new ArrayList<HashMap<String, String>>();
                masterclientinfo.forEach(entry1 -> {
                    Map singleE = new HashMap<String, String>();
                    entry1.keySet().forEach(integer -> {
                        singleE.put(userMap.get(integer), entry1.get(integer).toString());
                    });
                    subEntry.add(singleE);
                });

                List<GroupInfo> groupInfo = arServerUser.getListGroup(auth.getName(), null);

                Set<TempGroupInfo> tempGroupInfos = new HashSet<>();
                Set<TempGroupInfo1> userldapgroupinfos = new HashSet<>();
                groupInfo.forEach(groupinfo -> {
                    TempGroupInfo tempGroupInfo = new TempGroupInfo(
                            groupinfo.getId(),
                            groupinfo.getGroupType(),
                            groupinfo.getGroupNames(),
                            groupinfo.getCategory(),
                            groupinfo.getGroupParent(),
                            groupinfo.getGroupOverLay()
                    );
                    tempGroupInfos.add(tempGroupInfo);
                });


                //
                List<String> groups = new ArrayList();
                List<LDAPAccounts> ldapAccounts = ldapAccountsRepo.findByClientAndSsoIsTrue(subclient);
                if (ldapAccounts != null && !ldapAccounts.isEmpty()) {
                    String distinguishedname = "";
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "samaccountname=" + networklogin, "distinguishedname");
                    if (result != null && !result.isEmpty()) {
                        distinguishedname = result.get(0).get("distinguishedName").toString();
                    }
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "member=" + distinguishedname, "samaccountname");
                    if (result != null && !result.isEmpty()) {
                        result.forEach(stringObjectMap -> {
                            groups.add(stringObjectMap.get("sAMAccountName").toString());
                        });
                    }
                }

//sAMAccountName
                if (!group.isEmpty()) {

                    List<Entry> grouplist = RemedyBase.queryEntrysByQual(arServerUser, "Group", reqgroup, "('Group ID'=" + Integer.parseInt(group) + ")");
                    grouplist.forEach(entry1 -> {
                   */
/*     134, //Parent Group
                                107,//Group Type
                                105, //Group Name
                                106,//Group ID
                                120,//Group Category
                                136,//Real Overlay Group
                                137,//DisplayOverlayGroup*//*


     */
/* Map<String, Object> groups = new HashMap<>();
                        groups.put("name", entry1.get(105).toString());
                        groups.put("id", entry1.get(106).toString());*//*


     */
/* groups.put("groupNames", groupnames);
                        groups.put("groupType", entry1.get(107).toString());
                        groups.put("category", entry1.get(120).toString());
                        groups.put("groupOverLay", 0);
                        groups.put("groupParent", 0);*//*

                        // groupInfo.add(groups);
                        List<String> groupnames = new ArrayList<String>();
                        groupnames.add(entry1.get(105).toString() == null ? "" : entry1.get(105).toString());
                        TempGroupInfo tempGroupInfo = new TempGroupInfo(entry1.get(106).toString() == null ? 0 : Integer.parseInt(entry1.get(106).toString()),
                                entry1.get(107).toString() == null ? 0 : Integer.parseInt(entry1.get(107).toString()),
                                groupnames,
                                entry1.get(120).toString() == null ? 0 : Integer.parseInt(entry1.get(120).toString()),
                                0,
                                0);
                        tempGroupInfos.add(tempGroupInfo);
                    });
                }
                int i = 0;
                for (String s : groups) {
                    List<String> groupnames = new ArrayList<String>();
                    groupnames.add(s);

                    String groupid=RemedyBase.Md5hashing(subclient+"_"+s);
                TempGroupInfo1 tempGroupInfo = new TempGroupInfo1( s,groupid,
                        (groupid),
                        groupnames,
                        "AD",
                        groupid,
                        groupid);
                         userldapgroupinfos.add(tempGroupInfo);
                }
                //  Set<TempGroupInfo> tempGroupInfos1 = new HashSet<>();
                loginResults.put("userGroups", tempGroupInfos);
                loginResults.put("userldapGroups", userldapgroupinfos);
                loginResults.put("clientdetails", subEntry);
            } catch (Exception e) {
                loginResults.put("authenticated", "false");
                loginResults.put("server", serverName);
                loginResults.put("reason", e.getMessage());
            }
        } else {
            return loginResults;
        }
        return loginResults;
    }
*/

   /* @RequestMapping(path = "/api/customloginDetails", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public Map<String, Object> optimizedloginDetails(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        Set<com.htc.remedy.model.TempGroupInfo1> tempGroupInfos = new HashSet<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> loginResults = new HashMap<String, Object>();

        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                loginResults.put("authenticated", "true");
                String remedyid = auth.getName();
                loginResults.put("user", remedyid);
                UserModel userModel = userRepo.findByUserName(remedyid);
                String sQuery = "SELECT Request_ID,\n" +
                        "		Client,\n" +
                        "       Business_Organization,\n" +
                        "       Building,\n" +
                        "       Department,\n" +
                        "       Floor,\n" +
                        "       Suite,\n" +
                        "       Office,\n" +
                        "       Status,\n" +
                        "       Email_Address,\n" +
                        "       Employee_Number,\n" +
                        "       First_Name,\n" +
                        "       Last_Name,\n" +
                        "       Full_Name,\n" +
                        "       Login_ID,\n" +
                        "       Phone_Work,\n" +
                        "       Phone_Ext,\n" +
                        "       GUID,\n" +
                        "       Fax,\n" +
                        "       VIP,\n" +
                        "       Title,\n" +
                        "       Network_Login,\n" +
                        "       Queue,\n" +
                        "       Designation,\n" +
                        "       Middle_Init,\n" +
                        "       Company_Code,\n" +
                        "       Cost_Code,\n" +
                        "       Role_Prefix,\n" +
                        "       Role_x,\n" +
                        "       zAssigned_Group,\n" +
                        "       Support_Person_ \n" +
                        "       AD_Department \n" +
                        "FROM CMN_People_Information\n" +
                        " WITH (NOLOCK) WHERE Login_ID ='" + remedyid + "'\n" +
                        " AND (Client  ='" + userModel.getClient() + "' \n" +
                        " OR Client  ='" + userModel.getMasterClient() + "') \n" +
                        "AND (First_Name is NOT NULL AND Last_Name is NOT NULL  AND Full_Name is not NULL AND Login_ID is NOT NULL) AND Status = '2' \n";

                SQLResult sqlResult = arServerUser.getListSQL(sQuery.toString(), 0, true);
                String groupid = "", networklogin = "", client = "", masterclient1 = "";
                masterclient1 = userModel.getMasterClient();
                client = userModel.getClient();
                List<Map<String, ? extends Object>> fQuery = new ArrayList<>();
                for (List<com.bmc.arsys.api.Value> content : sqlResult.getContents()) {
                    loginResults.put("Request ID", content.get(0).toString());
                    loginResults.put("Client", content.get(1).toString());
                    loginResults.put("Business Organization", content.get(2).toString());
                    loginResults.put("Building", content.get(3).toString());
                    loginResults.put("Department", content.get(4).toString());
                    loginResults.put("Floor", content.get(5).toString());
                    loginResults.put("Suite", content.get(6).toString());
                    loginResults.put("Office", content.get(7).toString());
                    loginResults.put("Status", content.get(8).toString());
                    loginResults.put("Email Address", content.get(9).toString());
                    loginResults.put("Employee Number", content.get(10).toString());
                    loginResults.put("First Name", content.get(11).toString());
                    loginResults.put("Last Name", content.get(12).toString());
                    loginResults.put("Full Name", content.get(13).toString());
                    loginResults.put("Login ID", content.get(14).toString());
                    loginResults.put("Phone-Work", content.get(15).toString());
                    loginResults.put("Phone Number", content.get(16).toString());
                    loginResults.put("GUID", content.get(17).toString());
                    loginResults.put("Fax", content.get(18).toString());
                    loginResults.put("VIP", content.get(19).toString());
                    loginResults.put("Title", content.get(20).toString());
                    loginResults.put("Network Login", content.get(21).toString());
                    loginResults.put("Queue", content.get(22).toString());
                    loginResults.put("Designation", content.get(23).toString());
                    loginResults.put("Middle Init", content.get(24).toString());
                    loginResults.put("Company Code", content.get(25).toString());
                    loginResults.put("Cost Code", content.get(26).toString());
                    loginResults.put("Role Prefix", content.get(27).toString());
                    loginResults.put("Role", content.get(28).toString());
                    loginResults.put("zAssigned Group", content.get(29).toString());
                    loginResults.put("Support Person?", content.get(30).toString());
                    groupid = content.get(29).toString().replaceAll(";", "");
                    networklogin = content.get(21).toString();
                    remedyid = content.get(14).toString();
                    client = content.get(1).toString();
                    break;
                }

                List<LdapTemplate> ldapTemplate = ldapTemplateMap.get(masterclient1);

                if (!(ldapTemplate == null || ldapTemplate.isEmpty())) {
                    for (LdapTemplate template : ldapTemplate) {
                        tempGroupInfos.addAll(LDAPBase.getadgroupNamesforuser(template, networklogin, masterclient1));
                        break;
                    }
                } else {
                    List<LDAPAccounts> ldapAccounts = ldapAccountsRepo.findByClientAndSsoIsTrue(masterclient1);
                    if (ldapAccounts != null && !ldapAccounts.isEmpty()) {
                        tempGroupInfos.addAll(LDAPBase.ldapgroupsfromsamaccountname(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "samaccountname=" + networklogin, masterclient1));
                    }
                }

                tempGroupInfos.addAll(RemedyBase.getusergroupsfromremedy(arServerUser1, remedyid, groupid));
                loginResults.put("userGroups", tempGroupInfos);

            } catch (Exception e) {
                loginResults.put("authenticated", "false");
                loginResults.put("server", serverName);
                loginResults.put("reason", e.getMessage());
            }
        } else {
            return loginResults;
        }
        return loginResults;
    }*/


    @RequestMapping(path = "/api/customloginDet", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public Map<String, Object> customloginDetails(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        int[] rf = new int[]{
                8, //fullname 0
                7,//7	Status
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
                704000051,//support person
                112,//zAssigned Group
        };
        int reqgroup[] = new int[]{
                134, //Parent Group
                107,//Group Type
                105, //Group Name
                106,//Group ID
                120,//Group Category
                136,//Real Overlay Group
                137,//DisplayOverlayGroup
        };

        Map<String, Object> loginResults = new HashMap<String, Object>();


        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                loginResults.put("authenticated", "true");
                loginResults.put("user", auth.getName());
                //loginResults.put("token", "1234567897");

               /* Entry entry = queryEntrysByQual
                        (arServerUser, "User", "('Login Name' = \"" + auth.getName() + "\")");

                if (entry != null) {
                    Map<Integer, String> userMap = new HashMap<>();//RemedyBase.getFormFields2(arServerUser, "User");

                    userMap.put(1,"Request ID");
                    userMap.put(101,"Login Name");
                    userMap.put(8,"Full Name");
                    userMap.put(109,"License Type");
                    userMap.put(103,"Email Address");
                    userMap.put(108,"Default Notify Mechanism");
                    userMap.put(7,"Status");
                    entry.keySet().forEach(keyId -> {
                        loginResults.put(userMap.get(keyId), entry.get(keyId).toString());
                    });

                }*/

                List<Entry> peopleEntry = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:People Information", rf, "('Login ID' = \"" + auth.getName() + "\")");

                String subclient = null, networklogin = null;
                String group = "";
                String masterclient1 = null;
                if (peopleEntry != null) {
                    Map<Integer, String> userMap = new HashMap<>();//RemedyBase.getFormFields2(arServerUser, "CMN:People Information");
                    userMap.put(1, "Request ID");
                    userMap.put(8, "Full Name");
                    userMap.put(7, "Status");
                    userMap.put(200000006, "Department");
                    userMap.put(200000007, "Building");
                    userMap.put(700001012, "Email Address");
                    userMap.put(700001038, "Employee Number");
                    userMap.put(910000100, "Floor");
                    userMap.put(910000101, "Suite");
                    userMap.put(910000102, "Office");
                    userMap.put(910000103, "Business Organization");
                    userMap.put(910000104, "Phone-Work");
                    userMap.put(700001084, "First Name");
                    userMap.put(700001082, "Last Name");
                    userMap.put(700001039, "Login ID");
                    userMap.put(910000105, "Phone Number");
                    userMap.put(179, "GUID");
                    userMap.put(910000106, "Fax");
                    userMap.put(700001022, "VIP");
                    userMap.put(620000180, "Title");
                    userMap.put(536870949, "Network Login");
                    userMap.put(910000350, "Queue");
                    userMap.put(700001132, "Designation");
                    userMap.put(200000012, "Client");
                    userMap.put(700001083, "Middle Init");
                    userMap.put(910000416, "Cost Code");
                    userMap.put(910000415, "Role");
                    userMap.put(910000414, "Role Prefix");
                    userMap.put(704000051, "Support Person?");
                    userMap.put(112, "zAssigned Group");

                    for (Entry entry1 : peopleEntry) {
                        subclient = entry1.get(200000012).toString();       //client
                        networklogin = entry1.get(536870949).toString();//networklogin
                        if (entry1.get(112).toString() != null) {
                            group = entry1.get(112).toString();
                        }
                        group = group.replaceAll(";", "");
                        for (Integer keyId : entry1.keySet()) {
                            loginResults.put(userMap.get(keyId), entry1.get(keyId).toString());
                        }
                        break;
                    }
                }


                List<GroupInfo> groupInfo = arServerUser.getListGroup(auth.getName(), null);

                Set<TempGroupInfo1> tempGroupInfos = new HashSet<>();
                Set<TempGroupInfo1> userldapgroupinfos = new HashSet<>();
                groupInfo.forEach(groupinfo -> {
                    TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(
                            String.valueOf(groupinfo.getName()),
                            String.valueOf(groupinfo.getId()),
                            String.valueOf(groupinfo.getGroupType()),
                            groupinfo.getGroupNames(),
                            String.valueOf(groupinfo.getCategory()),
                            String.valueOf(groupinfo.getGroupParent()),
                            String.valueOf(groupinfo.getGroupOverLay()),
                            "Remedy"
                    );
                    tempGroupInfos.add(tempGroupInfo);
                });
                int[] clientfieldid = new int[]{536870913, 536870918, 536870914};
                List<Entry> entrie = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        "CMN:ClientInfo",
                        clientfieldid,
                        "('Client'= \"" + subclient + "\")"
                );
                masterclient1 = entrie.get(0).get(536870914).toString();
                //

                //
                List<String> groups = new ArrayList();
                List<LDAPAccounts> ldapAccounts = ldapAccountsRepo.findByClientAndSsoIsTrue(masterclient1);
                if (ldapAccounts != null && !ldapAccounts.isEmpty()) {
                    String distinguishedname = "";
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "samaccountname=" + networklogin, "distinguishedname");
                    if (result != null && !result.isEmpty()) {
                        distinguishedname = result.get(0).get("distinguishedName").toString();
                        distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
                    }
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "member=" + distinguishedname, "samaccountname");
                    if (result != null && !result.isEmpty()) {
                        result.forEach(stringObjectMap -> {
                            groups.add(stringObjectMap.get("sAMAccountName").toString());

                        });
                    }
                }
//sAMAccountName
                if (!group.isEmpty()) {
                    List<Entry> grouplist = RemedyBase.queryEntrysByQual(arServerUser, "Group", reqgroup, "('Group ID'=" + Integer.parseInt(group) + ")");
                    grouplist.forEach(entry1 -> {
                   /*     134, //Parent Group
                                107,//Group Type
                                105, //Group Name
                                106,//Group ID
                                120,//Group Category
                                136,//Real Overlay Group
                                137,//DisplayOverlayGroup*/

                       /* Map<String, Object> groups = new HashMap<>();
                        groups.put("name", entry1.get(105).toString());
                        groups.put("id", entry1.get(106).toString());*/

                       /* groups.put("groupNames", groupnames);
                        groups.put("groupType", entry1.get(107).toString());
                        groups.put("category", entry1.get(120).toString());
                        groups.put("groupOverLay", 0);
                        groups.put("groupParent", 0);*/
                        // groupInfo.add(groups);
                        List<String> groupnames = new ArrayList<String>();
                        groupnames.add(entry1.get(105).toString() == null ? "0" : entry1.get(105).toString());
                        TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(entry1.get(105).toString(), entry1.get(106).toString() == null ? "0" : entry1.get(106).toString(),
                                entry1.get(107).toString() == null ? "0" : entry1.get(107).toString(),
                                groupnames,
                                entry1.get(120).toString() == null ? "0" : entry1.get(120).toString(),
                                "0",
                                "0", "Remedy");
                        tempGroupInfos.add(tempGroupInfo);
                    });
                }
                int i = 0;
                for (String s : groups) {
                    List<String> groupnames = new ArrayList<String>();
                    groupnames.add(s);

                    String groupid = RemedyBase.Md5hashing(masterclient1 + "_" + s);
                    TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(s, groupid,
                            (groupid),
                            groupnames,
                            "AD",
                            groupid,
                            groupid, "AD");
                    tempGroupInfos.add(tempGroupInfo);
                }
                //  Set<TempGroupInfo> tempGroupInfos1 = new HashSet<>();
                loginResults.put("userGroups", tempGroupInfos);
                //loginResults.put("userldapGroups", userldapgroupinfos);
                //loginResults.put("clientdetails", subEntry);
            } catch (Exception e) {
                loginResults.put("authenticated", "false");
                loginResults.put("server", serverName);
                loginResults.put("reason", e.getMessage());
            }
        } else {
            return loginResults;
        }
        return loginResults;
    }


    /* Entry queryEntrysByQual(ARServerUser server, String formName, String qualStr) {
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


         } catch (ARException e) {
             e.printStackTrace();
         }
         return userEntry;
     }

 */
    @RequestMapping(path = "/api/loginDetails", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public Map<String, Object> loginDetails(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
                704000051,//support person
                112,//zAssigned Group
        };
        int reqgroup[] = new int[]{
                134, //Parent Group
                107,//Group Type
                105, //Group Name
                106,//Group ID
                120,//Group Category
                136,//Real Overlay Group
                137,//DisplayOverlayGroup
        };

        Map<String, Object> loginResults = new HashMap<String, Object>();


        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                loginResults.put("authenticated", "true");
                loginResults.put("user", auth.getName());
                //loginResults.put("token", "1234567897");

                Entry entry = queryEntrysByQual
                        (arServerUser, "User", "('Login Name' = \"" + auth.getName() + "\")");

                if (entry != null) {
                    Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "User");

                    entry.keySet().forEach(keyId -> {
                        loginResults.put(userMap.get(keyId), entry.get(keyId).toString());
                    });

                }

                List<Entry> peopleEntry = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:People Information", rf, "('Login ID' = \"" + auth.getName() + "\")");

                String subclient = null, networklogin = null;
                String group = "";
                String masterclient1 = null;
                if (peopleEntry != null) {
                    Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:People Information");
                    for (Entry entry1 : peopleEntry) {
                        subclient = entry1.get(200000012).toString();       //client
                        networklogin = entry1.get(536870949).toString();//networklogin
                        if (entry1.get(112).toString() != null) {
                            group = entry1.get(112).toString();
                        }
                        group = group.replaceAll(";", "");
                        for (Integer keyId : entry1.keySet()) {
                            loginResults.put(userMap.get(keyId), entry1.get(keyId).toString());
                        }
                        break;
                    }
                }

                List<Entry> masterclient = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:ClientInfo", new int[]{536870914}, "('Client' = \"" + subclient + "\")");

                for (Entry entry1 : masterclient) {
                    masterclient1 = entry1.get(536870914).toString();
                }


                List<Entry> masterclientinfo = RemedyBase.queryEntrysByQual
                        (arServerUser, "CMN:ClientInfo", new int[]{536870913,
                                536870914,
                                536870929,
                                112,
                                536871013,
                                536870918,
                                536871121,
                                536871120}, "('Master Client' = \"" + masterclient1 + "\")");

                Map<Integer, String> userMap = RemedyBase.getFormFields2(arServerUser, "CMN:ClientInfo");

                List subEntry = new ArrayList<HashMap<String, String>>();
                masterclientinfo.forEach(entry1 -> {
                    Map singleE = new HashMap<String, String>();
                    entry1.keySet().forEach(integer -> {
                        singleE.put(userMap.get(integer), entry1.get(integer).toString());
                    });
                    subEntry.add(singleE);
                });

                List<GroupInfo> groupInfo = arServerUser.getListGroup(auth.getName(), null);

                Set<TempGroupInfo1> tempGroupInfos = new HashSet<>();
                Set<TempGroupInfo1> userldapgroupinfos = new HashSet<>();
                groupInfo.forEach(groupinfo -> {
                    TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(
                            String.valueOf(groupinfo.getName()),
                            String.valueOf(groupinfo.getId()),
                            String.valueOf(groupinfo.getGroupType()),
                            groupinfo.getGroupNames(),
                            String.valueOf(groupinfo.getCategory()),
                            String.valueOf(groupinfo.getGroupParent()),
                            String.valueOf(groupinfo.getGroupOverLay()),
                            "Remedy"
                    );
                    tempGroupInfos.add(tempGroupInfo);
                });
                //

                //
                List<String> groups = new ArrayList();
                List<LDAPAccounts> ldapAccounts = ldapAccountsRepo.findByClientAndSsoIsTrue(masterclient1);
                if (ldapAccounts != null && !ldapAccounts.isEmpty()) {
                    String distinguishedname = "";
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "samaccountname=" + networklogin, "distinguishedname");
                    if (result != null && !result.isEmpty()) {
                        distinguishedname = result.get(0).get("distinguishedName").toString();
                        distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
                    }
                    result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "member=" + distinguishedname, "samaccountname");
                    if (result != null && !result.isEmpty()) {
                        result.forEach(stringObjectMap -> {
                            groups.add(stringObjectMap.get("sAMAccountName").toString());

                        });
                    }
                }
//sAMAccountName
                if (!group.isEmpty()) {
                    List<Entry> grouplist = RemedyBase.queryEntrysByQual(arServerUser, "Group", reqgroup, "('Group ID'=" + Integer.parseInt(group) + ")");
                    grouplist.forEach(entry1 -> {
                   /*     134, //Parent Group
                                107,//Group Type
                                105, //Group Name
                                106,//Group ID
                                120,//Group Category
                                136,//Real Overlay Group
                                137,//DisplayOverlayGroup*/

                       /* Map<String, Object> groups = new HashMap<>();
                        groups.put("name", entry1.get(105).toString());
                        groups.put("id", entry1.get(106).toString());*/

                       /* groups.put("groupNames", groupnames);
                        groups.put("groupType", entry1.get(107).toString());
                        groups.put("category", entry1.get(120).toString());
                        groups.put("groupOverLay", 0);
                        groups.put("groupParent", 0);*/
                        // groupInfo.add(groups);
                        List<String> groupnames = new ArrayList<String>();
                        groupnames.add(entry1.get(105).toString() == null ? "0" : entry1.get(105).toString());
                        TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(entry1.get(105).toString(), entry1.get(106).toString() == null ? "0" : entry1.get(106).toString(),
                                entry1.get(107).toString() == null ? "0" : entry1.get(107).toString(),
                                groupnames,
                                entry1.get(120).toString() == null ? "0" : entry1.get(120).toString(),
                                "0",
                                "0", "Remedy");
                        tempGroupInfos.add(tempGroupInfo);
                    });
                }
                int i = 0;
                for (String s : groups) {
                    List<String> groupnames = new ArrayList<String>();
                    groupnames.add(s);

                    String groupid = RemedyBase.Md5hashing(masterclient1 + "_" + s);
                    TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(s, groupid,
                            (groupid),
                            groupnames,
                            "AD",
                            groupid,
                            groupid, "AD");
                    tempGroupInfos.add(tempGroupInfo);
                }
                //  Set<TempGroupInfo> tempGroupInfos1 = new HashSet<>();
                loginResults.put("userGroups", tempGroupInfos);
                //loginResults.put("userldapGroups", userldapgroupinfos);
                loginResults.put("clientdetails", subEntry);
            } catch (Exception e) {
                loginResults.put("authenticated", "false");
                loginResults.put("server", serverName);
                loginResults.put("reason", e.getMessage());
            }
        } else {
            return loginResults;
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


        } catch (ARException e) {
            e.printStackTrace();
        }
        return userEntry;
    }


    @RequestMapping(value = "/api/allForms", method = RequestMethod.GET)
    @ResponseBody
    public List<FormModel> allForms() {


        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1
            );


            return RemedyBase.getAllForms(arServerUser);
        } catch (ARException e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/api/ping", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> callSomething() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "pong");

        return map;
    }


    @RequestMapping(value = "/api/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String queryTickets(
            @RequestParam(value = "formName") String formName,
            @RequestParam(value = "query") String query
    ) {

        JsonArray resultMap = new JsonArray();

        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

            List<Entry> enty = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    formName,
                    null,
                    query
            );

            Map<Integer, String> formFields = RemedyBase.getFormFields2(arServerUser, formName);

            for (Entry e : enty) {
                JsonObject entryObject = new JsonObject();
                for (Integer keys : e.keySet()) {
                    entryObject.addProperty(formFields.get(keys), e.get(keys).toString());
                }
                resultMap.add(entryObject);
            }
        } catch (ARException e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("error", e.getMessage());
            return jsonObject.toString();
        }

        return resultMap.toString();
    }


    @RequestMapping(value = "/api/formq/{form}")
    public BaseModel queryObject(@PathVariable(name = "form") String formName) {

        BaseModel baseModel = null;

        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(
                    userRepo, arServerUser1
            );

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

        } catch (ARException e) {
            baseModel = new ErrorModel(e.getMessage());
        }

        return baseModel;

    }


    @RequestMapping(path = "/api/myticket", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getTicketsForUser(
            @RequestParam("login_name") String user
    ) {

        Map<String, Object> loginResults = new HashMap<String, Object>();
        try {
            ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

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
        } catch (ARException e) {
            loginResults.put("authenticated", "false");
            loginResults.put("server", serverName);
            loginResults.put("reason", e.getMessage());

        }


        return loginResults;
    }


    @RequestMapping(value = "/api/filterticket", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> filterTickets(@RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {

        List<BaseModel> baseModels = new ArrayList<>();
        String ticketid = "";
        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {

                int[] requiredFields = {1, 7, 8, 3};

                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
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
        } else {
            baseModels.add(new ErrorModel("ok"));
        }

        return baseModels;
    }


    @RequestMapping(value = "/api/clientBuildingDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseModel clientBuildingDetails(@RequestParam("clientname") String client) {


        ClientBuildingDetailsModel clientBuildingDetailsModel = new ClientBuildingDetailsModel();
        try {
            ARServerUser user = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

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
        } catch (ARException e) {
            return new ErrorModel(e.getMessage());
        }

        return clientBuildingDetailsModel;
    }

    @RequestMapping(value = "/api/departmentinfo", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> departmentinfo(@RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {

        List<BaseModel> baseModels = new ArrayList<>();
        String ticketid = "";
        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {

                int[] requiredFields = {200000012,  //client
                        200000007,                  //Building
                        200000006,                  //Department
                        2,                          //Submitter
                        7,                          //Status
                        536870981                   //Business Organisation
                };

                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
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
            } catch (ARException e) {
                baseModels.add(new ErrorModel(e.getLocalizedMessage()));
            }
        } else {
            baseModels.add(new ErrorModel("ok"));
        }
        return baseModels;
    }

    @RequestMapping(value = "/api/clientBuildingDetails1", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public BaseModel clientBuildingDetails1(@RequestParam("clientname") String client) {


        ClientBuildingDetailsModel clientBuildingDetailsModel = new ClientBuildingDetailsModel();
        try {
            ARServerUser user = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);

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
        } catch (ARException e) {
            return new ErrorModel(e.getMessage());
        }
        return clientBuildingDetailsModel;
    }

    @CrossOrigin
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/api/ticketinfo")
    @ResponseBody
    public Map<String, Object> ticketInfo(
            HttpServletRequest request,
            @RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId) {

        Map<String, Object> resultMap = new HashMap<>();

        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser user = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                return RemedyBase.getTicketEntry(user, formName, ticketId);
            } catch (ARException e) {
                resultMap.put("error", e.getMessage());
            }
        } else {
            resultMap.put("success", "ok");
        }
        return resultMap;
    }

    @CrossOrigin
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, path = "/api/updateticket")
    @ResponseBody
    public Map<String, String> updateticket(
            HttpServletRequest request,
            /*@RequestParam("formName") String formName,
            @RequestParam("ticketId") String ticketId,*/
            @RequestBody Map<String, String> updateParam) {
        if (!request.getMethod().equalsIgnoreCase("options")) {
            String formName = null, ticketId = null;
            boolean formstatus = false, ticketIdstatus = false;
            try {
                ARServerUser user = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
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
                    return RemedyBase.updateTicket(user, updateParam, formName, ticketId);
                } else {
                    throw new Exception("Formname or Ticket id not available");
                }
            } catch (Exception e) {
                return RemedyBase.returnError(e.getMessage());
            }
        } else {
            return RemedyBase.returnSuccess();
        }
    }

    @RequestMapping(value = "/api/queryfields", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
    @ResponseBody
    public List<Map<String, String>> queryTicketsMap(
            HttpServletRequest request,
            @RequestParam(value = "formName") String formName,
            @RequestParam(value = "query") String query
    ) {

        List<Map<String, String>> resultMap = new ArrayList<>();
        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1
                );
                List<Entry> enty = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        formName,
                        RemedyBase.getAllFieldsInt(arServerUser, formName),
                        query
                );
                Map<Integer, String> formFields = RemedyBase.getFormFields2(arServerUser, formName);

                for (Entry e : enty) {
                    HashMap<String, String> entryObject = new HashMap<>();

                    for (Integer keys : e.keySet()) {
                        entryObject.put(formFields.get(keys).toString(), e.get(keys).toString());
                    }
                    resultMap.add(entryObject);
                }
            } catch (ARException e) {
                resultMap.add(RemedyBase.returnError(e.getLocalizedMessage()));
            }
        } else {
            resultMap.add(RemedyBase.returnSuccess());
        }
        return resultMap;
    }

    @RequestMapping(value = "/api/queryfields2", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
    @ResponseBody
    public List<BaseModel> queryTicketsMap2(
            HttpServletRequest request,
            @RequestParam(value = "query") String query
    ) {
        List<BaseModel> baseModels = new ArrayList<>();
        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                ARServerUser arServerUser = RemedyBase.getUserWithAuth1(userRepo, arServerUser1);
                Map<Integer, String> srFields = RemedyBase.getFormFields2(arServerUser, "SR:ServiceRequest");
                Map<Integer, String> ptFields = RemedyBase.getFormFields2(arServerUser, "SR:ServiceRequest");
                int[] requiredFields = {1, 7, 8, 3};
                int[] sr = new int[srFields.keySet().size()];
                int[] pt = new int[ptFields.keySet().size()];
                int c = 0;
                for (int x : srFields.keySet()) sr[c++] = x;
                int d = 0;
                for (int x : srFields.keySet()) pt[d++] = x;
                List<Entry> srt = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        "SR:ServiceRequest",
                        requiredFields,
                        query
                );
                List<Entry> ptt = RemedyBase.queryEntrysByQual(
                        arServerUser,
                        "PT:ProblemTicket",
                        requiredFields,
                        query
                );
                for (Entry e : srt) {
                    String date = e.get(requiredFields[3]).toString();
                    baseModels.add(new MiniTicketModel(
                            e.get(requiredFields[0]).toString(),
                            e.get(requiredFields[1]).toString(),
                            e.get(requiredFields[2]).toString(),
                            RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                            "ServiceRequest"
                    ));
                }
                for (Entry e : ptt) {
                    String date = e.get(requiredFields[3]).toString();
                    baseModels.add(new MiniTicketModel(
                            e.get(requiredFields[0]).toString(),
                            e.get(requiredFields[1]).toString(),
                            e.get(requiredFields[2]).toString(),
                            RemedyBase.formatEpocDate(date.substring(date.indexOf("=") + 1, date.indexOf("]"))),
                            "ProblemTicket"
                    ));
                }
            } catch (ARException e) {
                baseModels.add(new ErrorModel(e.getMessage()));
            }
        } else {
            baseModels.add(new ErrorModel("success"));
        }

        return baseModels;
    }


}


