package com.htc.remedy.ldap;
import com.htc.remedy.domain.LDAPAccounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LDAPComponent {

    private static final Logger logger = LoggerFactory.getLogger(LDAPComponent.class);


    public LdapContextSource getLdapContextSource(LDAPAccounts ldapAccounts) {
        LdapContextSource contextSource = new LdapContextSource();

        if (ldapAccounts.getPort().equals("636")) {
            contextSource.setUrl("ldaps://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        } else {
            contextSource.setUrl("ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        }
        contextSource.setBase(ldapAccounts.getBase());
        contextSource.setUserDn(ldapAccounts.getUsername());
        contextSource.setPassword(ldapAccounts.getPassword());
        contextSource.afterPropertiesSet();


        return contextSource;
    }


    public DirContext getLdapContext(LDAPAccounts ldapAccounts) throws NamingException {

        DirContext ctx = null;

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "1000");
        env.put(Context.SECURITY_AUTHENTICATION, "Simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapAccounts.getUsername());
        env.put(Context.SECURITY_CREDENTIALS, ldapAccounts.getPassword());
        if (ldapAccounts.getPort().equals("636")) {
            env.put(Context.PROVIDER_URL, "ldaps://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        } else {
            env.put(Context.REFERRAL, "follow");
            env.put(Context.PROVIDER_URL, "ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        }
        ctx = new InitialDirContext(env);
        return ctx;
    }

    public LdapTemplate getLdapTemplate(LDAPAccounts ldapAccounts) throws Exception {
        LdapTemplate ldapTemplate = new LdapTemplate(getLdapContextSource(ldapAccounts));
        ldapTemplate.afterPropertiesSet();
        return ldapTemplate;
    }

    public Boolean authenticate(LDAPAccounts ldapAccounts, String userName, String password) {
        String identifyingAttribute = "samaccountname";
        try {
            LdapTemplate ldapTemplate = getLdapTemplate(ldapAccounts);
            if (ldapTemplate.authenticate("", "(&(!(userAccountControl:1.2.840.113556.1.4.803:=2))(|(" + identifyingAttribute + "=" + userName + ")(userPrincipalName=" + userName + ")))", password))
                return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public boolean ldapBind(LDAPAccounts ldapAccounts, String userName, String password) {
        try {

            String preUser = "";
            String postUser = "";
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

            if (ldapAccounts.getPort().equals("636")) {
                env.put(Context.PROVIDER_URL, "ldaps://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
            } else {
                env.put(Context.PROVIDER_URL, "ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
            }

            preUser = ldapAccounts.getPretext() == null ? "" : ldapAccounts.getPretext();
            postUser = ldapAccounts.getPosttext() == null ? "" : ldapAccounts.getPosttext();
            String user = userName;

            if (preUser.length() > 0 && user.length() > preUser.length()) {
                if (user.substring(0, preUser.length()).equalsIgnoreCase(preUser)) {
                    user = user.substring(preUser.length());
                }
            }
            if (postUser.length() > 0 && user.length() > postUser.length()) {
                if (user.substring(user.length() - postUser.length()).equalsIgnoreCase(postUser)) {
                    user = user.substring(0, user.length() - postUser.length());
                }
            }

            env.put(Context.REFERRAL, "ignore");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, preUser + user + postUser);
            env.put(Context.SECURITY_CREDENTIALS, password);
            DirContext ctx = new InitialDirContext(env);
            ctx.addToEnvironment(ctx.REFERRAL, "ignore");

            logger.info(String.format("User %1$s is Authenticated", preUser + user + postUser));
            return true;

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }


    public List<Map<String, Object>> loginDetailLdapQuery(LDAPAccounts ldapAccounts, String base, String query, String rf) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

            if (!rf.equalsIgnoreCase("*")) {
                String[] reqF = rf.split(",");
                List<String> stringList = Arrays.stream(reqF).filter(s -> !s.isEmpty()).collect(Collectors.toList());


                if (stringList.size() > 0) {
                    constraints.setReturningAttributes(stringList.toArray(new String[0]));
                }
            }
            String[] constraint = constraints.getReturningAttributes();
            Map<String, Boolean> returningattributes = new HashMap<>();

            DirContext ctx = getLdapContext(ldapAccounts);

            NamingEnumeration answer = ctx.search(base, query, constraints);
            Boolean attributefound = false;
            if (answer != null) {                       //checking search result null or not
                while (answer.hasMore()) {
                    if (answer != null) {
                        if (constraint != null) {
                            for (String s : constraint) {
                                returningattributes.put(s, false);
                            }
                        }
                        Map<String, Object> eachobject = new HashMap<>();
                        Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                        NamingEnumeration<javax.naming.directory.Attribute> attribute = (NamingEnumeration<javax.naming.directory.Attribute>) attrs.getAll();

                        while (attribute.hasMore()) {
                            javax.naming.directory.Attribute attribute1 = attribute.next();


                            if (attribute1.get() != null || attribute1.getID() != null) {//checking attribute value null and attribute null

                                if (attribute1.size() <= 1) {
                                    eachobject.put(attribute1.getID(), attribute1.get().toString());
                                    for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                                        if (stringBooleanEntry.getKey().equalsIgnoreCase(attribute1.getID())) {
                                            stringBooleanEntry.setValue(true);
                                        }
                                    }

                                } else {
                                    List<String> ldapgroups = new ArrayList<>();
                                    for (int i = 0; i < attribute1.size(); i++) {
                                        ldapgroups.add(attribute1.get(i).toString());
                                    }
                                    eachobject.put(attribute1.getID(), ldapgroups);
                                    for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                                        if (stringBooleanEntry.getKey().equalsIgnoreCase(attribute1.getID())) {
                                            stringBooleanEntry.setValue(true);
                                        }
                                    }

                                }
                            }
                        }
                        for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                            if (!stringBooleanEntry.getValue()) {
                                eachobject.put(stringBooleanEntry.getKey(), "");
                            }
                        }
                        result.add(eachobject);
                    }
                }
            }
            ctx.close();
        } catch (Exception e) {

            if (e instanceof NamingException) {

            } else {
                Map<String, Object> errorresult = new HashMap<>();
                errorresult.put("error", e.getMessage());
                result.clear();
                result.add(errorresult);
            }
        }


        return result;
    }

    public String userGroupHashing(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
