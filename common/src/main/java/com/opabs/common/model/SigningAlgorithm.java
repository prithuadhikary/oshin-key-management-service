package com.opabs.common.model;

public enum SigningAlgorithm {
    SHA1withRSA(KeyType.RSA),
    SHA256withRSA(KeyType.RSA),
    SHA256withECDSA(KeyType.ELLIPTIC_CURVE);

    private final KeyType supportedIssuerKeyType;

    SigningAlgorithm(KeyType supportedIssuerKeyType) {
        this.supportedIssuerKeyType = supportedIssuerKeyType;
    }

    public KeyType getSupportedIssuerKeyType() {
        return supportedIssuerKeyType;
    }
}
