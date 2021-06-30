package com.htc.remedy.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    private final TokenStore tokenStore;

    private static final String BEARER_AUTHENTICATION = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "authorization";


    @Autowired
    public ResourceConfig(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {


        http
                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied");
                    }
                })
                .and()
                .logout()
                .logoutUrl("/oauth/logout")
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    String token = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

                    if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {

                        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token.split(" ")[0]);

                        if (oAuth2AccessToken != null) {
                            tokenStore.removeAccessToken(oAuth2AccessToken);
                        }

                    }

                    httpServletResponse.setStatus(HttpServletResponse.SC_OK,token);
                })
                .and()
                .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
                .headers()
                .frameOptions().disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/page/**","/api/ticketinfo","/api/loginDetails","/api/queryfields","/api/filterticket","/api/updateticket","/api/departmentinfo").permitAll()
                .antMatchers("/napi/**").authenticated();

    }

}
