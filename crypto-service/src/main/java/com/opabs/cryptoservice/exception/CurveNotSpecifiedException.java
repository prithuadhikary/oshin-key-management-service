package com.opabs.cryptoservice.exception;

public class CurveNotSpecifiedException extends BadRequestException {
    public CurveNotSpecifiedException() {
        super("Parameter 'namedCurve' not specified.", ErrorCode.NAMED_CURVE_MISSING);
    }
}
