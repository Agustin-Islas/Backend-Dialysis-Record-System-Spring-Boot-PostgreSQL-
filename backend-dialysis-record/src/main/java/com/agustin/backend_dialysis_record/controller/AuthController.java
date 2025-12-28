package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.auth.AuthResponse;
import com.agustin.backend_dialysis_record.dto.auth.LoginRequest;
import com.agustin.backend_dialysis_record.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
