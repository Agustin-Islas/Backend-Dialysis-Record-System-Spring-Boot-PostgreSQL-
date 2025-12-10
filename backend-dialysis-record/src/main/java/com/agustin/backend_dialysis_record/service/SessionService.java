package com.agustin.backend_dialysis_record.service;

import com.agustin.backend_dialysis_record.dto.SessionDto;

import java.time.LocalDate;
import java.util.List;

public interface SessionService {
    List<SessionDto> findAll();

    SessionDto findById(Long id);

    SessionDto create(SessionDto sessionDto);

    SessionDto update(Long id, SessionDto sessionDto);

    void delete(Long id);

    List<SessionDto> findSessionsByPatientId(Long patientId);

    List<SessionDto> findSessionsByPatientIdAndDateRange(Long patientId, LocalDate startDate, LocalDate endDate);

    List<SessionDto> findSessionsByDay(Long patientId, LocalDate day);
}
