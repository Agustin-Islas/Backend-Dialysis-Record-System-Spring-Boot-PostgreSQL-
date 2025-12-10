package com.agustin.backend_dialysis_record.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE session SET active = false WHERE id = ?")
@SQLRestriction("active = true")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDate date;
    private LocalTime hour;
    private int bag;
    private int concentration;
    private int infusion;
    private int drainage;
    private int partial;
    private String observations;
    @ManyToOne
    private Patient patient;

    public Session(LocalDate date, LocalTime hour, int bag, int concentration,
                   int infusion, int drainage, int partial, String observations) {
        this.date = date;
        this.hour = hour;
        this.bag = bag;
        this.concentration = concentration;
        this.infusion = infusion;
        this.drainage = drainage;
        this.partial = partial;
        this.observations = observations;
    }

    public void setPartial() {
        partial = infusion - drainage;
    }
}
