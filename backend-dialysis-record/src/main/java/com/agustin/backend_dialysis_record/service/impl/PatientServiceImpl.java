package com.agustin.backend_dialysis_record.service.impl;

import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.mapper.PatientMapper;
import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.model.Session;
import com.agustin.backend_dialysis_record.repository.PatientRepository;
import com.agustin.backend_dialysis_record.repository.SessionRepository;
import com.agustin.backend_dialysis_record.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final SessionServiceImpl sessionService;
    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper, SessionServiceImpl sessionService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.sessionService = sessionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> findAll() {
        return patientRepository.findAll()
                .stream().map(patientMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto findById(UUID id) {
        return patientRepository.findById(id).map(patientMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    @Override
    public PatientDto create(PatientDto patientDto) {
        Patient savePatient = patientRepository.save(patientMapper.toEntity(patientDto));
        return patientMapper.toDto(savePatient);
    }

    @Override
    public PatientDto update(UUID id, PatientDto patientDto) {
        if (patientDto.getId() != null && !patientDto.getId().equals(id))
            throw new IllegalArgumentException("Path id and DTO id must match");

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patientMapper.updateEntityFromDTO(patient, patientDto);
        Patient savePatient = patientRepository.save(patient);
        return patientMapper.toDto(savePatient);
    }

    @Override
    public void delete(UUID id) {
        if (!patientRepository.existsById(id))
            throw new RuntimeException("Patient not found with id: " + id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        List<Session> sessions = patient.getSessions();
        sessions.forEach(session -> sessionService.delete(session.getId()));

        patientRepository.deleteById(id);
    }

    @Override
    public PatientDto activate(UUID patientId) {
        Patient patient = patientRepository.findByIdIncludingInactive(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        patient.setActive(true);
        return patientMapper.toDto(patientRepository.save(patient));
    }
}
