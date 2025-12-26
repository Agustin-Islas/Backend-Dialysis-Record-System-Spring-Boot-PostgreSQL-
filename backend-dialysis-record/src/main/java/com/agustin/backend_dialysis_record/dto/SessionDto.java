package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter @Getter
public class SessionDto {
    private UUID id;
    private LocalDate date;
    private LocalTime hour;
    private int bag;
    private float concentration;
    private int infusion;
    private int drainage;
    private int partial;
    private String observations;
    private String patientName;
    private UUID patientId;
}
