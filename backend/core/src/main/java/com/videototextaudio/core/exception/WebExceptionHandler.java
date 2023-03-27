package com.videototextaudio.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException e) {
        log.error("Service error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FatalApplicationException.class)
    public ResponseEntity<Object> handleFatalApplicationException(FatalApplicationException e) {
        log.error("Service error: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<Object> handleInvalidContentTypeException(InvalidContentTypeException e) {
        log.error("Service error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
