package com.opabs.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JWTAuthToken extends UsernamePasswordAuthenticationToken {

    private final AccessToken accessToken;

    private final GroupPermissions group;

    public JWTAuthToken(AccessToken accessToken, GroupPermissions group, Collection<? extends GrantedAuthority> authorities) {
        super(
                new User(accessToken.getSub(), "", authorities),
                "",
                authorities
        );
        this.accessToken = accessToken;
        this.group = group;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public GroupPermissions getGroup() {
        return group;
    }
}
