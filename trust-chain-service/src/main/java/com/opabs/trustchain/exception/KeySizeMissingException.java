package com.opabs.trustchain.exception;

public class KeySizeMissingException extends BadRequestException {

    public KeySizeMissingException() {
        super("keySize not specified.", ErrorCode.KEY_SIZE_MISSING);
    }
}
