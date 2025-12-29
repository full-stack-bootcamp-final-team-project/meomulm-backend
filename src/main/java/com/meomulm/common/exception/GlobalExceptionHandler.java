package com.meomulm.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerError(Exception e) {
        System.err.println("========== SERVER ERROR ==========");
        System.err.println("Error message: " + e.getMessage());
        System.err.println("==================================");

        return ResponseEntity.internalServerError()
                .body("서버 오류: " + e.getMessage());
    }
}