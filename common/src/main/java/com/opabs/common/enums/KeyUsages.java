package com.opabs.common.enums;

import org.bouncycastle.asn1.x509.KeyUsage;

public enum KeyUsages {
    KEY_AGREEMENT(KeyUsage.keyAgreement),
    KEY_CERT_SIGN(KeyUsage.keyCertSign),
    KEY_ENCIPHERMENT(KeyUsage.keyEncipherment),
    DATA_ENCIPHERMENT(KeyUsage.dataEncipherment),
    DECIPHER_ONLY(KeyUsage.decipherOnly),
    ENCIPHER_ONLY(KeyUsage.encipherOnly),
    DIGITAL_SIGNATURE(KeyUsage.digitalSignature),
    NON_REPUDIATIUON(KeyUsage.nonRepudiation);

    private final int keyUsage;

    KeyUsages(int keyUsage) {
        this.keyUsage = keyUsage;
    }

    public int getKeyUsage() {
        return keyUsage;
    }
}
