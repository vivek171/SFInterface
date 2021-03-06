package com.htc.remedy.controller;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.google.gson.JsonObject;
import com.htc.remedy.base.LDAPBase;
import com.htc.remedy.base.RemedyBase;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.base.SFInterfaceConnectionBase;
import com.htc.remedy.domain.LDAPAccounts;
import com.htc.remedy.domain.LDAPEndPoints;
import com.htc.remedy.model.User;
import com.htc.remedy.repo.LDAPAccountsRepo;
import com.htc.remedy.repo.LDAPEndpointRepo;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin

@RequestMapping(value = "/ldap")
public class LDAPController {


    @Value("${spring.datasource.url}")
    String datasource;

    @Value("${spring.datasource.username}")
    String databaseusername;

    @Value("${spring.datasource.password}")
    String databasepassword;

    @Value("${distributionlist.endpointname}")
    String distributionendpointname;


    @Value("${ctsspi.username}")
    String username;

    @Value("${sendmail.password}")
    String sendmailpassword;

    @Value("${ctsspi.password}")
    String password;

    @Value("${remedy.host}")
    String serverName;

    @Value("${remedy.port}")
    Integer port;

   /* private final
    Map<String, List<LdapTemplate>> ldapTemplateMap;*/

    private final
    LDAPAccountsRepo ldapAccountsRepo;


    private final
    LDAPEndpointRepo ldapEndpointRepo;

    private final
    ARServerUser arServerUser3;


    @Autowired
    public LDAPController(/*Map<String, List<LdapTemplate>> ldapTemplateMap, */LDAPAccountsRepo ldapAccountsRepo, LDAPEndpointRepo ldapEndpointRepo, ARServerUser arServerUser3) {
        /* this.ldapTemplateMap = ldapTemplateMap;*/
        this.ldapAccountsRepo = ldapAccountsRepo;
        this.ldapEndpointRepo = ldapEndpointRepo;
        this.arServerUser3 = arServerUser3;
    }


    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String createQuery(Model model, HttpServletRequest request) {
        model.addAttribute("accounts", ldapAccountsRepo.findAll());
        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_query", request);
    }


    @RequestMapping(value = "/editep/{eid}", method = RequestMethod.GET)
    public String editQuery(Model model, @PathVariable("eid") Long eid, HttpServletRequest request) {
        model.addAttribute("accounts", ldapAccountsRepo.findAll());
        model.addAttribute("endpoint", ldapEndpointRepo.findOne(eid));

        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_edit_query", request);
    }

    @RequestMapping(value = "/deactiveep/{eid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deactiveEndPoint(@PathVariable("eid") Long eid, HttpServletRequest request) {
        JsonObject jsonObject = LDAPBase.endpointStatus(
                ldapEndpointRepo,
                ldapEndpointRepo.findOne(eid),
                false
        );

        return SFInterfaceBase.validateAndreturnPage("redirect:/ldap/displayep", request);
    }

    @RequestMapping(value = "/activeep/{eid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String activeEndPoint(@PathVariable("eid") Long eid, HttpServletRequest request) {
        JsonObject jsonObject = LDAPBase.endpointStatus(
                ldapEndpointRepo,
                ldapEndpointRepo.findOne(eid),
                true
        );

        return SFInterfaceBase.validateAndreturnPage("redirect:/ldap/displayep", request);
    }


    @ResponseBody
    @RequestMapping(value = "/run", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List run(
            @RequestParam("connName") String connName,
            @RequestParam("base") String base,
            @RequestParam("query") String query,
            @RequestParam("rf") String rf
    ) {
        LDAPAccounts ldapAccounts = ldapAccountsRepo.findByConnName(connName);
        return LDAPBase.runldapQuery(ldapAccounts, base, query, rf);

    }

    @RequestMapping(value = "/displayep", method = RequestMethod.GET)
    public String displayEndPoints(Model model, HttpServletRequest request) {
        /*model.addAttribute("endpoints", ldapEndpointRepo.findAll());*/

        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_query_display", request);
    }

    @RequestMapping(path = "/editldapaccountdetails/{q}", method = RequestMethod.GET)
    public String editendpoint(Model model,
                               @PathVariable(name = "q", required = true) String endpointName, HttpServletRequest request) {

        List<String> clients = new ArrayList<>();
        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            List<Entry> entries = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    "CMN:ClientInfo",
                    new int[]{536870913},
                    null
            );

            for (Entry entry : entries) {
                if (entry != null) {
                    clients.add(entry.get(536870913).toString());
                }
            }
            LDAPAccounts ldapAccounts = ldapAccountsRepo.findByConnName(endpointName);
            model.addAttribute("ldapaccount", ldapAccounts);

            model.addAttribute("clients", clients);


        } catch (Exception e) {
            clients.add(e.getLocalizedMessage());
        }


        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_edit_account", request);
    }


    @ResponseBody
    @RequestMapping(path = "/ldapendpoints", method = RequestMethod.GET)
    public Map<String, Object> ldapendpoints(
            @RequestParam(value = "draw", defaultValue = "1") int draw,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "length") int length,
            @RequestParam(value = "search[value]", required = false, defaultValue = "") String searchvalue,
            @RequestParam(value = "order[0][column]", defaultValue = "2") String sortcolumn,
            @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sorttype
    ) {
        Map<String, Object> Repo = new HashMap<>();
        try {
            int filteredcount = 0;
            int pageindex = 0;
            if (length != 0) {
                pageindex = start / length;
            }
            List<LDAPEndPoints> endPointDomains = new ArrayList<>();
            Pageable pageable = new PageRequest(pageindex, length);
            if (sortcolumn.equalsIgnoreCase("4")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByLdapAccounts_ConnNameAsc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByLdapAccounts_ConnNameAsc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByLdapAccounts_ConnNameDesc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByLdapAccounts_ConnNameDesc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("2")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByBaseAsc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByBaseAsc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByBaseDesc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByBaseDesc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                }

            } else {
                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByEndPointNameAsc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByEndPointNameAsc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByEndPointNameDesc(pageable, searchvalue, searchvalue));
                        filteredcount = ldapEndpointRepo.countByEndPointNameContainingOrLdapAccounts_ConnNameContaining(searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapEndpointRepo.findAllByOrderByEndPointNameDesc(pageable));
                        filteredcount = (int) ldapEndpointRepo.count();
                    }
                }
            }
            Repo.put("draw", draw);
            Repo.put("recordsTotal", ldapEndpointRepo.count());
            Repo.put("recordsFiltered", filteredcount);
            Repo.put("data", endPointDomains);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Repo;
    }


    @ResponseBody
    @RequestMapping(path = "/ldapaccounts", method = RequestMethod.GET)
    public Map<String, Object> ldapaccounts(
            @RequestParam(value = "draw", defaultValue = "1") int draw,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "length") int length,
            @RequestParam(value = "search[value]", required = false, defaultValue = "") String searchvalue,
            @RequestParam(value = "order[0][column]", defaultValue = "2") String sortcolumn,
            @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sorttype
    ) {
        Map<String, Object> Repo = new HashMap<>();
        try {
            int filteredcount = 0;
            int pageindex = 0;
            if (length != 0) {
                pageindex = start / length;
            }
            List<LDAPAccounts> endPointDomains = new ArrayList<>();
            Pageable pageable = new PageRequest(pageindex, length);
            if (sortcolumn.equalsIgnoreCase("4")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByDomainAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByDomainAsc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByDomainDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByDomainDesc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("2")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByHostAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByHostAsc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByHostDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByHostDesc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("1")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByClientAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByClientAsc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByClientDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByClientDesc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                }

            } else {
                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByConnNameAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByConnNameAsc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(ldapAccountsRepo.findByConnNameContainingOrClientContainingOrHostContainingOrDomainOrderByConnNameDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = ldapAccountsRepo.countByConnNameContainingOrClientContainingOrHostContainingOrDomain(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(ldapAccountsRepo.findAllByOrderByConnNameDesc(pageable));
                        filteredcount = (int) ldapAccountsRepo.count();
                    }
                }
            }
            Repo.put("draw", draw);
            Repo.put("recordsTotal", ldapAccountsRepo.count());
            Repo.put("recordsFiltered", filteredcount);
            Repo.put("data", endPointDomains);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Repo;
    }


/*    @ResponseBody
    @RequestMapping(value = "/groupsofuser", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> usergroupsfromsamaccountname(@RequestParam("networklogin") String samaccountname,
                                                            @RequestParam("client") String client) {
        List<LdapTemplate> ldapTemplates = ldapTemplateMap.get(client);
        return LDAPBase.getadgroupNamesforuserwithcommaseparated(ldapTemplates.get(0), samaccountname, client);

    }*/


    @ResponseBody
    @RequestMapping(value = "/createep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createLDAPEndpoint(
            @RequestParam("ename") String ename,
            @RequestParam String connName,
            @RequestParam String base,
            @RequestParam String query,
            @RequestParam String rf,
            @RequestParam(required = false, defaultValue = "0") String op
    ) {
        JsonObject jsonObject = new JsonObject();
        try {
            LDAPAccounts ldapAccounts = ldapAccountsRepo.findByConnName((connName));
            LDAPEndPoints ldapEndPoints = null;


            ldapEndPoints = new LDAPEndPoints();

            ldapEndPoints = ldapEndpointRepo.findByEndPointName(ename.trim());
            if (ldapEndPoints == null) {
                ldapEndPoints = new LDAPEndPoints();
            }

            if (ldapAccounts != null) {
                ldapEndPoints.setBase(base);
                ldapEndPoints.setEndPointName(ename.trim().toLowerCase());
                ldapEndPoints.setLdapAccounts(ldapAccounts);
                ldapEndPoints.setRequiredFields(rf);
                ldapEndPoints.setQuery(query);

                if (ldapEndpointRepo.save(ldapEndPoints) != null) {
                    jsonObject.addProperty("success", ename + " Created Successfully");
                } else {
                    jsonObject.addProperty("error", ename + " Endpoint creation failed");
                }
            } else {
                jsonObject.addProperty("error", ename + " Endpoint creation failed – invalid ldap account connection name:" + connName);
            }
        } catch (Exception e) {
            jsonObject.addProperty("error", ename + " Endpoint creation failed – duplicate ldap account/endpoint present. Ldap Account connname: " + connName);
        }
        return jsonObject.toString();
    }


    @ResponseBody
    @RequestMapping(path = "/resetpassword", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, Object>> resetpassword(
            @RequestParam String username,
            @RequestParam String client,
            @RequestParam String newpassword) {
        DirContext ctx = null;
        List<Map<String, Object>> result = new ArrayList<>();

        List<LDAPAccounts> ldapAccountswithssl = ldapAccountsRepo.findByClientAndPort(client, 636);
        try {
            ctx = LDAPBase.getDirContext(ldapAccountswithssl.get(0));
            ModificationItem[] mods = new ModificationItem[1];
            String newQuotedPassword = "\"" + newpassword + "\"";
            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                    new BasicAttribute("unicodePwd", newUnicodePassword));
            String distinguishedname = "";

            List<LDAPAccounts> ldapaccountswithoutssl = ldapAccountsRepo.findByClientAndPortAndSsoIsTrue(client, 389);
            result = LDAPBase.logindetailsrunldapQuery(ldapaccountswithoutssl.get(0), ldapaccountswithoutssl.get(0).getBase(), "samaccountname=" + username, "distinguishedname");

            if (result != null && !result.isEmpty()) {
                distinguishedname = result.get(0).get("distinguishedName").toString();
                distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
            } else {
                result = new ArrayList<Map<String, Object>>();
                Map<String, Object> error = new HashMap<>();
                result.clear();
                error.put("error", username + ":user not found");
                result.add(error);
            }
            ctx.modifyAttributes(distinguishedname, mods);

            mods = new ModificationItem[2];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountcontrol", "512"));
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("lockouttime", "0"));
            ctx.modifyAttributes(distinguishedname, mods);

            System.out.println("Changed Password for successfully");

            //        LDAPBase.sendemail(jdbc(), arServerUser3, username, "CTS", newpassword);
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("success", newpassword);
            result.add(error);
            ctx.close();
        } catch (Exception e) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("error", e);
            result.add(error);
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(path = "/unlockaccount", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, Object>> unlockaccount(
            @RequestParam String username,
            @RequestParam String client) {
        DirContext ctx = null;
        List<Map<String, Object>> result = new ArrayList<>();

        List<LDAPAccounts> ldapAccountswithssl = ldapAccountsRepo.findByClientAndPort(client, 636);

        try {
            ctx = LDAPBase.getDirContext(ldapAccountswithssl.get(0));
            String distinguishedname = "";

            List<LDAPAccounts> ldapaccountswithoutssl = ldapAccountsRepo.findByClientAndPortAndSsoIsTrue(client, 389);
            result = LDAPBase.logindetailsrunldapQuery(ldapaccountswithoutssl.get(0), ldapaccountswithoutssl.get(0).getBase(), "samaccountname=" + username, "distinguishedname");

            if (result != null && !result.isEmpty()) {
                distinguishedname = result.get(0).get("distinguishedName").toString();
                distinguishedname = distinguishedname.replaceAll("\\\\", "\\\\\\\\");
            } else {
                result = new ArrayList<Map<String, Object>>();
                Map<String, Object> error = new HashMap<>();
                result.clear();
                error.put("error", username + ":user not found");
                result.add(error);
            }
            ModificationItem[] mods = new ModificationItem[2];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountcontrol", "512"));
            mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("lockouttime", "0"));
            ctx.modifyAttributes(distinguishedname, mods);

            System.out.println("Changed Password for successfully");

            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("success", username + ":user unlocked");
            result.add(error);
            ctx.close();
        } catch (Exception e) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("error", e);
            result.add(error);
        }
        return result;

    }

    @ResponseBody
    @RequestMapping(path = "/sendmail", method = {RequestMethod.GET, RequestMethod.POST})
    public List<Map<String, Object>> sendmail(
            @RequestParam("user") String username,
            @RequestParam(value = "password", required = false) String newpassword) {

        List<Map<String, Object>> result = new ArrayList<>();

        try {
            newpassword = sendmailpassword;

            //   LDAPBase.sendemail(jdbc(), arServerUser3, username, "CTS", newpassword);
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("success", newpassword);
            result.add(error);

        } catch (Exception e) {
            result = new ArrayList<Map<String, Object>>();
            Map<String, Object> error = new HashMap<>();
            result.clear();
            error.put("error", e);
            result.add(error);
        }
        return result;

    }


    @ResponseBody
    @RequestMapping(value = "/getconnection", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LDAPAccounts getConnection(@RequestParam("connName") String connName) {
        JsonObject jsonObject = new JsonObject();
        return ldapAccountsRepo.findByConnName(connName);
    }

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String create(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String domain,
            @RequestParam String client,
            @RequestParam String desc,
            @RequestParam String host,
            @RequestParam Integer port,
            @RequestParam String base,
            @RequestParam String filter,
            @RequestParam String pretext,
            @RequestParam String posttext,
            @RequestParam String searchDN,
            @RequestParam String connName,
            @RequestParam(value = "sso", defaultValue = "false") boolean sso

    ) {
        JsonObject response = new JsonObject();
        LDAPAccounts ldapAccounts = new LDAPAccounts();
        LdapConnectionConfig ldapConnectionConfig = new LdapConnectionConfig();
        ldapConnectionConfig.setCredentials(password);
        ldapConnectionConfig.setLdapPort(port);
        ldapConnectionConfig.setLdapHost(host);
        ldapConnectionConfig.setName(username);
        if (port == 636) {
            ldapConnectionConfig.setUseSsl(true);
        }
        LdapConnection ldapConnection = new LdapNetworkConnection(ldapConnectionConfig);
        try {

            ldapAccounts = ldapAccountsRepo.findByConnName(connName.trim().toLowerCase());
            if (ldapAccounts == null) {
                ldapAccounts = new LDAPAccounts();
            }
            ldapAccounts.setBase(base);
            ldapAccounts.setClient(client);
            ldapAccounts.setConnName(connName.trim().toLowerCase());
            ldapAccounts.setDesc(desc);
            ldapAccounts.setDomain(domain);
            ldapAccounts.setUsername(username);
            ldapAccounts.setSearchDN(searchDN);
            ldapAccounts.setPort(port);
            ldapAccounts.setPassword(password);
            ldapAccounts.setHost(host);
            if (!pretext.isEmpty()) {
                ldapAccounts.setPretext(pretext);
            } else {
                ldapAccounts.setPretext(null);
            }
            if (!posttext.isEmpty()) {
                ldapAccounts.setPosttext(posttext);
            } else {
                ldapAccounts.setPosttext(null);
            }
            ldapAccounts.setFilter(filter);
            ldapAccounts.setSso(sso);
            ldapConnection.connect();           //connecting ldap
            ldapConnection.bind();              //check ldap connection bind
            LDAPBase.getLdapContext(ldapAccounts);  //ldap account with login successful
            ldapAccountsRepo.save(ldapAccounts);
            response.addProperty("success", connName + " Connected");

        } catch (Exception e) {
            if (ldapAccounts.getId() != null) {
                ldapAccountsRepo.save(ldapAccounts);
            }
            response.addProperty("error", e.getMessage() + " " + connName + " account not created");
        }


        return response.toString();
    }


    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model, HttpServletRequest request) {
        List<String> clients = new ArrayList<>();

        try {
            ARServerUser arServerUser = RemedyBase.loginUser(
                    serverName,
                    port,
                    username,
                    password
            );

            List<Entry> entries = RemedyBase.queryEntrysByQual(
                    arServerUser,
                    "CMN:ClientInfo",
                    new int[]{536870913},
                    null
            );

            for (Entry entry : entries) {
                if (entry != null) {
                    clients.add(entry.get(536870913).toString());
                }
            }


            model.addAttribute("clients", clients);


        } catch (Exception e) {
            clients.add(e.getLocalizedMessage());
        }

        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_create_account", request);
    }


    @RequestMapping(value = "/connections", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("ldapaccount", ldapAccountsRepo.findAll());
        return SFInterfaceBase.validateAndreturnPage("ldap/ldap_accounts", request);
    }

    @RequestMapping("/query")
    public String queryLDAP(
            @RequestHeader("atoken") String token,
            @RequestParam("modelName") String modelName,
            @RequestParam("client") String client,
            @RequestParam("qualification") String qualification) {


        return "";
    }

    @ResponseBody
    @RequestMapping(path = "/distributionlist", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> distributionlist(
            HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Map<String, Object>> finalresult = new ArrayList<>();
        try {
            LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue(distributionendpointname);
            if (ldapEndPoints == null) {

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
                finalresult.addAll(result);

                for (Map<String, Object> stringObjectMap : result) {
                    rValue = "member=" + stringObjectMap.get("distinguishedName").toString();
                    result = LDAPBase.runldapQuery(
                            ldapEndPoints.getLdapAccounts(),
                            ldapEndPoints.getBase(),
                            rValue,
                            ldapEndPoints.getRequiredFields()
                    );
                    finalresult.addAll(result);
                }
            }
        } catch (Exception e) {
            Map<String, Object> errorobject = new HashMap<>();
            errorobject.put("error", e.getMessage());
            finalresult.add(errorobject);
        }
        return finalresult;
    }

    @ResponseBody
    @RequestMapping(path = "/{epname}", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> ldapQuery1(@PathVariable("epname") String endpointName,
                                                @RequestParam(value = "sortfield", required = false) String sortfield,
                                                @RequestParam(value = "distinct", required = false, defaultValue = "false") String distinct,
                                                @RequestParam(value = "message", required = false, defaultValue = "") String message,
                                                HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            User user = SFInterfaceConnectionBase.fetchRequestInfo(request);
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


}
