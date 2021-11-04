package com.opabs.cryptoservice.exception;

import java.text.MessageFormat;

public class UnsupportedIssuerKeyTypeException extends BadRequestException {

    public UnsupportedIssuerKeyTypeException(String keyType) {
        super(MessageFormat.format("Unsupported issuer key type: {1}. Must be either RSA or EC.", keyType)
                , ErrorCode.UNSUPPORTED_ISSUER_KEYTYPE);
    }
}
