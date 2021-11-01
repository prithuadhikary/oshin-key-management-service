package com.opabs.common.enums;

import com.opabs.common.model.KeyType;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.List;

public enum KeyUsages {
    DIGITAL_SIGNATURE(KeyUsage.digitalSignature, List.of(KeyType.RSA, KeyType.ELLIPTIC_CURVE), 0),
    NON_REPUDIATION(KeyUsage.nonRepudiation, List.of(KeyType.RSA, KeyType.ELLIPTIC_CURVE), 1),
    KEY_ENCIPHERMENT(KeyUsage.keyEncipherment, List.of(KeyType.RSA), 2),
    DATA_ENCIPHERMENT(KeyUsage.dataEncipherment, List.of(KeyType.RSA), 3),
    KEY_AGREEMENT(KeyUsage.keyAgreement, List.of(KeyType.ELLIPTIC_CURVE), 4),
    KEY_CERT_SIGN(KeyUsage.keyCertSign, List.of(KeyType.ELLIPTIC_CURVE, KeyType.RSA), 5),
    CRL_SIGN(KeyUsage.cRLSign, List.of(KeyType.RSA, KeyType.ELLIPTIC_CURVE), 6),
    ENCIPHER_ONLY(KeyUsage.encipherOnly, List.of(KeyType.RSA), 7),
    DECIPHER_ONLY(KeyUsage.decipherOnly, List.of(KeyType.RSA), 8);

    private final int keyUsage;

    private final List<KeyType> supportedKeyTypes;

    private final int keyUsageByteIndex;

    KeyUsages(int keyUsage, List<KeyType> supportedKeyTypes, int keyUsageByteIndex) {
        this.keyUsage = keyUsage;
        this.supportedKeyTypes = supportedKeyTypes;
        this.keyUsageByteIndex = keyUsageByteIndex;
    }

    public int getKeyUsage() {
        return keyUsage;
    }

    public List<KeyType> getSupportedKeyTypes() {
        return supportedKeyTypes;
    }

    public int getKeyUsageByteIndex() {
        return keyUsageByteIndex;
    }
}
