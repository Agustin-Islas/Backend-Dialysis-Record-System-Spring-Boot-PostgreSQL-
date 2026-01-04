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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final PatientServiceImpl patientService;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, PatientRepository patientRepository, DoctorMapper doctorMapper,
                             PatientMapper patientMapper, PatientServiceImpl patientService) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorMapper = doctorMapper;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto> findAll() {
        return doctorRepository.findAll()
                .stream().map(doctorMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto findById(UUID id) {
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
    public DoctorDto update(UUID id, DoctorDto doctorDto) {
        if (doctorDto.getId() != null && !doctorDto.getId().equals(id))
            throw new IllegalArgumentException("Path id and DTO id must match");

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        doctorMapper.updateEntityFromDTO(doctor, doctorDto);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(savedDoctor);
    }

    @Override
    public void delete(UUID id) {
        if (!doctorRepository.existsById(id))
            throw new RuntimeException("Doctor not found with id: " + id);
        doctorRepository.deleteById(id);
    }

    @Override
    public PatientDto addPatientToDoctor(UUID doctorId, UUID patientId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        doctor.addPatient(patient);
        patientRepository.save(patient);

        return patientMapper.toDto(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto> getPatientsByDoctor(UUID doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        return doctor.getPatients()
                .stream().map(patientMapper::toDto).toList();
    }

    @Override
    public void removePatientFromDoctor(UUID doctorId, UUID patientId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        doctor.removePatient(patient);
    }

    @Override
    public DoctorDto activate(UUID doctorId) {
        Doctor doctor = doctorRepository.findByIdIncludingInactive(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        doctor.setActive(true);
        return doctorMapper.toDto(doctorRepository.save(doctor));
    }
}
