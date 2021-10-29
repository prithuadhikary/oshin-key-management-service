package com.opabs.trustchain.exception;

public class CurveNameMissingException extends BadRequestException {

    public CurveNameMissingException() {
        super("curveName not specified.", ErrorCode.CURVE_NAME_MISSING);
    }
}

