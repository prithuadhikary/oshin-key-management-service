package com.opabs.trustchain.exception.model;

import com.opabs.trustchain.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InternalServerError {

    private final String errorCode = ErrorCode.INTERNAL_SERVER_ERROR.name();

    private final String message = "Internal Server Error";

}
