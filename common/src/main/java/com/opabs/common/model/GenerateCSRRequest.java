package com.opabs.common.model;

import com.opabs.common.validator.DistinguishedName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * The GenerateCSRRequest contains the subject distinguished name to generate the certificate for,
 * the key type, representing the type of key to generate and any associated parameters required for
 * generation of the key.
 */
@Data
public class GenerateCSRRequest {

    /**
     * The subject distinguished name to generate the certificate signing request for.
     */
    @NotEmpty
    @DistinguishedName
    private String subjectDN;

    /**
     * The type of the key to generate.
     */
    @NotNull
    private KeyType keyType;

    /**
     * A map containing parameters required for generation of the key. Currently supports RSA and Elliptic Curves.
     */
    @NotNull
    private Map<String, Object> keyGenParams;

    /**
     * The alias which will be associated with the generated private key stored in HSM.
     */
    @NotEmpty
    private String privateKeyAlias;

}
