package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Setter @Getter
public class PatientDto {
    private UUID id;
    private String name;
    private String surname;
    private int dni;
    private LocalDate dateOfBirth;
    private String address;
    private int number;
    private String doctorName;
    private UUID doctorId;
}
