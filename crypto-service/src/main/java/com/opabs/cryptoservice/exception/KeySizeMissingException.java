package com.opabs.cryptoservice.exception;

public class KeySizeMissingException extends BadRequestException {
    public KeySizeMissingException() {
        super("Parameter 'keySize' not specified.", ErrorCode.KEY_SIZE_MISSING);
    }
}
