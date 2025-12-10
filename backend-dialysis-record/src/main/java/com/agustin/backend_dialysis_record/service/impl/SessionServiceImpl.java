package com.agustin.backend_dialysis_record.service.impl;

import com.agustin.backend_dialysis_record.dto.SessionDto;
import com.agustin.backend_dialysis_record.mapper.SessionMapper;
import com.agustin.backend_dialysis_record.model.Session;
import com.agustin.backend_dialysis_record.repository.SessionRepository;
import com.agustin.backend_dialysis_record.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository, SessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public List<SessionDto> findAll() {
        return sessionRepository.findAll()
                .stream().map(sessionMapper::toDto).toList();
    }

    @Override
    public SessionDto findById(Long id) {
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
    public SessionDto update(Long id, SessionDto sessionDto) {
        if (sessionDto.getId() != null && !sessionDto.getId().equals(id))
            throw new IllegalArgumentException("Path id and DTO id must match");

        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        sessionMapper.updateEntityFromDTO(session, sessionDto);
        session = sessionRepository.save(session);
        return sessionMapper.toDto(session);
    }

    @Override
    public void delete(Long id) {
        if (!sessionRepository.existsById(id))
            throw new RuntimeException("Session not found with id: " + id);
        sessionRepository.deleteById(id);
    }

    @Override
    public List<SessionDto> findSessionsByPatientId(Long patientId) { //TODO: CHECK NULLS
        return sessionRepository.findByPatientIdOrderByDateDesc(patientId)
                .stream().map(sessionMapper::toDto).toList();
    }

    @Override
    public List<SessionDto> findSessionsByPatientIdAndDateRange(Long patientId, LocalDate startDate, LocalDate endDate) {
        List<SessionDto> sessions = findSessionsByPatientId(patientId);
        return sessions.stream().filter(
                sessionDto -> sessionDto.getDate().isAfter(startDate) && sessionDto.getDate().equals(endDate)).toList();
    }

    @Override
    public List<SessionDto> findSessionsByDay(Long patientId, LocalDate day) { //TODO: CHECK NULLS
        return findSessionsByPatientId(patientId)
                .stream().filter(session -> session.getDate().equals(day)).toList();
    }
}
