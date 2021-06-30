package com.htc.remedy.controller;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.base.SFInterfaceConnectionBase;
import com.htc.remedy.base.SFInterfaceLoggerBase;
import com.htc.remedy.base.SFInterfaceMessages;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.db.ReferenceDBConnector;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.ldap.LDAPComponent;
import com.htc.remedy.model.GroupRDB;
import com.htc.remedy.model.User;
import com.htc.remedy.oauth.CustomUserDetail;
import com.htc.remedy.services.SFInterfaceServices;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static com.htc.remedy.constants.SFInterfaceConstants.*;

@RestController
@RequestMapping(path = "/auth/v1")
public class OAUTHController {

    private static final Logger logger = LogManager.getLogger(OAUTHController.class);

    @Autowired
    private TokenStore tokenStore;

    private final
    SFInterfaceMessages messages;

    private static
    Gson gson;


    private final ReferenceDBConnector referenceDBConnector;

    private final
    LDAPComponent ldapComponent;

    @Autowired
    public OAUTHController(SFInterfaceMessages messages, Gson gson, ReferenceDBConnector referenceDBConnector, LDAPComponent ldapComponent) {
        this.messages = messages;
        this.gson = gson;
        this.referenceDBConnector = referenceDBConnector;
        this.ldapComponent = ldapComponent;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity me(HttpServletRequest request) throws IOException {
        Map<String, Object> loginResult = new HashMap<>();
        User user = new User();
        String subclient = null, networklogin = null, masterclient1 = null;
        Set<GroupRDB> tempGroupInfos = new HashSet<>();
        if (!request.getMethod().equalsIgnoreCase("options")) {
            try {
                user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                /*User Details Indexer Start*/
                loginResult = fetchUserProfile(user.getUserID(), user);
                loginResult.put(CLIENT_INSTANCE, user.getClientInstance());
                String application = null;
                /*User Details Indexer END*/

                loginResult.put("authenticated", true);
                loginResult.put("loginName", user.getAuthUserName());
                loginResult.put(AUTH_TYPE_CODE, user.getAuthTokenType());

                if (loginResult.containsKey("Client"))
                    subclient = loginResult.get("Client").toString();
                if (loginResult.containsKey("Network_Login") && !(loginResult.get("Network_Login") == null))
                    networklogin = loginResult.get("Network_Login").toString();

                masterclient1 = loginResult.get("MasterAccount").toString();

                /*Accounts Details INdexer Start*/
                List<Map<String, Object>> clientDetails = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_ACCOUNT, MASTER_ACCOUNT + COLON + masterclient1 + OR + ACCOUNT + COLON + subclient, null,
                        null, null, null,
                        null, null, 0, Boolean.FALSE, null, user);
                //defaultapplication starts
                loginResult = updateDefaultApplication(loginResult, masterclient1, subclient, clientDetails, request, user);
                //defaultapplication ends
                List<Map<String, String>> clientDetailss = new ArrayList<>();
                clientDetails.forEach(client -> {
                    Map<String, String> res = new HashMap<>();
                    res.put("Client_ID", client.get("Account_sk").toString());
                    res.put("Client_Name", client.get("AccountName").toString());
                    res.put("Client", client.get("Account").toString());
                    res.put("Client_Status", client.get("Status_sk").toString());
                    res.put("Parent_Client", client.get("MasterAccount").toString());
                    clientDetailss.add(res);
                });
                loginResult.put("clientdetails", gson.toJsonTree(clientDetailss));
                /*Accounts Details INdexer End*/

                /*User Groups Start*/
                loginResult = updateGroups(loginResult, user, networklogin, masterclient1, subclient, ldapComponent, request);
                /*User Groups End*/

                if (request.getParameterMap().containsKey("Preference") && request.getParameter("Preference").toString().equalsIgnoreCase("1")) {
                    loginResult = updatePreference(loginResult, user, masterclient1, subclient);
                }

                //Roles
                if (!(request.getParameterMap().containsKey("Role")
                        && request.getParameter("Role").equalsIgnoreCase(ZERO)
                )) {
                    loginResult = updateRoles(loginResult, user, subclient);
                }

                loginResult.put("applications", new JsonArray());
            } catch (Exception e) {
                loginResult.put("authenticated", "false");
                loginResult.put("reason", e.getMessage());
                return new ResponseEntity(gson.toJson(loginResult), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity(gson.toJson(loginResult), HttpStatus.OK);
        }
        return new ResponseEntity(gson.toJson(loginResult), HttpStatus.OK);
    }

    public static Map<String, Object> updateRoles(Map<String, Object> loginResult, User user, String subclient) {
        List<Map<String, Object>> rolesResult = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_ACCOUNT_ROLE_USER, "User_sk:" + user.getUserID() + " AND AccountRoleCode:\"ACRU0000008\"",
                null, new String[]{"AccountRole_sk", "Role", "User_sk", "Account_sk", "Account"}, null,
                null, null, null, 0, Boolean.FALSE, null, user);
        //Support users
        if (loginResult.containsKey("Support_Person_") && loginResult.get("Support_Person_").toString().equalsIgnoreCase("Yes")) {
            Map<String, Object> support = new HashMap<>();
            support.put("Role", "Support");
            support.put("Account_sk", loginResult.get("Account_sk").toString());
            support.put("Account", subclient);
            rolesResult.add(support);
        }
        List<Map<String, Object>> resultgroups = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_USERGROUPS,
                "User_sk:" + user.getUserID() + " AND " + GROUP_NAME + ":\"" + APP_SERVICE_DESK + "\"",
                null, new String[]{"Group_sk", "GroupCode", "GroupName", "LongGroupName", "AccountGroup_sk"}, null,
                null, null, null, 0, Boolean.FALSE, null, user);
        if (resultgroups != null && !resultgroups.isEmpty()) {
            Map<String, Object> serviceDesk = new HashMap<>();
            serviceDesk.put("Role", APP_SERVICE_DESK);
            rolesResult.add(serviceDesk);

        }

        Map<String, String[]> params = new HashMap<>();
        params.put(GROUP_NAME, SFInterfaceBase.convertcommaseparatedStringtoStringarray(SFInterfaceConstants.getCr_coordinator_groupname()));
        params.put(USER_SK, new String[]{user.getUserID()});
        String query = SFInterfaceBase.generateWhooshQueryDynamic(params);
        List<Map<String, Object>> resultgroupscr_coordinator = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_USERGROUPS,
                query,
                null, new String[]{"Group_sk", "GroupCode", "GroupName", "LongGroupName", "AccountGroup_sk"}, null,
                null, null, null, 0, Boolean.FALSE, null, user);
        if (resultgroupscr_coordinator != null && !resultgroupscr_coordinator.isEmpty()) {
            Map<String, Object> serviceDesk = new HashMap<>();
            serviceDesk.put("Role", cr_coordinator);
            rolesResult.add(serviceDesk);
        }
        params.put(GROUP_NAME, SFInterfaceBase.convertcommaseparatedStringtoStringarray(SFInterfaceConstants.getCrAdminGroupname()));
        query = SFInterfaceBase.generateWhooshQueryDynamic(params);
        List<Map<String, Object>> resultgroupscr_admin = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_USERGROUPS,
                query,
                null, new String[]{"Group_sk", "GroupCode", "GroupName", "LongGroupName", "AccountGroup_sk"}, null,
                null, null, null, 0, Boolean.FALSE, null, user);
        if (resultgroupscr_admin != null && !resultgroupscr_admin.isEmpty()) {
            Map<String, Object> serviceDesk = new HashMap<>();
            serviceDesk.put("Role", CR_ADMIN);
            rolesResult.add(serviceDesk);
        }

        //Problem Manager
        List<Map<String, Object>> problemManagerResult = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_PROBLEM_MANAGER, "User_sk:" + user.getUserID() + AND + STATUS_CODE + COLON + "A",
                null, new String[]{"ProblemManager_sk", "User_sk", "Account_sk", "Account"}, null,
                null, "True", null, 0, Boolean.FALSE, "Account_sk", user);

        if (problemManagerResult.size() > 0) {
            Map<String, Object> problemManager = new HashMap<>();
            List<Map<String, Object>> problemManagerAccounts = new ArrayList<>();
            for (Map<String, Object> stringObjectMap : problemManagerResult) {
                Map<String, Object> problemManageraccount = new HashMap<>();
                problemManager.put("Role", "IncidentManager");
                //  problemManager.put("ProblemManager_sk", stringObjectMap.get("ProblemManager_sk"));
                problemManager.put("User_sk", stringObjectMap.get("User_sk"));
                problemManageraccount.put("Account_sk", stringObjectMap.get("Account_sk"));
                problemManageraccount.put("Account", stringObjectMap.get("Account"));
                problemManagerAccounts.add(problemManageraccount);
            }
            problemManager.put("Accounts", problemManagerAccounts);
            rolesResult.add(problemManager);
            //rolesResult.addAll(problemManagerResult);
        }
        loginResult.put("Roles", gson.toJsonTree(rolesResult));
        return loginResult;
    }

    public static Map<String, Object> updateGroups(Map<String, Object> loginResult, User user, String networklogin, String masterclient1, String subclient, LDAPComponent ldapComponent, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        Set<GroupRDB> tempGroupInfos = new HashSet<>();
        if (
                !(request.getParameterMap().containsKey("remedy_groups")
                        && request.getParameter("remedy_groups").equalsIgnoreCase(ZERO)
                )
                        &&
                        !(request.getParameterMap().containsKey("groups")
                                &&
                                request.getParameter("groups").equalsIgnoreCase(ZERO)
                        )
        ) {
            List<Map<String, Object>> resultgroups = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_USERGROUPS, "User_sk:" + user.getUserID() + " OR GroupCode:\"" + subclient + "\"",
                    null, new String[]{"Group_sk", "GroupCode", "GroupName", "LongGroupName", "AccountGroup_sk"}, null,
                    null, null, null, 0, Boolean.FALSE, null, user);
            resultgroups.forEach(groupinfo -> {
                tempGroupInfos.add(new GroupRDB(
                        groupinfo.get("Group_sk").toString(),
                        groupinfo.get("GroupCode").toString(),
                        groupinfo.get("AccountGroup_sk").toString(),
                        groupinfo.get("GroupName").toString(),
                        groupinfo.get("LongGroupName").toString(),
                        "Remedy"
                ));
            });
        }
        if ((request.getParameter("ad_groups") == null || request.getParameter("ad_groups").equalsIgnoreCase("1")) && StringUtils.isNotBlank(networklogin)) {
            List<Map<String, Object>> result = new ArrayList<>();
            List<LDAPAccounts> clientLdapAccounts = SFInterfaceServices.getLdapAccountsForClient(masterclient1, user);
            List<String> groups = new ArrayList<>();
            if (clientLdapAccounts != null && !clientLdapAccounts.isEmpty()) {
                String distinguishedname = "";
                result = ldapComponent.loginDetailLdapQuery(clientLdapAccounts.get(0), clientLdapAccounts.get(0).getBase(), "samaccountname=" + networklogin, "distinguishedname");
                if (result != null && !result.isEmpty()) {
                    distinguishedname = result.get(0).get("distinguishedName").toString();
                    distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
                }
                result = ldapComponent.loginDetailLdapQuery(clientLdapAccounts.get(0), clientLdapAccounts.get(0).getBase(), "member=" + distinguishedname, "samaccountname");
                if (result != null && !result.isEmpty()) {
                    result.forEach(stringObjectMap -> {
                        groups.add(stringObjectMap.get("sAMAccountName").toString());
                    });
                }
            }
            for (String s : groups) {
                String groupid = ldapComponent.userGroupHashing(masterclient1 + "_" + s);

                GroupRDB groupRDB = new GroupRDB(
                        groupid,
                        groupid,
                        groupid,
                        s,
                        s,
                        "AD"
                );
                tempGroupInfos.add(groupRDB);
            }
        }

        loginResult.put("userGroups", gson.toJsonTree(tempGroupInfos, new TypeToken<Set<GroupRDB>>() {
        }.getType()));
        return loginResult;
    }

    public static Map<String, Object> updateDefaultApplication(Map<String, Object> loginResult, String masterclient1, String subclient, List<Map<String, Object>> clientDetails, HttpServletRequest request, User user) {
        String application = null;
        if (request.getParameter("default_application") == null || request.getParameter("default_application").equalsIgnoreCase("1")) {
            if (!(loginResult.get("Support_Person_") == null) && loginResult.get("Support_Person_").toString().equalsIgnoreCase("Yes")) {
                if (loginResult.get("AccApplication_sk") == null) {

                    application = clientDetails.stream()
                            .filter(stringObjectMap ->
                                    stringObjectMap.get(ACCOUNT).toString().equalsIgnoreCase(subclient)
                                            && stringObjectMap.get("AccApplication_sk") != null
                            )
                            .map(stringObjectMap -> stringObjectMap.get("AccApplication_sk").toString())
                            .limit(1)
                            .collect(Collectors.joining());
                    if (StringUtils.isBlank(application)) {
                        application = clientDetails.stream()
                                .filter(stringObjectMap ->
                                        stringObjectMap.get(MASTER_ACCOUNT).toString().equalsIgnoreCase(masterclient1)
                                                && stringObjectMap.get("AccApplication_sk") != null)
                                .map(stringObjectMap -> stringObjectMap.get("AccApplication_sk").toString())
                                .limit(1)
                                .collect(Collectors.joining());
                    }
                } else {
                    application = loginResult.get("AccApplication_sk").toString();
                }
            }
        }
        List<Map<String, Object>> accountapplication = new ArrayList<>();
        if (StringUtils.isNotBlank(application)) {
            accountapplication = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_AccountApplication, "AccApplication_sk :" + application,
                    null, new String[]{"AccApplication_sk", "Application_sk",
                            "ApplicationName", "ApplicationURL",
                            "ApplicationSubURL", "ThumbnailURL", "Target"}, null,
                    null, null, null, 0, Boolean.FALSE, null, user);
        }//   , "AuthType_sk", "IsSSO", "SortOrder",ACCOUNT_SK
        loginResult.put("DefaultApplication", gson.toJsonTree(accountapplication));
        return loginResult;
    }

    public static Map<String, Object> updatePreference(Map<String, Object> loginResult, User user, String masterclient1, String subclient) {
        List<Map<String, Object>> userpreferences = new ArrayList<>();
        int i = 1;
        do {
            switch (i) {
                case 1: {
                    userpreferences = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERPREFERENCE, USER_SK + COLON + loginResult.get(USER_SK) + AND + TYPE + COLON + "COMM"
                            , null, new String[]{PREFRENCE}, null, null, null, null, NUM_ZERO, user);
                }
                break;
                case 2: {
                   /* userpreferences = SFInterfaceBase.fetchWhooshRecords(INDEXERNAME_FETCH_USERPREFERENCE, OPEN_BRACKET + ACCOUNT + COLON + (SFInterfaceBase.checkBlankorEmpty(masterclient1) ? masterclient1 : subclient) +
                                    ACCOUNT + COLON + subclient + CLOSE_BRACKET + AND + TYPE + COLON + "GEN"
                            , null, new String[]{PREFRENCE}, null, null, null, null, NUM_ZERO, user);*/
                }
            }

            i++;
        } while (userpreferences.isEmpty() && i <= 2);
        if (!userpreferences.isEmpty()) {
            if (SFInterfaceBase.isJSONValid((String) userpreferences.get(NUM_ZERO).get(PREFRENCE))) {
                loginResult.put(PREFRENCE, gson.fromJson((String) userpreferences.get(NUM_ZERO).get(PREFRENCE), Object.class));
            }
        } else {
            loginResult.put(PREFRENCE, userpreferences);
        }
        return loginResult;
    }


    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public String user(HttpServletRequest request) throws IOException {
        JsonObject loginResult = new JsonObject();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loginName = auth.getName();

        File profile_query = ResourceUtils.getFile("classpath:sql/login_detail_profile_query.txt");
        String profileQuery = new String(Files.readAllBytes(profile_query.toPath()));
        User user = new User();
        String[] usernameAndClient = loginName.split("~");
        if (!request.getMethod().equalsIgnoreCase("options")) {

            try {
                user = SFInterfaceConnectionBase.fetchRequestInfo(request);
                loginResult.addProperty("authenticated", true);
                loginResult.addProperty("loginName", usernameAndClient[0]);

                //over
                SFInterfaceConnectionBase.fetchJdbcTemplatebyType(user, SFInterfaceConstants.getDbRefConnectionName()).queryForObject(String.format(profileQuery, usernameAndClient[0], usernameAndClient[1]), (RowMapper<Map<String, String>>) (resultSet, i) -> {
                    if (resultSet != null) {
                        for (int i1 = 1; i1 <= resultSet.getMetaData().getColumnCount(); i1++) {
                            loginResult.addProperty(
                                    resultSet.getMetaData().getColumnName(i1),
                                    resultSet.getString(i1)
                            );
                        }
                    }
                    return null;
                });

            } catch (Exception e) {
                loginResult.addProperty("authenticated", "false");
                loginResult.addProperty("reason", e.getMessage());
            }
        } else {
            return gson.toJson(loginResult);
        }

        return gson.toJson(loginResult);
    }

    @RequestMapping(value = "/revoke-token", method = RequestMethod.GET)
    public Map<String, String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        Map<String, String> resp = new HashMap<>();
        if (authHeader != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = "";
            if (auth != null && auth.getName() != null) {
                username = auth.getName();
            }
            try {
                String tokenValue = authHeader.replace("Bearer", "").trim();
                OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
                if (accessToken == null) {
                    SFInterfaceLoggerBase.log(logger, USER + COLON + username + "\t" + messages.get("AUTHENTICATION.REVOKE.TOKEN") + COLON + messages.get("AUTHENTICATION.INVALID.ACCESS.TOKEN"));
                    throw new Exception("Invalid Access Token:" + tokenValue);
                } else {
                    if (accessToken.getRefreshToken() != null) {
                        revokeUserSession(auth.getName(), accessToken.getRefreshToken());
                    }
                    this.tokenStore.removeAccessToken(accessToken);
                }
                SFInterfaceLoggerBase.log(logger, USER + COLON + username + "\t" + messages.get("AUTHENTICATION.REVOKE.TOKEN") + COLON + SUCCESS);
                resp.put("result", "success");
            } catch (Exception e) {
                resp.put("result", "error");
                resp.put("error", e.getMessage());
                return resp;
            }
        }
        return resp;
    }

    @RequestMapping(value = "/validaterefreshtoken", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity validaterefreshtoken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh_token");
        Map<String, Boolean> resp = new HashMap<>();
        if (refreshToken != null) {
            try {
                OAuth2RefreshToken readRefreshToken = tokenStore.readRefreshToken(refreshToken);
                if (readRefreshToken == null) {
                    SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.REFRESH.TOKEN") + COLON + refreshToken + EMPTY + STATUS + COLON + ERROR);
                    resp.put("validrefreshtoken", false);
                } else {
                    // SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.REFRESH.TOKEN") + COLON + refreshToken + EMPTY + STATUS + COLON + SUCCESS);
                    resp.put("validrefreshtoken", true);
                }
            } catch (Exception e) {
                SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.REFRESH.TOKEN") + COLON + refreshToken + EMPTY + STATUS + COLON + ERROR);
                resp.put("validrefreshtoken", false);
                return new ResponseEntity(resp, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity(resp, HttpStatus.OK);
    }


    @RequestMapping(value = "/revokerefresh-token", method = RequestMethod.GET)
    public Map<String, String> revokefreshtoken(HttpServletRequest request) {
        String authHeader = request.getHeader("refresh_token");
        Map<String, String> resp = new HashMap<>();
        if (authHeader != null) {
            try {
                // String tokenValue = authHeader.replace("Bearer", "").trim();
                OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(authHeader);

                if (refreshToken == null) {
                    throw new Exception("invalid refresh token: " + authHeader);
                }

                OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
                //OAuth2AccessToken accessToken = tokenStore.getAccessToken(authentication);
                revokeUserSession(authentication.getName(), refreshToken);
                resp.put("result", "success");

            } catch (Exception e) {
                resp.put("result", "error");
                resp.put("error", e.getMessage());
                return resp;
            }
        }
        return resp;
    }

    public void revokeUserSession(String username, OAuth2RefreshToken userSessionRefreshtoken) {
        Collection<OAuth2AccessToken> accessTokens = this.tokenStore.findTokensByClientIdAndUserName("ctssp", username);
        accessTokens.stream()
                .filter(oAuth2AccessToken -> oAuth2AccessToken.getRefreshToken().getValue().equalsIgnoreCase(userSessionRefreshtoken.getValue()))
                .forEach(oAuth2AccessToken -> {
                    this.tokenStore.removeAccessToken(oAuth2AccessToken);
                });
        this.tokenStore.removeRefreshToken(userSessionRefreshtoken);
    }


    public static Map<String, Object> fetchUserProfile(String usersk, User user) {
        Map<String, Object> loginResult = new LinkedHashMap<>();
        List<Map<String, Object>> profiles = SFInterfaceBase.fetchWhooshRecords(SFInterfaceConstants.INDEXERNAME_FETCH_PROFILE,
                "User_sk:" + usersk, null, null,
                null, null, null, null, 0, Boolean.FALSE, null, user);
        profiles.forEach(profile -> {

            loginResult.put("People_Info_SK", usersk);
            loginResult.put("Full_Name", (String) profile.get("FullName") != null ? (String) profile.get("FullName") : "");
            loginResult.put("Department", (String) profile.get("Department") != null ? (String) profile.get("Department") : "");
            loginResult.put("Building", (String) profile.get("Building") != null ? (String) profile.get("Building") : "");
            loginResult.put("Floor", (String) profile.get("Floor") != null ? (String) profile.get("Floor") : "");
            loginResult.put("Suite", (String) profile.get("Suite") != null ? (String) profile.get("Suite") : "");
            loginResult.put("Office", (String) profile.get("Office") != null ? (String) profile.get("Office") : "");
            loginResult.put("Business_Organization", (String) profile.get("BusinessOrganization") != null ? (String) profile.get("BusinessOrganization") : "");
            loginResult.put("Phone_Work", (String) profile.get("PhoneWork") != null ? (String) profile.get("PhoneWork") : "");
            loginResult.put("First_Name", (String) profile.get("FirstName") != null ? (String) profile.get("FirstName") : "");
            loginResult.put("Last_Name", (String) profile.get("LastName") != null ? (String) profile.get("LastName") : "");
            loginResult.put("Phone_Ext", (String) profile.get("PhoneExt") != null ? (String) profile.get("PhoneExt") : "");
            loginResult.put("Designation", (String) profile.get("Designation") != null ? (String) profile.get("Designation") : "");
            loginResult.put("Fax", (String) profile.get("Fax") != null ? (String) profile.get("Fax") : "");
            loginResult.put("VIP", (String) profile.get("VIP") != null ? (String) profile.get("VIP") : "");
            loginResult.put("Title", (String) profile.get("Title") != null ? (String) profile.get("Title") : "");
            loginResult.put("ITSM_Login", (String) profile.get("LoginID") != null ? (String) profile.get("LoginID") : "");
            loginResult.put("Network_Login", (String) profile.get("NetworkLogin") != null ? (String) profile.get("NetworkLogin") : "");
            loginResult.put("Local_Login", (String) profile.get("RefID3") != null ? (String) profile.get("RefID3") : "");
            loginResult.put("Queue", (String) profile.get("Queue") != null ? (String) profile.get("Queue") : "");
            loginResult.put("Client", (String) profile.get("Account") != null ? (String) profile.get("Account") : "");
            loginResult.put("Middle_Init", (String) profile.get("MiddleInitial") != null ? (String) profile.get("MiddleInitial") : "");
            loginResult.put("Role_x", (String) profile.get("Role") != null ? (String) profile.get("Role") : "");
            loginResult.put("Role_Prefix", (String) profile.get("RoleDescription") != null ? (String) profile.get("RoleDescription") : "");
            loginResult.put("Support_Person_", profile.get("IsSupportPerson").toString().equalsIgnoreCase("True") ? "Yes" : "No");
            loginResult.put("Status", (String) profile.get("Status") != null ? (String) profile.get("Status") : "");
            loginResult.put("Status_sk", (String) profile.get("Status_sk"));
            loginResult.put("Fname", (String) profile.get("FirstName") != null ? (String) profile.get("FirstName") : "");
            loginResult.put("LName", (String) profile.get("LastName") != null ? (String) profile.get("LastName") : "");
            loginResult.put("Employee_Number", (String) profile.get("EmployeeNumber") != null ? (String) profile.get("EmployeeNumber") : "");
            loginResult.put("Emp_No", (String) profile.get("EmployeeNumber") != null ? (String) profile.get("EmployeeNumber") : "");
            loginResult.put("BusinessEmailAddress", (String) profile.get("EmailAddress") != null ? (String) profile.get("EmailAddress") : "");
            loginResult.put("RefID4", (String) profile.get("RefID4") != null ? (String) profile.get("RefID4") : "");
            loginResult.put("Pager_Alpha", (String) profile.get("PagerAlpha") != null ? (String) profile.get("PagerAlpha") : "");
            loginResult.put("Pager_Numeric", (String) profile.get("PagerNumeric") != null ? (String) profile.get("PagerNumeric") : "");
            loginResult.put("Phone_Cell", (String) profile.get("PhoneCell") != null ? (String) profile.get("PhoneCell") : "");
            loginResult.put("Phone_Home", (String) profile.get("PhoneHome") != null ? (String) profile.get("PhoneHome") : "");
            loginResult.put("supervisor_login", (String) profile.get("supervisor_login") != null ? (String) profile.get("supervisor_login") : "");
            loginResult.put("supervisor_name", (String) profile.get("supervisor_name") != null ? (String) profile.get("supervisor_name") : "");
            loginResult.put("Cost_code", (String) profile.get("CostCode") != null ? (String) profile.get("CostCode") : "");
            loginResult.put("Notify_Method", (String) profile.get("NotifyMethod") != null ? (String) profile.get("NotifyMethod") : "");
            loginResult.put("CST", (String) profile.get("CST") != null ? (String) profile.get("CST") : "");
            loginResult.put("Client_Note", (String) profile.get("Client_Note") != null ? (String) profile.get("Client_Note") : "");
            loginResult.put("ad_company", "");
            loginResult.put("ad_department", "");
            loginResult.put("AssignedGroup", (String) profile.get("AssignedGroup") != null ? (String) profile.get("AssignedGroup") : "");
            loginResult.put("GUID", (String) profile.get("GUID") != null ? (String) profile.get("GUID") : "");
            loginResult.put("Pager_Pin", (String) profile.get("Pager_Pin") != null ? (String) profile.get("Pager_Pin") : "");
            loginResult.put("ExtSysID", (String) profile.get("ExtSysID") != null ? (String) profile.get("ExtSysID") : "");
            loginResult.put("Client_Note_Expiration_Date", (String) profile.get("Client_Note_Expiration_Date") != null ? (String) profile.get("Client_Note_Expiration_Date") : "");
            loginResult.put("MasterAccount", (String) profile.get("MasterAccount") != null ? (String) profile.get("MasterAccount") : "");
            loginResult.put("Title_sk", (String) profile.get("Title_sk") != null ? (String) profile.get("Title_sk") : "0");
            loginResult.put("Account_sk", (String) profile.get("Account_sk"));
            loginResult.put("MasterAccount_sk", (String) profile.get("MasterAccount_sk"));
            loginResult.put("AccApplication_sk", (String) profile.get("AccApplication_sk"));

//recently added

            loginResult.put("Account", (String) profile.get("Account"));
            loginResult.put("AccountCodeName", (String) profile.get("AccountCodeName"));
            loginResult.put("AccountName", (String) profile.get("AccountName"));
            loginResult.put("AccApplication_sk", (String) profile.get("AccApplication_sk"));
            loginResult.put("AccountQueue", (String) profile.get("AccountQueue"));
            loginResult.put("BuildingCode", (String) profile.get("BuildingCode"));
            loginResult.put("Building_sk", (String) profile.get("Building_sk"));
            //    loginResult.put("BusinessOrganization", (String) profile.get("BusinessOrganization"));
            loginResult.put("ClientExecutive", (String) profile.get("ClientExecutive"));
            loginResult.put("ClientNote", (String) profile.get("ClientNote"));
            loginResult.put("ClientNoteExpirationDate", (String) profile.get("ClientNoteExpirationDate"));
            loginResult.put("CompanyCode", (String) profile.get("CompanyCode"));
            loginResult.put("ContactType", (String) profile.get("ContactType"));
            loginResult.put("ContactTypeCode", (String) profile.get("ContactTypeCode"));
            loginResult.put("ContactType_sk", (String) profile.get("ContactType_sk"));
            loginResult.put("CostCode", (String) profile.get("CostCode"));
         /*   loginResult.put("CreatedBy", (String) profile.get("CreatedBy"));
            loginResult.put("CreatedByFirstName", (String) profile.get("CreatedByFirstName"));
            loginResult.put("CreatedByLastName", (String) profile.get("CreatedByLastName"));
            loginResult.put("CreatedByLoginID", (String) profile.get("CreatedByLoginID"));
            loginResult.put("CreatedByNetworkLoginID", (String) profile.get("CreatedByNetworkLoginID"));
            loginResult.put("CreatedBy_sk", (String) profile.get("CreatedBy_sk"));
            loginResult.put("CreatedOn", (String) profile.get("CreatedOn"));*/
            loginResult.put("DepartmentCode", (String) profile.get("DepartmentCode"));
            loginResult.put("Department_sk", (String) profile.get("Department_sk"));
            loginResult.put("Designation_sk", (String) profile.get("Designation_sk"));
            //    loginResult.put("EmailAddress", (String) profile.get("EmailAddress"));
            //    loginResult.put("EmployeeNumber", (String) profile.get("EmployeeNumber"));
            //    loginResult.put("FirstName", (String) profile.get("FirstName"));
            loginResult.put("FloorCode", (String) profile.get("FloorCode"));
            loginResult.put("Floor_sk", (String) profile.get("Floor_sk"));
            //    loginResult.put("FullName", (String) profile.get("FullName"));
            loginResult.put("IsPrimaryLocation", (String) profile.get("IsPrimaryLocation"));
            loginResult.put("IsSupportPerson", (String) profile.get("IsSupportPerson"));
            //    loginResult.put("LastName", (String) profile.get("LastName"));
            //    loginResult.put("LoginID", (String) profile.get("LoginID"));
            loginResult.put("MasterAccountCodeName", (String) profile.get("MasterAccountCodeName"));
            loginResult.put("MasterAccountName", (String) profile.get("MasterAccountName"));
            //    loginResult.put("MiddleInitial", (String) profile.get("MiddleInitial"));
         /*   loginResult.put("ModifiedBy", (String) profile.get("ModifiedBy"));
            loginResult.put("ModifiedByFirstName", (String) profile.get("ModifiedByFirstName"));
            loginResult.put("ModifiedByLastName", (String) profile.get("ModifiedByLastName"));
            loginResult.put("ModifiedByLoginID", (String) profile.get("ModifiedByLoginID"));
            loginResult.put("ModifiedByNetworkLoginID", (String) profile.get("ModifiedByNetworkLoginID"));
            loginResult.put("ModifiedBy_sk", (String) profile.get("ModifiedBy_sk"));
            loginResult.put("ModifiedOn", (String) profile.get("ModifiedOn"));*/
            //    loginResult.put("NetworkLogin", (String) profile.get("NetworkLogin"));
            //   loginResult.put("PagerAlpha", (String) profile.get("PagerAlpha"));
            //    loginResult.put("PagerNumeric", (String) profile.get("PagerNumeric"));
            //    loginResult.put("PagerPin", (String) profile.get("PagerPin"));
            //   loginResult.put("People_Info_sk", (String) profile.get("People_Info_sk"));
            loginResult.put("PeoplesoftEmpid", (String) profile.get("PeoplesoftEmpid"));
            loginResult.put("PerEmailID", (String) profile.get("PerEmailID"));
          /*  loginResult.put("PhoneCell", (String) profile.get("PhoneCell"));
            loginResult.put("PhoneExt", (String) profile.get("PhoneExt"));
            loginResult.put("PhoneHome", (String) profile.get("PhoneHome"));
            loginResult.put("PhoneWork", (String) profile.get("PhoneWork"));*/
            loginResult.put("QueueCode", (String) profile.get("QueueCode"));
            loginResult.put("Queue_sk", (String) profile.get("Queue_sk"));
            loginResult.put("RefID3", (String) profile.get("RefID3"));
            loginResult.put("ReturnOn", (String) profile.get("ReturnOn"));
            loginResult.put("Role", (String) profile.get("Role"));
            loginResult.put("RoleDescription", (String) profile.get("RoleDescription"));
            loginResult.put("SSPPreferedContactMethod", (String) profile.get("SSPPreferedContactMethod"));
            loginResult.put("SuiteCode", (String) profile.get("SuiteCode"));
            loginResult.put("Suite_sk", (String) profile.get("Suite_sk"));
            loginResult.put("SupervisorLogin", (String) profile.get("SupervisorLogin"));
            loginResult.put("SupervisorName", (String) profile.get("SupervisorName"));
            loginResult.put("TitleCode", (String) profile.get("TitleCode"));
            loginResult.put("Type", (String) profile.get("Type"));
            loginResult.put("UserPositionUser_sk", (String) profile.get("UserPositionUser_sk"));
            loginResult.put("UserPosition_sk", (String) profile.get("UserPosition_sk"));
            loginResult.put("UserTypeCode", (String) profile.get("UserTypeCode"));
            loginResult.put("UserType_sk", (String) profile.get("UserType_sk"));
            loginResult.put("User_sk", (String) profile.get("User_sk"));
            loginResult.put("AccountShortDescription", (String) profile.get("AccountShortDescription"));

        });
        return loginResult;
    }


}


