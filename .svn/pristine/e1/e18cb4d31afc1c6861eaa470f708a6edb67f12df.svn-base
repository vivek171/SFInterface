package com.htc.remedy.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

public class CustomTokenStore extends DefaultTokenServices {

    @Autowired
    TokenStore tokenStore;

    @Override
    public synchronized OAuth2AccessToken createAccessToken(OAuth2Authentication authentication)
            throws AuthenticationException {
        return super.createAccessToken(authentication);
    }

    @Override
    @Transactional(noRollbackFor = {InvalidTokenException.class, InvalidGrantException.class})
    public synchronized OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {
        return super.refreshAccessToken(refreshTokenValue, tokenRequest);
    }

}
