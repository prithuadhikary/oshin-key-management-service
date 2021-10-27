package com.opabs.common.model;

import lombok.Data;

/**
 * The GenerateCSRResponse object contains the wrapped private key generated and
 * the certificate signing request in PKCS10 format.
 */
@Data
public class GenerateCSRResponse {

    /**
     * The wrapped private key.
     */
    private String wrappedKey;

    /**
     * The Certificate Signing Request in PKCS10 format.
     */
    private String pkcs10CSR;

}
