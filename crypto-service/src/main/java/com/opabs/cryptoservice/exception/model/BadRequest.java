package com.opabs.cryptoservice.exception.model;

import lombok.Data;

@Data
public class BadRequest {

    private String errorCode;

    private String message;

}
