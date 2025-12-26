package com.agustin.backend_dialysis_record.mapper;

import com.agustin.backend_dialysis_record.dto.PatientDto;
import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.repository.DoctorRepository;
import com.agustin.backend_dialysis_record.repository.SessionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientMapper implements GenericMapper<Patient, PatientDto> {
    private final SessionRepository sessionRepository;
    private final DoctorRepository doctorRepository;

    public PatientMapper(SessionRepository sessionRepository, DoctorRepository doctorRepository) {
        this.sessionRepository = sessionRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Patient toEntity(PatientDto patientDto) {
        if (patientDto == null) { return null; }

        Patient patient = new Patient();
        patient.setName(patientDto.getName());
        patient.setSurname(patientDto.getSurname());
        patient.setDni(patientDto.getDni());
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setAddress(patientDto.getAddress());
        patient.setNumber(patientDto.getNumber());
        patient.setEmail(patientDto.getEmail());
        if (patientDto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.getReferenceById(patientDto.getDoctorId());
            patient.setDoctor(doctor);
        } else {
            patient.setDoctor(null);
        }

        return patient;
    }

    @Override
    public PatientDto toDto(Patient patient) {
        if  (patient == null) { return null; }

        PatientDto patientDto = new PatientDto();
        patientDto.setId(patient.getId());
        patientDto.setName(patient.getName());
        patientDto.setSurname(patient.getSurname());
        patientDto.setDni(patient.getDni());
        patientDto.setDateOfBirth(patient.getDateOfBirth());
        patientDto.setAddress(patient.getAddress());
        patientDto.setNumber(patient.getNumber());
        patientDto.setEmail(patient.getEmail());
        if (patientDto.getDoctorId() != null) {
            patientDto.setDoctorName(patient.getDoctor().getName());
            patientDto.setDoctorId(patient.getDoctor().getId());
        } else {
            patient.setDoctor(null);
        }
        return patientDto;
    }

    @Override
    public void updateEntityFromDTO(Patient patient, PatientDto patientDto) {
        if (patient == null || patientDto == null) { return; }

        patient.setName(patientDto.getName());
        patient.setSurname(patientDto.getSurname());
        patient.setDni(patientDto.getDni());
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setAddress(patientDto.getAddress());
        patient.setNumber(patientDto.getNumber());
        patient.setEmail(patientDto.getEmail());
        if (patientDto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.getReferenceById(patientDto.getDoctorId());
            patient.setDoctor(doctor);
        }
        //List<Session> sessions = sessionRepository.findByPatientIdOrderByDateDesc(patient.getId());
        //patient.setSessions(sessions);
    }
}
