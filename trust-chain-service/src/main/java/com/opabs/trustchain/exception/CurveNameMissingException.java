package com.opabs.trustchain.exception;

public class CurveNameMissingException extends BadRequestException {

    public CurveNameMissingException() {
        super("namedCurve not specified.", ErrorCode.NAMED_CURVE_MISSING);
    }
}

