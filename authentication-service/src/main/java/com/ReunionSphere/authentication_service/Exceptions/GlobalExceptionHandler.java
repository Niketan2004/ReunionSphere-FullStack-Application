package com.ReunionSphere.authentication_service.Exceptions;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

     @ExceptionHandler(UserAlreadyExistsException.class)
     public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
          log.error("UserAlreadyExistsException: {}", ex.getMessage());
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.CONFLICT.value())
                    .error(HttpStatus.CONFLICT.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
     }

     @ExceptionHandler(BadCredentialsException.class)
     public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
          log.error("BadCredentialsException: {}", ex.getMessage());
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Invalid email or password")
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
     }

     @ExceptionHandler(UsernameNotFoundException.class)
     public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
          log.error("UsernameNotFoundException: {}", ex.getMessage());
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
          log.error("IllegalArgumentException: {}", ex.getMessage());
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .message(ex.getMessage())
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
     }

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
          String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
          log.error("Validation error: {}", errorMessage);
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Validation Failed")
                    .message(errorMessage)
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
     }

     @ExceptionHandler(Exception.class)
     public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
          log.error("Unexpected exception occurred", ex);
          ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .message(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred")
                    .path(request.getDescription(false))
                    .build();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
     }
}
