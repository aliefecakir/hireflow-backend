package com.hireflow.backend.controller;

import com.hireflow.backend.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

/** Tüm REST katmanındaki iş kuralı / not-found hatalarını JSON 400/404'e çevirir. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) { // 400
        return ResponseEntity.badRequest().body(Map.of("message", safeMessage(ex)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) { // 400
        return ResponseEntity.badRequest().body(Map.of("message", safeMessage(ex)));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException ex) { // 404
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", safeMessage(ex)));
    }

    /** Boş mesaj gelirse genel Türkçe metin döner. */
    private String safeMessage(Exception ex) {
        String message = ex.getMessage();
        return (message == null || message.isBlank()) ? "İşlem başarısız." : message;
    }
}
