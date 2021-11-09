package com.opabs.trustchain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BadRequestException {
    public ForbiddenException() {
        super("Forbidden", ErrorCode.FORBIDDEN);
    }
}
