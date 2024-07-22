package com.automaticparking.exception;

import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j
public class GlobalExceptionHandler extends BaseError {

    @ExceptionHandler({Exception.class, LogicException.class})
    ResponseEntity<?> server(Exception ex) {
        log.error(ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(setError(status, "An error occurred"), status);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> conflict(RuntimeException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ExceptionHandler(AuthorizedException.class)
    public ResponseEntity<Object> authorized(RuntimeException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFound(RuntimeException ex) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ExceptionHandler({InvalidException.class, BadRequestException.class})
    public ResponseEntity<Object> badRequest(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(setError(status, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()), status);
    }
}
