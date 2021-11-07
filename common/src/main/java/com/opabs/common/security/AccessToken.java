package com.opabs.common.security;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AccessToken {

    private List<String> groups;

    private UUID tenantIdentifier;

    private String sub;

}
