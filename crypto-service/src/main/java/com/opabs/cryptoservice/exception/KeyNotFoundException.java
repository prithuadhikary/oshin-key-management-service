package com.opabs.cryptoservice.exception;

import java.text.MessageFormat;

public class KeyNotFoundException extends BadRequestException {

    public KeyNotFoundException(String keyAlias) {
        super(MessageFormat.format("Key alias {1} not found", keyAlias), ErrorCode.KEY_NOT_FOUND);
    }
}
