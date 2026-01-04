package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.auth.AuthResponse;
import com.agustin.backend_dialysis_record.dto.auth.LoginRequest;
import com.agustin.backend_dialysis_record.dto.auth.RegisterDoctorRequest;
import com.agustin.backend_dialysis_record.dto.auth.RegisterPatientRequest;
import com.agustin.backend_dialysis_record.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register/doctor")
    public AuthResponse registerDoctor(@Valid @RequestBody RegisterDoctorRequest req) {
        return authService.registerDoctor(req);
    }

    @PostMapping("/register/patient")
    public AuthResponse registerPatient(@Valid @RequestBody RegisterPatientRequest req) {
        return authService.registerPatient(req);
    }
}
