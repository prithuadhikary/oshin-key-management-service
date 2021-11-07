package com.opabs.common.exceptions;

import org.springframework.security.core.AuthenticationException;

import java.util.List;

public class GroupInvalidException extends AuthenticationException {

    public GroupInvalidException(List<String> groupNames) {
        super("Group names " + groupNames + " found in access token do not match any valid group names.");
    }
}
