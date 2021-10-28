package com.opabs.tenent.management.advice;

import com.opabs.tenent.management.exception.BadRequestException;
import com.opabs.tenent.management.exception.ErrorCode;
import com.opabs.tenent.management.exception.model.BadRequest;
import com.opabs.tenent.management.exception.model.InternalServerError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequest> handleInvalidArgument(MethodArgumentNotValidException exception) {
        String errors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(
                BadRequest
                        .builder()
                        .errorCode(ErrorCode.BAD_REQUEST.name())
                        .message(errors)
                        .build()
        );
    }


}
