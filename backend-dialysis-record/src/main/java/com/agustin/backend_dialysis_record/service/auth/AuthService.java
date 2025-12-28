package com.agustin.backend_dialysis_record.service.auth;

import com.agustin.backend_dialysis_record.dto.auth.AuthResponse;
import com.agustin.backend_dialysis_record.dto.auth.LoginRequest;
import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import com.agustin.backend_dialysis_record.repository.UserAccountRepository;
import com.agustin.backend_dialysis_record.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest req) {
        UserAccount acc = userAccountRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!acc.isEnabled()) {
            throw new RuntimeException("Account disabled");
        }

        boolean ok = passwordEncoder.matches(req.getPassword(), acc.getPasswordHash());
        if (!ok) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateAccessToken(acc);
        return new AuthResponse(token);
    }
}
