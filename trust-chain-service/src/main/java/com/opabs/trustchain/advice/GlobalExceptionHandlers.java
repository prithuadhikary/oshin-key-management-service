package com.opabs.trustchain.advice;

import com.opabs.trustchain.exception.BadRequestException;
import com.opabs.trustchain.exception.ErrorCode;
import com.opabs.trustchain.exception.model.BadRequest;
import com.opabs.trustchain.exception.model.InternalServerError;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlers {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerError> handleGenericException(Exception ex) {
        log.error("Error occurred:", ex);
        return ResponseEntity.internalServerError().body(new InternalServerError());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequest> handleBadRequest(BadRequestException ex) {
        log.error("Error occurred:", ex);
        return ResponseEntity.internalServerError().body(
                BadRequest
                        .builder()
                        .message(ex.getMessage())
                        .errorCode(ex.getErrorCode().name())
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequest> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(
                BadRequest
                        .builder()
                        .errorCode(ErrorCode.BAD_REQUEST.name())
                        .message(exception.getMostSpecificCause().getMessage())
                        .build()
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BadRequest> handleFeignException(FeignException exception) {
        return ResponseEntity.badRequest().body(BadRequest.builder().errorCode(ErrorCode.BAD_REQUEST.name())
                .message(exception.getMessage()).build());
    }

}
