package com.lzbz.auth.config;

import com.lzbz.auth.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorDTO>> handleValidationErrors(WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation error: {}", errorMessage);
        return Mono.just(
                ResponseEntity.badRequest()
                        .body(ErrorDTO.builder()
                                .message(errorMessage)
                                .code("VALIDATION_ERROR")
                                .build())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorDTO>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ErrorDTO.builder()
                                .message(ex.getMessage())
                                .code("INTERNAL_ERROR")
                                .build())
        );
    }
}
