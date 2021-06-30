package com.htc.remedy.controller;


import com.google.gson.Gson;
import com.htc.remedy.base.RemedyBase;
import com.htc.remedy.domain.*;
import com.htc.remedy.repo.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Controller
@CrossOrigin

@RequestMapping(path = "/promote")
public class PromoteController {

    @Autowired
    private EndPointRepo endPointRepo;

    @Autowired
    private FieldRepo fieldRepo;

    @Autowired
    private LDAPAccountsRepo ldapAccountsRepo;

    @Autowired
    private LDAPEndpointRepo ldapEndpointRepo;

    @Autowired
    private QualificationRepo qualificationRepo;


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/remedyendpoint")
    public Map<String, String> remedyendpoint(String[] endpointnames, @RequestParam(value = "transactionurl") String transactionurl) {

        Map<String, String> stringMap = new HashMap<>();

        for (String endpointname : endpointnames) {
            try {
                EndPointDomain endPointDomain = endPointRepo.findByEndPointNameAndActiveIsTrue(endpointname.trim());
                Set<FieldsDomain> fieldsDomains = fieldRepo.findByFieldsEndpoint(endPointDomain);
                Set<QualificationDomain> qualificationDomains = qualificationRepo.findByQualificationEndPoint(endPointDomain);
                List<String> qualification = new ArrayList<>();
                List<String> requiredfields = new ArrayList<>();
                String formname = "", endpointdescription = "", endpointurl = "", qualificationstring = "";
                StringBuilder stringBuilder = new StringBuilder("");
                qualificationDomains.forEach(qualificationDomain -> {
                    stringBuilder.append(qualificationDomain.getColumnName()).append("^");
                    stringBuilder.append(qualificationDomain.getCondition()).append("^");
                    stringBuilder.append(qualificationDomain.getColumnValue()).append("^");
                    stringBuilder.append(qualificationDomain.getAppendCondition());
                    qualification.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                });

                fieldsDomains.forEach(fieldsDomain -> {
                    stringBuilder.append(fieldsDomain.getFieldId()).append("^");
                    stringBuilder.append(fieldsDomain.getFieldName());
                    requiredfields.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                });
                formname = endPointDomain.getFormName();
                endpointdescription = endPointDomain.getEndPointDescription();
                endpointurl = endPointDomain.getEndPointKey();
                qualificationstring = endPointDomain.getQualificationString();


                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(transactionurl + "/page/updateendpointpromote");


                // org.springframework.util.LinkedMultiValueMap<String,Object> mMap = new LinkedMultiValueMap<>();
               /* Map<String,Object> mMap = new HashMap<>();


                        // Add query parameter
                        mMap.put("formName", formname);
                        mMap.put("endpointName", endpointname);
                        mMap.put("endpointDesc", endpointdescription);
                        mMap.put("endpointUrl", endpointurl);
                        mMap.put("qualification[]", (qualification));
                        mMap.put("qualificationString", qualificationstring);
                        mMap.put("selectedFields[]", requiredfields);*/


                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                //      headers.set("Authorization","Basic Y3Rzc3A6c2VjcmV0");

                for (QualificationDomain qualificationDomain : qualificationDomains) {
                    qualificationDomain.setQualificationEndPoint(null);
                    qualificationDomain.setId(null);
                }

                for (FieldsDomain fieldsDomain : fieldsDomains) {
                    fieldsDomain.setFieldsEndpoint(null);
                    fieldsDomain.setId(null);
                }
                endPointDomain.setFilter(qualificationDomains);
                endPointDomain.setSelectedFields(fieldsDomains);

                endPointDomain.setDate(null);
                endPointDomain.setId(null);


                // HttpEntity<org.springframework.util.MultiValueMap<String, Object>> httpEntity = new HttpEntity<>( mMap , headers);
                HttpEntity<String> httpEntity = new HttpEntity<>(new JSONObject(endPointDomain).toString(), headers);

                //  HttpEntity<String> httpEntity1=new HttpEntity<>(new JSONObject().toString(),headers );

                ResponseEntity<EndPointDomain> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, EndPointDomain.class);
                if (endPointDomain.equals(response.getBody())) {
                    stringMap.put(endpointname, "success");
                } else {
                    stringMap.put(endpointname, "failed");
                }
            } catch (Exception e) {
                stringMap.put(endpointname, "not found." + "system message:" + e.getMessage());
            }
        }
        return stringMap;
    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/ldapendpoint")
    public List<Object> ldapendpoint(String[] endpointnames, @RequestParam(value = "transactionurl") String transactionurl) {

        List<Object> result = new ArrayList<>();

        for (String endpointname : endpointnames) {
            try {

                LDAPEndPoints ldapEndPoints = ldapEndpointRepo.findByEndPointNameAndActiveIsTrue(endpointname);

                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(transactionurl + "/ldap/createep");
                // Add query parameter

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                org.springframework.util.MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();

                mMap.add("ename", ldapEndPoints.getEndPointName());
                mMap.add("connName", ldapAccountsRepo.findByLdapEndPoints(ldapEndPoints).getConnName());
                mMap.add("base", ldapEndPoints.getBase());
                mMap.add("query", ldapEndPoints.getQuery());
                mMap.add("rf", ldapEndPoints.getRequiredFields());


                HttpEntity<org.springframework.util.MultiValueMap<String, String>> httpEntity = new HttpEntity<>(mMap, headers);

                ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);

                result.add(new Gson().fromJson(response.getBody(), HashMap.class));
            } catch (Exception e) {
                result.add(RemedyBase.returnError(endpointname + " ldapendpoint not found in source." + "system message:" + e.getMessage()));
            }
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/ldapaccount")
    public List<Object> ldapaccount(String[] endpointnames, @RequestParam(value = "transactionurl") String transactionurl) {

        List<Object> result = new ArrayList<>();
        for (String endpointname : endpointnames) {
            try {
                LDAPAccounts ldapAccounts = ldapAccountsRepo.findByConnName(endpointname.trim().toLowerCase());


                UriComponentsBuilder builder = UriComponentsBuilder
                        .fromUriString(transactionurl + "/ldap/create");

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


                org.springframework.util.MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();
                mMap.add("username", ldapAccounts.getUsername());
                mMap.add("password", ldapAccounts.getPassword());
                mMap.add("domain", ldapAccounts.getDomain());
                mMap.add("client", ldapAccounts.getClient());
                mMap.add("desc", ldapAccounts.getDesc());
                mMap.add("host", ldapAccounts.getHost());
                mMap.add("port", ldapAccounts.getPort().toString());
                mMap.add("base", ldapAccounts.getBase());
                mMap.add("filter", ldapAccounts.getFilter());
                mMap.add("searchDN", ldapAccounts.getSearchDN());
                mMap.add("connName", ldapAccounts.getConnName());
                mMap.add("sso", ldapAccounts.isSso() == true ? "true" : "false");
                HttpEntity<org.springframework.util.MultiValueMap<String, String>> httpEntity = new HttpEntity<>(mMap, headers);
                ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);

                result.add(new Gson().fromJson(response.getBody(), HashMap.class));

            } catch (Exception e) {
                result.add(RemedyBase.returnError(endpointname + " ldapaccount not found in source." + "system message:" + e.getMessage()));
            }
        }
        return result;
    }
}