package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.NamedCurve;
import com.opabs.common.enums.RSAKeySize;
import com.opabs.common.model.KeyType;
import com.opabs.trustchain.validator.DistinguishedName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GenerateCSRBase {

    @NotNull
    private KeyType keyType;

    @NotEmpty
    @DistinguishedName
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
