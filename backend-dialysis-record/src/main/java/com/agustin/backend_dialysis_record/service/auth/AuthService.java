package com.agustin.backend_dialysis_record.service.auth;

import com.agustin.backend_dialysis_record.dto.auth.AuthResponse;
import com.agustin.backend_dialysis_record.dto.auth.LoginRequest;
import com.agustin.backend_dialysis_record.dto.auth.RegisterDoctorRequest;
import com.agustin.backend_dialysis_record.dto.auth.RegisterPatientRequest;
import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.model.auth.UserAccount;
import com.agustin.backend_dialysis_record.model.auth.UserRole;
import com.agustin.backend_dialysis_record.repository.DoctorRepository;
import com.agustin.backend_dialysis_record.repository.PatientRepository;
import com.agustin.backend_dialysis_record.repository.UserAccountRepository;
import com.agustin.backend_dialysis_record.security.jwt.JwtService;
import com.agustin.backend_dialysis_record.security.jwt.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    public AuthService(UserAccountRepository userAccountRepository, PatientRepository patientRepo, DoctorRepository doctorRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userAccountRepository = userAccountRepository;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        UserAccount acc = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!acc.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account disabled");
        }

        boolean ok = passwordEncoder.matches(req.getPassword(), acc.getPasswordHash());
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateAccessToken(acc);
        var issuedRefresh = refreshTokenService.issue(acc);

        return new AuthResponse(token, issuedRefresh.plainToken());
    }

    public AuthResponse refresh(String refreshTokenPlain) {
        var rotated = refreshTokenService.rotate(refreshTokenPlain);

        String access = jwtService.generateAccessToken(rotated.entity().getUserAccount());
        return new AuthResponse(access, rotated.plainToken());
    }

    public AuthResponse registerDoctor(RegisterDoctorRequest req) {
        if (userAccountRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        Doctor doctor = new Doctor();
        doctor.setName(req.name());
        doctor.setSurname(req.surname());
        doctor = doctorRepo.save(doctor);

        UserAccount ua = new UserAccount();
        ua.setEmail(req.email().trim().toLowerCase());
        ua.setPasswordHash(passwordEncoder.encode(req.password()));
        ua.setRole(UserRole.DOCTOR);
        ua.setDoctor(doctor);
        userAccountRepository.save(ua);

        String token = jwtService.generateAccessToken(ua); // incluye role + id
        String refresh = refreshTokenService.issue(ua).plainToken();

        return new AuthResponse(token, refresh);
    }

    public AuthResponse registerPatient(RegisterPatientRequest req) {
        if (userAccountRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        Patient patient = new Patient();
        patient.setName(req.name());
        patient.setSurname(req.surname());
        patient.setDni(req.dni());
        patient.setDateOfBirth(req.dateOfBirth());
        patient.setAddress(req.address());
        patient.setNumber(req.number());
        patient = patientRepo.save(patient);

        UserAccount ua = new UserAccount();
        ua.setEmail(req.email());
        ua.setPasswordHash(passwordEncoder.encode(req.password()));
        ua.setRole(UserRole.PATIENT);
        ua.setPatient(patient);
        userAccountRepository.save(ua);

        String token = jwtService.generateAccessToken(ua); // incluye role + id
        String refresh = refreshTokenService.issue(ua).plainToken();

        return new AuthResponse(token, refresh);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public void logoutAll(UserAccount userAccount) {
        refreshTokenService.revokeAll(userAccount);
    }

}
