package com.iprody.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<CustomErrorResponse> handleApplicationException(ApplicationException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new CustomErrorResponse(ex.getMessage()));
    }

}
