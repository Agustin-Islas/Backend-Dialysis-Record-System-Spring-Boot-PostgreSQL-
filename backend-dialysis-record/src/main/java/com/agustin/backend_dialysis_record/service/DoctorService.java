package com.agustin.backend_dialysis_record.service;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;

import java.util.List;

public interface DoctorService {
    List<DoctorDto> findAll();

    DoctorDto findById(Long id);

    DoctorDto create(DoctorDto doctorDto);

    DoctorDto update(Long id, DoctorDto doctorDto);

    void delete(Long id);

    PatientDto addPatientToDoctor(Long doctorId, Long patientId);

    void removePatientFromDoctor(Long doctorId, Long patientId);

    DoctorDto activate(Long doctorId);
}
