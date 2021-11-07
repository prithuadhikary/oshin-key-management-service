package com.opabs.tenent.management.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JWTAuthToken extends UsernamePasswordAuthenticationToken {

    private final AccessToken accessToken;

    public JWTAuthToken(AccessToken accessToken, Collection<? extends GrantedAuthority> authorities) {
        super(
                new User(accessToken.getSub(), "", authorities),
                "",
                authorities
        );
        this.accessToken = accessToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
