package com.opabs.cryptoservice.exception;

public class KeyLabelMissingException extends BadRequestException {
    public KeyLabelMissingException() {
        super("Parameter 'keyLabel' not specified", ErrorCode.KEY_LABEL_MISSING);
    }
}
