package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.auth.*;
import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import com.agustin.backend_dialysis_record.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/register/doctor")
    public AuthResponse registerDoctor(@Valid @RequestBody RegisterDoctorRequest req) {
        return authService.registerDoctor(req);
    }

    @PostMapping("/register/patient")
    public AuthResponse registerPatient(@Valid @RequestBody RegisterPatientRequest req) {
        return authService.registerPatient(req);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(Authentication authentication) {
        UserAccount user = (UserAccount) authentication.getPrincipal();
        authService.logoutAll(user);
        return ResponseEntity.noContent().build();
    }


}
