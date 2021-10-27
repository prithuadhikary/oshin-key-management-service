package com.opabs.common.model;

public enum KeyType {
    RSA("RSA", "SHA256withRSA"),
    ELLIPTIC_CURVE("EC", "SHA256withECDSA");

    private final String generatorName;
    private final String certificateSignatureAlgo;

    KeyType(String generatorName, String certificateSignatureAlgo) {
        this.certificateSignatureAlgo = certificateSignatureAlgo;
        this.generatorName = generatorName;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public String getCertificateSignatureAlgo() {
        return certificateSignatureAlgo;
    }
}
