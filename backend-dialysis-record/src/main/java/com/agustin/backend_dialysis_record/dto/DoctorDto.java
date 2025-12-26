package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class DoctorDto {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private List<UUID> patientIds;
}
