package com.xpguardian.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/protected")
@Tag(name = "Protected Resources")
@SecurityRequirement(name = "bearerAuth") // Exige autenticação para todos os endpoints neste controller
public class ProtectedController {

    @GetMapping("/user-data")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')") // Acessível por USER e ADMIN
    @Operation(summary = "Get user-specific data", description = "A protected endpoint for regular users.")
    public ResponseEntity<String> getUserData() {
        return ResponseEntity.ok("Hello User! This is your protected data.");
    }

    @GetMapping("/admin-data")
    @PreAuthorize("hasAuthority('ADMIN')") // Acessível apenas por ADMIN
    @Operation(summary = "Get admin-specific data", description = "A protected endpoint only for administrators.")
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("Hello Admin! This is the secret admin panel.");
    }
}