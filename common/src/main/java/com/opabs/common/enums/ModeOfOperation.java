package com.opabs.common.enums;

public enum ModeOfOperation {
    CBC("AES/CBC/PKCS5Padding"),
    GCM("AES/GCM/NoPadding");

    private final String modeOfOperation;

    ModeOfOperation(String modeOfOperation) {
        this.modeOfOperation = modeOfOperation;
    }

    public String modeOfOperation() {
        return modeOfOperation;
    }
}
