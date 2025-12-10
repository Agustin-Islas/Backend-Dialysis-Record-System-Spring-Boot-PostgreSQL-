package com.agustin.backend_dialysis_record.service;

import com.agustin.backend_dialysis_record.dto.PatientDto;
import java.util.List;

public interface PatientService {
    List<PatientDto> findAll();

    PatientDto findById(Long id);

    PatientDto create(PatientDto patientDto);

    PatientDto update(Long id, PatientDto patientDto);

    void delete(Long id);

    PatientDto activate(Long patientId);

}
