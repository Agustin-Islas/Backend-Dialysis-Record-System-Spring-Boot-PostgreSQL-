package com.agustin.backend_dialysis_record.mapper;

import com.agustin.backend_dialysis_record.dto.DoctorDto;
import com.agustin.backend_dialysis_record.model.Doctor;
import com.agustin.backend_dialysis_record.model.Patient;
import com.agustin.backend_dialysis_record.repository.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DoctorMapper implements GenericMapper<Doctor, DoctorDto> {

    private final PatientRepository patientRepository;

    public DoctorMapper(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Doctor toEntity(DoctorDto doctorDto) {
        if (doctorDto == null) { return null; }

        Doctor doctor = new Doctor();
        doctor.setName(doctorDto.getName());
        doctor.setSurname(doctorDto.getSurname());
        doctor.setEmail(doctorDto.getEmail());

        return doctor;
    }

    @Override
    public DoctorDto toDto(Doctor doctor) {
        if (doctor == null) { return null; }

        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setId(doctor.getId());
        doctorDto.setName(doctor.getName());
        doctorDto.setSurname(doctor.getSurname());
        doctorDto.setEmail(doctor.getEmail());

        List<Long> patientIds = new ArrayList<>();
        if (doctor.getPatients() != null && !doctor.getPatients().isEmpty()) {
            for (Patient patient: doctor.getPatients()) {
                patientIds.add(patient.getId());
            }
        }
        doctorDto.setPatientIds(patientIds);

        return doctorDto;
    }

    @Override
    public void updateEntityFromDTO(Doctor doctor, DoctorDto doctorDto) {
        if (doctor == null || doctorDto == null) {
            return; //Todo: exception?
        }
        doctor.setName(doctorDto.getName());
        doctor.setSurname(doctorDto.getSurname());
        doctor.setEmail(doctorDto.getEmail());

        if (doctorDto.getPatientIds() != null) {
            List<Patient> patients = patientRepository.findAllById(doctorDto.getPatientIds());
            doctor.setPatients(patients);

            // Keep the bidirectional relationship consistent
            for (Patient patient : patients) {
                patient.setDoctor(doctor);
            }
        }

    }
}
