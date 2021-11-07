package com.opabs.tenent.management.exception;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenInvalidException extends AuthenticationException {

    public AccessTokenInvalidException() {
        super("Access token invalid.");
    }
}
