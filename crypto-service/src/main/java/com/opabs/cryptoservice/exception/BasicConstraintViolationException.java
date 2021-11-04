package com.opabs.cryptoservice.exception;

public class BasicConstraintViolationException extends BadRequestException {

    public BasicConstraintViolationException() {
        super(
                "Can not issue non root CA certificate as the path length constraint is violated. Max path length constraint crossed.",
                ErrorCode.MAX_PATH_LENGTH_CONSTRAINT_CROSSED
        );
    }

}
