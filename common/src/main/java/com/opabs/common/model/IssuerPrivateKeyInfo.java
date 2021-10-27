package com.opabs.common.model;

import lombok.Data;

import java.security.PrivateKey;

@Data
public class IssuerPrivateKeyInfo {

    private PrivateKey privateKey;

    private KeyType keyType;

}
