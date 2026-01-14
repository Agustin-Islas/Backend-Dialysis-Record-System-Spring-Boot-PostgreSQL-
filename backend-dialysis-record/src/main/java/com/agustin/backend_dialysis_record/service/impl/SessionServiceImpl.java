package com.agustin.backend_dialysis_record.service.impl;

import com.agustin.backend_dialysis_record.dto.SessionDto;
import com.agustin.backend_dialysis_record.mapper.SessionMapper;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.model.Session;
import com.agustin.backend_dialysis_record.repository.PatientRepository;
import com.agustin.backend_dialysis_record.repository.SessionRepository;
import com.agustin.backend_dialysis_record.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final SessionMapper sessionMapper;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository, PatientRepository patientRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.patientRepository = patientRepository;
        this.sessionMapper = sessionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> findAll() {
        return sessionRepository.findAll()
                .stream().map(sessionMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SessionDto findById(UUID id) {
        return sessionRepository.findById(id)
                .map(sessionMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
    }

    @Override
    public SessionDto create(SessionDto sessionDto) {
        Session session = sessionMapper.toEntity(sessionDto);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    @Override
    public SessionDto update(UUID id, SessionDto sessionDto) {
        if (sessionDto.getId() != null && !sessionDto.getId().equals(id))
            throw new IllegalArgumentException("Path id and DTO id must match");

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        sessionMapper.updateEntityFromDTO(session, sessionDto);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    @Override
    public void delete(UUID id) {
        if (!sessionRepository.existsById(id))
            throw new RuntimeException("Session not found with id: " + id);
        sessionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> findSessionsByPatientId(UUID patientId) { //TODO: CHECK NULLS
        return sessionRepository.findByPatientIdOrderByDateDesc(patientId)
                .stream().map(sessionMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> findSessionsByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate) {
        List<SessionDto> sessions = findSessionsByPatientId(patientId);
        return sessions.stream().filter(
                sessionDto -> sessionDto.getDate().isAfter(startDate) && sessionDto.getDate().equals(endDate)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDto> findSessionsByDay(UUID patientId, LocalDate day) { //TODO: CHECK NULLS
        return findSessionsByPatientId(patientId)
                .stream().filter(session -> session.getDate().equals(day)).toList();
    }

    @Override
    public SessionDto createForPatient(UUID patientId, SessionDto sessionDto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));

        Session session = sessionMapper.toEntity(sessionDto);

        // 3) forzar ownership: paciente desde path
        session.setPatient(patient);

        if (session.getDate() == null) session.setDate(LocalDate.now());

        Session saved = sessionRepository.save(session);
        return sessionMapper.toDto(saved);
    }
}
