package com.htc.remedy.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@EnableWebSecurity
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new CustomAuthenticationManager();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .antMatchers("/ldap/**", "/page/**", "/api/loginDetails", "/api/ticketinfo", "/api/filterticket", "/api/updateticket", "/api/departmentinfo", "/api/queryfields").permitAll()
                .and()
                .formLogin().disable() // disable form authentication
                .antMatcher("/api/**")
                .anonymous().disable() // disable anonymous user
                //.and()
                .authorizeRequests().anyRequest().denyAll(); // denying all access

    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {


        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new StandardPasswordEncoder("secret"));

        // auth.authenticationProvider(remedyAuthenticationProvider);
        auth.eraseCredentials(false);
    }


}

