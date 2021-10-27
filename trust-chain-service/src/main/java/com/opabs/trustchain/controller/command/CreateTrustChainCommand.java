package com.opabs.trustchain.controller.command;

import com.opabs.common.enums.KeyUsages;
import com.opabs.common.enums.NamedCurve;
import com.opabs.common.enums.RSAKeySize;
import com.opabs.common.model.KeyType;
import com.opabs.common.model.SigningAlgorithm;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CreateTrustChainCommand {

    private String name;

    private String description;

    private String subjectDistinguishedName;

    private OffsetDateTime validFrom;

    private Integer validityInYears;

    private KeyType keyType;

    private List<KeyUsages> keyUsages;

    private SigningAlgorithm signatureAlgorithm;

    /**
     * To be used in case of RSA key type.
     */
    private RSAKeySize keySize;

    /**
     * To be used in case of elliptic curve key type.
     */
    private NamedCurve namedCurve;

}
