package com.meomulm.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ===================== 400 ===================== */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException e) {
        return ResponseEntity.badRequest()
                .body(buildErrorJson(400, "BAD_REQUEST", e.getMessage()));
    }

    /* ===================== 401 ===================== */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorJson(401, "UNAUTHORIZED", e.getMessage()));
    }

    /* ===================== 403 ===================== */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbidden(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorJson(403, "FORBIDDEN", e.getMessage()));
    }

    /* ===================== 404 ===================== */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorJson(404, "NOT_FOUND", e.getMessage()));
    }

    /* ===================== 500 ===================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleServerError(Exception e) {
        System.err.println("========== SERVER ERROR ==========");
        e.printStackTrace();
        System.err.println("==================================");

        return ResponseEntity.internalServerError()
                .body(buildErrorJson(
                        500,
                        "INTERNAL_SERVER_ERROR",
                        "서버 내부 오류가 발생했습니다."
                ));
    }

    /* ===================== 공통 JSON String 생성 ===================== */
    private String buildErrorJson(int status, String error, String message) {
        return String.format(
                "{ \"status\": %d, \"error\": \"%s\", \"message\": \"%s\" }",
                status, error, message
        );
    }
}
