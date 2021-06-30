package com.htc.remedy.base;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.google.gson.JsonObject;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.domain.LDAPEndPoints;
import com.htc.remedy.model.TempGroupInfo1;
import com.htc.remedy.repo.LDAPAccountsRepo;
import com.htc.remedy.repo.LDAPEndpointRepo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;


public class LDAPBase {


    public static DirContext getLdapContext(LDAPAccounts ldapAccounts) throws NamingException {

        DirContext ctx = null;

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "1000");
        env.put(Context.SECURITY_AUTHENTICATION, "Simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapAccounts.getUsername());
        env.put(Context.SECURITY_CREDENTIALS, ldapAccounts.getPassword());
        if (ldapAccounts.getPort() == 636) {
            env.put(Context.PROVIDER_URL, "ldaps://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        } else {
            env.put(Context.REFERRAL, "follow");
            env.put(Context.PROVIDER_URL, "ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        }
        ctx = new InitialDirContext(env);
        return ctx;
    }

    public static DirContext getDirContext(LDAPAccounts ldapAccounts) throws NamingException {
        DirContext ctx1 = null;

        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put("com.sun.jndi.ldap.connect.timeout", "1000");
        env.put(Context.SECURITY_AUTHENTICATION, "Simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapAccounts.getUsername());
        env.put(Context.SECURITY_CREDENTIALS, ldapAccounts.getPassword());
        if (ldapAccounts.getPort() == 636) {
            env.put(Context.PROVIDER_URL, "ldaps://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
            env.put(Context.REFERRAL, "follow");
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        } else {

            env.put(Context.PROVIDER_URL, "ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        }
        ctx1 = new InitialDirContext(env);

        return ctx1;
    }

    public static String sendemail(JdbcTemplate jdbcTemplate, ARServerUser arServerUser, String username, String client, String password) {
        int[] rf = new int[]{
                700001012, //email 3
        };
        try {
            String email = "";
            List<Entry> ent = RemedyBase.queryEntrysByQual(arServerUser, "CMN:People Information", rf, "'Client'=\"" + client + "\" AND ('Network Login' =\"" + username + "\" OR 'Login ID' = \"" + username + "\")");
            if (ent.size() > 0)
                email = ent.get(0).get(700001012).toString();

            String subject = "Password Reset";
            String content = "Your password has been reset to " + password;

            String proc = String.format("exec USP_CTSSP_SetMailSchedules @SendDate = '2019-01-25',@Sent = 0,@Enabled = 1,@RemainderCount = 1,@RemainderIndex = 1,@Delay = 0,@DelayType = 'NO',@ToEmail = '%1$s',@Subject = '%2$s' ,@MailBody = '%3$s' , @EmailType = 'Notification',@FromMail = NULL", email, subject, content);
            jdbcTemplate.execute(proc);

        } catch (Exception e) {
            return "success";
        }

        return "success";
    }

    public static JsonObject endpointStatus(LDAPEndpointRepo ldapEndpointRepo, LDAPEndPoints ldapEndPoints, boolean status) {
        ldapEndPoints.setActive(status);
        JsonObject jsonObject = new JsonObject();
        if (ldapEndpointRepo.save(ldapEndPoints) != null) {

            jsonObject.addProperty("success", status ? "Activated" : "Deactived");
        } else {
            jsonObject.addProperty("error", status ? "Activate " : "Deactive");
        }

        return jsonObject;
    }


    public static List<Map<String, Object>> runldapQuery(LDAPAccounts ldapAccounts, String base, String query, String rf) {
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
                            if (attribute1.get() != null || attribute1.getID() != null) {           //checking attribute value null and attribute null
                                eachobject.put(attribute1.getID(), attribute1.get().toString());

                                for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                                    if (stringBooleanEntry.getKey().equalsIgnoreCase(attribute1.getID())) {
                                        stringBooleanEntry.setValue(true);
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
        } catch (
                Exception e)

        {

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

    public static Set<TempGroupInfo1> ldapgroupsfromsamaccountname(LDAPAccounts ldapAccounts, String base, String query, String masterclient) {
        Set<TempGroupInfo1> ldapgroups = new HashSet<>();

        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(new String[]{"memberof"});

            String[] constraint = constraints.getReturningAttributes();

            DirContext ctx = getLdapContext(ldapAccounts);

            NamingEnumeration answer = ctx.search(base, query, constraints);

            while (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                NamingEnumeration<javax.naming.directory.Attribute> attribute = (NamingEnumeration<javax.naming.directory.Attribute>) attrs.getAll();
                javax.naming.directory.Attribute attribute1 = attribute.next();
                for (int i = 0; i < attribute1.size(); i++) {
                    String s = LdapUtils.getStringValue(new LdapName(attribute1.get(i).toString()), "cn");

                    List<String> groupnames = new ArrayList<String>();
                    groupnames.add(s);
                    String groupid = RemedyBase.Md5hashing(masterclient + "_" + s);
                    TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(s, groupid,
                            (groupid),
                            groupnames,
                            "AD",
                            groupid,
                            groupid, "AD");
                    ldapgroups.add(tempGroupInfo);
                }
            }

            ctx.close();
        } catch (Exception e) {

        }
        return ldapgroups;
    }


    public static Map<String, String> ldapgroupsfromsamaccountnamewithcommaseparated(LDAPAccounts ldapAccounts, String base, String query) {
        Map<String, String> response = new HashMap<>();

        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            constraints.setReturningAttributes(new String[]{"memberof"});
            String groups = "";

            String[] constraint = constraints.getReturningAttributes();

            DirContext ctx = getLdapContext(ldapAccounts);

            NamingEnumeration answer = ctx.search(base, query, constraints);

            while (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                NamingEnumeration<javax.naming.directory.Attribute> attribute = (NamingEnumeration<javax.naming.directory.Attribute>) attrs.getAll();
                javax.naming.directory.Attribute attribute1 = attribute.next();
                for (int i = 0; i < attribute1.size(); i++) {
                    String s = LdapUtils.getStringValue(new LdapName(attribute1.get(i).toString()), "cn");
                    if (i == attribute1.size() - 1) {
                        groups += s;
                    } else {
                        groups += s + ",";
                    }
                }
            }
            response.put("usergroups", groups);
            ctx.close();
        } catch (Exception e) {
            response = new HashMap<>();
            response.put("usergroups",e.getMessage());
        }
        return response;
    }


    public static List<Map<String, Object>> logindetailsrunldapQuerydirectory(LDAPAccounts ldapAccounts, String base, String query, String rf) {
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
        } catch (
                Exception e)

        {

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

    public static List<Map<String, Object>> logindetailsrunldapQuery(LDAPAccounts ldapAccounts, String base, String query, String rf) {
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
        } catch (
                Exception e)

        {

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

    public static List<Map<String, Object>> logindetailsrunldapdirQuery(LDAPAccounts ldapAccounts, String base, String query, String rf) {
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

            DirContext ctx = getDirContext(ldapAccounts);

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
        } catch (
                Exception e)

        {

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


    public static List<Map<String, Object>> runldapQuerywithsort(LDAPAccounts ldapAccounts, String base, String query, String rf, String sortfield, String distinct) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> distinctresult = new ArrayList<>();
        /* LDAPBaseContext ctx=null;*/
        try {
            DirContext ctx = getLdapContext(ldapAccounts);
            /*ctx= (LDAPBaseContext) getLdapContext(ldapAccounts);*/
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

            NamingEnumeration answer = ctx.search(base, query, constraints);
            Boolean attributefound = false;
            if (answer != null) {
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
                            if (attribute1.get() != null || attribute1.getID() != null) {           //checking attribute value null and attribute null
                                eachobject.put(attribute1.getID(), attribute1.get().toString());

                                for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                                    if (stringBooleanEntry.getKey().equalsIgnoreCase(attribute1.getID())) {
                                        stringBooleanEntry.setValue(true);
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


        } catch (Exception e) {
            if (e instanceof NamingException) {
            } else {
                Map<String, Object> errorresult = new HashMap<>();
                errorresult.put("error", e.getMessage());
                result.clear();
                result.add(errorresult);
            }
        } finally {
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
        }
        return result;
    }


    public static List<Map<String, Object>> runldapQuerywithsortauthorizingmanager(LDAPAccounts ldapAccounts, String base, String query, String rf, String sortfield, String distinct) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> result2 = new ArrayList<>();
        List<Map<String, Object>> distinctresult = new ArrayList<>();
        /* LDAPBaseContext ctx=null;*/
        try (LDAPBaseContext ctx = (LDAPBaseContext) getLdapContext(ldapAccounts);) {
            /*ctx= (LDAPBaseContext) getLdapContext(ldapAccounts);*/
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

            NamingEnumeration answer = ctx.search(base, query, constraints);
            Boolean attributefound = false;
            if (answer != null) {
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
                            if (attribute1.get() != null || attribute1.getID() != null) {           //checking attribute value null and attribute null
                                eachobject.put(attribute1.getID(), attribute1.get().toString());

                                for (Map.Entry<String, Boolean> stringBooleanEntry : returningattributes.entrySet()) {
                                    if (stringBooleanEntry.getKey().equalsIgnoreCase(attribute1.getID())) {
                                        stringBooleanEntry.setValue(true);
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

            for (Map<String, Object> stringObjectMap : result) {

                Map<String, Object> eachobject = new HashMap<>();
                eachobject.put("fullname", stringObjectMap.get("givenName") + " " + stringObjectMap.get("sn"));
                eachobject.put("networklogin", stringObjectMap.get("sAMAccountName"));
                result2.add(eachobject);
            }


        } catch (Exception e) {
            if (e instanceof NamingException) {
            } else {
                Map<String, Object> errorresult = new HashMap<>();
                errorresult.put("error", e.getMessage());
                result2.clear();
                result2.add(errorresult);
            }
        } finally {
            Collections.sort(result2, new Comparator<Map<String, Object>>() {
                public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
                    if (o1.get("fullname") instanceof String && o2.get("fullname") instanceof String)
                        return String.class.cast(o1.get("fullname")).compareTo(String.class.cast(o2.get("fullname")));
                    else
                        return 0;
                }
            });

            if (distinct.equalsIgnoreCase("true")) {
                result2.stream().forEach(stringStringMap -> {
                    List result1 = distinctresult.stream()
                            .filter(stringStringMap1 ->
                                    stringStringMap1.get("fullname").toString().equalsIgnoreCase(stringStringMap.get("fullname").toString())
                            )
                            .collect(Collectors.toList());
                    if (result1.isEmpty() || result1 == null) {
                        distinctresult.add(stringStringMap);
                    }
                });
                result2.clear();
                result2 = new ArrayList<>(distinctresult);
            }
        }
        return result2;
    }

    public static Map<String, Object> setConnectionTimeOut() {
        Map<String, Object> baseEnvironmentProperties = new HashMap<String, Object>();
        baseEnvironmentProperties.put("com.sun.jndi.ldap.read.timeout", "1000");
        baseEnvironmentProperties.put("com.sun.jndi.ldap.connect.timeout", "1000");
        return baseEnvironmentProperties;
    }

    public static LdapContextSource contextSource(LDAPAccounts ldapAccounts) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
        contextSource.setBase(ldapAccounts.getSearchDN());
        contextSource.setUserDn(ldapAccounts.getUsername());
        contextSource.setPassword(ldapAccounts.getPassword());
        contextSource.setBaseEnvironmentProperties(setConnectionTimeOut());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    public static Map<String, String> ldaplogin(String username, String password, Map<String, List<LdapTemplate>> ldapTemplateMap) {

        String identifyingAttribute = "samaccountname";
        Map<String, String> userdetails = new HashMap<>();

        for (Map.Entry<String, List<LdapTemplate>> listEntry : ldapTemplateMap.entrySet()) {


            for (LdapTemplate ldapTemplate : listEntry.getValue()) {

                try {
                    if (clientldaplogin(username, password, ldapTemplate)) {
                        userdetails.put("client", listEntry.getKey());
                        break;
                    }

                } catch (Exception e) {

                }
            }
        }

        return userdetails;
    }


    public static Boolean clientldaplogin(String username, String password, ARServerUser arServerUser, LDAPAccounts ldapAccounts) {

        String identifyingAttribute = "samaccountname";
        DirContext ctx = null;
        try {
            ctx = getLdapContext(ldapAccounts);

            // we don't need all attributes, just let it get the identifying one
            String[] attributeFilter = {identifyingAttribute};
            SearchControls sc = new SearchControls();
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            // use a search filter to find only the user we want to authenticate
            String searchFilter = "(" + identifyingAttribute + "=" + username + ")";
            NamingEnumeration<SearchResult> results = null;

            results = ctx.search(ldapAccounts.getBase(), searchFilter, sc);

            if (results.hasMore()) {
                // get the users DN (distinguishedName) from the result
                SearchResult result = results.next();
                String distinguishedName = result.getNameInNamespace();

                // attempt another authentication, now with the user
                Properties authEnv = new Properties();
                authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                authEnv.put(Context.PROVIDER_URL, "ldap://" + ldapAccounts.getHost() + ":" + ldapAccounts.getPort());
                authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
                authEnv.put(Context.SECURITY_CREDENTIALS, password);
                new InitialDirContext(authEnv);
                return true;
            }
        } catch (NamingException e) {
        }
        return false;
    }


    public static Map<String, String> splitUserName(String username) throws Exception {
        Map<String, String> splittedusername = new HashMap<>();
        String pretext = null;
        String posttext = null;
        String samaccountname = "";
        int splitlength = -1, pretextlenght = -1, posttextlength = -1;
        pretextlenght = username.indexOf("\\");
        posttextlength = username.lastIndexOf("@");
        if (pretextlenght > 0 && posttextlength > 0 && posttextlength > pretextlenght) {
            samaccountname = username.substring(pretextlenght + 1, posttextlength);
            pretext = username.substring(0, pretextlenght + 1);
            posttext = username.substring(posttextlength, username.length());
        } else if (posttextlength > 0) {
            posttext = username.substring(posttextlength, username.length());
            samaccountname = username.substring(0, posttextlength);
        } else if (pretextlenght > 0) {
            pretext = username.substring(0, pretextlenght + 1);
            samaccountname = username.substring(pretextlenght + 1, username.length());
        } else {
            samaccountname = username;
        }
        splittedusername.put("pretext", pretext);
        splittedusername.put("samaccountname", samaccountname);
        splittedusername.put("posttext", posttext);
        return splittedusername;
    }


    public static Boolean clientldaplogin(String username, String password, LdapTemplate ldapTemplate) {
        String identifyingAttribute = "samaccountname";
        try {
            if (ldapTemplate.authenticate("", "(&(!(userAccountControl:1.2.840.113556.1.4.803:=2))(|(" + identifyingAttribute + "=" + username + ")(userPrincipalName=" + username + ")))", password))
                return true;
        } catch (Exception ex) {
        }
        return false;
    }

    public static Map<String, String> getadgroupNamesforuserwithcommaseparated(LdapTemplate ldapTemplate, String username, String masterclient) {
        Map<String, String> response = new HashMap<>();
        List<String> distinguishedname = new ArrayList<>();
        String groups = "";
        try {
            distinguishedname = (List<String>) ldapTemplate.search(
                    query().where("sAMAccountName").is(username),
                    (AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("distinguishedname").getAll())
            ).get(0);
            List records = ldapTemplate.search(
                    query().where("member").is(distinguishedname.get(0)),
                    (AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("samaccountname").getAll())
            );
            distinguishedname.clear();
            for (Object o : records) {
                distinguishedname.add(((ArrayList) o).get(0).toString());
            }
        } catch (Exception e) {
            distinguishedname = new ArrayList<String>();
        }
        for (String s : distinguishedname) {
            groups += s + ",";
        }
        response.put("usergroups", groups);
        return response;
    }


    public static Set<TempGroupInfo1> getadgroupNamesforuser(LdapTemplate ldapTemplate, String username, String masterclient) {
        Set<TempGroupInfo1> ldapTempGroupInfo1s = new HashSet<>();
        List<String> distinguishedname = new ArrayList<>();
        try {
            distinguishedname = (List<String>) ldapTemplate.search(
                    query().where("sAMAccountName").is(username),
                    (AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("distinguishedname").getAll())
            ).get(0);
            List records = ldapTemplate.search(
                    query().where("member").is(distinguishedname.get(0)),
                    (AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("samaccountname").getAll())
            );
            distinguishedname.clear();
            for (Object o : records) {
                distinguishedname.add(((ArrayList) o).get(0).toString());
            }
        } catch (Exception e) {
            distinguishedname = new ArrayList<String>();
        }
        for (String s : distinguishedname) {
            String s1 = "";
            try {
                //   s1 = LdapUtils.getStringValue(new LdapName(s.toString()), "cn");
                List<String> groupnames = new ArrayList<String>();
                groupnames.add(s);
                String groupid = "";
                groupid = RemedyBase.Md5hashing(masterclient + "_" + s);
                TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(s, groupid,
                        (groupid),
                        groupnames,
                        "AD",
                        groupid,
                        groupid, "AD");
                ldapTempGroupInfo1s.add(tempGroupInfo);

            } catch (Exception e) {
                ldapTempGroupInfo1s = new HashSet<>();
            }
        }
        return ldapTempGroupInfo1s;
    }

    /* public static Set<TempGroupInfo1> getadgroupNamesforuser(LdapTemplate ldapTemplate, String username, String masterclient) {
         Set<TempGroupInfo1> ldapTempGroupInfo1s = new HashSet<>();
         List<String> distinguishedname = new ArrayList<>();
         try {
             distinguishedname = (List<String>) ldapTemplate.search(
                     query().where("sAMAccountName").is(username),
                     (AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("memberOf").getAll())
             ).get(0);
         }catch(Exception e){
             distinguishedname=new ArrayList<String>();
         }
         for (String s : distinguishedname) {
             String s1 = "";
             try {
                 s1 = LdapUtils.getStringValue(new LdapName(s.toString()), "cn");
                 List<String> groupnames = new ArrayList<String>();
                 groupnames.add(s1);
                 String groupid = "";
                 groupid = RemedyBase.Md5hashing(masterclient + "_" + s1);
                 TempGroupInfo1 tempGroupInfo = new TempGroupInfo1(s1, groupid,
                         (groupid),
                         groupnames,
                         "AD",
                         groupid,
                         groupid, "AD");
                 ldapTempGroupInfo1s.add(tempGroupInfo);

             } catch (Exception e) {
                 ldapTempGroupInfo1s=new HashSet<>();
             }
         }
         return ldapTempGroupInfo1s;
     }
 */
    public static Boolean BMTHlogin(String username, String password, LDAPAccountsRepo ldapAccountsRepo) {
        List<LDAPAccounts> ldapAccounts = ldapAccountsRepo.findByClientAndSsoIsTrue("BMTH");
        List<Map<String, Object>> result = new ArrayList<>();
        if (ldapAccounts != null && !ldapAccounts.isEmpty()) {
            String distinguishedname = "";
            result = LDAPBase.logindetailsrunldapQuery(ldapAccounts.get(0), ldapAccounts.get(0).getBase(), "samaccountname=" + username, "distinguishedname");
            if (result != null && !result.isEmpty()) {
                distinguishedname = result.get(0).get("distinguishedName").toString();
               // distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
                Hashtable<String, String> env2 = new Hashtable<String, String>();
                env2.put(Context.PROVIDER_URL, "ldaps://" + ldapAccounts.get(0).getHost() + ":636");
                env2.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env2.put(Context.SECURITY_AUTHENTICATION, "simple");
                env2.put(Context.SECURITY_PRINCIPAL, distinguishedname);
                env2.put(Context.SECURITY_CREDENTIALS, password);
                InitialContext ctx1 = null;
                try {
                    ctx1 = new InitialDirContext(env2);
                    ctx1.close();
                    return true;
                } catch (Exception e) {
                    //  return false;
                }
            }
        }
        return false;

    }

}
