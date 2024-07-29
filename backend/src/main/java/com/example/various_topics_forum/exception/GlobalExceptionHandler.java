package com.example.various_topics_forum.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.various_topics_forum.exception.exceptions.EmailException;
import com.example.various_topics_forum.exception.exceptions.UserAlreadyEnabledException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(String message, HttpStatus httpStatus) {
        return ErrorResponse.builder()
                .message(message != null ? message : "Unknown error")
                .status(httpStatus.value())
                .error(httpStatus.name())
                .timestamp(new Date())
                .build();
    }

    private ResponseEntity<ErrorResponse> buildResponseEntity(String message, HttpStatus httpStatus) {
        ErrorResponse errorResponse = buildErrorResponse(message, httpStatus);
        return ResponseEntity
                .status(httpStatus)
                .body(errorResponse);
    }

    @ExceptionHandler({JwtException.class, SignatureException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(Exception exception) {
        return buildResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({EmailException.class})
    public ResponseEntity<ErrorResponse> handleServerErrorExceptions(Exception exception) {
        return buildResponseEntity(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler({UserAlreadyEnabledException.class})
    public ResponseEntity<ErrorResponse> handleConflictExceptions(Exception exception) {
        return buildResponseEntity(exception.getMessage(), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler({EntityNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception exception) {
        return buildResponseEntity(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(Exception exception) {
        return buildResponseEntity("Database conflict", HttpStatus.CONFLICT);
    }
}
