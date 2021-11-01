package com.opabs.trustchain.advice;

import com.opabs.trustchain.exception.BadRequestException;
import com.opabs.trustchain.exception.ErrorCode;
import com.opabs.trustchain.exception.model.BadRequest;
import com.opabs.trustchain.exception.model.InternalServerError;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlers {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<InternalServerError> handleGenericException(Exception ex) {
        log.error("Error occurred:", ex);
        return ResponseEntity.internalServerError().body(new InternalServerError());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequest> handleBadRequest(BadRequestException ex) {
        log.error("Error occurred:", ex);
        return ResponseEntity.badRequest().body(
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<BadRequest>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<BadRequest> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
                .map(message -> BadRequest.builder().message(message).errorCode(ErrorCode.BAD_REQUEST.name()).build())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<BadRequest> handleFeignException(FeignException exception) {
        return ResponseEntity.badRequest().body(BadRequest.builder().errorCode(ErrorCode.BAD_REQUEST.name())
                .message(exception.getMessage()).build());
    }

}
