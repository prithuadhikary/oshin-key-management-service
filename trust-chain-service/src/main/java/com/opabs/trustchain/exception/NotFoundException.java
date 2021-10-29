package com.opabs.trustchain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BadRequestException {

    public NotFoundException(String entity, UUID id) {
        super(
                MessageFormat.format("Entity {0} with id {1} not found.", entity, id),
                ErrorCode.NOT_FOUND
        );
    }
}
