package com.opabs.cryptoservice.advice;

import com.opabs.cryptoservice.exception.BadRequestException;
import com.opabs.cryptoservice.exception.ErrorCode;
import com.opabs.cryptoservice.exception.model.BadRequest;
import com.opabs.cryptoservice.exception.model.InternalServerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlers {

    private final MessageSource messageSource;

    @ExceptionHandler(BadRequestException.class)
    public <T extends BadRequestException> ResponseEntity<BadRequest> handleBadRequestException(T exception) {
        log.error("Bad request exception:", exception);
        BadRequest badRequest = new BadRequest();
        badRequest.setMessage(exception.getMessage());
        badRequest.setErrorCode(exception.getErrorCode().name());
        return ResponseEntity.badRequest().body(badRequest);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerError> handleGenericException(Exception ex) {
        log.error("Error occurred:", ex);
        return ResponseEntity.internalServerError().body(new InternalServerError());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BadRequest> handleBindException(WebExchangeBindException exchangeBindException) {
        String allErrors = exchangeBindException
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : " + messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
                .collect(Collectors.joining(", "));
        BadRequest badRequest = new BadRequest();
        badRequest.setErrorCode(ErrorCode.BAD_REQUEST.name());
        badRequest.setMessage(allErrors);
        return ResponseEntity.badRequest().body(badRequest);
    }

}
