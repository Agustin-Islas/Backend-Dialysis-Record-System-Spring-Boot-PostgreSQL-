package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /* =====================================================
       /me (Self endpoints)
       ===================================================== */

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/me")
    public ResponseEntity<DoctorDto> getMe(Authentication auth) { //TODO: crear DoctorMeDto que extiende DoctorDto
        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(doctorService.getMyDoctor(userAccountId));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/me/patients")
    public ResponseEntity<List<PatientDto>> getMyPatients(Authentication auth) {
        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(doctorService.getMyPatients(userAccountId));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/me/patients/{patientId}")
    public ResponseEntity<PatientDto> addPatientToMe(Authentication auth, @PathVariable UUID patientId) {
        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());
        return ResponseEntity.ok(doctorService.addPatientToMyDoctor(userAccountId, patientId));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/me/patients/{patientId}")
    public ResponseEntity<Void> removePatientFromMe(Authentication auth, @PathVariable UUID patientId) {
        UUID userAccountId = UUID.fromString(auth.getPrincipal().toString());
        doctorService.removePatientFromMyDoctor(userAccountId, patientId);
        return ResponseEntity.noContent().build();
    }

    /* =====================================================
       Admin/Managed endpoints (by doctorId)
       ===================================================== */

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{doctorId}/activate")
    public ResponseEntity<DoctorDto> activateDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.activate(doctorId));
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.canAccessDoctor(#doctorId)")
    @PostMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<PatientDto> addPatient(@PathVariable UUID doctorId, @PathVariable UUID patientId) {
        return ResponseEntity.ok(doctorService.addPatientToDoctor(doctorId, patientId));
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.canAccessDoctor(#doctorId)")
    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<List<PatientDto>> getPatientsByDoctor(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.getPatientsByDoctor(doctorId));
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.canAccessDoctor(#doctorId)")
    @DeleteMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<Void> removePatient(@PathVariable UUID doctorId, @PathVariable UUID patientId) {
        doctorService.removePatientFromDoctor(doctorId, patientId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.create(doctorDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.canAccessDoctor(#doctorId)")
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable UUID doctorId) {
        return ResponseEntity.ok(doctorService.findById(doctorId));
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.canAccessDoctor(#doctorId)")
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable UUID doctorId,
                                                  @Valid @RequestBody DoctorDto doctorDto) {
        return ResponseEntity.ok(doctorService.update(doctorId, doctorDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable UUID doctorId) {
        doctorService.delete(doctorId);
        return ResponseEntity.noContent().build();
    }
}
