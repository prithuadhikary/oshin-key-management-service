package com.opabs.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthToken extends AbstractAuthenticationToken {

    private final AccessToken accessToken;

    private final GroupPermissions group;

    public JWTAuthToken(AccessToken accessToken, GroupPermissions group, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.accessToken = accessToken;
        this.group = group;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return accessToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public GroupPermissions getGroup() {
        return group;
    }
}
