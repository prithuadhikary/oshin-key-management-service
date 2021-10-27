package com.opabs.common.enums;

import com.opabs.common.model.KeyType;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.List;

public enum KeyUsages {
    KEY_AGREEMENT(KeyUsage.keyAgreement, List.of(KeyType.ELLIPTIC_CURVE)),
    KEY_CERT_SIGN(KeyUsage.keyCertSign, List.of(KeyType.ELLIPTIC_CURVE, KeyType.RSA)),
    KEY_ENCIPHERMENT(KeyUsage.keyEncipherment, List.of(KeyType.RSA)),
    DATA_ENCIPHERMENT(KeyUsage.dataEncipherment, List.of(KeyType.RSA)),
    DECIPHER_ONLY(KeyUsage.decipherOnly, List.of(KeyType.RSA)),
    ENCIPHER_ONLY(KeyUsage.encipherOnly, List.of(KeyType.RSA)),
    DIGITAL_SIGNATURE(KeyUsage.digitalSignature, List.of(KeyType.RSA, KeyType.ELLIPTIC_CURVE)),
    NON_REPUDIATIUON(KeyUsage.nonRepudiation, List.of(KeyType.RSA, KeyType.ELLIPTIC_CURVE));

    private final int keyUsage;

    private final List<KeyType> supportedKeyTypes;

    KeyUsages(int keyUsage, List<KeyType> supportedKeyTypes) {
        this.keyUsage = keyUsage;
        this.supportedKeyTypes = supportedKeyTypes;
    }

    public int getKeyUsage() {
        return keyUsage;
    }

    public List<KeyType> getSupportedKeyTypes() {
        return supportedKeyTypes;
    }
}
