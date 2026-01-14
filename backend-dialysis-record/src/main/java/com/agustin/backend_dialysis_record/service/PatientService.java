package com.agustin.backend_dialysis_record.service;

import com.agustin.backend_dialysis_record.dto.PatientDto;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    List<PatientDto> findAll();

    PatientDto findById(UUID id);

    PatientDto create(PatientDto patientDto);

    PatientDto update(UUID id, PatientDto patientDto);

    void delete(UUID id);

    PatientDto activate(UUID patientId);

    @Nullable PatientDto getMyPatient(UUID userAccountId);
}
