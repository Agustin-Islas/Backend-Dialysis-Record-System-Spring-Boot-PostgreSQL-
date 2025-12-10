package com.agustin.backend_dialysis_record.service.impl;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.mapper.DoctorMapper;
import com.agustin.backend_dialysis_record.mapper.PatientMapper;
import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.repository.DoctorRepository;
import com.agustin.backend_dialysis_record.repository.PatientRepository;
import com.agustin.backend_dialysis_record.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final PatientServiceImpl patientService;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, DoctorMapper doctorMapper,
                             PatientMapper patientMapper, PatientServiceImpl patientService) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @Override
    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream().map(doctorMapper::toDto).toList();
    }

    @Override
    public DoctorDto findById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    @Override
        public DoctorDto create(DoctorDto doctorDto) {
            Doctor doctor = doctorMapper.toEntity(doctorDto);
            doctor = doctorRepository.save(doctor);
            return doctorMapper.toDto(doctor);
        }

    @Override
    public DoctorDto update(Long id, DoctorDto doctorDto) {
        if (doctorDto.getId() != null && !doctorDto.getId().equals(id))
            throw new IllegalArgumentException("Path id and DTO id must match");

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        doctorMapper.updateEntityFromDTO(doctor, doctorDto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(savedDoctor);
    }

    @Override
    public void delete(Long id) {
        if (!doctorRepository.existsById(id))
            throw new RuntimeException("Doctor not found with id: " + id);
        doctorRepository.deleteById(id);
    }

    public PatientDto addPatientToDoctor(Long doctorId, Long patientId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        PatientDto patientDto = patientService.findById(patientId);
        doctor.addPatient(patientMapper.toEntity(patientDto));
        return patientDto;
    }

    public void removePatientFromDoctor(Long doctorId, Long patientId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        Patient patient = patientMapper.toEntity(patientService.findById(patientId));
        doctor.removePatient(patient);
    }

    public DoctorDto activate(Long doctorId) {
        Doctor doctor = doctorRepository.findByIdIncludingInactive(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        doctor.setActive(true);
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }
}
