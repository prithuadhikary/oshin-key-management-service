package com.opabs.cryptoservice.exception;

import com.opabs.common.model.KeyType;
import com.opabs.common.model.SigningAlgorithm;

import java.text.MessageFormat;

public class SigningAlgorithmAndSigningKeyMismatchException extends BadRequestException {

    public SigningAlgorithmAndSigningKeyMismatchException(SigningAlgorithm signingAlgorithm, KeyType mismatchedKeyType) {
        super(
                MessageFormat.format("Signing algorithm {0} does not match the supported issuer key type {1}.", signingAlgorithm, mismatchedKeyType),
                ErrorCode.SIGNING_ALGO_ISSUER_KEY_TYPE_MISMATCH
        );
    }
}
