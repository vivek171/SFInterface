package com.htc.remedy.base;

import ca.krasnay.sqlbuilder.InsertCreator;
import ca.krasnay.sqlbuilder.UpdateCreator;
import com.bmc.thirdparty.org.apache.commons.lang.StringEscapeUtils;
import com.google.gson.JsonObject;
import com.htc.remedy.domain.JDBCEndpoints;
import com.htc.remedy.repo.JDBCEndpointRepo;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.http.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.htc.remedy.base.RemedyBase.validate;

@Component
public class JDBCBase {


    public static JsonObject endpointStatus(JDBCEndpointRepo jdbcEndpointRepo, JDBCEndpoints jdbcEndpoints, boolean status) {
        jdbcEndpoints.setActive(status);
        JsonObject jsonObject = new JsonObject();
        if (jdbcEndpointRepo.save(jdbcEndpoints) != null) {

            jsonObject.addProperty("success", status ? "Activated" : "Deactived");
        } else {
            jsonObject.addProperty("error", status ? "Activate " : "Deactive");
        }

        return jsonObject;
    }

    public static List<Map<String, ? extends Object>> runjdbcQuery(JdbcTemplate jdbcTemplate, String query) {
        return jdbcTemplate.query(query, (resultSet, position) -> {
            int cCount = resultSet.getMetaData().getColumnCount();
            Map<String, Object> jsonObject = new HashMap<>();
            for (int i = 1; i <= cCount; i++) {
                jsonObject.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
            }
            return jsonObject;
        });
    }

    public static List<Map<String, Object>> runjdbcQueryforlist(JdbcTemplate jdbcTemplate, String query) {
        return jdbcTemplate.queryForList(query);
    }

    public static List<Map<String, ? extends Object>> insertupdatejdbcQuery(JdbcTemplate jdbcTemplate, String query) {

        List<Map<String, ? extends Object>> response = new ArrayList<>();
        Map<String, Object> errorobject = new HashMap<>();
        int rowcount = 0;
        try {
            rowcount = jdbcTemplate.update(query);
            errorobject.put("status", "success");
            errorobject.put("Message", rowcount + " Records has been posted");
            response.add(errorobject);
        } catch (Exception e) {
            response = new ArrayList<>();
            errorobject.put("status", "error");
            errorobject.put("Message", e.getMessage());
            response.add(errorobject);
        }

        return response;
    }

    public static List<Map<String, ? extends Object>> executeprocedure(JdbcTemplate jdbcTemplate, String query) {
        List<Map<String, ? extends Object>> response = new ArrayList<>();
        Map<String, Object> errorobject = new HashMap<>();
        Map<String, Object> errorobject1 = new HashMap<>();
        int rowcount = 0;
        try {

            errorobject1 = findData(jdbcTemplate, query);
            //jdbcTemplate.execute("Exec cibrsubmission " + "wer");
            errorobject.put("status", "success");
            errorobject.put("referencesk", errorobject1.get("referencesk"));
            errorobject.put("Message", errorobject1.get("#update-count-1") + " Records has been posted");
            response.add(errorobject);
        } catch (Exception e) {
            response = new ArrayList<>();
            errorobject.put("status", "error");
            errorobject.put("referencesk", "-1");
            errorobject.put("Message", e.getMessage());
            response.add(errorobject);
        }
        return response;
    }


    public static Map<String, Object> findData(JdbcTemplate jdbcTemplate, String query) {
        List prmtrsList = new ArrayList();
        prmtrsList.add(new SqlParameter(Types.VARCHAR));
        // prmtrsList.add(new SqlParameter(Types.VARCHAR));
        prmtrsList.add(new SqlOutParameter("referencesk", Types.VARCHAR));

        Map<String, Object> resultData = jdbcTemplate.call(new CallableStatementCreator() {

            @Override
            public CallableStatement createCallableStatement(Connection connection)
                    throws SQLException {

                CallableStatement callableStatement = connection.prepareCall("{call cibrsubmission(?,?)}");
                callableStatement.setString(1, query);
                callableStatement.registerOutParameter(2, Types.VARCHAR);
                return callableStatement;

            }
        }, prmtrsList);
        return resultData;
    }

    public static int insert(JdbcTemplate jdbcTemplate, String tableName, Map<String, Object> data) {
        PreparedStatementCreator preparedStatementCreator = new InsertCreator(tableName);

        data.keySet().forEach(key ->
                ((InsertCreator) preparedStatementCreator).setValue(key, data.get(key))
        );
        return jdbcTemplate.update(preparedStatementCreator);
    }

    public static int update(JdbcTemplate jdbcTemplate, String tableName, Map<String, Object> data) {
        PreparedStatementCreator preparedStatementCreator = new UpdateCreator(tableName);
        data.keySet().forEach(key -> ((UpdateCreator) preparedStatementCreator).setValue(key, data.get(key)));

        ((UpdateCreator) preparedStatementCreator).whereEquals("id", 42);

        return jdbcTemplate.update(preparedStatementCreator);
    }


    public static List<Map<String, Object>> redirectrefdb(String endpointName, String custom, String sortfield, String toLowerCase, String distinct, Integer noofrecords, String sortorder, String errormessage, Map<String,String[]> paramsmap, String referencedburl) {
        RestTemplate restTemplate = new RestTemplate();
        List<Map<String, Object>> result = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
        headers.setContentType(MediaType.APPLICATION_JSON);
        StringBuilder builder = new StringBuilder("");

        body.add("endpointname", endpointName);
        body.add("custom", custom);
        body.add("sortfield", sortfield);
        body.add("toLowerCase", toLowerCase);
        body.add("distinct", distinct);
        body.add("noofrecords", noofrecords.toString());
        body.add("sortorder", sortorder);
        body.add("errormessage", errormessage);
        body.add("parameters",paramsmap);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<List> response = null;

        try {
            response = restTemplate.exchange(referencedburl+endpointName, HttpMethod.POST, httpEntity, List.class);
            result = response.getBody();
        } catch (Exception e) {
            result = new ArrayList<>();
        }
        return result;
    }


    public static List<Map<String, Object>> userSearchsqlquery(
            JdbcTemplate jdbcTemplate,
            String client,
            String creteria,
            String mailid1,
            String networklogin1,
            String fullname1,
            String partialname1,
            String firstname1,
            String lastname1,
            String loginid1,
            String empnumber1

    ) {
        List<Map<String, Object>> resultrecords = new ArrayList<>();
        creteria = StringEscapeUtils.escapeSql(creteria);
        String c1 = creteria;
        String[] splittedCreteria = creteria.split(" ");

        String sQuery = "SELECT Full_Name as full_name,\n" +
                "       Department as department,\n" +
                "       Building as building,\n" +
                "       BusEmailID AS email_address,\n" +
                "       Emp_No AS employee_number,\n" +
                "       Floor AS floor,\n" +
                "       Suite AS suite,\n" +
                "       Office AS office,\n" +
                "       CASE\n" +
                "           WHEN Client='NMC' THEN 'NMC'\n" +
                "           ELSE Business_Organization\n" +
                "       END AS business_organization,\n" +
                "       CASE\n" +
                "           WHEN Client='NMC' THEN 'Nebraska Medicine'\n" +
                "           ELSE (SELECT TOP 1 accountname FROM sec_account WITH (NOLOCK) WHERE sec_account.accountcode ='" + client + "' )\n" +
                "       END AS business_organization_name,\n" +
                "       Phone_Work AS phone_work,\n" +
                "       Fname AS first_name,\n" +
                "       LName AS last_name,\n" +
                "       RefID1 AS login_id,\n" +
                "       Phone_Ext,\n" +
                "       GUID,\n" +
                "       Designation,\n" +
                "       Fax,\n" +
                "       VIP,\n" +
                "       Title,\n" +
                "       RefID2 AS Network_Login,\n" +
                "       Queue,\n" +
                "       Designation,\n" +
                "       Client,\n" +
                "       Middle_Init,\n" +
                "       Cost_Code,\n" +
                "       role AS Role,\n" +
                "       role_desc AS Role_Prefix,\n" +
                "       Is_Support_Person AS support_person,\n" +
                "       Pager_Numeric AS pager_numeric,\n" +
                "       Phone_Ext AS pager_pin,\n" +
                "       Pager_Alpha AS pager_alpha,\n" +
                "       null AS company_code\n" +
                "FROM FDN_PEOPLE_INFO\n" +
                " WITH (NOLOCK) WHERE Client IN (SELECT distinct accountcode FROM sec_account WITH (NOLOCK) WHERE sec_account.accountcode = '" + client + "' OR parentaccount_sk  IN (SELECT account_sk FROM sec_account WITH (NOLOCK) WHERE sec_account.accountcode ='" + client + "'))\n" +
                "AND ((Fname is NOT NULL AND LNAME is NOT NULL  AND Full_Name is not NULL AND RefID1 is NOT NULL) \n";

        StringBuilder peopleQuery = new StringBuilder(sQuery);

        peopleQuery.append(" AND ");

        if (splittedCreteria.length == 1 && validate(splittedCreteria[0])) {
            peopleQuery.append(buildCreateriaForUserSearch2("BusEmailID", splittedCreteria, "email"));

        }
        if (empnumber1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("Emp_No", splittedCreteria, "empno"));
        }

        if (fullname1.equalsIgnoreCase("true") || partialname1.equalsIgnoreCase("true") || firstname1.equalsIgnoreCase("true") || lastname1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("FName", splittedCreteria, "name"));
            peopleQuery.append(buildCreateriaForUserSearch2("LName", splittedCreteria, "name"));
        }

        if (loginid1.equalsIgnoreCase("true") || networklogin1.equalsIgnoreCase("true")) {
            peopleQuery.append(buildCreateriaForUserSearch2("RefID1", splittedCreteria, "login"));
        }


        peopleQuery.append("(Full_Name = '" + creteria + "')");
        peopleQuery.append(") ");
        String status = "1";
        peopleQuery.append("AND ISActive = '" + status + "'");

        List<Map<String, Object>> peopleresult = runjdbcQueryforlist(jdbcTemplate, peopleQuery.toString());

        if (peopleresult != null && peopleresult.size() > 0) {


            if (!creteria.contains("'")) {
                LevenshteinDistance levenshteinDistance = new LevenshteinDistance(10);


                resultrecords = peopleresult.parallelStream().filter(stringHashMap -> {
                            String fname = (String) stringHashMap.get("first_name");
                            String lname = (String) stringHashMap.get("last_name");
                            String fullname = (String) stringHashMap.get("full_name");

                            boolean passed = false;

                            if ((splittedCreteria.length >= 2) && fullname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
//
                                if (fullname.equalsIgnoreCase(c1)) {
                                    return true;
                                }
                            }

                            if ((splittedCreteria.length == 2) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                                if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[1].toLowerCase()))) {
                                    return true;
                                }
                            }
                            if ((splittedCreteria.length == 3) && partialname1.equalsIgnoreCase("true")) {//full name filter with first name and last name
                                if (fname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase()) && (lname.toLowerCase().startsWith(splittedCreteria[2].toLowerCase()))) {
                                    return true;
                                }
                            }

                            if (firstname1.equalsIgnoreCase("true")) {     //firstname filter
                                String tempfname = stringHashMap.get("first_name").toString();
                                String[] tempCreteria = tempfname.split(" ");
                                if (tempfname != null && !tempfname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                    if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                        if (tempfname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                            return true;
                                        }
                                    }
                                }
                            }

                            if (lastname1.equalsIgnoreCase("true")) {     //lastname filter
                                String templname = stringHashMap.get("last_name").toString();
                                String[] tempCreteria = templname.split(" ");
                                if (templname != null && !templname.isEmpty() && tempCreteria.length == splittedCreteria.length) {     //validating search value and orignal value splits are same
                                    if (splittedCreteria.length >= 1 && (splittedCreteria[0].length() > 1)) {
                                        if (templname.toLowerCase().startsWith(splittedCreteria[0].toLowerCase())) {
                                            return true;
                                        }
                                    }
                                }
                            }


                            if (stringHashMap.get("email_address") != null && mailid1.equalsIgnoreCase("true") && validate(splittedCreteria[0])) {     //emailid filter
                                String email = stringHashMap.get("email_address").toString();
                                if (email != null && !email.isEmpty()) {
                                    if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                        if (email.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                            return true;
                                        }
                                    }
                                }
                            }
                            if (stringHashMap.get("Network_Login") != null && networklogin1.equalsIgnoreCase("true")) {   //networklogin filter
                                String login = stringHashMap.get("Network_Login").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            return true;
                                        }
                                    }
                                }
                            }

                            if (loginid1.equalsIgnoreCase("true")) {         //loginid filter
                                String login = stringHashMap.get("login_id").toString();
                                if (login != null && !login.isEmpty()) {
                                    if (splittedCreteria.length > 0) {
                                        if (login.toLowerCase().equalsIgnoreCase(c1)) {
                                            return true;
                                        }
                                    }
                                }
                            }
                            if (stringHashMap.get("employee_number") != null && empnumber1.equalsIgnoreCase("true")) {        //employeeid filter
                                String empno = stringHashMap.get("employee_number").toString();
                                if (empno != null && !empno.isEmpty()) {
                                    if (splittedCreteria.length == 1 && (splittedCreteria[0].length() > 1)) {
                                        if (empno.toLowerCase().equalsIgnoreCase(splittedCreteria[0].toLowerCase())) {
                                            return true;
                                        }
                                    }
                                }
                            }

                            return passed;
                        }
                ).collect(Collectors.toList());
                if (resultrecords.isEmpty() && !(resultrecords.size() > 0))
                    resultrecords.add(RemedyBase.returnErrorobject("No data found for the given criteria"));
            }
        } else {
            resultrecords.add(RemedyBase.returnErrorobject("No data found for the given criteria"));
        }


        return resultrecords;
    }


    private static String buildCreateriaForUserSearch2(String columnName, String[] splittedCreteria, String type) {

        StringBuilder peopleQuery = new StringBuilder("");


        if (type.equalsIgnoreCase("name") && splittedCreteria.length == 1 && splittedCreteria[0].length() >= 2) {

            peopleQuery.append(" (").append("FNAME").append(" LIKE '").append(splittedCreteria[0]).append("%'");
            peopleQuery.append(" OR ");
            peopleQuery.append("LNAME").append(" LIKE '").append(splittedCreteria[0]).append("%' ) OR ");

        } else if (type.equalsIgnoreCase("email") && splittedCreteria.length == 1) {

            peopleQuery.append(" (").append("BusEmailID").append(" = '").append(splittedCreteria[0]).append("' ) OR ");

        } else if (type.equalsIgnoreCase("login")) {
            peopleQuery.append(" (").append("RefID2").append(" = '").append(String.join(" ", splittedCreteria)).append("'");
            peopleQuery.append(" OR ");
            peopleQuery.append("RefID1").append(" = '").append(String.join(" ", splittedCreteria)).append("') OR ");

        } else if (type.equalsIgnoreCase("empno") && splittedCreteria.length == 1) {

            peopleQuery.append(" (").append("Emp_No").append(" = '").append(splittedCreteria[0]).append("' ) OR ");

        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length >= 2 && splittedCreteria.length <= 3) {
            peopleQuery.append("((");
            for (int i = 0; i < splittedCreteria.length; i++) {
                peopleQuery.append(columnName).append(" LIKE '").append(splittedCreteria[i]).append("%'");
                if (i < splittedCreteria.length - 1) {
                    peopleQuery.append(" OR ");
                }
                if (i == splittedCreteria.length - 1) {
                    peopleQuery.append(" )");
                    peopleQuery.append(" AND ");
                }
            }
            peopleQuery.append(" ((").append("Full_Name").append(" LIKE '").append(String.join("%", splittedCreteria)).append("%') OR (");
            peopleQuery.append(columnName).append(" = '").append(String.join(" ", splittedCreteria)).append("'))) OR ");
        } else if (type.equalsIgnoreCase("name") && splittedCreteria.length > 3) {
            peopleQuery.append(" ((").append("FName").append(" LIKE '").append(splittedCreteria[0]).append("%'");
            peopleQuery.append(" OR ");
            peopleQuery.append("LName").append(" LIKE '").append(splittedCreteria[1]).append("%' ) AND ");
            peopleQuery.append(" (").append("Full_Name").append(" LIKE '").append(String.join("%", splittedCreteria)).append("%' )) OR ");
        }
        return peopleQuery.toString();
    }


}