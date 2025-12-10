package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class DoctorDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<Long> patientIds;
}
