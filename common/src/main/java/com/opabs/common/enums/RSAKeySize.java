package com.opabs.common.enums;

public enum RSAKeySize {
    LENGTH_1024(1024),
    LENGTH_2048(2048),
    LENGTH_3072(3072),
    LENGTH_4096(4096);

    private final int keyLength;

    RSAKeySize(int keyLength) {
        this.keyLength = keyLength;
    }

    public int getLength() {
        return keyLength;
    }
}
