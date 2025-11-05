package com.technicalchallenge.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(cz.jirutka.rsql.parser.RSQLParserException.class)
    public ResponseEntity<?> handleRsqlParserException(cz.jirutka.rsql.parser.RSQLParserException ex) {
        return ResponseEntity
            .badRequest()
            .body("Invalid RSQL syntax: " + ex.getCause().getMessage());
    }

    @ExceptionHandler(io.github.perplexhub.rsql.UnknownPropertyException.class)
    public ResponseEntity<?> handleUnknownProperty(io.github.perplexhub.rsql.UnknownPropertyException ex) {
        return ResponseEntity.badRequest().body(
        "Invalid RSQL field: " + ex.getMessage() +
        " — check field names");
    }

    @ExceptionHandler(TradeAuthorizationException.class)
        public ResponseEntity<String> handleTradeAuthorizationException(TradeAuthorizationException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }

    @ExceptionHandler(TradeValidationException.class)
    public ResponseEntity<String> handleTradeValidationException(TradeValidationException ex) {
        return ResponseEntity.status(422).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body("Error creating trade: " + ex.getMessage());
    }
}
