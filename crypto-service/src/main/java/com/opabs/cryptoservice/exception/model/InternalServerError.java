package com.opabs.cryptoservice.exception.model;

import com.opabs.cryptoservice.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InternalServerError {

    private final String errorCode = ErrorCode.INTERNAL_SERVER_ERROR.name();

    private final String message = "Internal Server Error";

}
