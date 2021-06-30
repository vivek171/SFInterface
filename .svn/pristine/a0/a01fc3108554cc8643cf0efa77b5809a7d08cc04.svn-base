package com.htc.remedy.base;

import com.google.gson.Gson;
import com.htc.remedy.constants.Constants;
import com.htc.remedy.model.Token;
import com.htc.remedy.model.UserDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class SSOBase {


    public static UserDetails ssologindetails(Token token) throws Exception {
        UserDetails userDetails = new UserDetails();

        RestTemplate restTemplate = new RestTemplate();
        Gson g = new Gson();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);

        headers.add("authorization", "Bearer " + token.getAccess_token());
        httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(Constants.getSsologindetailsurl(), HttpMethod.GET, httpEntity, String.class);

            userDetails = g.fromJson(response.getBody(), UserDetails.class);
            userDetails.setToken(token.getAccess_token());
            userDetails.setRefreshtoken(token.getRefresh_token());

        } catch (Exception e) {
            System.out.println("user details failed" + e.getMessage());
            throw new Exception(e.getMessage());
        }

        return userDetails;
    }


    public static Token verifyCredentialswithoauth(String token) throws IOException {
        Token refreshtoken = new Token();
        try {
            refreshtoken = refreshtoken(token);
        } catch (Exception e) {
            System.out.println("Login Failed" + e.getMessage());
        }
        return refreshtoken;
    }

    public static Token refreshtoken(String token) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        Gson g = new Gson();
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response;

        headers.add("content-type", "application/x-www-form-urlencoded");
        headers.add("authorization", "Basic " + Constants.getSsosecretkey());

        body.add("refresh_token", token);
        body.add("grant_type", "refresh_token");

        httpEntity = new HttpEntity<>(body, headers);

        Token refreshtoken = new Token();

        try {
            response = restTemplate.exchange(Constants.getSsologinurl(), HttpMethod.POST, httpEntity, String.class);
        } catch (Exception e) {
            System.out.println("Refresh token failed" + e.getMessage());
            throw new Exception(e.getMessage());
        }

        refreshtoken = g.fromJson(response.getBody(), Token.class);
        return refreshtoken;
    }


    public static String ssovalidate(String accesstoken, String refreshtoken) throws Exception {
        try {
            Token token = new Token();
            token.setAccess_token(accesstoken);
            token.setRefresh_token(refreshtoken);
            return ITSMBase.validatesupportuserfromtoken(token);
        } catch (Exception e) {
            throw new Exception("Authorization Error!");
        }
    }

   /* public static String ssovalidateforrefdb(String accesstoken, String refreshtoken) throws Exception {
        try {
            Token token = new Token();
            token.setAccess_token(accesstoken);
            token.setRefresh_token(refreshtoken);
            return ITSMBase.validatesupportuserfromtoken(token);
        } catch (Exception e) {
            throw new Exception("Authorization Error!");
        }
    }*/




}
