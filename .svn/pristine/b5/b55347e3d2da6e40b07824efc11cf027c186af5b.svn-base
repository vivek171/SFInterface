package com.htc.remedy.oauth;


import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.htc.remedy.base.SFInterfaceConnectionBase;
import com.htc.remedy.base.SFInterfaceLoggerBase;
import com.htc.remedy.base.SFInterfaceMessages;
import com.htc.remedy.constants.SFInterfaceConstants;
import com.htc.remedy.db.ReferenceDBConnector;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.ldap.LDAPComponent;
import com.htc.remedy.model.User;
import com.htc.remedy.services.SFInterfaceServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.htc.remedy.constants.SFInterfaceConstants.*;

@Configuration
public class CustomAuthenticationManager implements AuthenticationManager {

    private static final Logger logger = LogManager.getLogger(CustomAuthenticationManager.class);

    @Value("${remedy.host}")
    String remedyHost;

    @Value("${remedy.port}")
    Integer remedyPort;

    @Value("${ad_auth_test_mode_enabled}")
    String testinmodeenabled;

    @Value("${check_ldap}")
    String check_ldap;

    @Autowired
    ReferenceDBConnector referenceDBConnector;

    @Autowired
    SFInterfaceMessages messages;


    @Autowired
    LDAPComponent ldapComponent;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String client = "",
                authType = "",
                adfscode = "",
                clientInstance = "",
                userName = "",
                password = "";

        if (Map.class.cast(authentication.getDetails()).get("client") != null) {
            client = Map.class.cast(authentication.getDetails()).get("client").toString();
        }

        if (Map.class.cast(authentication.getDetails()).get("auth_type") != null) {
            authType = Map.class.cast(authentication.getDetails()).get("auth_type").toString().toLowerCase();
        }
        if (Map.class.cast(authentication.getDetails()).get("code") != null) {
            adfscode = Map.class.cast(authentication.getDetails()).get("code").toString().toLowerCase();
        }

        if (Map.class.cast(authentication.getDetails()).get(CLIENT_INSTANCE) != null) {
            clientInstance = Map.class.cast(authentication.getDetails()).get(CLIENT_INSTANCE).toString().toLowerCase();
        } else {
            throw new BadCredentialsException(messages.get("AUTHENTICATION.ERROR.CLIENT_INSTANCE_NOTFOUND"));
        }

        userName = authentication.getName();
        password = authentication.getCredentials().toString();
        userName = userName.toUpperCase();

        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();

        boolean isADAuthenticated = false;
        boolean isRemedyAuthenticated = false;
        boolean isLocalAuthenticated = false;
        boolean isadfsAuthenticated = false;
        User inputRequest = new User();
        inputRequest.setClientInstance(clientInstance);

        String CustomUserName = "";
        try {
            if (client.isEmpty())
                client = "CTS";
            User indexerLoggedinUser = SFInterfaceServices.getUserLogin(userName, client, inputRequest,authType);
            User user;
            if (indexerLoggedinUser != null) {
                switch (authType) {
                    case "ldap": {
                        if (testinmodeenabled.equalsIgnoreCase("true")) {
                            user = referenceDBConnector.authenticateUserLocal(
                                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(inputRequest,
                                            SFInterfaceConstants.getDbRefConnectionName())
                                    , indexerLoggedinUser.getUserID(), password, "local");
                            if (user != null) {
                                isADAuthenticated = Boolean.TRUE;
                            } else if (check_ldap.equalsIgnoreCase("true")) {
                                List<LDAPAccounts> accounts = SFInterfaceServices.getLdapAccountsForClient(client,indexerLoggedinUser);
                                if (accounts != null) {
                                    String finalUserName = userName;
                                    String finalPassword = password;
                                    if (accounts.parallelStream().anyMatch(account -> ldapComponent.ldapBind(account, finalUserName, finalPassword))) {
                                        isADAuthenticated = Boolean.TRUE;
                                        referenceDBConnector.syncLdapPassword(
                                                SFInterfaceConnectionBase.fetchJdbcTemplatebyType(inputRequest,
                                                        SFInterfaceConstants.getDbRefConnectionName()),
                                                indexerLoggedinUser.getUserID(), password, indexerLoggedinUser.getClient());
                                    }
                                }
                            }
                        }    else {
                            user = referenceDBConnector.authenticateUserLocal(
                                    SFInterfaceConnectionBase.fetchJdbcTemplatebyType(inputRequest,
                                            SFInterfaceConstants.getDbRefConnectionName()),
                                    indexerLoggedinUser.getUserID(), password, "ad");
                            if (user != null) {
                                isADAuthenticated = Boolean.TRUE;
                            } else {
                                List<LDAPAccounts> accounts = SFInterfaceServices.getLdapAccountsForClient(client,indexerLoggedinUser);
                                if (accounts != null) {
                                    String finalUserName = userName;
                                    String finalPassword = password;
                                    if (accounts.parallelStream().anyMatch(account -> ldapComponent.ldapBind(account, finalUserName, finalPassword))) {
                                        isADAuthenticated = Boolean.TRUE;
                                        referenceDBConnector.syncLdapPassword(
                                                SFInterfaceConnectionBase.fetchJdbcTemplatebyType(inputRequest,
                                                        SFInterfaceConstants.getDbRefConnectionName()),
                                                indexerLoggedinUser.getUserID(), password, indexerLoggedinUser.getClient());
                                    }
                                }
                            }

                        }
                    }
                    break;
                    case "local": {
                        user = referenceDBConnector.authenticateUserLocal(
                                SFInterfaceConnectionBase.fetchJdbcTemplatebyType(inputRequest,
                                        SFInterfaceConstants.getDbRefConnectionName()),
                                indexerLoggedinUser.getUserID(), password, "local");
                        if (user != null)
                            isLocalAuthenticated = Boolean.TRUE;
                    }
                    break;
                    case "remedy": {
                        ARServerUser arServerUser = new ARServerUser();
                        arServerUser.setPort(remedyPort);
                        arServerUser.setServer(remedyHost);
                        arServerUser.setPassword(password);
                        arServerUser.setUser(userName);
                        try {
                            arServerUser.login();
                            isRemedyAuthenticated = true;
                        } catch (ARException e) {
                            SFInterfaceLoggerBase.exception(logger, AUTH_TOKEN, e);
                        }
                    }
                    break;
                    case "adfs": {
                        if (adfscode.equalsIgnoreCase("adfs")) {
                            isadfsAuthenticated = true;
                            indexerLoggedinUser.setAuthTokenType("ldap");
                        }
                    }
                    break;
                }

                CustomUserName = userName + "~" + client + "~" + indexerLoggedinUser.getUserID() + "~" + indexerLoggedinUser.getClientInstance()+ "~" + indexerLoggedinUser.getAuthTokenType();
                if (isLocalAuthenticated || isRemedyAuthenticated || isADAuthenticated || isadfsAuthenticated) {
                    if (indexerLoggedinUser == null) {
                        SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.STATUS.LOGIN") + EMPTY + ERROR + EMPTY + ACCOUNT + COLON + client + EMPTY + USERS + COLON + userName + EMPTY + messages.get("AUTHENTICATION.SUCCESS.PROFILE.NOTFOUND"));
                        throw new BadCredentialsException(messages.get("AUTHENTICATION.SUCCESS.PROFILE.NOTFOUND"));
                    } else {
                        SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.STATUS.LOGIN") + EMPTY + SUCCESS + EMPTY + ACCOUNT + COLON + client + EMPTY + USERS + COLON + userName);
                    }
                } else {
                    SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.STATUS.LOGIN") + EMPTY + ERROR + EMPTY + ACCOUNT + COLON + client + EMPTY + USERS + COLON + userName + EMPTY + messages.get("AUTHENTICATION.ERROR.LOGINFAILED"));
                    throw new BadCredentialsException(messages.get("AUTHENTICATION.ERROR.LOGINFAILED"));
                }
            } else {
                SFInterfaceLoggerBase.log(logger, messages.get("AUTHENTICATION.STATUS.LOGIN") + EMPTY + ERROR + EMPTY + ACCOUNT + COLON + client + EMPTY + USERS + COLON + userName + EMPTY + messages.get("AUTHENTICATION.ERROR.PROFILE.NOTFOUND"));
                throw new BadCredentialsException(messages.get("AUTHENTICATION.ERROR.LOGINFAILED"));
            }
        } catch (
                Exception e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return new
                UsernamePasswordAuthenticationToken(
                CustomUserName,
                password,
                grantedAuths
        );
    }

}
