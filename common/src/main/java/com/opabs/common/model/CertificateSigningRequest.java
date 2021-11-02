package com.opabs.common.model;

import com.opabs.common.enums.KeyUsages;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Used to sign and provision an X509 certificate from a Certificate Signing Request with mentioned validity and
 * key usages.
 */
@Data
public class CertificateSigningRequest {

    /**
     * PKCS10 certificate signing request containing subject public key information and subject information etc.
     */
    @NotEmpty
    private String pkcs10CSR;

    /**
     * The date onwards of which the signed certificate will be valid.
     */
    private OffsetDateTime validFrom;

    /**
     * Validity of the certificate in years.
     */
    @Min(value = 1)
    @NotNull
    private Integer validityInYears;

    /**
     * List of key usages the certificate is being provisioned for.
     */
    private List<KeyUsages> keyUsages;

    /**
     * If true, will generate a self signed certificate.
     */
    private boolean selfSigned;

    /**
     * For generation of self signed certificate,the wrapped private key must be passed to sign the sign the CSR.
     * This is for the use cases, where we need a root CA certificate.
     */
    @NotEmpty
    private String wrappedIssuerPrivateKey;

    /**
     * Issuer Certificate as PEM.
     */
    private String issuerCertificate;

    /**
     * The key alias identifying the key to use to unwrap the wrapped key.
     */
    @NotEmpty
    private String unwrappingKeyAlias;

    /**
     * The certificate serial number.
     */
    private Integer serial = 1;

    /**
     * The certificate signing algorithm.
     */
    @NotNull
    private SigningAlgorithm signatureAlgorithm;

    /**
     * The path length constraint gets added as an extension to the certificate being generated in
     * case the key usage contains keyCertSign and cRLSign bit on.
     */
    private Integer pathLengthConstraint;

}
