package com.opabs.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * The GenerateCSRResponse object contains the wrapped private key generated and
 * the certificate signing request in PKCS10 format.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateCSRResponse {

    /**
     * The key alias of the generated private key.
     */
    private String privateKeyAlias;

    /**
     * The Certificate Signing Request in PKCS10 format.
     */
    private String pkcs10CSR;

}
