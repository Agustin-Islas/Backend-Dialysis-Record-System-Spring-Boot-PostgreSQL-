package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.dto.PatientMeDto;
import com.agustin.backend_dialysis_record.dto.SessionDto;
import com.agustin.backend_dialysis_record.service.PatientService;
import com.agustin.backend_dialysis_record.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final SessionService sessionService;

    @Autowired
    public PatientController(PatientService patientService, SessionService sessionService) {
        this.patientService = patientService;
        this.sessionService = sessionService;
    }

    /* =====================================================
       PATIENTS
       ===================================================== */

    @PostMapping
    public ResponseEntity<PatientDto> create(@Valid @RequestBody PatientDto patientDto) {
        PatientDto patient = patientService.create(patientDto);
        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public ResponseEntity<PatientMeDto> getMe(Authentication auth) {
        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(patientService.getMyPatient(userAccountId));
    }

    // Listar pacientes: doctor ve solo los suyos (filtrado en service/repo), admin ve todos
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<PatientDto> patients = patientService.findAll();
        return ResponseEntity.ok(patients);
    }

    // Ver paciente: doctor dueño o el propio paciente
    @PreAuthorize("@authz.canAccessPatient(#patientId)")
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable UUID patientId) {
        PatientDto patient = patientService.findById(patientId);
        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("@authz.canAccessPatient(#patientId)")
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientDto> update(@PathVariable UUID patientId,
                                             @Valid @RequestBody PatientDto patientDto) {
        PatientDto patient = patientService.update(patientId, patientDto);
        return ResponseEntity.ok(patient);
    }

    @PreAuthorize("(hasRole('DOCTOR') or hasRole('ADMIN')) and @authz.canAccessPatient(#patientId)")
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> delete(@PathVariable UUID patientId) {
        patientService.delete(patientId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("(hasRole('DOCTOR') or hasRole('ADMIN')) and @authz.canAccessPatient(#patientId)")
    @PatchMapping("/{patientId}/activate")
    public ResponseEntity<PatientDto> activatePatient(@PathVariable UUID patientId) {
        PatientDto patientDto = patientService.activate(patientId);
        return ResponseEntity.ok(patientDto);
    }

    /* =====================================================
       SESSIONS UNDER PATIENT
       ===================================================== */

    @PreAuthorize("@authz.canAccessPatient(#patientId)")
    @PostMapping("/{patientId}/sessions")
    public ResponseEntity<SessionDto> createSession(@PathVariable UUID patientId,
                                                    @Valid @RequestBody SessionDto sessionDto) {
        SessionDto created = sessionService.createForPatient(patientId, sessionDto);
        return ResponseEntity.ok(created);
    }

    // Listar sesiones del paciente (con rango opcional)
    @PreAuthorize("@authz.canAccessPatient(#patientId)")
    @GetMapping("/{patientId}/sessions")
    public ResponseEntity<List<SessionDto>> getSessions(@PathVariable UUID patientId,
                                                        @RequestParam(required = false) LocalDate startDate,
                                                        @RequestParam(required = false) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(
                    sessionService.findSessionsByPatientIdAndDateRange(patientId, startDate, endDate)
            );
        }
        return ResponseEntity.ok(sessionService.findSessionsByPatientId(patientId));
    }

    // Sesiones por día
    @PreAuthorize("@authz.canAccessPatient(#patientId)")
    @GetMapping("/{patientId}/sessions/day/{day}")
    public ResponseEntity<List<SessionDto>> getSessionsByDay(@PathVariable UUID patientId,
                                                             @PathVariable LocalDate day) {
        return ResponseEntity.ok(sessionService.findSessionsByDay(patientId, day));
    }
}
