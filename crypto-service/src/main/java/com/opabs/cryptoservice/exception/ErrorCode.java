package com.opabs.cryptoservice.exception;

public enum ErrorCode {
    BAD_AEAD_TAG,
    INTERNAL_SERVER_ERROR,
    KEY_SIZE_MISSING,
    NAMED_CURVE_MISSING,
    SIGNING_ALGO_ISSUER_KEY_TYPE_MISMATCH,
    CSR_SIGNATURE_INVALID,
    UNSUPPORTED_ISSUER_KEYTYPE,
    MAX_PATH_LENGTH_CONSTRAINT_CROSSED,
    BAD_REQUEST,
    KEY_LABEL_MISSING,
    KEY_NOT_FOUND
}
