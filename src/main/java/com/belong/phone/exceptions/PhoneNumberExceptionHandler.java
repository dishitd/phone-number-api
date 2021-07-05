package com.belong.phone.exceptions;

import com.belong.phone.models.ApiError;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@NoArgsConstructor
@Slf4j
public class PhoneNumberExceptionHandler {

    ApiErrorBuilder apiErrorBuilder;

    public PhoneNumberExceptionHandler(ApiErrorBuilder apiErrorBuilder) {
        this.apiErrorBuilder = apiErrorBuilder;
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiError> handleDataNotFound(DataNotFoundException exception, HttpServletRequest request) {
        return new ResponseEntity<>(ApiErrorBuilder.buildErrorResponse(ApiError.builder()
                        .errorId(HttpStatus.NOT_FOUND.toString())
                        .message(exception.getMessage())
                        .build())
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.error("Server Error: {}", exception.getMessage());
        return new ResponseEntity<>(ApiErrorBuilder.buildErrorResponse(ApiError.builder()
                .errorId(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message("INTERNAL SERVER ERROR")
                .build())
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
