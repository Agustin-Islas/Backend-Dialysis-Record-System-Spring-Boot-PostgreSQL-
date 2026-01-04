package com.agustin.backend_dialysis_record.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter @Getter
public class SessionDto {
    private UUID id;

    @NotNull(message = "date is required")
    @PastOrPresent(message = "date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "hour is required")
    private LocalTime hour;

    @Min(value = 0, message = "bag must be >= 0 ")
    private Integer bag;

    // libre, pero no negativa
    @DecimalMin(value = "0.0", inclusive = true, message = "concentration must be >= 0")
    private Float concentration;

    // ml, sin máximo
    @Min(value = 0, message = "infusion must be >= 0 (ml)")
    private Integer infusion;

    // ml, sin máximo
    @Min(value = 0, message = "drainage must be >= 0 (ml)")
    private Integer drainage;

    // ml, puede ser negativo. Le pongo piso para evitar valores ridículos.
    @Min(value = -1000000, message = "partial is too small")
    private Integer partial;

    @Size(max = 500, message = "observations max length is 500")
    private String observations;

    // se completa solo
    @Size(max = 120, message = "patientName max length is 120")
    private String patientName;

    // se completa solo
    private UUID patientId;
}
