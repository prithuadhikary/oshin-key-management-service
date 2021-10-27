package com.opabs.cryptoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadTagException extends BadRequestException {

    public BadTagException() {
        super("Bad AEAD tag encountered.", ErrorCode.BAD_AEAD_TAG);
    }
}
