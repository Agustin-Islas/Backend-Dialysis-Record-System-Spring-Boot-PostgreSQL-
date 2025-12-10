package com.agustin.backend_dialysis_record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter @Getter
public class SessionDto {
    private Long id;
    private LocalDate date;
    private LocalTime hour;
    private int bag;
    private int concentration;
    private int infusion;
    private int drainage;
    private int partial;
    private String observations;
    private String patientName;
    private Long patientId;
}
