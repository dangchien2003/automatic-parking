package com.automaticparking.exception;

import com.automaticparking.types.ResponseException;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Log4j
public class GlobalExceptionHandler extends BaseError {

    @ExceptionHandler({Exception.class, LogicException.class})
    ResponseEntity<?> server(Exception ex) {
        log.error(ex.getMessage(), ex);
        System.out.println("server error: " + ex.getMessage());
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
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ExceptionHandler({InvalidException.class, BadRequestException.class, ResponseException.class})
    public ResponseEntity<Object> badRequest(RuntimeException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, NoHandlerFoundException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(setError(status, ex.getMessage()), status);
    }
}
