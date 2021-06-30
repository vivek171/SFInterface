package com.htc.remedy.controller;


import com.bmc.arsys.api.ARException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htc.remedy.base.JDBCBase;
import com.htc.remedy.base.SFInterfaceBase;
import com.htc.remedy.domain.JDBCAccounts;
import com.htc.remedy.domain.JDBCEndpoints;
import com.htc.remedy.repo.JDBCAccountsRepo;
import com.htc.remedy.repo.JDBCEndpointRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin

@RequestMapping(path = "/jdbc")
public class JDBCController {

    @Value("${ctsspi.hash}")
    String atoken1;

    private final JDBCEndpointRepo jdbcEndpointRepo;
    private final JDBCAccountsRepo jdbcAccountsRepo;
    private final Map<String, JdbcTemplate> jdbcAccTemplate;

    @Autowired
    public JDBCController(JDBCEndpointRepo jdbcEndpointRepo, JDBCAccountsRepo jdbcAccountsRepo, @Qualifier("jdbcAccTemplate") Map<String, JdbcTemplate> jdbcAccTemplate) {
        this.jdbcEndpointRepo = jdbcEndpointRepo;
        this.jdbcAccountsRepo = jdbcAccountsRepo;
        this.jdbcAccTemplate = jdbcAccTemplate;
    }


    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String createQuery(Model model, HttpServletRequest request) {
        model.addAttribute("accounts", jdbcAccountsRepo.findAll());
        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_query", request);
    }

    @RequestMapping(value = "/editep/{eid}", method = RequestMethod.GET)
    public String editQuery(Model model, @PathVariable("eid") Long eid, HttpServletRequest request) {
        model.addAttribute("accounts", jdbcAccountsRepo.findAll());
        model.addAttribute("endpoint", jdbcEndpointRepo.findOne(eid));

        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_edit_query", request);
    }


    @RequestMapping(value = "/runquery/{endpointname}", method = {RequestMethod.GET, RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, ? extends Object>> runQueryForEndPoint(
            @PathVariable("endpointname") String endpointname,
            @RequestBody Map<String, Object> updateParam,
            @RequestHeader(value = "atoken", required = false) String atoken,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> jsonObjects = new ArrayList<>();
        JDBCEndpoints jdbcEndpoints = jdbcEndpointRepo.findByEndpointNameAndActiveIsTrue(endpointname);
        try {
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
            } else {
                throw new Exception("Authorization error");
            }
            if (jdbcEndpoints != null) {
                Map<String, String[]> params = request.getParameterMap();

                String rValue = jdbcEndpoints.getQuery();
                for (Map.Entry<String, Object> stringObjectEntry : updateParam.entrySet()) {
                    if (stringObjectEntry.getKey() != null) {
                        rValue = rValue.replace("{{" + stringObjectEntry.getKey() + "}}", "\'" + stringObjectEntry.getValue() + "\'");
                    }
                }

                for (String s : params.keySet()) {
                    rValue = rValue.replace("{{" + s + "}}", "\'" + params.get(s)[0] + "\'");
                }
                String querystatus = jdbcEndpoints.getInup();
                if (querystatus != null && querystatus.equalsIgnoreCase("insert")) {
                    return JDBCBase.insertupdatejdbcQuery(jdbcAccTemplate.get(jdbcEndpoints.getJdbcAccounts().getEnvironment()), rValue);
                } else {
                    return JDBCBase.runjdbcQuery(jdbcAccTemplate.get(jdbcEndpoints.getJdbcAccounts().getEnvironment()), rValue);
                }
            } else {
                Map<String, Object> jsonObject = new HashMap<>();
                jsonObject.put("error", "Endpoint not found");
                jsonObjects.add(jsonObject);
            }
        } catch (Exception e) {
            jsonObjects = new ArrayList<>();
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("error", e.getMessage());
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    @RequestMapping(value = "/cibrquery/{endpointname}", method = {RequestMethod.GET, RequestMethod.POST}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, ? extends Object>> runQueryForEndPointcibr(
            @PathVariable("endpointname") String endpointname,
            @RequestBody Map<String, Object> updateParam,
            @RequestHeader(value = "atoken", required = false) String atoken,
            HttpServletRequest request
    ) {
        List<Map<String, ? extends Object>> jsonObjects = new ArrayList<>();
        Map<String, Object> jsonObject = new HashMap<>();
        JDBCEndpoints jdbcEndpoints = jdbcEndpointRepo.findByEndpointNameAndActiveIsTrue(endpointname);
        try {
            if (atoken != null && !atoken.isEmpty() && atoken.equals(atoken1)) {
            } else {
                throw new Exception("Authorization error");
            }
            Gson gson = new Gson();
            if (jdbcEndpoints != null) {
                Map<String, String[]> params = request.getParameterMap();

                String rValue = jdbcEndpoints.getQuery();
                for (Map.Entry<String, Object> stringObjectEntry : updateParam.entrySet()) {
                    if (stringObjectEntry.getKey() != null && stringObjectEntry.getKey().equalsIgnoreCase("submissiondata")) {
                        rValue = gson.toJson(stringObjectEntry.getValue());
                        break;
                    }
                }

                String querystatus = jdbcEndpoints.getInup();
                if (querystatus != null && querystatus.equalsIgnoreCase("insert")) {
                    return JDBCBase.executeprocedure(jdbcAccTemplate.get(jdbcEndpoints.getJdbcAccounts().getEnvironment()), rValue);
                } else {
                    return JDBCBase.runjdbcQuery(jdbcAccTemplate.get(jdbcEndpoints.getJdbcAccounts().getEnvironment()), rValue);
                }
            } else {
                jsonObject = new HashMap<>();
                jsonObject.put("error", "Endpoint not found");
                jsonObjects.add(jsonObject);
            }
        } catch (Exception e) {
            jsonObjects = new ArrayList<>();
            jsonObject = new HashMap<>();
            jsonObject.put("error", e.getMessage());
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }

    @RequestMapping(value = "/deactiveep/{eid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deactiveEndPoint(@PathVariable("eid") Long eid, HttpServletRequest request) {
        JsonObject jsonObject = JDBCBase.endpointStatus(
                jdbcEndpointRepo,
                jdbcEndpointRepo.findOne(eid),
                false
        );

        return SFInterfaceBase.validateAndreturnPage("redirect:/jdbc/displayep", request);
    }


    @RequestMapping(value = "/activeep/{eid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String activeEndPoint(@PathVariable("eid") Long eid, HttpServletRequest request) {
        JsonObject jsonObject = JDBCBase.endpointStatus(
                jdbcEndpointRepo,
                jdbcEndpointRepo.findOne(eid),
                false
        );

        return SFInterfaceBase.validateAndreturnPage("redirect:/jdbc/displayep", request);
    }


    @ResponseBody
    @RequestMapping(value = "/run", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public List run(
            @RequestParam("connName") String connName,
            @RequestParam("query") String query
    ) {
        // JDBCAccounts jdbcAccounts = jdbcAccountsRepo.findByEnvironment(connName);
        return JDBCBase.runjdbcQuery(jdbcAccTemplate.get(connName), query);

    }

    @RequestMapping(value = "/displayep", method = RequestMethod.GET)
    public String displayEndPoints(Model model, HttpServletRequest request) {
        model.addAttribute("endpoints", jdbcEndpointRepo.findAll());

        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_query_display", request);
    }


    @RequestMapping(path = "/editjdbcaccountdetails/{q}", method = RequestMethod.GET)
    public String editendpoint(Model model,
                               @PathVariable(name = "q", required = true) String endpointName,
                               HttpServletRequest request) {

        List<String> clients = new ArrayList<>();
        try {
            JDBCAccounts jdbcAccounts = jdbcAccountsRepo.findByEnvironment(endpointName);
            model.addAttribute("jdbcaccount", jdbcAccounts);

        } catch (Exception e) {
            clients.add(e.getLocalizedMessage());
        }


        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_edit_account", request);
    }

    @ResponseBody
    @RequestMapping(path = "/jdbcendpoints", method = RequestMethod.GET)
    public Map<String, Object> jdbcendpoints(
            @RequestParam(value = "draw", defaultValue = "1") int draw,
            @RequestParam(value = "start") int start,
            @RequestParam(value = "length") int length,
            @RequestParam(value = "search[value]", required = false, defaultValue = "") String searchvalue,
            @RequestParam(value = "order[0][column]", defaultValue = "2") String sortcolumn,
            @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sorttype
    ) {
        Map<String, Object> Repo = new HashMap<>();
        try {
            /*int filteredcount = 0;
            int pageindex = 0;
            if (length != 0) {
                pageindex = start / length;
            }
            List<JDBCEndpoints> endPointDomains = new ArrayList<>();
            Pageable pageable = new PageRequest(pageindex, length);
            if (sortcolumn.equalsIgnoreCase("4")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(JDBCEndpointRepo.findByEndPointNameContainingOrLdapAccounts_ConnNameContainingOrderByLdapAccounts_ConnNameAsc(pageable, searchvalue, searchvalue));
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
            Repo.put("data", endPointDomains);*/

            Repo.put("draw", draw);
            Repo.put("recordsTotal", jdbcEndpointRepo.count());
            Repo.put("recordsFiltered", jdbcEndpointRepo.count());
            Repo.put("data", jdbcEndpointRepo.findAll());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Repo;
    }


    @ResponseBody
    @RequestMapping(path = "/jdbcaccounts", method = RequestMethod.GET)
    public Map<String, Object> jdbcaccounts(
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
            List<JDBCAccounts> endPointDomains = new ArrayList<>();
            Pageable pageable = new PageRequest(pageindex, length);
            if (sortcolumn.equalsIgnoreCase("4")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByDatabaseNameAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByDatabaseNameAsc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByDatabaseNameDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByDatabaseNameDesc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("2")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByUsernameAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByUsernameAsc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByUsernameDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByUsernameDesc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                }

            } else if (sortcolumn.equalsIgnoreCase("1")) {

                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByEnvironmentAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByEnvironmentAsc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByEnvironmentDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByEnvironmentDesc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                }

            } else {
                if (sorttype.equalsIgnoreCase("asc")) {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByHostAsc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByHostAsc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                } else {
                    if (!searchvalue.isEmpty()) {
                        endPointDomains.addAll(jdbcAccountsRepo.findByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContainingOrderByHostDesc(pageable, searchvalue, searchvalue, searchvalue, searchvalue));
                        filteredcount = jdbcAccountsRepo.countByEnvironmentContainingOrHostContainingOrDatabaseNameContainingOrUsernameContaining(searchvalue, searchvalue, searchvalue, searchvalue);
                    } else {
                        endPointDomains.addAll(jdbcAccountsRepo.findAllByOrderByHostDesc(pageable));
                        filteredcount = (int) jdbcAccountsRepo.count();
                    }
                }
            }
            Repo.put("draw", draw);
            Repo.put("recordsTotal", jdbcAccountsRepo.count());
            Repo.put("recordsFiltered", filteredcount);
            Repo.put("data", endPointDomains);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Repo;
    }


    @ResponseBody
    @RequestMapping(value = "/createep", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createLDAPEndpoint(
            @RequestParam("ename") String ename,
            @RequestParam String connName,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") String op,
            @RequestParam String inup
    ) {
        JsonObject jsonObject = new JsonObject();
        try {
            JDBCAccounts jdbcAccounts = jdbcAccountsRepo.findByEnvironment((connName));
            JDBCEndpoints jdbcEndPoints = null;


            jdbcEndPoints = new JDBCEndpoints();

            jdbcEndPoints = jdbcEndpointRepo.findByEndpointName(ename.trim());
            if (jdbcEndPoints == null) {
                jdbcEndPoints = new JDBCEndpoints();
            }

            if (jdbcAccounts != null) {
                jdbcEndPoints.setEndpointName(ename.trim().toLowerCase());
                jdbcEndPoints.setJdbcAccounts(jdbcAccounts);
                jdbcEndPoints.setQuery(query);
                jdbcEndPoints.setInup(inup);

                if (jdbcEndpointRepo.save(jdbcEndPoints) != null) {
                    jsonObject.addProperty("success", ename + " Created Successfully");
                } else {
                    jsonObject.addProperty("error", ename + " Endpoint creation failed");
                }
            } else {
                jsonObject.addProperty("error", ename + " Endpoint creation failed – invalid jdbc account connection name:" + connName);
            }
        } catch (Exception e) {
            jsonObject.addProperty("error", ename + " Endpoint creation failed – duplicate jdbc account/endpoint present. JDBC Account connname: " + connName);
        }
        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/insert", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String insert(
            @RequestParam("ename") String ename,
            @RequestParam String connName,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0") String op
    ) {
        JsonObject jsonObject = new JsonObject();
        try {
            JDBCAccounts jdbcAccounts = jdbcAccountsRepo.findByEnvironment((connName));
            JDBCEndpoints jdbcEndPoints = null;


            jdbcEndPoints = new JDBCEndpoints();

            jdbcEndPoints = jdbcEndpointRepo.findByEndpointName(ename.trim());
            if (jdbcEndPoints == null) {
                jdbcEndPoints = new JDBCEndpoints();
            }

            if (jdbcAccounts != null) {
                jdbcEndPoints.setEndpointName(ename.trim().toLowerCase());
                jdbcEndPoints.setJdbcAccounts(jdbcAccounts);
                jdbcEndPoints.setQuery(query);

                if (jdbcEndpointRepo.save(jdbcEndPoints) != null) {
                    jsonObject.addProperty("success", ename + " Created Successfully");
                } else {
                    jsonObject.addProperty("error", ename + " Endpoint creation failed");
                }
            } else {
                jsonObject.addProperty("error", ename + " Endpoint creation failed – invalid jdbc account connection name:" + connName);
            }
        } catch (Exception e) {
            jsonObject.addProperty("error", ename + " Endpoint creation failed – duplicate jdbc account/endpoint present. JDBC Account connname: " + connName);
        }
        return jsonObject.toString();
    }


    @ResponseBody
    @RequestMapping(value = "/insertdata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> insertdata(
            @RequestParam("tablename") String ename,
            @RequestParam String connName,
            @RequestBody Map<String, Object> data

    ) {
        Map<String, Object> succcessdata = new HashMap<>();
        try {
            int affected = JDBCBase.insert(jdbcAccTemplate.get(connName), ename, data);

            succcessdata.put("row", affected);
            succcessdata.put("status", true);
        } catch (Exception e) {
            succcessdata.put("status", false);
            succcessdata.put("error", e.getLocalizedMessage());
        }
        return succcessdata;
    }


    @ResponseBody
    @RequestMapping(value = "/getconnection", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public JDBCAccounts getConnection(@RequestParam("connName") String connName) {
        JsonObject jsonObject = new JsonObject();
        return jdbcAccountsRepo.findByEnvironment(connName);
    }


    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String create(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String database,
            @RequestParam String desc,
            @RequestParam String host,
            @RequestParam Integer port,
            @RequestParam String connName,
            @RequestParam(value = "status", defaultValue = "false") boolean sso

    ) {
        JsonObject response = new JsonObject();


        JDBCAccounts jdbcAccounts = jdbcAccountsRepo.findByEnvironment(connName.trim().toLowerCase());
        if (jdbcAccounts == null) {
            jdbcAccounts = new JDBCAccounts();
        }
        jdbcAccounts.setActive(sso);
        jdbcAccounts.setDatabaseName(database);
        jdbcAccounts.setDesc(desc);
        jdbcAccounts.setEnvironment(connName);
        jdbcAccounts.setHost(host);
        jdbcAccounts.setPort(port);
        jdbcAccounts.setPassword(password);
        jdbcAccounts.setUsername(username);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(
                    DataSourceBuilder
                            .create()
                            .url("jdbc:sqlserver//" + host + ":" + port.toString() + ";databaseName=" + database)
                            .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                            .username(username)
                            .password(password)
                            .build());
            jdbcAccountsRepo.save(jdbcAccounts);
            response.addProperty("success", connName + " Connected");
        } catch (Exception e) {
            if (jdbcAccounts.getId() != null) {
                jdbcAccountsRepo.save(jdbcAccounts);
            }
            response.addProperty("error", e.getMessage() + " " + connName + " account not created");
        }
        return response.toString();
    }


    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model, HttpServletRequest request) {
        List<String> clients = new ArrayList<>();

        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_create_account", request);
    }


    @RequestMapping(value = "/connections", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("jdbcaccounts", jdbcAccountsRepo.findAll());
        return SFInterfaceBase.validateAndreturnPage("jdbc/jdbc_accounts", request);
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
    @RequestMapping(path = "/forms", method = RequestMethod.GET)
    public void forms() throws ARException {


        //return filteredformModelsList;
    }


}
