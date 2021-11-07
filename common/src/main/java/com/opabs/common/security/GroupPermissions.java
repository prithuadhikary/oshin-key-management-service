package com.opabs.common.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public enum GroupPermissions {
    OPABS_ADMIN(
            List.of(
                    Permissions.TENANT_CREATE,
                    Permissions.TENANT_DELETE,
                    Permissions.TENANT_EDIT,
                    Permissions.TENANT_VIEW,
                    Permissions.TRUST_CHAIN_CREATE,
                    Permissions.TRUST_CHAIN_EDIT,
                    Permissions.TRUST_CHAIN_VIEW,
                    Permissions.CERTIFICATE_CREATE,
                    Permissions.CERTIFICATE_VIEW
            ), "opabs-admin"),
    TENANT_ADMIN(
            List.of(
                    Permissions.TENANT_VIEW,
                    Permissions.TRUST_CHAIN_CREATE,
                    Permissions.TRUST_CHAIN_EDIT,
                    Permissions.TRUST_CHAIN_VIEW,
                    Permissions.CERTIFICATE_CREATE,
                    Permissions.CERTIFICATE_VIEW
            ), "tenant-admin"
    );

    private final String groupName;

    private final List<String> authorities;

    GroupPermissions(List<String> authorities, String groupName) {
        this.authorities = authorities;
        this.groupName = groupName;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public static GroupPermissions getByGroupName(List<String> groupNames) {
        for (String groupName : groupNames) {
            if (OPABS_ADMIN.groupName.equals(groupName)) {
                return OPABS_ADMIN;
            } else if (TENANT_ADMIN.groupName.equals(groupName)) {
                return TENANT_ADMIN;
            }
        }
        return null;
    }
}
