package com.hireflow.backend.controller;

import com.hireflow.backend.dto.SystemRoleResponse;
import com.hireflow.backend.security.AcademyRoles;
import com.hireflow.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Aktif sistem rolleri (GNL_TP SHRT_CODE). */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final UserService userService;

    public RoleController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize(AcademyRoles.ADMIN)
    public ResponseEntity<List<SystemRoleResponse>> listActiveRoles() {
        return ResponseEntity.ok(userService.listActiveRoles());
    }
}
