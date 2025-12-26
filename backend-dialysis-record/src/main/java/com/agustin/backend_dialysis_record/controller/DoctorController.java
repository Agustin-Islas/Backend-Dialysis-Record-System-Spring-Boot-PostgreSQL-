package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PatchMapping("/{doctorId}/activate")
    public ResponseEntity<DoctorDto> activateDoctor(@PathVariable("doctorId") UUID doctorId){
        DoctorDto doctorDto = doctorService.activate(doctorId);
        return ResponseEntity.ok(doctorDto);
    }

    @PostMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<PatientDto> addPatient(@PathVariable UUID doctorId, @PathVariable UUID patientId) {
        PatientDto savedPatient = doctorService.addPatientToDoctor(doctorId, patientId);
        return ResponseEntity.ok(savedPatient);
    }

    @DeleteMapping("/{doctorId}/patients/{patientId}")
    public ResponseEntity<Void> removePatient(@PathVariable UUID doctorId, @PathVariable UUID patientId) {
        doctorService.removePatientFromDoctor(doctorId, patientId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody DoctorDto doctorDto) {
        DoctorDto doctor = doctorService.create(doctorDto);
        return ResponseEntity.ok().body(doctor);
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.findAll();
        return ResponseEntity.ok().body(doctors);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable UUID doctorId) {
        DoctorDto doctor = doctorService.findById(doctorId);
        return ResponseEntity.ok().body(doctor);
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable UUID doctorId, @RequestBody DoctorDto doctorDto) {
        DoctorDto doctor = doctorService.update(doctorId, doctorDto);
        return ResponseEntity.ok().body(doctor);
    }

    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable UUID doctorId) {
        doctorService.delete(doctorId);
        return ResponseEntity.noContent().build();
    }
}
