package com.opabs.common.model;

import lombok.Data;

/**
 * Certificate signing response containing the issued X509 certificate.
 */
@Data
public class CertificateSigningResponse {

    /**
     * The issued X509 certificate.
     */
    private String certificate;

}
