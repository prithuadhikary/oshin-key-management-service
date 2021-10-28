package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.NamedCurve;
import com.opabs.common.enums.RSAKeySize;
import com.opabs.common.model.KeyType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GenerateCSRBase {

    private KeyType keyType;

    @NotEmpty
    private String subjectDistinguishedName;

    /**
     * To be used in case of RSA key type.
     */
    private RSAKeySize keySize;

    /**
     * To be used in case of elliptic curve key type.
     */
    private NamedCurve namedCurve;

}
