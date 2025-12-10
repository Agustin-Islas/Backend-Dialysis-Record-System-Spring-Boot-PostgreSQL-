package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Setter @Getter
public class PatientDto {
    private Long id;
    private String name;
    private String surname;
    private int dni;
    private LocalDate dateOfBirth;
    private String address;
    private int number;
    private String email;
    private String doctorName;
    private Long doctorId;
}
