package com.htc.remedy.oauth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;


@Configuration
@EnableAuthorizationServer
public class OAuthServer extends AuthorizationServerConfigurerAdapter {

    @Qualifier("userDetailsService")
    private final UserDetailsService userDetailsService;

    @Value("${oauth.tokenTimeout}")
    private int expiration;

    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;


    public OAuthServer(UserDetailsService userDetailsService, AuthenticationManager authenticationManager, DataSource dataSource,RedisConnectionFactory redisConnectionFactory) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.redisConnectionFactory = redisConnectionFactory;
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()

                .withClient("ctssp")
                .secret("secret")
                .scopes("read", "write", "remove")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                .redirectUris("http://localhost:8080")
                .autoApprove(true)

                .and()
                .withClient("workflow")
                .secret("secret")
                .scopes("read", "write", "remove")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
                .redirectUris("http://localhost:8080")
                .autoApprove(true);


         //
    }


    @Bean
    public WebResponseExceptionTranslator loggingExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                // This is the line that prints the stack trace to the log. You can customise this to format the trace etc if you like
                e.printStackTrace();

                // Carry on handling the exception
                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
                HttpHeaders headers = new HttpHeaders();
                headers.setAll(responseEntity.getHeaders().toSingleValueMap());
                OAuth2Exception excBody = responseEntity.getBody();
                return new ResponseEntity<>(excBody, headers, responseEntity.getStatusCode());
            }
        };
    }

    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore tokenStore = new EnhancedTokenStore(redisConnectionFactory);
        tokenStore.setAuthenticationKeyGenerator(new CustomAuthenticationKeyGenerator());
        return tokenStore;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .pathMapping("/oauth/authorize", "/auth/v1/authorize")
                .pathMapping("/oauth/token", "/auth/v1/token")
                .tokenStore(tokenStore())
                .tokenServices(tokenServices())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .exceptionTranslator(loggingExceptionTranslator());
    }

    @Bean
    @Primary
    public CustomTokenStore tokenServices() {
        CustomTokenStore defaultTokenServices = new CustomTokenStore();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds(expiration);
        defaultTokenServices.setRefreshTokenValiditySeconds(expiration * 10);
        return defaultTokenServices;
    }
}
