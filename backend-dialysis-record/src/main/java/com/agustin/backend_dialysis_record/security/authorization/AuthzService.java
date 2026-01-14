package com.agustin.backend_dialysis_record.security.authorization;

import com.agustin.backend_dialysis_record.repository.PatientRepository;
import com.agustin.backend_dialysis_record.repository.SessionRepository;
import com.agustin.backend_dialysis_record.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("authz")
public class AuthzService {

    private final UserAccountRepository userAccountRepository;
    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;

    public AuthzService(UserAccountRepository userAccountRepository,
                        PatientRepository patientRepository,
                        SessionRepository sessionRepository) {
        this.userAccountRepository = userAccountRepository;
        this.patientRepository = patientRepository;
        this.sessionRepository = sessionRepository;
    }

    public boolean canAccessDoctor(UUID doctorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return false;

        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());

        boolean isAdmin = has(auth, "ROLE_ADMIN");
        if (isAdmin) return true;

        boolean isDoctor = has(auth, "ROLE_DOCTOR");
        if (!isDoctor) return false;

        // ownership: el doctor asociado a ese userAccount es este doctorId
        return userAccountRepository.existsByIdAndDoctor_Id(userAccountId, doctorId);
    }

    public boolean canAccessPatient(UUID patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return false;

        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());

        boolean isAdmin = has(auth, "ROLE_ADMIN");
        if (isAdmin) return true;

        boolean isDoctor = has(auth, "ROLE_DOCTOR");
        boolean isPatient = has(auth, "ROLE_PATIENT");

        if (isDoctor) {
            UUID doctorId = userAccountRepository.findDoctorIdByUserAccountId(userAccountId).orElse(null);
            if (doctorId == null) return false;
            return patientRepository.existsByIdAndDoctor_Id(patientId, doctorId);
        }

        if (isPatient) {
            // ownership vía UserAccount -> patient (sin tocar Patient)
            return userAccountRepository.existsByIdAndPatient_Id(userAccountId, patientId);
        }

        return false;
    }

    public boolean canAccessSession(UUID sessionId) {
        UUID patientId = sessionRepository.findPatientIdBySessionId(sessionId).orElse(null);
        if (patientId == null) return false;
        return canAccessPatient(patientId);
    }

    private boolean has(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
