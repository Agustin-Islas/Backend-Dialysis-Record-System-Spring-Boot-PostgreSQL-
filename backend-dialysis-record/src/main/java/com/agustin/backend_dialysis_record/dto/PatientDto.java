package com.agustin.backend_dialysis_record.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Setter @Getter
public class PatientDto {

    private UUID id;

    @NotBlank
    @Size(max = 60)
    private String name;

    @NotBlank
    @Size(max = 60)
    private String surname;

    @NotNull
    @Min(1_000_000)
    @Max(99_999_999)
    private Integer dni;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(max = 120)
    private String address;

    @NotNull
    @Min(1)
    @Max(99999)
    private Integer number;

    private String doctorName;
    private UUID doctorId;
}
