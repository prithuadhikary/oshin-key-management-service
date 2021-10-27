package com.opabs.cryptoservice.exception;

public class CSRSignatureInvalidException extends BadRequestException {

    public CSRSignatureInvalidException() {
        super("CSR signature invalid.", ErrorCode.CSR_SIGNATURE_INVALID);
    }
}
