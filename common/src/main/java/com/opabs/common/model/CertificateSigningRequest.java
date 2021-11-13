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
     * The issuer private key alias will point to the issuer private key stored in the hsm.
     * For self signed certificate the issuer private key alias will be same as the private key's alias.
     *
     */
    private String issuerPrivateKeyAlias;

    /**
     * Issuer Certificate as PEM. Only required when issuing a non root certificate.
     * Used to retrieve the DN of the issuer. But in case of root certificate, the
     * certificate is self signed and the issuer DN is same as the subject DN.
     */
    private String issuerCertificate;

    /**
     * The certificate serial number.
     */
    private Long serial = 1L;

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
