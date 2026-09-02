package com.hireflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * Public endpoint - Herkese açık, token gerektirmez
     */
    @GetMapping("/public")
    public ResponseEntity<String> publicEndpoint() {
        return ResponseEntity.ok("Public endpoint çalışıyor");
    }

    /**
     * Private endpoint - JWT token gerektirir
     * Token'ın geçerli olduğunu doğrular ve subject (user UUID) döner
     */
    @GetMapping("/private")
    public ResponseEntity<Map<String, Object>> privateEndpoint(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Private endpoint çalışıyor - Token geçerli!");
        response.put("subject", jwt.getSubject()); // User UUID
        response.put("tokenId", jwt.getId());
        response.put("issuedAt", jwt.getIssuedAt());
        response.put("expiresAt", jwt.getExpiresAt());
        
        return ResponseEntity.ok(response);
    }
}
