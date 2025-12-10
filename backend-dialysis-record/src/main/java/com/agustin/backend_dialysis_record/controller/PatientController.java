package com.agustin.backend_dialysis_record.controller;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.dto.SessionDto;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.service.PatientService;
import com.agustin.backend_dialysis_record.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    //get ultrafilter

    @PatchMapping("{patientId}/activate")
    public ResponseEntity<PatientDto> activatePatient(@PathVariable("patientId") Long patientId){
        PatientDto patientDto = patientService.activate(patientId);
        return ResponseEntity.ok(patientDto);
    }


    @PostMapping
    public ResponseEntity<PatientDto> create(@RequestBody PatientDto patientDto) {
        PatientDto patient = patientService.create(patientDto);
        return ResponseEntity.ok().body(patient);
    }

    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        List<PatientDto> patients = patientService.findAll();
        return ResponseEntity.ok().body(patients);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable long patientId) {
        PatientDto patient = patientService.findById(patientId);
        return ResponseEntity.ok().body(patient);
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<PatientDto> update(@PathVariable long patientId, @RequestBody PatientDto patientDto) {
        PatientDto patient = patientService.update(patientId, patientDto);
        return ResponseEntity.ok().body(patient);
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> delete(@PathVariable("patientId") long patientId) {
        patientService.delete(patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{patientId}/sessions")
    public ResponseEntity<List<SessionDto>> getSessions(
                                                    @PathVariable Long patientId,
                                                    @RequestParam(required = false) LocalDate startDate,
                                                    @RequestParam(required = false) LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(sessionService.findSessionsByPatientIdAndDateRange(patientId, startDate, endDate));
        }
        return ResponseEntity.ok(sessionService.findSessionsByPatientId(patientId));
    }


    @GetMapping("/{patientId}/sessions/day/{day}")
    public ResponseEntity<List<SessionDto>> getSessionsByDay(@PathVariable long patientId,
                                                             @PathVariable LocalDate day) {
        return ResponseEntity.ok(sessionService.findSessionsByDay(patientId, day));
    }
}
