package com.opabs.tenent.management.exception.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadRequest {

    private String errorCode;

    private String message;

}
