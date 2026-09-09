package com.hireflow.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/** JWT / rol erişimini denemek için geçici test uçları. */
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    /**
     * Public endpoint - Herkese açık, token gerektirmez
     */
    @GetMapping("/public")
    public String testPublic() {
        return "Public endpoint calisiyor!";
    }

    /**
     * Private endpoint - JWT token gerektirir
     * Token'ın geçerli olduğunu doğrular ve subject (user UUID) döner
     */
    @GetMapping("/private")
    public ResponseEntity<Map<String, Object>> testPrivate(
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        System.out.println("=== TEST CONTROLLER /private ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Authentication class: " + (authentication != null ? authentication.getClass().getName() : "null"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Private endpoint çalışıyor - Token geçerli!");
        response.put("subject", jwt.getSubject());
        response.put("email", jwt.getClaimAsString("email"));
        
        // Authorities'i Authentication object'inden al (JWT claim'inden değil!)
        if (authentication != null) {
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .collect(Collectors.toList()));
            System.out.println("Authorities from Authentication: " + authentication.getAuthorities());
        } else {
            response.put("authorities", null);
            System.out.println("Authentication is NULL!");
        }
        
        System.out.println("================================");
        
        return ResponseEntity.ok(response);
    }

    /**
     * CAND rolü testi - Sadece CAND rolüne sahip kullanıcılar erişebilir
     */
    @GetMapping("/cand")
    @PreAuthorize("hasRole('CAND')")
    public ResponseEntity<Map<String, Object>> testCandRole(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CAND rolü test endpoint'i - Erişim başarılı!");
        response.put("email", jwt.getClaimAsString("email"));
        response.put("role", "CAND");
        
        return ResponseEntity.ok(response);
    }

    /**
     * HR rolü testi - Sadece HR rolüne sahip kullanıcılar erişebilir
     */
    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, Object>> testHrRole(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "HR rolü test endpoint'i - Erişim başarılı!");
        response.put("email", jwt.getClaimAsString("email"));
        response.put("role", "HR");
        
        return ResponseEntity.ok(response);
    }
}
