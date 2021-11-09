package com.opabs.trustchain.exception;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR,
    NOT_FOUND,
    BAD_REQUEST,
    KEY_SIZE_MISSING,
    NAMED_CURVE_MISSING,
    KEY_TYPE_USAGE_MISMATCH,
    PARENT_KEY_USAGE_INVALID,
    /**
     * This is thrown when a tenant admin tries to create a trust chain with tenant id other than that of his own.
     */
    TENANT_ID_MISMATCH,
    FORBIDDEN
}
