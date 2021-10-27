package com.opabs.trustchain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BadRequestException {

    public NotFoundException() {
        super("Not found.", ErrorCode.NOT_FOUND);
    }
}
